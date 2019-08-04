package com.aidn5.mcqa;

import org.bukkit.ChatColor;

public class Constants {
	/**
	 * ID defines which book item is from this mod
	 * 
	 * Uses: nothing yet
	 * 
	 * Idea: give a copy of the content to the player as a book
	 */
	public static final String SERIAL_BOOK_ID = ChatColor.BLACK + "qa.book.id";
	/**
	 * the char, which is used as a chat-style indicator
	 * <p>
	 * value: §
	 */
	public static final String CHAT_FORMAT = String.valueOf('\u00a7');

	/**
	 * SQL can only save one field as an answer. We use book as an answer viewer
	 * Book can contain multiple pages. to save the pages into database, we need to
	 * combine the pages into one text and save it. this char indicates when is the
	 * page ends and when the second one starts
	 * <p>
	 * current value: hex(FFFF)
	 * <p>
	 * can change to any char except ASCI-chars, a-z, A-Z, 0-9 and common special
	 * chars (e.g. ":","?")
	 * 
	 */
	public static final String ANSWER_SPIRATOR = String.valueOf('\uffff');
}
