
package com.aidn5.mcqa.language;

import static net.md_5.bungee.api.ChatColor.AQUA;
import static net.md_5.bungee.api.ChatColor.BOLD;
import static net.md_5.bungee.api.ChatColor.GOLD;
import static net.md_5.bungee.api.ChatColor.GRAY;
import static net.md_5.bungee.api.ChatColor.GREEN;
import static net.md_5.bungee.api.ChatColor.RED;
import static net.md_5.bungee.api.ChatColor.RESET;
import static net.md_5.bungee.api.ChatColor.WHITE;
import static net.md_5.bungee.api.ChatColor.YELLOW;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aidn5.mcqa.PluginConfig;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;


/**
 * Creates styled success,error and normal messages
 * 
 * @author aidn5
 * @version 1.0
 */
public class BaseMessage {
  protected static final ChatColor neurtalColor = GRAY;
  protected static final ChatColor primaryColor = YELLOW;
  protected static final ChatColor secondaryColor = AQUA;
  protected static final ChatColor errorColor = RED;
  protected static final ChatColor successColor = GREEN;

  protected static final BaseComponent baseMessage;
  static {
    // result: "[%modName%]: "
    String logo = BOLD + "" + GOLD + "[";
    logo += BOLD + "" + WHITE + PluginConfig.NAME;
    logo += BOLD + "" + GOLD + "] " + RESET + neurtalColor;

    baseMessage = new TextComponent(logo);
  }

  protected static final TextComponent lineSeperator;
  static {
    lineSeperator = new TextComponent("");

    TextComponent line = new TextComponent("--------------------");
    line.setColor(primaryColor);

    lineSeperator.addExtra(line);
    lineSeperator.addExtra(baseMessage);
    lineSeperator.addExtra(line);
  }

  public static void debugMessage(String message) {
    System.out.println(message);
    // showMessage(GRAY + message);
  }

  public static TextComponent createReportCrash(Throwable e) {
    playErrorSoundEffect();
    return createBaseError("mod crashed", e);
  }

  protected static TextComponent createBaseError(String msg, Throwable e) {
    String textMessage = RED + "ERROR: " + msg;
    TextComponent errorMessage = new TextComponent(textMessage);

    if (e != null) {
      String hoverMessage = e.getMessage() + "\r";

      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      String sStackTrace = sw.toString();
      pw.close();

      errorMessage.setHoverEvent(
          new HoverEvent(Action.SHOW_TEXT,
              new BaseComponent[] { new TextComponent(hoverMessage + sStackTrace) }));
    }

    return createBaseMessage(errorMessage);
  }

  protected static TextComponent createBaseMessage(String msg) {
    return createBaseMessage(new TextComponent(msg));
  }

  protected static TextComponent createBaseMessage(TextComponent msg) {
    TextComponent chat = new TextComponent("");
    chat.addExtra(baseMessage);
    chat.addExtra(msg);
    return chat;
  }

  public static void sendChatComponent(CommandSender sender, TextComponent chat) {
    if (sender instanceof Player) {
      Player player = (Player) sender;
      player.spigot().sendMessage(chat);

    } else {
      sender.sendMessage(chat.getText());
    }
  }

  protected static void playSoundEffect() {
    // Minecraft.getMinecraft().thePlayer.playSound("minecraft:block.anvil.hit",
    // 100, 1f);
  }

  protected static void playErrorSoundEffect() {
    // Minecraft.getMinecraft().thePlayer.playSound("minecraft:block.anvil.hit",
    // 100, 1f);
  }
}
