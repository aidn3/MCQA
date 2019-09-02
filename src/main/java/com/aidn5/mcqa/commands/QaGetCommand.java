
package com.aidn5.mcqa.commands;

import com.aidn5.mcqa.Mcqa;
import com.aidn5.mcqa.McqaPermission;
import com.aidn5.mcqa.core.content.Content;
import com.aidn5.mcqa.utils.Book;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class QaGetCommand implements CommandExecutor {
  private final Mcqa mcqa;

  public QaGetCommand(Mcqa mcqa) {
    this.mcqa = mcqa;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    Player player = null;
    if (sender instanceof Player) {
      player = (Player) sender;
    }


    int id;
    try {
      id = Integer.parseInt(args[0]);
    } catch (@SuppressWarnings("unused") Exception ignored) {
      mcqa.getLanguage().invalidId(sender);
      return true;
    }

    Content content = mcqa.getDatabase().getContent(id);
    if (content == null) {
      mcqa.getLanguage().contentNotFound(sender, id);
      return true;
    }

    if (!content.hasProvedAnswer()) { // questions not proved by the staff
      // not the creator
      if (player != null && !content.questionCreator.equals(player.getUniqueId())) {
        // and not a staff have permission
        if (!sender.hasPermission(McqaPermission.VIEW_ALL_CONTETNS)) {
          mcqa.getLanguage().noPermissionToView(sender, id);
          return true;
        }
      }
    }

    mcqa.getLanguage().showContentsInfo(sender, content);

    if (player != null) {
      ItemStack book = Book.createBook(content, 0, true, content.hasProvedAnswer());
      Book.showBook(book, player);

    } else {
      String[] answer = content.hasProvedAnswer() ? content.answerProvedContent
          : content.answerUnProvedContent;

      for (String page : answer) {
        sender.sendMessage(ChatColor.stripColor(page));
      }
    }
    return true;
  }


}
