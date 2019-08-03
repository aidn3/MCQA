package com.aidn5.mcqa.core.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Contents-sanitizer sanitize the question/answer from all the non-alphanumeric
 * char
 * 
 * @author aidn5
 * @version 1.0
 */
public class Sanitizer {
	private static String unsanitizePattern = "[^A-Za-z0-9 ]";
	private static Pattern unsanitizedChecker = Pattern.compile(unsanitizePattern);

	// do NOT change it to "[^[:ascii:]]"!
	// chars like "a", "i", ":" and others are removed for some reason
	private static String unsanitizePatternIgnoreSpecialChar = "[^[:ascii:]a-zA-Z0-9\\:]";
	private static Pattern unsanitizeCheckerIgnoreSpecialChar = Pattern.compile(unsanitizePatternIgnoreSpecialChar);

	private static Pattern onlynumbersChecker = Pattern.compile("^[0-9]{0,10000}$");

	public static String sanitizeText(String text, boolean ignoreSpecialChar) {
		if (text == null || text.isEmpty()) return "";

		if (ignoreSpecialChar) {
			text = unsanitizeCheckerIgnoreSpecialChar.matcher(text).replaceAll("").trim();
		} else {
			text = unsanitizedChecker.matcher(text).replaceAll("").trim();
		}

		return onlynumbersChecker.matcher(text).matches() ? "" : text;
	}

	/**
	 * sanitize the text array in this sequence
	 * <ul>
	 * <li>remove NULL and empty elements</li>
	 * <li>remove any nun-alphanumeric char from every element</li>
	 * <li>remove if element is now empty or contains only numbers</li>
	 * <p>
	 * 
	 * @param text
	 *            the array to sanitize
	 * @param ignoreSpecialChar
	 *            ignore special chars like %@#&">
	 * @return new array with only alphanumeric char or alphabet char (not only
	 *         numeric char)
	 */
	public static String[] sanitizeText(String[] text, boolean ignoreSpecialChar) {
		if (text == null) return new String[0];
		if (text.length == 0) return text;

		List<String> sanitizedText = new ArrayList<>(text.length);
		for (int i = 0; i < text.length; i++) {
			String string = sanitizeText(text[i], ignoreSpecialChar);
			if (!string.isEmpty()) sanitizedText.add(string);
		}

		return sanitizedText.toArray(new String[0]);
	}

	public static boolean isTextSanitized(String text, boolean ignoreSpecialChar) {
		if (ignoreSpecialChar) return !unsanitizeCheckerIgnoreSpecialChar.matcher(text).find();
		return !unsanitizedChecker.matcher(text).find();
	}
}
