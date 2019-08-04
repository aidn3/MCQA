
package com.aidn5.mcqa.language;

import static net.md_5.bungee.api.ChatColor.GREEN;

import java.util.Calendar;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.aidn5.mcqa.core.dataholders.Content;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

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
      TextComponent contentInfo = createContentInfo(content);
      contentInfo.setColor(primaryColor);

      TextComponent chat = createBaseMessage(neurtalColor + "Content's Info: ");
      chat.addExtra(contentInfo);

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


        TextComponent item = createBaseMessage((i + 1) + ". ");
        item.addExtra(createContentInfo(content));

        // String rawMessage = IChatBaseComponent.ChatSerializer.a(item);
        // player.sendRawMessage(rawMessage);
        sendChatComponent(sender, item);
        if (i > 9) break;
      }

      // empty line
      sendChatComponent(sender, new TextComponent(""));
    }

    private static TextComponent createContentInfo(Content content) {
      TextComponent chat = new TextComponent(content.getQuestionContent());

      // create the hover-text holder
      TextComponent hoverText = new TextComponent("");

      // add the tip to indicates on-hover that the player can click the text
      TextComponent hoverTextClickTip = new TextComponent("Click to open the Content!\n");
      hoverTextClickTip.setColor(GREEN);
      hoverText.addExtra(hoverTextClickTip);

      for (String iChatBaseComponent : createInfo(content)) {
        hoverText.addExtra(new TextComponent("\n" + iChatBaseComponent));
      }

      chat.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new BaseComponent[] { hoverText }));
      
      chat.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/qaget " + content.getContentId()));
      
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

      String info3 = neurtalColor + "Created at: " + primaryColor.toString()
          + cal.getTime().toString();
      info3 += neurtalColor.toString() + " by " + primaryColor + content.getQuestionCreator();

      return new String[] { info1, info2, info3 };
    }

    public static void sanitizeProblem(CommandSender sender) {
      sendChatComponent(sender,
          createBaseError("Content must only contains Alphabet, Numbers, Common special chars",
              null));
    }

    public static void questionSanitizeProblem(CommandSender sender) {
      sendChatComponent(sender,
          createBaseError("question must only contains Alphanumeric chars", null));
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
      sendChatComponent(sender,
          createBaseError("You must hold the Book you want to distribute it", null));
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
      sendChatComponent(sender,
          createBaseError("Book must be either yours, not signed or MCQA's book", null));
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
