
package com.aidn5.mcqa.commands;

import com.aidn5.mcqa.Mcqa;
import com.aidn5.mcqa.McqaPermission;
import com.aidn5.mcqa.core.content.Content;
import com.aidn5.mcqa.core.content.Sanitizer;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;


public class QaAddCommand implements CommandExecutor {
  private final Mcqa mcqa;

  public QaAddCommand(Mcqa mcqa) {
    this.mcqa = mcqa;
  }

  // TODO: QaAddCommand fix: can add with the same questions
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return true;
    }

    final Player player = (Player) sender;

    final PlayerInventory inv = player.getInventory();

    // get the book from their hand (must have)
    int slot = inv.getHeldItemSlot();
    ItemStack book = inv.getItem(slot);

    if (book == null
        || (book.getType() != Material.WRITABLE_BOOK && book.getType() != Material.WRITTEN_BOOK)) {
      mcqa.getLanguage().mustHoldBookToUpload(sender);
      return true;
    }

    BookMeta bookMeta = (BookMeta) book.getItemMeta();
    List<String> pagesList = bookMeta.getPages();

    String[] pages = Sanitizer.sanitizeText(pagesList.toArray(new String[0]), true);
    int charCount = 0;
    for (String page : pages) {
      charCount += page.length();
    }

    // check the length of the answer
    final long shortestContent = mcqa.getShortestContent();
    final long longestContent = mcqa.getLongestContent();

    if (charCount < shortestContent || charCount > longestContent) {
      mcqa.getLanguage().contentLengthProblem(sender, shortestContent, longestContent);
      return true;
    }

    // after getting the contents and confirmed,
    // we can now look at the command
    if (args.length < 2) {
      return false;
    }

    // get the category sanitize it and check for error
    final String category = Sanitizer.sanitizeText(args[0], false);

    final long shortestCategory = mcqa.getShortestCategory();
    final long longestCategory = mcqa.getLongestCategory();

    if (!category.equals(args[0])) {
      mcqa.getLanguage().categorySanitizeProblem(sender);
      return true;

    } else if (category.length() < shortestCategory || category.length() > longestCategory) {
      mcqa.getLanguage().categoryLengthProblem(sender, shortestCategory, longestCategory);
      return true;
    }

    // get the questions, sanitize it and check for error
    final StringBuilder questionBuilder = new StringBuilder(args.length * 7);
    for (int i = 1; i < args.length; i++) {
      questionBuilder.append(args[i]).append(" ");
    }

    final String question = Sanitizer.sanitizeText(questionBuilder.toString(), false);

    // check questions's content
    final long shortestQuestion = mcqa.getShortestQuestion();
    final long longestQuestion = mcqa.getLongestQuestion();

    if (!question.trim().equals(questionBuilder.toString().trim())) {
      mcqa.getLanguage().questionSanitizeProblem(sender);
      return true;

    } else if (question.length() < shortestQuestion || question.length() > longestQuestion) {
      mcqa.getLanguage().questionLengthProblem(sender, shortestQuestion, longestQuestion);
      return true;
    }

    boolean isApproved = sender.hasPermission(McqaPermission.APPROVE_CONTENTS);

    Content content = Content.createNewContent(category.trim().toLowerCase(), isApproved,
        question.toLowerCase().trim(), pages, player.getUniqueId());


    mcqa.getDatabase().addContent(content);

    // remove the book from their inventory
    inv.setItem(slot, null);

    if (isApproved) {
      mcqa.getLanguage().contentAdded(sender, content);

    } else {
      mcqa.getLanguage().contentAddedWaitingForApproval(sender, content);
    }

    return true;
  }
}
