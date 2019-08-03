package com.aidn5.mcqa.commands;

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

import com.aidn5.mcqa.Constants;
import com.aidn5.mcqa.MCQA;
import com.aidn5.mcqa.core.dataholders.Content;
import com.aidn5.mcqa.language.Notifier;
import com.aidn5.mcqa.utils.Book;


public class QACOPYCommand implements CommandExecutor, TabCompleter {
	private final MCQA mcqa;

	public QACOPYCommand(MCQA mcqa) {
		this.mcqa = mcqa;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			Notifier.Commands.mustBePlayerWithInventory(sender);
			return true;
		}

		Player player = (Player) sender;

		if (!mcqa.getPerms().canCopyContents(player.getUniqueId())) {
			Notifier.Commands.noPermsToUse(sender);
			return true;
		}


		final PlayerInventory inv = player.getInventory();

		// get the book from their hand (must have)
		int slot = inv.getHeldItemSlot();
		ItemStack book = inv.getItem(slot);

		boolean canBypassBookRequire = mcqa.getPerms().canBypassCopyContentsRequireBook(player.getUniqueId());

		// check in case of abuse (#doPlayerCheck does send message to the player)
		if (!canBypassBookRequire && !doPlayerCheck(book, player)) return true;

		// get the id of the contents
		int id;
		try {
			id = Integer.parseInt(args[0]);
		} catch (Exception e) {
			Notifier.ContentsMsg.invalidId(sender);
			return true;
		}

		// use the id to get the content and its properties
		Content content = mcqa.getStorage().getContent(id);
		if (content == null) {
			Notifier.ContentsMsg.contentNotFound(sender);
			return true;
		}

		// check if the player can view not a public contents
		if (!canViewThisContent(content, player)) {
			Notifier.Commands.noPermsToView(player);
			return true;
		}


		Notifier.ContentsMsg.showContentsInfo(sender, content);


		boolean isWritten;
		if (canBypassBookRequire && (book == null) || args.length > 1) {
			if (args.length > 1 && args[1].toLowerCase().contains("notsigned")) isWritten = false;
			else
				isWritten = true;
		} else
			isWritten = book.getType() == Material.WRITTEN_BOOK ? true : false;


		inv.setItem(slot, Book.createBook(content, isWritten));
		return true;
	}

	private boolean canViewThisContent(Content content, Player player) {
		// question proved by the staff
		if (content.isQuestionProved()) return true;
		// player is the creator
		if (content.getQuestionCreator().equals(player.getUniqueId().toString())) return true;
		// is a staff/have perms
		if (mcqa.getPerms().canViewAllContents(player.getUniqueId())) return true;

		return false;
	}

	private boolean doPlayerCheck(ItemStack book, Player player) {
		// no book to use/sacrifice
		if (book == null || (book.getType() != Material.BOOK_AND_QUILL && book.getType() != Material.WRITTEN_BOOK)) {
			Notifier.Commands.mustHoldBookToCopy(player);
			return false;
		}

		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		if (book.getType() == Material.WRITTEN_BOOK) {
			// if a player wants to rewrite another player's book (abuse)
			if (bookMeta.hasAuthor() && !bookMeta.getAuthor().equals(player.getName())) {
				// Player should be able to use the copy-command on the same book multiple times
				if (bookMeta.getLore().size() == 0 || !bookMeta.getLore().get(0).equals(Constants.SERIAL_BOOK_ID)) {
					Notifier.Commands.bookMustBeYours(player);
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return null;

		Player player = (Player) sender;

		if (args.length == 2 && mcqa.getPerms().canBypassCopyContentsRequireBook(player.getUniqueId())) {
			return Arrays.asList("signed", "notSigned");
		}

		return null;
	}
}
