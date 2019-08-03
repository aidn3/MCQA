package com.aidn5.mcqa.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aidn5.mcqa.MCQA;
import com.aidn5.mcqa.core.dataholders.Content;
import com.aidn5.mcqa.language.Notifier;


public class QACommand implements CommandExecutor {
	private final MCQA mcqa;

	public QACommand(MCQA mcqa) {
		this.mcqa = mcqa;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!mcqa.getPerms().canViewContents(player.getUniqueId())) {
				Notifier.Commands.noPermsToUse(sender);
				return true;
			}
		}


		List<Content> contents;

		if (args.length == 0) contents = mcqa.getStorage().getAllContents(false);
		else
			contents = mcqa.getStorage().searchForContents(false, args);

		if (contents == null || contents.size() == 0) {
			Notifier.ContentsMsg.contentNotFound(sender);
			return true;
		}

		Notifier.ContentsMsg.viewSelectableList(sender, contents);
		return true;
	}
}
