package com.aidn5.mcqa.utils;

import com.aidn5.mcqa.Constants;
import com.aidn5.mcqa.PluginConfig;
import com.aidn5.mcqa.core.dataholders.Content;

import net.minecraft.server.v1_12_R1.EnumChatFormat;

/**
 * On first creating the database. This content will be automatically added to
 * the database and viewed as first when executing "/qa". This helps the user to
 * learn how to use the plugin
 * 
 * @author aidn5
 *
 */
public class TutorialContent {
	private static final String question = "how to use " + PluginConfig.NAME.toLowerCase();
	private static final String lineSep = Constants.CHAT_FORMAT + "0\n";

	/**
	 * Get the content to add to the database
	 * 
	 * @return tutorial/guide about the plugin
	 */
	public static Content getTutorialContent() {
		return Content.createNewContent(PluginConfig.ID, true, question, getAnswer(), PluginConfig.AUTHOR);
	}

	private static String getAnswer() {
		String answer = "";

		answer += starter() + Constants.ANSWER_SPIRATOR;
		answer += contains() + Constants.ANSWER_SPIRATOR;
		answer += uses1() + Constants.ANSWER_SPIRATOR;
		answer += uses2() + Constants.ANSWER_SPIRATOR;
		answer += problems() + Constants.ANSWER_SPIRATOR;
		answer += commandQA() + Constants.ANSWER_SPIRATOR;
		answer += commandQAADD() + Constants.ANSWER_SPIRATOR;
		answer += commandQAGET() + Constants.ANSWER_SPIRATOR;

		return answer;
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

		answer += EnumChatFormat.BOLD + "Pages: " + lineSep + lineSep;
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

		answer += EnumChatFormat.BOLD + "Uses (1): " + lineSep + lineSep;
		answer += "You can use the plugin to write guides, tutorials ";
		answer += "or just contents for the players to use." + lineSep;

		answer += "It can also be used to write answers ";
		answer += "to the most asked questions on a server." + lineSep;

		return answer;
	}

	private static String uses2() {
		String answer = "";

		answer += EnumChatFormat.BOLD + "Uses (2): " + lineSep + lineSep;
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

		answer += EnumChatFormat.BOLD + "Problems: " + lineSep + lineSep;
		answer += "To ensure that contents don't exploit the plugin,";
		answer += " all unacceptable chars are automatically removed ";
		answer += "without a warning." + lineSep + lineSep;

		answer += "Allowed chars:" + lineSep;
		answer += "- ascii chars" + lineSep;
		answer += "- a-z, A-Z, 0-9" + lineSep;
		answer += "- :, ., ?, etc.";

		return answer;
	}

	private static String commandQA() {
		String answer = "";

		answer += EnumChatFormat.BOLD + "Command /qa: " + lineSep;
		answer += "Search for a question or get all saved questions." + lineSep + lineSep;

		answer += "examples: " + lineSep;
		answer += "- '/qa' (get everything)" + lineSep;
		answer += "- '/qa is pvp allowed'" + lineSep;
		answer += "- '/qa how to'" + lineSep;
		answer += "- '/qa % hOW' (seaches for 'how')" + lineSep;
		answer += "- '/qa %#' (produces error)" + lineSep;
		return answer;
	}

	private static String commandQAADD() {
		String answer = "";

		answer += EnumChatFormat.BOLD + "Command /qaadd:" + lineSep;
		answer += "Add new contents to the databases" + lineSep + lineSep;

		answer += "Usage:" + lineSep;
		answer += "get a book, write the answer in it then do" + lineSep;
		answer += "'/qa <category> <question>'" + lineSep;
		answer += "'<category>' must contain only ONE word" + lineSep;
		answer += "<question> must only contain alphanumeric chars, between 3-128 char" + lineSep;

		return answer;
	}

	private static String commandQAGET() {
		String answer = "";

		answer += EnumChatFormat.BOLD + "Command /qaget:" + lineSep;
		answer += "get an answer from the database directly without searching." + lineSep + lineSep;

		answer += "Usage:" + lineSep;
		answer += "/qaget <id>" + lineSep;

		answer += "you can get '<id>' by:" + lineSep;
		answer += "-search with /qa" + lineSep;
		answer += "-hover over the question" + lineSep;
		answer += "-copy the number with '#'";

		return answer;
	}
}
