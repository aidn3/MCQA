
package com.aidn5.mcqa.commands;

import com.aidn5.mcqa.Constants;
import com.aidn5.mcqa.Mcqa;
import com.aidn5.mcqa.McqaPermission;
import com.aidn5.mcqa.core.content.Content;
import com.aidn5.mcqa.utils.Book;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;


public class QaCopyCommand implements CommandExecutor, TabCompleter {
  private final Mcqa mcqa;

  public QaCopyCommand(Mcqa mcqa) {
    this.mcqa = mcqa;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      mcqa.getLanguage().mustBePlayerWithInventory(sender);
      return true;
    }

    Player player = (Player) sender;

    final PlayerInventory inv = player.getInventory();

    // get the book from their hand
    int slot = inv.getHeldItemSlot();
    ItemStack book = inv.getItem(slot);

    boolean canBypassBookRequire = sender
        .hasPermission(McqaPermission.BypassCopyContentsRequireBook);

    // check in case of abuse (#doPlayerCheck(...) does send message to the player)
    if (!canBypassBookRequire && !doPlayerCheck(book, player)) {
      return true;
    }

    // get the id of the contents
    int id;
    try {
      id = Integer.parseInt(args[0]);
    } catch (Exception ignored) {
      mcqa.getLanguage().invalidId(sender);
      return true;
    }

    // use the id to get the content and its properties
    Content content = mcqa.getDatabase().getContent(id);
    if (content == null) {
      mcqa.getLanguage().contentNotFound(sender, id);
      return true;
    }

    // check if the player can view not a public contents
    if (!canViewThisContent(content, player)) {
      mcqa.getLanguage().noPermissionToView(sender, id);
      return true;
    }

    mcqa.getLanguage().showContentsInfo(sender, content);

    boolean isWritten = true;
    if (book != null) {
      isWritten = book.getType() == Material.WRITTEN_BOOK ? true : false;

      // TODO: is this the right thing to do?
    } else if (canBypassBookRequire) {
      if (args.length > 1 && args[1].equalsIgnoreCase("notsigned")) {
        isWritten = false;
      } else {
        isWritten = true;
      }
    }

    boolean approved = true;
    // this player is viewing their own tutorial
    if (content.answerProvedContent == null || content.answerProvedContent.length == 0) {
      approved = false;
    }

    inv.setItem(slot, Book.createBook(content, 0, isWritten, approved));
    return true;
  }

  private boolean canViewThisContent(Content content, Player player) {
    // questions proved by the staff
    if (content.answerProvedContent != null && content.answerProvedContent.length > 0) {
      return true;
    }

    // player is the creator
    if (content.questionCreator.equals(player.getUniqueId())) {
      return true;
    }

    // is a staff/have perms
    if (player.hasPermission(McqaPermission.VIEW_ALL_CONTETNS)) {
      return true;
    }

    return false;
  }

  private boolean doPlayerCheck(ItemStack book, Player player) {
    // no book to use/sacrifice
    if (book == null
        || (book.getType() != Material.WRITABLE_BOOK && book.getType() != Material.WRITTEN_BOOK)) {
      mcqa.getLanguage().mustHoldBookToCopy(player);
      return false;
    }

    BookMeta bookMeta = (BookMeta) book.getItemMeta();
    if (book.getType() == Material.WRITTEN_BOOK) {
      // if a player wants to rewrite another player's book (abuse)
      if (bookMeta.hasAuthor() && !bookMeta.getAuthor().equals(player.getName())) {
        // Player should be able to use the copy-command on the same book multiple times
        if (bookMeta.getLore().size() == 0
            || !bookMeta.getLore().get(0).equals(Constants.SERIAL_BOOK_ID)) {
          mcqa.getLanguage().bookNotYours(player);
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label,
      String[] args) {

    if (args.length == 2 && sender.hasPermission(McqaPermission.BypassCopyContentsRequireBook)) {
      return Arrays.asList("signed", "NotSigned");
    }

    return null;
  }
}
