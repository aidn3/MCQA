package com.aidn5.mcqa.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aidn5.mcqa.MCQA;
import com.aidn5.mcqa.core.dataholders.Content;
import com.aidn5.mcqa.language.Notifier;

public class QADELCommand implements CommandExecutor {
	private final MCQA mcqa;

	// TODO: add alias commands(qadelete, qaremove, qarem, qadel)
	public QADELCommand(MCQA mcqa) {
		this.mcqa = mcqa;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		int id;
		try {
			id = Integer.parseInt(args[0]);
		} catch (Exception e) {
			Notifier.ContentsMsg.invalidId(sender);
			return true;
		}

		if (sender instanceof Player) {
			final Player player = (Player) sender;

			// Check permission
			if (!mcqa.getPerms().canRemoveContents(player.getUniqueId())) {

				Content content = mcqa.getStorage().getContent(id);
				if (content != null) {
					if (!player.getUniqueId().toString().equals(content.getQuestionCreator())) {
						Notifier.Commands.noPermsToUse(sender);
						return true;
					}
				} else {
					Notifier.Commands.noContentToRemove(sender);
					return true;
				}
			}
		}

		if (mcqa.getStorage().removeContent(id)) Notifier.Commands.contentRemoved(sender);
		else
			Notifier.Commands.noContentToRemove(sender);

		return true;
	}

}
