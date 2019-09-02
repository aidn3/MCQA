
package com.aidn5.mcqa.utils;

import com.aidn5.mcqa.PluginConfig;
import com.aidn5.mcqa.core.content.Content;

import org.bukkit.ChatColor;

/**
 * On first creating the database. This content will be automatically added to
 * the database and viewed as first when executing "/qa". This helps the user to
 * learn how to use the plugin.
 * 
 * @author aidn5
 */
public class TutorialContent {
  private static final String question = "how to use " + PluginConfig.NAME.toLowerCase();
  private static final String lineSep = ChatColor.COLOR_CHAR + "0\n";

  /**
   * Get the content to add to the database.
   * 
   * @return tutorial/guide about the plugin
   */
  public static Content getTutorialContent() {
    return Content
        .createNewContent(PluginConfig.ID, true, question, getAnswer(), PluginConfig.AUTHOR_UUID);
  }

  private static String[] getAnswer() {
    return new String[] {
        starter(), contains(),
        uses1(), uses2(), problems(),
        commandQa(), commandQaAdd(), commandQaGet() };
  }

  private static String starter() {
    String answer = "";

    answer += "Welcome to " + PluginConfig.NAME + lineSep + lineSep;
    answer += "Author: " + PluginConfig.AUTHOR + lineSep;
    answer += "Version: " + PluginConfig.VERSION;

    return answer;
  }

  private static String contains() {
    String answer = "";

    answer += ChatColor.BOLD + "Pages: " + lineSep + lineSep;
    answer += "1. Plugin Info" + lineSep;
    answer += "2. Contains (this page)" + lineSep;
    answer += "3. Uses (1)" + lineSep;
    answer += "4. Uses (2)" + lineSep;
    answer += "5. Problems" + lineSep;
    answer += "6. Command /qa" + lineSep;
    answer += "7. Command /qaadd" + lineSep;
    answer += "8. Command /qaget" + lineSep;

    return answer;
  }

  private static String uses1() {
    String answer = "";

    answer += ChatColor.BOLD + "Uses (1): " + lineSep + lineSep;
    answer += "You can use the plugin to write guides, tutorials ";
    answer += "or just contents for the players to use." + lineSep;

    answer += "It can also be used to write answers ";
    answer += "to the most asked questions on a server." + lineSep;

    return answer;
  }

  private static String uses2() {
    String answer = "";

    answer += ChatColor.BOLD + "Uses (2): " + lineSep + lineSep;
    answer += "examples: " + lineSep;
    answer += "- How to claim?" + lineSep;
    answer += "- Is (tldr) allowed?" + lineSep;
    answer += "- How to sell?" + lineSep;
    answer += "- Where is the pvp arena?" + lineSep;
    answer += "- how much does wood cost?" + lineSep;

    return answer;
  }

  private static String problems() {
    String answer = "";

    answer += ChatColor.BOLD + "Problems: " + lineSep + lineSep;
    answer += "To ensure that contents don't exploit the plugin,";
    answer += " all unacceptable chars are automatically removed ";
    answer += "without a warning." + lineSep + lineSep;

    answer += "Allowed chars:" + lineSep;
    answer += "- ascii chars" + lineSep;
    answer += "- a-z, A-Z, 0-9" + lineSep;
    answer += "- :, ., ?, etc.";

    return answer;
  }

  private static String commandQa() {
    String answer = "";

    answer += ChatColor.BOLD + "Command /qa: " + lineSep;
    answer += "Search for a questions or get all saved questions." + lineSep + lineSep;

    answer += "examples: " + lineSep;
    answer += "- '/qa' (get everything)" + lineSep;
    answer += "- '/qa is pvp allowed'" + lineSep;
    answer += "- '/qa how to'" + lineSep;
    answer += "- '/qa % hOW' (seaches for 'how')" + lineSep;
    answer += "- '/qa %#' (produces error)" + lineSep;
    return answer;
  }

  private static String commandQaAdd() {
    String answer = "";

    answer += ChatColor.BOLD + "Command /qaadd:" + lineSep;
    answer += "Add new contents to the databases" + lineSep + lineSep;

    answer += "Usage:" + lineSep;
    answer += "get a book, write the answer in it then do" + lineSep;
    answer += "'/qa <category> <questions>'" + lineSep;
    answer += "'<category>' must contain only ONE word" + lineSep;
    answer += "<questions> must only contain alphanumeric chars, between 3-128 char" + lineSep;

    return answer;
  }

  private static String commandQaGet() {
    String answer = "";

    answer += ChatColor.BOLD + "Command /qaget:" + lineSep;
    answer += "get an answer from the database directly without searching." + lineSep + lineSep;

    answer += "Usage:" + lineSep;
    answer += "/qaget <id>" + lineSep;

    answer += "you can get '<id>' by:" + lineSep;
    answer += "-search with /qa" + lineSep;
    answer += "-hover over the questions" + lineSep;
    answer += "-copy the number with '#'";

    return answer;
  }
}
