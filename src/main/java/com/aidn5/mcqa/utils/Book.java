
package com.aidn5.mcqa.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.aidn5.mcqa.Constants;
import com.aidn5.mcqa.core.dataholders.Content;

import xyz.upperlevel.spigot.book.BookUtil;

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
   *          the book to open
   * @param player
   *          the player to show
   */
  public static void showBook(ItemStack book, Player player) {
    BookUtil.openPlayer(player, book);
  }

  /**
   * Craft MCQA book and add {@link Constants#SERIAL_BOOK_ID} to its
   * {@link BookMeta#getLore()} at 0 index to be identified as MCQA-book
   * 
   * 
   * @param content
   *          the content to use to craft the book
   * 
   * @param isWritten
   *          TRUE to use {@link Material#WRITTEN_BOOK}. FALSE to use
   *          {@link Material#BOOK_AND_QUILL}
   * 
   * @return a crafted book contains the content and assigned as MCQA-book
   * 
   */
  public static ItemStack createBook(Content content, boolean isWritten) {
    ItemStack book = new ItemStack(
        isWritten ? Material.WRITTEN_BOOK : Material.LEGACY_BOOK_AND_QUILL);

    BookMeta bookMeta = (BookMeta) book.getItemMeta();

    bookMeta.setDisplayName(content.getQuestionContent());
    bookMeta.setAuthor(content.getQuestionCreator());
    bookMeta.setTitle(content.getQuestionContent());
    bookMeta.addPage(content.getAnswerProvedContent().split(Constants.ANSWER_SPIRATOR));

    List<String> description = new ArrayList<>();
    description.add(ChatColor.BLACK + Constants.SERIAL_BOOK_ID);
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
