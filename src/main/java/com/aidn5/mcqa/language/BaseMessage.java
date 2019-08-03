package com.aidn5.mcqa.language;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.ChatColor.WHITE;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;

import com.aidn5.mcqa.PluginConfig;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_12_R1.ChatComponentText;
import net.minecraft.server.v1_12_R1.ChatHoverable;
import net.minecraft.server.v1_12_R1.ChatHoverable.EnumHoverAction;
import net.minecraft.server.v1_12_R1.ChatModifier;
import net.minecraft.server.v1_12_R1.EnumChatFormat;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;

/**
 * Creates styled success,error and normal messages
 * 
 * @author aidn5
 * @version 1.0
 */
public class BaseMessage {
	protected static final ChatColor neurtalColor = ChatColor.GRAY;
	protected static final ChatColor primaryColor = ChatColor.YELLOW;
	protected static final ChatColor secondaryColor = ChatColor.AQUA;
	protected static final ChatColor errorColor = ChatColor.RED;
	protected static final ChatColor successColor = ChatColor.GREEN;

	protected static final ChatComponentText baseMessage;
	static {
		// result: "[%modName%]: "
		String logo = BOLD + "" + GOLD + "[";
		logo += BOLD + "" + WHITE + PluginConfig.NAME;
		logo += BOLD + "" + GOLD + "] " + RESET + neurtalColor;

		baseMessage = new ChatComponentText(logo);
	}

	protected static final ChatComponentText lineSeperator;
	static {
		lineSeperator = new ChatComponentText("");

		ChatComponentText line = new ChatComponentText("--------------------");
		line.getChatModifier().setColor(EnumChatFormat.valueOf(primaryColor.name()));

		lineSeperator.addSibling(line).addSibling(baseMessage).addSibling(line);
	}

	public static void debugMessage(String message) {
		// System.out.println(message);
		// showMessage(GRAY + message);
	}

	public static ChatComponentText createReportCrash(Throwable e) {
		playErrorSoundEffect();
		return createBaseError("mod crashed", e);
	}

	protected static ChatComponentText createBaseError(String msg, Throwable e) {
		String textMessage = RED + "ERROR: " + msg;
		ChatComponentText errorMessage = new ChatComponentText(textMessage);

		if (e != null) {
			String hoverMessage = e.getMessage() + "\r";

			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String sStackTrace = sw.toString();
			pw.close();

			ChatModifier chatModifier = errorMessage.getChatModifier();
			chatModifier.setChatHoverable(
					new ChatHoverable(EnumHoverAction.SHOW_TEXT, new ChatComponentText(hoverMessage + sStackTrace)));
		}

		return createBaseMessage(errorMessage);
	}

	protected static ChatComponentText createBaseMessage(String msg) {
		return createBaseMessage(new ChatComponentText(msg));
	}

	protected static ChatComponentText createBaseMessage(ChatComponentText msg) {
		ChatComponentText chat = new ChatComponentText("");
		chat.addSibling(baseMessage).addSibling(msg);
		return chat;
	}

	public static void sendChatComponent(CommandSender sender, ChatComponentText chat) {
		if (sender instanceof CraftPlayer) {
			ByteBuf buf = Unpooled.buffer(256);
			buf.setByte(0, (byte) 0);
			buf.writerIndex(1);

			PacketPlayOutChat playOutChat = new PacketPlayOutChat(chat);
			((CraftPlayer) sender).getHandle().playerConnection.sendPacket(playOutChat);
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
