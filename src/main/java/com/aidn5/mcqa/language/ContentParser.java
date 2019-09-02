
package com.aidn5.mcqa.language;

import com.aidn5.mcqa.core.content.Content;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

class ContentParser {
  private static final Pattern CONTENT_ID = Pattern.compile("{content_id}", Pattern.LITERAL);
  private static final Pattern CONTENT_DATE = Pattern.compile("{content_date}", Pattern.LITERAL);

  private static final Pattern CONTENT_CATEGORY = Pattern
      .compile("{content_category}", Pattern.LITERAL);
  private static final Pattern CONTENT_QUESTION = Pattern
      .compile("{content_question}", Pattern.LITERAL);
  private static final Pattern CONTENT_AUTHOR = Pattern
      .compile("{content_author}", Pattern.LITERAL);

  private static final Pattern SHORTEST = Pattern
      .compile("{shortest}", Pattern.LITERAL);
  private static final Pattern LONGEST = Pattern
      .compile("{longest}", Pattern.LITERAL);


  static String parseId(String text, long id) {
    return CONTENT_ID.matcher(text).replaceAll(id + "");
  }

  static String parseDate(String text, String date) {
    return CONTENT_DATE.matcher(text).replaceAll(date);
  }

  static String parseCategory(String text, String category) {
    return CONTENT_CATEGORY.matcher(text).replaceAll(category);
  }

  static String parseQuestion(String text, String question) {
    return CONTENT_QUESTION.matcher(text).replaceAll(question);
  }

  static String parseAuthor(String text, String author) {
    return CONTENT_AUTHOR.matcher(text).replaceAll(author);
  }

  static String parseLength(String text, long shortest, long longest) {
    String m = SHORTEST.matcher(text).replaceAll(shortest + "");
    return LONGEST.matcher(m).replaceAll(longest + "");
  }

  static String parseContent(String text, Content content) {
    // set content's ID
    String m = parseId(text, content.contentId);
    // set category
    m = parseCategory(m, content.category);
    // set question
    m = parseQuestion(m, content.questions[0]);


    // set author
    Matcher authorMatcher = CONTENT_AUTHOR.matcher(m);
    if (authorMatcher.find()) {
      String author;

      if (content.questionCreator == null) {
        author = "...";

      } else {
        OfflinePlayer p = Bukkit.getOfflinePlayer(content.questionCreator);
        author = p.getName();

        if (author == null) {
          author = content.questionCreator.toString();
        }
      }

      m = authorMatcher.replaceAll(author);
    }


    // set date
    Matcher dateMatcher = CONTENT_DATE.matcher(m);
    if (dateMatcher.find()) {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(content.addedAt * 1000);

      String date = cal.getTime().toString();
      m = dateMatcher.replaceAll(date);
    }

    return m;
  }
}
