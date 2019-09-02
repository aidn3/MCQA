
package com.aidn5.mcqa.utils;

import com.aidn5.mcqa.Constants;
import com.aidn5.mcqa.core.content.Content;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import xyz.upperlevel.spigot.book.BookUtil;

/**
 * Utils helps with crafting Mcqa-books and provide methods to show the players
 * its contents.
 * 
 * @author aidn5
 *
 */
public class Book {
  /**
   * make the player view book's content.
   * 
   * @param book
   *          the book to open
   * @param player
   *          the player to show the book to
   */
  public static void showBook(ItemStack book, Player player) {
    BookUtil.openPlayer(player, book);
  }

  /**
   * Create new book with all the content.
   * 
   * @param content
   *          the content to write in book.
   * @param questionIndex
   *          what question was the asker used to get these contents.
   *          Used to put this particular question as display name.
   * @param isWritten
   *          <code>true</code> if the book should be unchangeable.
   * @param proved
   *          <code>true</code> if the answer should be the approved one.
   * 
   * @return
   *         book as item contains the content ready to be sent to the player.
   */
  public static ItemStack createBook(Content content, int questionIndex, boolean isWritten,
      boolean proved) {
    ItemStack book = new ItemStack(
        isWritten ? Material.WRITTEN_BOOK : Material.WRITABLE_BOOK);

    BookMeta bookMeta = (BookMeta) book.getItemMeta();

    bookMeta.setDisplayName(content.questions[questionIndex]);
    bookMeta.setAuthor(content.questionCreator.toString());
    bookMeta.setTitle(content.questions[questionIndex]);
    if (proved) {
      bookMeta.addPage(content.answerProvedContent);

    } else {
      bookMeta.addPage(content.answerUnProvedContent);
    }

    List<String> description = new ArrayList<>();
    description.add(ChatColor.BLACK + Constants.SERIAL_BOOK_ID);
    description.add("id: " + content.contentId);
    description.add("Category: " + content.category);
    description.add("question: " + content.questions[0]);
    if (!proved) {
      description.add("author of new content: " + content.creatorOfUnprovedAnswer.toString());
      description.add("This book is not available for the public.");
    }

    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(content.addedAt * 1000);

    description.add("created at: " + cal.getTime().toString());
    bookMeta.setLore(description);

    book.setItemMeta(bookMeta);

    return book;
  }
}
