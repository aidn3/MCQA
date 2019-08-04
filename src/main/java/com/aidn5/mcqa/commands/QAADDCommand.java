package com.aidn5.mcqa.commands;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

import com.aidn5.mcqa.Constants;
import com.aidn5.mcqa.MCQA;
import com.aidn5.mcqa.core.dataholders.Content;
import com.aidn5.mcqa.core.tools.Sanitizer;
import com.aidn5.mcqa.language.Notifier;

public class QAADDCommand implements CommandExecutor {
	private final MCQA mcqa;

	public QAADDCommand(MCQA mcqa) {
		this.mcqa = mcqa;
	}

	// TODO: QAADDCommand fix: can add with the same question
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return true;
		final Player player = (Player) sender;

		// Check permission to add new contents
		if (!mcqa.getPerms().canAddContents(player.getUniqueId())) {
			Notifier.Commands.noPermsToUse(sender);
			return true;
		}

		final PlayerInventory inv = player.getInventory();

		// get the book from their hand (must have)
		int slot = inv.getHeldItemSlot();
		ItemStack book = inv.getItem(slot);

		if (book == null || (book.getType() != Material.WRITABLE_BOOK && book.getType() != Material.WRITTEN_BOOK)) {
			Notifier.Commands.mustHoldTheBookToAdd(sender);
			return true;
		}

		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		List<String> pages = bookMeta.getPages();

		// answer length
		StringBuilder answer = new StringBuilder(pages.size() * 100);

		// sanitize (special chars are not removed) the pages and remove any empty one
		for (String string : pages) {
			string = Sanitizer.sanitizeText(string, true);
			if (!string.isEmpty()) {
				answer.append(string).append(Constants.ANSWER_SPIRATOR);
			}
		}

		if (answer.length() > 0) answer.deleteCharAt(answer.length() - 1);

		// check the length of the answer
		if (answer.length() < 32 || answer.length() > 64000) {
			Notifier.ContentsMsg.answerLengthError(sender, 32, 6400);
			return true;
		}

		// after getting the contents and confirmed,
		// we can now look at the command
		if (args.length < 2) return false;

		// get the category sanitize it and check for error
		final String category = Sanitizer.sanitizeText(args[0], false);
		if (!category.equals(args[0])) {
			Notifier.ContentsMsg.sanitizeProblem(sender);
			return true;
		}

		// get the question, sanitize it and check for error
		final StringBuilder questionBuilder = new StringBuilder(args.length * 5);
		for (int i = 1; i < args.length; i++)
			questionBuilder.append(args[i]).append(" ");

		final String question = Sanitizer.sanitizeText(questionBuilder.toString(), false);

		// check question's content
		if (!question.trim().equals(questionBuilder.toString().trim())) {
			sender.sendMessage("1");
			Notifier.ContentsMsg.questionSanitizeProblem(sender);
			return true;
		} else if (question.length() < 4 || question.length() > 128) {
			Notifier.ContentsMsg.questionLengthError(sender, 4, 128);
			return true;
		}

		boolean isProved = mcqa.getPerms().canApproveContents(player.getUniqueId());

		Content content = Content.createNewContent(category.toLowerCase().trim(), isProved,
				question.toLowerCase().trim(), answer.toString(), player.getUniqueId().toString());
		mcqa.getStorage().addContent(content);
		Notifier.Commands.contentsAdded(sender, isProved);

		// remove the book from their inventory
		inv.setItem(slot, null);

		return true;
	}
}
