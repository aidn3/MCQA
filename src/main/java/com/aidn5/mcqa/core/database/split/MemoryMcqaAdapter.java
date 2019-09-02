
package com.aidn5.mcqa.core.database.split;

import com.aidn5.mcqa.core.content.Content;
import com.aidn5.mcqa.core.database.StorageException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


/**
 * Adapter saves the {@link Content} {@link HashMap}. This adapter has the best
 * question search query and also one of the fastest adapter. see
 * {@link SplitMcqaAdapter}. <i>Note: data will be lost if the adapter is
 * discarded See {@link ByteMcqaAdapter} or {@link JsonMcqaAdapter} for an
 * adapter that saves data.</i> This adapter is the fastest from latency. It can
 * read and write up to one million {@link Content} in less than a second and
 * search (one operation) between the one million in one second.
 *
 * @author aidn5
 * 
 * @see SplitMcqaAdapter
 * @see JsonMcqaAdapter
 * @see ByteMcqaAdapter
 */
public class MemoryMcqaAdapter extends SplitMcqaAdapter {
  HashMap<Long, Content> contents = new HashMap<>();

  /**
   * Constructor for the adapter.
   */
  public MemoryMcqaAdapter() {
    super();
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

      // write the content to file
      contents.put(newId, content);

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
      Content content = contents.remove(contentId);

      if (content == null) {
        return false;
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
  public Content getContent(long contentId) throws StorageException {
    lock.lock();

    try {
      Content content = contents.get(contentId);

      if (content == null) {
        return null;
      }

      content.contentId = contentId;
      return content;

    } catch (Exception e) {
      throw new StorageException("db.error.general", e);

    } finally {
      lock.unlock();
    }
  }

  @Override
  public void setCurrentId(long id) {
    this.currentId = id;
  }

  @Override
  protected Content toContent(byte[] contentBytes) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  protected byte[] fromContent(Content content) throws Exception {
    throw new UnsupportedOperationException();
  }

}
