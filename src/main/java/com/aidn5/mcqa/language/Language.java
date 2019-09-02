
package com.aidn5.mcqa.language;

import com.aidn5.mcqa.Mcqa;
import com.aidn5.mcqa.core.content.Content;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class Language {
  private static final char ColorChar = '&';

  public final FileConfiguration language;

  private static final Pattern OpenableContentsInfo = Pattern
      .compile("{OpenableContentsInfo}", Pattern.LITERAL);

  private static final Pattern PREFIX = Pattern.compile("{prefix}", Pattern.LITERAL);

  public Language(FileConfiguration language) {
    this.language = language;
  }

  private static final Pattern COUNT = Pattern.compile("{count}", Pattern.LITERAL);

  public void showSearch(CommandSender sender, List<Content> contents) {
    // Do the header
    String header = language.getString("Language.ShowSearch.Header");

    if (header != null && !header.isEmpty()) {
      header = parseGeneral(sender, header);
      sendChatComponent(sender, new TextComponent(header));
    }

    // list the contents
    String content = language.getString("Language.ShowSearch.Content");
    content = parseGeneral(sender, content);

    for (int i = 0; i < contents.size(); i++) {
      Content content2 = contents.get(i);

      String contentInfo = COUNT.matcher(content).replaceAll(i + "");
      contentInfo = ContentParser.parseContent(contentInfo, content2);

      sendChatComponent(sender, createWithOpenableContentsInfo(sender, contentInfo, content2));
    }

    // Do the footer
    String footer = language.getString("Language.ShowSearch.Footer");

    if (footer != null && !footer.isEmpty()) {
      footer = parseGeneral(sender, footer);
      sendChatComponent(sender, new TextComponent(footer));
    }
  }

  public void showContentsInfo(CommandSender sender, Content content) {
    String m = language.getString("Language.ShowContentsInfo");

    m = parseGeneral(sender, m);
    m = ContentParser.parseContent(m, content);
    sendChatComponent(sender, createWithOpenableContentsInfo(sender, m, content));
  }

  public void contentNotFound(CommandSender sender, int id) {
    String m = language.getString("Language.ContentNotFound");

    m = parseGeneral(sender, m);
    m = ContentParser.parseId(m, id);

    sendChatComponent(sender, new TextComponent(m));
  }

  public void noContent(CommandSender sender) {
    String m = language.getString("Language.NoContent");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void invalidId(CommandSender sender) {
    String m = language.getString("Language.InvalidId");
    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }


  public void categorySanitizeProblem(CommandSender sender) {
    String m = language.getString("Language.CategorySanitizeProblem");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void categoryLengthProblem(CommandSender sender, long shortest, long longest) {

    String m = language.getString("Language.CategoryLengthProblem");

    m = parseGeneral(sender, m);
    m = ContentParser.parseLength(m, shortest, longest);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void contentSanitizeProblem(CommandSender sender) {
    String m = language.getString("Language.ContentSanitizeProblem");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void contentLengthProblem(CommandSender sender, long shortest, long longest) {
    String m = language.getString("Language.ContentLengthProblem");

    m = parseGeneral(sender, m);
    m = ContentParser.parseLength(m, shortest, longest);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void questionSanitizeProblem(CommandSender sender) {
    String m = language.getString("Language.QuestionSanitizeProblem");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void questionLengthProblem(CommandSender sender, long shortest, long longest) {
    String m = language.getString("Language.QuestionSanitizeProblem");

    m = parseGeneral(sender, m);
    m = ContentParser.parseLength(m, shortest, longest);
    sendChatComponent(sender, new TextComponent(m));
  }


  public void noPermissionToUse(CommandSender sender) {
    String m = language.getString("Language.NoPermissionToUse");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void noPermissionToView(CommandSender sender, int contentId) {
    String m = language.getString("Language.NoPermissionToView");

    m = parseGeneral(sender, m);
    m = ContentParser.parseId(m, contentId);
    sendChatComponent(sender, new TextComponent(m));
  }


  public void mustHoldBookToUpload(CommandSender sender) {
    String m = language.getString("Language.MustHoldBookToUpload");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void mustHoldBookToCopy(CommandSender sender) {
    String m = language.getString("Language.MustHoldBookToCopy");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void mustBePlayerWithInventory(CommandSender sender) {
    String m = language.getString("Language.MustBePlayerWithInventory");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void bookNotYours(CommandSender sender) {
    String m = language.getString("Language.BookNotYours");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }


  public void contentAdded(CommandSender sender, Content content) {
    String m = language.getString("Language.ContentAdded");

    m = parseGeneral(sender, m);
    m = ContentParser.parseContent(m, content);
    sendChatComponent(sender, createWithOpenableContentsInfo(sender, m, content));
  }

  public void contentAddedWaitingForApproval(CommandSender sender, Content content) {
    String m = language.getString("Language.ContentAddedWaitingForApproval");

    m = parseGeneral(sender, m);
    m = ContentParser.parseContent(m, content);
    sendChatComponent(sender, createWithOpenableContentsInfo(sender, m, content));
  }

  public void contentRemoved(CommandSender sender, long contentId) {
    String m = language.getString("Language.ContentRemoved");

    m = parseGeneral(sender, m);
    m = ContentParser.parseId(m, contentId);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void noContentToRemove(CommandSender sender, long contentId) {
    String m = language.getString("Language.NoContentToRemove");

    m = parseGeneral(sender, m);
    m = ContentParser.parseId(m, contentId);
    sendChatComponent(sender, new TextComponent(m));
  }


  public void pluginReloaded(CommandSender sender) {
    String m = language.getString("Language.PluginReloaded");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void pluginNotReloaded(CommandSender sender) {
    String m = language.getString("Language.PluginNotReloaded");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void tutorialBookReceived(CommandSender sender) {
    String m = language.getString("Language.TutorialBookReceived");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void featureDisabled(CommandSender sender) {
    String m = language.getString("Language.FeatureDisabled");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }


  public void helpReload(CommandSender sender) {
    String m = language.getString("Language.HelpReload");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void helpMigrate(CommandSender sender) {
    String m = language.getString("Language.HelpMigrate");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void helpBook(CommandSender sender) {
    String m = language.getString("Language.HelpBook");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void helpHelp(CommandSender sender) {
    String m = language.getString("Language.HelpHelp");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }

  public void helpInfo(CommandSender sender) {
    String m = language.getString("Language.HelpInfo");

    m = parseGeneral(sender, m);
    sendChatComponent(sender, new TextComponent(m));
  }


  private String parseGeneral(CommandSender sender, String textToParse) {
    if (sender instanceof Player) {
      Player player = (Player) sender;

      if (Mcqa.placeHolderApi()) {
        textToParse = PlaceholderAPI.setPlaceholders(player, textToParse);
      }
    }

    textToParse = PREFIX.matcher(textToParse).replaceAll(language.getString("Language.Prefix"));

    return ChatColor.translateAlternateColorCodes(ColorChar, textToParse);

  }

  private TextComponent createWithOpenableContentsInfo(CommandSender sender, String text,
      Content content) {

    final String[] texts = OpenableContentsInfo.split(text, -1);

    if (texts.length == 1) {
      return new TextComponent(text);
    }

    final TextComponent openableContentsInfo = new TextComponent(content.questions[0]);
    final TextComponent openableContentsHover = new TextComponent("");

    final List<String> OpenableContentsInfoL = language
        .getStringList("Language.OpenableContentsInfo");

    for (int i = 0; i < OpenableContentsInfoL.size(); i++) {
      String line = OpenableContentsInfoL.get(i);

      line = parseGeneral(sender, line);
      line = ContentParser.parseContent(line, content);

      openableContentsHover.addExtra(line);

      if (i < OpenableContentsInfoL.size() - 1) {
        openableContentsHover.addExtra("\n");
      }
    }

    openableContentsInfo.setHoverEvent(
        new HoverEvent(Action.SHOW_TEXT, new TextComponent[] { openableContentsHover }));

    openableContentsInfo.setClickEvent(new ClickEvent(
        net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/qaget " + content.contentId));


    final TextComponent result = new TextComponent("");
    for (int i = 0; i < texts.length; i++) {
      result.addExtra(texts[i]);

      if (i < texts.length - 1) {
        result.addExtra(openableContentsInfo);
      }
    }

    return result;
  }

  private static void sendChatComponent(CommandSender sender, TextComponent chat) {
    if (sender instanceof Player) {
      Player player = (Player) sender;
      player.spigot().sendMessage(chat);

    } else {
      sender.sendMessage(chat.getText());
    }
  }
}
