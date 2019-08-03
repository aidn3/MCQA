package com.aidn5.mcqa.language;

import static org.bukkit.ChatColor.GREEN;

import java.util.Calendar;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.aidn5.mcqa.core.dataholders.Content;

import net.minecraft.server.v1_12_R1.ChatClickable;
import net.minecraft.server.v1_12_R1.ChatClickable.EnumClickAction;
import net.minecraft.server.v1_12_R1.ChatComponentText;
import net.minecraft.server.v1_12_R1.ChatHoverable;
import net.minecraft.server.v1_12_R1.ChatHoverable.EnumHoverAction;
import net.minecraft.server.v1_12_R1.EnumChatFormat;

/**
 * Create styled-chat-messages This is a wrapper, used to give a united style
 * for the messages. It also gives reliability since you can change the messages
 * and add methods/functions to it
 * 
 * @author aidn5
 *
 */
public class Notifier extends BaseMessage {
	public static class ContentsMsg {
		public static void showContentsInfo(CommandSender sender, Content content) {
			ChatComponentText contentInfo = createContentInfo(content);
			contentInfo.getChatModifier().setColor(EnumChatFormat.valueOf(primaryColor.name()));

			ChatComponentText chat = (ChatComponentText) createBaseMessage(neurtalColor + "Content's Info: ")
					.addSibling(contentInfo);

			sendChatComponent(sender, chat);
		}

		public static void contentNotFound(CommandSender sender) {
			sendChatComponent(sender, createBaseError("No such Content found", null));
		}

		public static void invalidId(CommandSender sender) {
			sendChatComponent(sender, createBaseError("Invalid ID", null));
		}

		public static void viewSelectableList(CommandSender sender, List<Content> contents) {
			sendChatComponent(sender, lineSeperator);

			for (int i = 0; i < contents.size(); i++) {
				Content content = contents.get(i);


				ChatComponentText item = (ChatComponentText) createBaseMessage((i + 1) + ". ")
						.addSibling(createContentInfo(content));

				// String rawMessage = IChatBaseComponent.ChatSerializer.a(item);
				// player.sendRawMessage(rawMessage);
				sendChatComponent(sender, item);
				if (i > 9) break;
			}

			// empty line
			sendChatComponent(sender, new ChatComponentText(""));
		}

		private static ChatComponentText createContentInfo(Content content) {
			ChatComponentText chat = new ChatComponentText(content.getQuestionContent());

			// create the hover-text holder
			ChatComponentText hoverText = new ChatComponentText("");

			// add the tip to indicates on-hover that the player can click the text
			ChatComponentText hoverTextClickTip = new ChatComponentText("Click to open the Content!\n");
			hoverTextClickTip.getChatModifier().setColor(EnumChatFormat.GREEN);
			hoverText.addSibling(hoverTextClickTip);

			for (String iChatBaseComponent : createInfo(content)) {
				hoverText.addSibling(new ChatComponentText("\n" + iChatBaseComponent));
			}

			chat.getChatModifier().setChatHoverable(new ChatHoverable(EnumHoverAction.SHOW_TEXT, hoverText));

			chat.getChatModifier().setChatClickable(
					new ChatClickable(EnumClickAction.RUN_COMMAND, "/qaget " + content.getContentId()));

			return chat;
		}

		private static String[] createInfo(Content content) {
			// info to #{id}
			String info1 = neurtalColor + "Info to " + primaryColor + "#" + content.getContentId();

			// {category}: {question}
			String info2 = primaryColor.toString() + content.getCategory() + ": ";
			info2 += ChatColor.ITALIC.toString() + neurtalColor + content.getQuestionContent();
			Calendar.getInstance().getTime().toString();

			// Created at: {time} by {question_author}
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(content.getAddedAt() * 1000);

			String info3 = neurtalColor + "Created at: " + primaryColor.toString() + cal.getTime().toString();
			info3 += neurtalColor.toString() + " by " + primaryColor + content.getQuestionCreator();

			return new String[] { info1, info2, info3 };
		}

		public static void sanitizeProblem(CommandSender sender) {
			sendChatComponent(sender,
					createBaseError("Content must only contains Alphabet, Numbers, Common special chars", null));
		}

		public static void questionSanitizeProblem(CommandSender sender) {
			sendChatComponent(sender, createBaseError("question must only contains Alphanumeric chars", null));
		}

		public static void questionLengthError(CommandSender sender, int shorted, int longest) {
			String message = "Question must be between %d and %d length";
			sendChatComponent(sender, createBaseError(String.format(message, shorted, longest), null));
		}

		public static void answerLengthError(CommandSender sender, int shorted, int longest) {
			String message = "Answer must be between %d and %d length";
			sendChatComponent(sender, createBaseError(String.format(message, shorted, longest), null));
		}
	}

	public static class Commands {
		public static void noPermsToUse(CommandSender sender) {
			sendChatComponent(sender, createBaseError("You don't have permission to do this", null));
		}

		public static void noPermsToView(CommandSender sender) {
			sendChatComponent(sender, createBaseError("You don't have permission to view this", null));
		}

		public static void mustHoldTheBookToAdd(CommandSender sender) {
			sendChatComponent(sender, createBaseError("You must hold the Book you want to distribute it", null));
		}

		public static void contentsAdded(CommandSender sender, boolean isProved) {
			String s = successColor + "New Content has been created!";
			if (!isProved) s += neurtalColor + " waiting for approval of a staff.";
			sendChatComponent(sender, createBaseMessage(s));
		}

		public static void contentRemoved(CommandSender sender) {
			sendChatComponent(sender, createBaseMessage(successColor + "Content removed!"));
		}

		public static void noContentToRemove(CommandSender sender) {
			sendChatComponent(sender, createBaseError("Could not found the content to remove", null));
		}

		public static void mustBePlayerWithInventory(CommandSender sender) {
			sendChatComponent(sender,
					createBaseError("You must be a player with an inventory to execute this command", null));
		}

		public static void mustHoldBookToCopy(CommandSender sender) {
			sendChatComponent(sender, createBaseError("You must hold a Book to copy", null));
		}

		public static void bookMustBeYours(CommandSender sender) {
			sendChatComponent(sender, createBaseError("Book must be either yours, not signed or MCQA's book", null));
		}
	}

	// TODO: recreate Notifier.StorageMessages and integrate them
	public static class StorageMessages {
		public static void canNotLoadDataSet(Throwable e) {
			createBaseError("could not load the data of the username", e);
			playErrorSoundEffect();
		}

		public static void canNotSaveDataSet(Throwable e) {
			createBaseError("can not save the data", e);
		}

		public static void dataSetSaved() {
			createBaseMessage(GREEN + "Notes is saved");
		}
	}
}
