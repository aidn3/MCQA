
package com.aidn5.mcqa.core.database.split;

import com.aidn5.mcqa.core.content.Content;
import com.aidn5.mcqa.core.content.Sanitizer;
import com.aidn5.mcqa.core.database.IContentsIterator;
import com.aidn5.mcqa.core.database.IMcqaDatabase;
import com.aidn5.mcqa.core.database.StorageException;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Adapter saves the data in separate files for every individual content. This
 * adapter has the best question search query, but also one of the slowest
 * adapter.
 * 
 * <p>
 * <b>The search query for this adapter goes like this:</b> Check if
 * <ul>
 * <li>the whole query matches the question</li>
 * <li>the whole query matches the start of the question</li>
 * <li>the whole query matches any whole part of the question</li>
 * <li>the number of words matches the question</li>
 * </ul>
 * 
 * <p>
 * <b>Understanding of the adapter:</b>
 * The file name is the id of the content with the extension ".content" (e.g.
 * "2.content"). Another file with the name "settings.txt" will be created. The
 * file only holds the current ID increment. the whole dictionary will be
 * utilized for the adapter. To reduce the I/O uses, questions and their ID are
 * cached in hashmap as [question, id]. The file is updated with every new
 * content write. The adapter is immune to data lost (like sudden crash), since
 * all data are all directly written to storage.
 * 
 * @author aidn5
 */
public abstract class SplitMcqaAdapter implements IMcqaDatabase {

  protected final ReentrantLock lock = new ReentrantLock();
  protected final HashMap<String, Long> questions = new HashMap<>();
  protected final TreeSet<Long> approvedQuestions = new TreeSet<>();
  protected final TreeSet<Long> unapprovedQuestions = new TreeSet<>();


  protected final File dirContents;
  protected final File currentIdFile;
  protected volatile boolean closed = false;
  protected volatile long currentId = 0;

  /**
   * Create an instance of the adapter for costume adapter that support caching
   * and advanced search. Note: {@link #dirContents} and {@link #currentIdFile}
   * will be null. Use your own implementation to fulfill them.
   */
  protected SplitMcqaAdapter() {
    dirContents = null;
    currentIdFile = null;
  }

  /**
   * Create an instance of the adapter for that support caching
   * and advanced search.
   * 
   * @param dirContents
   *          the dictionary to utilize for the database
   * 
   * @throws IOException
   *           if any I/O error occurs while trying to utilize the dictionary
   * @throws NullPointerException
   *           if {@code dirContents} is <code>null</code>
   */
  protected SplitMcqaAdapter(@Nullable File dirContents) throws IOException, NullPointerException {
    this.dirContents = Objects.requireNonNull(dirContents);

    if (dirContents.isFile()) {
      throw new FileAlreadyExistsException(dirContents.getPath());
    }

    dirContents.mkdirs();

    this.currentIdFile = new File(dirContents, "settings.txt");

    if (currentIdFile.isDirectory()) {
      throw new FileAlreadyExistsException(currentIdFile.getPath());
    }

    if (currentIdFile.exists()) {
      String content = Files.readFirstLine(currentIdFile, StandardCharsets.UTF_8);
      this.currentId = Long.valueOf(content.trim());

    } else {
      Files.write("0", currentIdFile, StandardCharsets.UTF_8);
      this.currentId = 0;
    }

    initSplitContents();
  }

  /**
   * get the current (last used) {@code id} for contents. The {@code id} is used
   * as key for the content and the {@code content} as value. To register new
   * content the {@code id} should be increased and re-saved with
   * {@link #setCurrentId(long)}.
   * 
   * @return
   *         the current, last used, id.
   */
  public long getCurrentId() {
    return currentId;
  }

  /**
   * Set new {@code id} for the database. Usually used to either update the
   * {@code id} after adding new content or to restore the database state.
   * 
   * @param newId
   *          the {@code id} to update to.
   * 
   * @throws StorageException
   *           if any error occurs while updating the {@code id}.
   */
  public void setCurrentId(long newId) throws StorageException {
    long oldId = currentId;
    currentId = newId;
    try {
      Files.write(newId + "", currentIdFile, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new StorageException("could not update the id from " + oldId + "->" + newId, e);
    }
  }

  @Override
  public boolean connectionOpened() {
    return !closed;
  }

  @Override
  public long addContent(Content content) throws StorageException {
    lock.lock();

    // content has been sanitized and cleaned from the calling method
    try {
      boolean replace = false;

      // check for questions clash with other contents questions
      for (String question : content.questions) {
        Long existsIdObj = questions.get(question);

        if (existsIdObj != null) {
          if (content.contentId == existsIdObj) {
            replace = true;

          } else {
            throw new IllegalArgumentException("db.addContent.error.existedQuestion");
          }
        }
      }

      final long newId = replace ? content.contentId : ++this.currentId;

      // add its questions to the hashmap for faster lookups.
      for (String question : content.questions) {
        questions.put(question, newId);
      }

      // add to the hashmap for faster lookups with the {offset} parameter.
      if (content.hasProvedAnswer()) {
        unapprovedQuestions.remove(newId); // just in case
        approvedQuestions.add(newId);

      } else {
        approvedQuestions.remove(newId); // just in case
        unapprovedQuestions.add(newId);
      }

      // save the current id counter
      setCurrentId(newId);

      // write the content to file
      byte[] data = fromContent(content);
      Files.write(data, new File(dirContents, newId + ".content"));

      return newId;
    } catch (Exception e) {
      throw new StorageException("db.error.general", e);

    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean removeContent(long contentId) throws StorageException {
    lock.lock();

    try {
      File contentFile = new File(dirContents, contentId + ".content");
      if (!contentFile.exists()) {
        return false;
      }

      if (!contentFile.delete()) {
        throw new IOException("Could not delete the file: " + contentFile.getName());
      }


      Iterator<Entry<String, Long>> iterator = questions.entrySet().iterator();
      while (iterator.hasNext()) {
        Entry<String, Long> entry = iterator.next();
        if (entry.getValue() == contentId) {
          iterator.remove();
        }
      }

      approvedQuestions.remove(contentId);
      unapprovedQuestions.remove(contentId);

      return true;
    } catch (Exception e) {
      throw new StorageException("db.error.general", e);

    } finally {
      lock.unlock();
    }
  }

  @Override
  public IContentsIterator searchForContents(boolean onlyApprovedContents, int offset,
      String[] words) throws StorageException {
    lock.lock();
    try {
      if (words == null || words.length == 0) {
        return getAllContents(onlyApprovedContents, offset);
      }

      return searchContents(onlyApprovedContents, offset, words);

    } catch (Exception e) {
      throw new StorageException("db.error.general", e);

    } finally {
      lock.unlock();
    }
  }

  @Override
  public Content getContent(long contentId) throws StorageException {
    lock.lock();

    try {
      File contentFile = new File(dirContents, contentId + ".content");
      if (!contentFile.exists()) {
        return null;
      }

      byte[] contentBytes = Files.toByteArray(contentFile);
      Content content = toContent(contentBytes);

      content.contentId = contentId;
      return content;

    } catch (Exception e) {
      throw new StorageException("db.error.general", e);

    } finally {
      lock.unlock();
    }
  }

  @Override
  public void closeConnection() throws StorageException {
    lock.lock();
    closed = true;
    lock.unlock();
  }

  protected void initSplitContents() {
    System.out.println("initiating JsonDB...");

    questions.clear();
    approvedQuestions.clear();
    unapprovedQuestions.clear();

    // #list() is used instead of #listFiles() for speed performance
    String[] all = this.dirContents.list();

    if (all.length - 1 < 1) {
      return;
    }

    System.out.println((all.length - 1) + " contents found...");


    for (String fileName : all) { // "{id}.content"
      try {
        if (!fileName.endsWith(".content")) {
          continue;
        }

        File file = new File(this.dirContents, fileName);
        if (!file.isFile()) {
          continue;
        }

        long contentId = Long.valueOf(fileName.split(Pattern.quote("."))[0]);
        byte[] contentBytes = Files.toByteArray(file);

        final Content content = toContent(contentBytes);

        if (content.questions == null || content.questions.length == 0) {
          System.out.println("Ignoring " + contentId + " for not having a question...");
          continue;
        }


        if (content.hasProvedAnswer()) {
          approvedQuestions.add(contentId);

        } else {
          unapprovedQuestions.add(contentId);
        }


        for (int i = 0; i < content.questions.length; i++) {
          String question = content.questions[i];

          Long anotherContentId = questions.get(question);
          if (anotherContentId != null) {
            System.out.println("Content " + contentId + " has question[" + i
                + "], which is already used by Content " + anotherContentId
                + ". Ignoring this question...");

          } else {
            questions.put(question, contentId);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    System.out.println(
        "Done. ready questions:" + questions.size()
            + ", contents: " + approvedQuestions.size());
  }

  private SplitContentsIterator getAllContents(boolean onlyApprovedContents, int offset)
      throws StorageException {

    final ArrayList<Long> allContentsId = new ArrayList<>(approvedQuestions);

    if (!onlyApprovedContents) {
      allContentsId.addAll(unapprovedQuestions);
    }


    allContentsId.sort((Long o1, Long o2) -> {
      return o1.compareTo(o2);
    });


    final Iterator<Long> iterator = allContentsId.iterator();
    while (iterator.hasNext() && offset > 0) {
      offset--;
      iterator.next();
      iterator.remove();
    }

    return new SplitContentsIterator(this, iterator);
  }

  private SplitContentsIterator searchContents(boolean onlyApprovedContents, int offset,
      String[] words) {

    final List<String> wordsToSearch = new ArrayList<String>();
    StringBuilder sb = new StringBuilder(words.length * 7);
    for (String word : words) {

      if (word != null) {
        word = Sanitizer.sanitizeText(word, false);

        if (!word.isEmpty()) {
          wordsToSearch.add(word);
          sb.append(word).append(" ");
        }
      }
    }


    final String finalSentence = sb.toString();
    final ArrayList<SortQuestion> questionsDetailed = new ArrayList<>();

    // collect info about how much do the words match every question
    for (Entry<String, Long> question : questions.entrySet()) {
      final Long contentId = question.getValue();
      final String questionS = question.getKey();


      if (onlyApprovedContents && !approvedQuestions.contains(contentId)) {
        continue;
      }


      int rate = 0;

      for (String word : wordsToSearch) {
        if (question.getKey().contains(word)) {
          rate++;
        }
      }


      SortQuestion s = new SortQuestion();

      s.contentId = contentId;
      s.rate = rate;
      s.likeExactAtStart = questionS.startsWith(finalSentence);
      s.likeExact = questionS.contains(finalSentence);
      s.exact = questionS.equalsIgnoreCase(finalSentence);

      if (rate > 0 || s.likeExact || s.likeExactAtStart || s.exact) {
        questionsDetailed.add(s);
      }
    }


    questionsDetailed.sort(new Comparator<SortQuestion>() {
      @Override
      public int compare(SortQuestion o1, SortQuestion o2) {

        int exact = Boolean.compare(o1.exact, o2.exact);
        if (exact != 0) {
          return exact;
        }

        int likeExactAtStart = Boolean.compare(o1.likeExactAtStart, o2.likeExactAtStart);
        if (likeExactAtStart != 0) {
          return likeExactAtStart;
        }

        int likeExact = Boolean.compare(o1.likeExact, o2.likeExact);
        if (likeExact != 0) {
          return likeExact;
        }

        return Integer.compare(o1.rate, o2.rate);
      }
    });

    Iterator<SortQuestion> iterator = questionsDetailed.iterator();
    while (iterator.hasNext() && offset > 0) {
      offset--;
      iterator.next();
      iterator.remove();
    }


    final ArrayList<Long> contentsIds = new ArrayList<>(questionsDetailed.size());

    for (SortQuestion sortQuestion : questionsDetailed) {
      contentsIds.add(sortQuestion.contentId);
    }

    return new SplitContentsIterator(this, contentsIds.iterator());
  }

  /**
   * Write new content from the given {@code contentBytes} and return it. The
   * output of the method should be use-able to transform the content to bytes
   * again by {@link #fromContent(Content)}.
   * 
   * @param contentBytes
   *          the bytes to use to write the content from
   * 
   * @return content equal to the bytes.
   * 
   * @throws Exception
   *           if any error occurs
   */
  @Nullable
  protected abstract Content toContent(@Nonnull byte[] contentBytes) throws Exception;

  /**
   * Write the equivalent bytes for the content from the given {@code content} and
   * return it. The output of the method should be use-able to transform the
   * byte to content again by {@link #toContent(byte[])}.
   * 
   * @param content
   *          the content to use to write the bytes from
   * 
   * @return
   *         equivalent bytes to the content
   * 
   * @throws Exception
   *           if any error occurs
   */
  @Nonnull
  protected abstract byte[] fromContent(@Nonnull Content content) throws Exception;
}
