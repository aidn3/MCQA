package com.aidn5.mcqa.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

import com.aidn5.mcqa.Constants;
import com.aidn5.mcqa.core.dataholders.Content;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_12_R1.EnumChatFormat;
import net.minecraft.server.v1_12_R1.PacketDataSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload;

/**
 * Util helps with crafting MCQA-books and provide methods to show the players
 * its contents
 * 
 * @author aidn5
 *
 */
public class Book {
	/**
	 * give the book to the player, send packet to open it up and then remove the
	 * book from their inventory.
	 * <p>
	 * The original item in their inventory will be saved, replaced with book (to
	 * open) then restored
	 * 
	 * @param book
	 *            the book to open
	 * @param player
	 *            the player to show
	 */
	public static void showBook(ItemStack book, Player player) {
		PlayerInventory inv = player.getInventory();

		int slot = inv.getHeldItemSlot();

		// store the old item to replace it with the book and then restore it afterward
		ItemStack originItem = inv.getItem(slot);

		inv.setItem(slot, Objects.requireNonNull(book));

		// force the player to open the book
		ByteBuf buf = Unpooled.buffer(256);
		buf.setByte(0, (byte) 0);
		buf.writerIndex(1);

		PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload("MC|BOpen", new PacketDataSerializer(buf));
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

		// after opening the book, remove the book from the inventory
		// and restore the Original item
		inv.setItem(slot, originItem);
	}

	/**
	 * Craft MCQA book and add {@link Constants#SERIAL_BOOK_ID} to its
	 * {@link BookMeta#getLore()} at 0 index to be identified as MCQA-book
	 * 
	 * 
	 * @param content
	 *            the content to use to craft the book
	 * 
	 * @param isWritten
	 *            TRUE to use {@link Material#WRITTEN_BOOK}. FALSE to use
	 *            {@link Material#BOOK_AND_QUILL}
	 * 
	 * @return a crafted book contains the content and assigned as MCQA-book
	 * 
	 */
	public static ItemStack createBook(Content content, boolean isWritten) {
		ItemStack book = new ItemStack(isWritten ? Material.WRITTEN_BOOK : Material.BOOK_AND_QUILL);

		BookMeta bookMeta = (BookMeta) book.getItemMeta();

		bookMeta.setDisplayName(content.getQuestionContent());
		bookMeta.setAuthor(content.getQuestionCreator());
		bookMeta.setTitle(content.getQuestionContent());
		bookMeta.addPage(content.getAnswerProvedContent().split(Constants.ANSWER_SPIRATOR));

		List<String> description = new ArrayList<>();
		description.add(EnumChatFormat.BLACK + Constants.SERIAL_BOOK_ID);
		description.add("id: " + content.getContentId());
		description.add("Category: " + content.getCategory());
		description.add("question: " + content.getQuestionContent());

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(content.getAddedAt() * 1000);

		description.add("created at: " + cal.getTime().toString());
		bookMeta.setLore(description);

		book.setItemMeta(bookMeta);

		return book;
	}
}
