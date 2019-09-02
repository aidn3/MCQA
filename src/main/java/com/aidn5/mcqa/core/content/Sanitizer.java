
package com.aidn5.mcqa.core.content;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Contents-sanitizer sanitize the questions/answer from all the
 * non-alphanumeric and special chars.
 * 
 * @author aidn5
 */
public class Sanitizer {
  private static Pattern unsanitizedChecker = Pattern.compile("[^A-Za-z0-9 ]");

  // do NOT change it to "[^[:ascii:]]"!
  // chars like "a", "i", ":" and others are removed for some reason
  // TODO: find why chars like "a", "i", ":" are removed by regex "[^[:ascii:]]"
  private static Pattern unsanitizeCheckerIgnoreSpecialChar = Pattern
      .compile("[^[:ascii:]a-zA-Z0-9\\:]");

  private static Pattern onlynumbersChecker = Pattern.compile("^[0-9]{0,10000}$");

  private Sanitizer() {
    throw new AssertionError();
  }

  /**
   * sanitize the text by.:
   * <ul>
   * <li>Return empty string if {@code text} is empty or <code>null</code> to
   * avoid {@link NullPointerException}</li>
   * <li>Remove any unicode/non-alphanumeric chars to avoid exploiting clients and
   * databases</li>
   * </ul>
   * 
   * @param text
   *          the text to clean
   * @param ignoreSpecialChar
   *          <code>true</code> to not remove special chars like "%", "!", etc.
   * 
   * @return
   *         non empty string with sanitized text.
   */
  @Nonnull
  public static String sanitizeText(@Nullable String text, boolean ignoreSpecialChar) {
    if (text == null || text.isEmpty()) {
      return "";
    }

    if (ignoreSpecialChar) {
      text = unsanitizeCheckerIgnoreSpecialChar.matcher(text).replaceAll("").trim();
    } else {
      text = unsanitizedChecker.matcher(text).replaceAll("").trim();
    }

    return onlynumbersChecker.matcher(text).matches() ? "" : text;
  }

  /**
   * sanitize the text array in this sequence.
   * <ul>
   * <li>remove NULL and empty elements</li>
   * <li>remove any nun-alphanumeric char from every element</li>
   * <li>remove if element is now empty or contains only numbers</li>
   * </ul>
   * 
   * @param text
   *          the array to sanitize
   * @param ignoreSpecialChar
   *          ignore special chars like %@#&">
   * @return new array with only alphanumeric char or alphabet char (not only
   *         numeric char)
   */
  @Nonnull
  public static String[] sanitizeText(@Nullable String[] text, boolean ignoreSpecialChar) {
    if (text == null) {
      return new String[0];
    }
    if (text.length == 0) {
      return text;
    }

    List<String> sanitizedText = new ArrayList<>(text.length);
    for (int i = 0; i < text.length; i++) {
      String string = sanitizeText(text[i], ignoreSpecialChar);
      if (!string.isEmpty()) {
        sanitizedText.add(string);
      }
    }

    return sanitizedText.toArray(new String[0]);
  }

  /**
   * check whether the text is sanitized and need not to sanitize it anymore.
   * 
   * @param text
   *          the text to check.
   * @param ignoreSpecialChar
   *          <code>true</code> to ignore special chars like "?", ":", etc. and
   *          count them as sanitized if they exists in {@code text}.
   * 
   * @return
   *         <code>true</code> if the text meets all required of sanitized text.
   */
  public static boolean isTextSanitized(String text, boolean ignoreSpecialChar) {
    if (ignoreSpecialChar) {
      return !unsanitizeCheckerIgnoreSpecialChar.matcher(text).find();
    }
    return !unsanitizedChecker.matcher(text).find();
  }
}
