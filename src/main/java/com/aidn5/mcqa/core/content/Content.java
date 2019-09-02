
package com.aidn5.mcqa.core.content;

import com.aidn5.mcqa.Constants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Object holder holds an instance of one "content". The {@code Content} holds
 * the {@code questions} and its {@code answers} along with the other metadata
 * like {@code category}, {@code register time}, {@code question creator} and
 * others. This class also have methods help with reading from the Object.
 * {@link #equals(Object)} and {@link #hashCode()} is also declared and
 * generated.
 * 
 * @author aidn5
 */
public class Content implements Serializable {
  private static final long serialVersionUID = -947152418974666394L;

  @Nonnull
  private static final Pattern uuidWithDashesP = Pattern
      .compile("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)");
  /**
   * the category of this content.
   */
  @Nonnull
  public String category;

  /**
   * the Unix timestamp in seconds when the questions was added/created.
   */
  public long addedAt;
  /**
   * the id associated with the questions. Created by the database adapter.
   */
  public long contentId;
  /**
   * is the questions proved by an authority to be viewed by the public.
   */
  @Nullable
  public UUID questionProvedBy;
  /**
   * The person's id who created the questions.
   */
  @Nonnull
  public UUID questionCreator;

  /**
   * what the questions is. First index is the main questions. The others are
   * Parallel questions, whose answers are the same.
   */
  @Nonnull
  public String[] questions;

  /**
   * The answer, which is proved by an authority to be view by the public.
   * If this is <code>null</code>, then {@link #answerUnProvedContent} must not be
   * null.
   */
  @Nullable
  public String[] answerProvedContent;

  /**
   * The creator/Editor of the content, which is waiting to be approved by an
   * authority. This must not be <code>null</code> if
   * {@link #answerUnProvedContent} is not null.
   */
  @Nullable
  public UUID creatorOfUnprovedAnswer;

  /**
   * The answer, which <i><u>is waiting to be proved by an authority</u></i>, to
   * be able to view by the public.This must not be <code>null</code> if
   * {@link #creatorOfUnprovedAnswer} is not null.
   * 
   * @see #creatorOfUnprovedAnswer
   */
  @Nullable
  public String[] answerUnProvedContent;


  /**
   * create new instance to use, hold data or add to the database.
   * 
   * @param category
   *          the category of the questions
   * @param isApproved
   *          is the content of the questions and answer are proved/made by an
   *          Authority
   * @param questionContent
   *          what the questions is.
   * @param answerContent
   *          the content of the answer to the questions
   * @param questionCreator
   *          the name of the person who created the questions and the answer
   * 
   * @return an instance holds the content
   */
  @Nonnull
  public static Content createNewContent(@Nonnull String category, boolean isApproved,
      @Nonnull String questionContent, @Nonnull String[] answerContent,
      @Nonnull UUID questionCreator) {

    Content content = new Content();

    content.addedAt = System.currentTimeMillis() / 1000L;
    content.category = Objects.requireNonNull(category);

    content.questions = new String[] { questionContent };
    content.questionCreator = questionCreator;

    if (isApproved) {
      content.answerProvedContent = answerContent;
      content.questionCreator = questionCreator;

    } else {
      content.answerUnProvedContent = answerContent;
      content.creatorOfUnprovedAnswer = questionCreator;
    }

    return content;
  }

  /**
   * Create {@link UUID} from the given {@link String}.
   * This method works with {@code uuid} with and without dashes like "xxxx-xxxx"
   * and "xxxxxxxxxxxx" unlike {@link UUID#fromString(String)}. If the given
   * {@code uuid} is <code>null</code> or empty <code>null</code> will be
   * returned.
   * 
   * @param uuid
   *          the given uuid with or without dashes
   * @return
   *         the equivalent {@link UUID}
   */
  @Nullable
  public static UUID getUuid(@Nullable String uuid) {
    if (uuid == null) {
      return null;

    } else if (uuid.length() == 36) {
      return UUID.fromString(uuid);

    } else if (uuid.length() >= 32) {
      String uuidWithDashes = uuidWithDashesP.matcher(uuid).replaceAll("$1-$2-$3-$4-$5");
      return UUID.fromString(uuidWithDashes);
    }

    return null;
  }

  /**
   * Convert the given {@code UUID} to {@link String}. <code>null</code> will be
   * returned, if the given {@code UUID} is <code>null</code>.
   * 
   * <p><code>
   * return uuid != null ? uuid.toString() : null;
   * </code>
   * 
   * @param uuid
   *          The provided {@link UUID}
   * 
   * @return
   *         a String object representing this UUID.
   */
  @Nullable
  public static String fromUuid(@Nullable UUID uuid) {
    return uuid != null ? uuid.toString() : null;
  }

  /**
   * Sanitize all {@code strings} by removing all non-acceptable chars and pack
   * them into one {@link String} by adding {@link Constants#ANSWER_SPIRATOR}
   * after every {@code string}. The output of this method is mostly safe and can
   * be used to save the answers into the database. Empty {@link String} is
   * returned if nothing or only non-acceptable chars are given as argument.
   * 
   * @param strings
   *          all answers to sanitize and pack
   * @return a packed string contains all the answers, can be used with
   *         {@link #unpackAnswer(String)} to return the answers.
   *         Never returns <code>null</code>.
   */
  @Nonnull
  public static String packAnswer(String... strings) {
    if (strings.length == 0) {
      return null;
    }

    strings = Sanitizer.sanitizeText(strings, true);
    // answer length
    StringBuilder answer = new StringBuilder(strings.length * 100);

    // sanitize (special chars are not removed) the pages and remove any empty one
    for (String page : strings) {
      answer.append(page).append(Constants.ANSWER_SPIRATOR);
    }

    return answer.toString();
  }

  /**
   * Unpack an answer by splitting it with {@link Constants#ANSWER_SPIRATOR}.
   * 
   * @param answer
   *          the answer to unpack and extract the array of.
   * 
   * @return the extracted array of this {@code answer}, or empty array if the
   *         given {@code answer} is <code>null</code> or empty.
   */
  // TODO: use JSON instead of this method?
  @Nonnull
  public static String[] unpackAnswer(@Nullable String answer) {
    if (answer == null || answer.isEmpty()) {
      return new String[0];
    }

    return answer.split(Constants.ANSWER_SPIRATOR);
  }

  /**
   * Check whether this content has valid answer ({@link #answerProvedContent} not
   * <code>null</code> and bigger than 0).
   * 
   * @return
   *         <code>true</code> if this content has valid answer.
   */
  public boolean hasProvedAnswer() {
    return answerProvedContent != null && answerProvedContent.length > 0;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (addedAt ^ (addedAt >>> 32));
    result = prime * result + Arrays.hashCode(answerProvedContent);
    result = prime * result + Arrays.hashCode(answerUnProvedContent);
    result = prime * result + ((category == null) ? 0 : category.hashCode());
    result = prime * result + (int) (contentId ^ (contentId >>> 32));
    result = prime * result
        + ((creatorOfUnprovedAnswer == null) ? 0 : creatorOfUnprovedAnswer.hashCode());
    result = prime * result + ((questionCreator == null) ? 0 : questionCreator.hashCode());
    result = prime * result + ((questionProvedBy == null) ? 0 : questionProvedBy.hashCode());
    result = prime * result + Arrays.hashCode(questions);
    return result;
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Content other = (Content) obj;
    if (addedAt != other.addedAt) {
      return false;
    }
    if (!Arrays.equals(answerProvedContent, other.answerProvedContent)) {
      return false;
    }
    if (!Arrays.equals(answerUnProvedContent, other.answerUnProvedContent)) {
      return false;
    }
    if (category == null) {
      if (other.category != null) {
        return false;
      }
    } else if (!category.equals(other.category)) {
      return false;
    }
    if (contentId != other.contentId) {
      return false;
    }
    if (creatorOfUnprovedAnswer == null) {
      if (other.creatorOfUnprovedAnswer != null) {
        return false;
      }
    } else if (!creatorOfUnprovedAnswer.equals(other.creatorOfUnprovedAnswer)) {
      return false;
    }
    if (questionCreator == null) {
      if (other.questionCreator != null) {
        return false;
      }
    } else if (!questionCreator.equals(other.questionCreator)) {
      return false;
    }
    if (questionProvedBy == null) {
      if (other.questionProvedBy != null) {
        return false;
      }
    } else if (!questionProvedBy.equals(other.questionProvedBy)) {
      return false;
    }
    if (!Arrays.equals(questions, other.questions)) {
      return false;
    }
    return true;
  }

}
