package com.aidn5.mcqa.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aidn5.mcqa.Constants;
import com.aidn5.mcqa.MCQA;
import com.aidn5.mcqa.core.dataholders.Content;
import com.aidn5.mcqa.language.Notifier;
import com.aidn5.mcqa.utils.Book;


public class QAGETCommand implements CommandExecutor {
	private final MCQA mcqa;

	public QAGETCommand(MCQA mcqa) {
		this.mcqa = mcqa;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		if (sender instanceof Player) player = (Player) sender;


		if (player != null && !mcqa.getPerms().canViewContents(player.getUniqueId())) {
			Notifier.Commands.noPermsToUse(sender);
			return true;
		}

		int id;
		try {
			id = Integer.parseInt(args[0]);
		} catch (Exception e) {
			Notifier.ContentsMsg.invalidId(sender);
			return true;
		}

		Content content = mcqa.getStorage().getContent(id);
		if (content == null) {
			Notifier.ContentsMsg.contentNotFound(sender);
			return true;
		}

		if (!content.isQuestionProved()) { // question not proved by the staff
			// not the creator
			if (player != null && !content.getQuestionCreator().equals(player.getUniqueId().toString())) {
				if (!mcqa.getPerms().canViewAllContents(player.getUniqueId())) { // and not a staff/have perms
					Notifier.Commands.noPermsToView(sender);
					return true;
				}
			}
		}

		Notifier.ContentsMsg.showContentsInfo(sender, content);

		if (player != null) Book.showBook(Book.createBook(content, true), player);
		else {
			for (String string : content.getAnswerProvedContent().split(Constants.ANSWER_SPIRATOR)) {
				sender.sendMessage(string.replaceAll("(§0)", ""));
			}
		}
		return true;
	}


}
