
package com.aidn5.mcqa.core.database.split;

import com.aidn5.mcqa.core.content.Content;
import com.aidn5.mcqa.core.database.IMcqaDatabase;

/**
 * Object used to store the relatives to the Question while searching by
 * {@link IMcqaDatabase#searchForContents(boolean, int, String[])}. It is used
 * to speed up sorting Questions by creating pre-compared data for every
 * Question to avoid the extensive use of methods like
 * {@link String#equalsIgnoreCase(String)}, {@link String#startsWith(String)},
 * {@link String#contains(CharSequence)}. The data are compared against the
 * requested question and sorted upon it.
 * 
 * @author aidn5
 */
class SortQuestion {
  SortQuestion() {
    // Constructor
  }

  /**
   * The {@code id} of the content (aka. {@link Content#contentId}).
   */
  long contentId;
  /**
   * How many words does this content's question share with the requested
   * question.
   */
  int rate;
  /**
   * Whether <code>{content's question}#startsWith(RequestedQuestion)</code> is
   * <code>true</code>.
   */
  boolean likeExactAtStart;
  /**
   * Whether <code>{content's question}#contains(RequestedQuestion)</code> is
   * <code>true</code>.
   */
  boolean likeExact;
  /**
   * Whether
   * <code>{content's question}#equalsIgnoreCase(RequestedQuestion)</code> is
   * <code>true</code>.
   */
  boolean exact;
}
