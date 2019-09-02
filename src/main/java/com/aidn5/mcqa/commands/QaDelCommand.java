
package com.aidn5.mcqa.commands;

import com.aidn5.mcqa.Mcqa;
import com.aidn5.mcqa.McqaPermission;
import com.aidn5.mcqa.core.content.Content;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QaDelCommand implements CommandExecutor {
  private final Mcqa mcqa;

  public QaDelCommand(Mcqa mcqa) {
    this.mcqa = mcqa;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    int id;
    try {
      id = Integer.parseInt(args[0]);
    } catch (Exception ignored) {
      mcqa.getLanguage().invalidId(sender);
      return true;
    }

    if (sender instanceof Player) {
      final Player player = (Player) sender;

      Content content = mcqa.getDatabase().getContent(id);
      if (content == null) {
        mcqa.getLanguage().noContentToRemove(sender, id);
        return true;
      }

      if (!player.getUniqueId().equals(content.questionCreator)) {
        if (!player.hasPermission(McqaPermission.REMOVE_CONTENTS)) {
          mcqa.getLanguage().noPermissionToUse(sender);
        }
        return true;
      }
    }

    if (mcqa.getDatabase().removeContent(id)) {
      mcqa.getLanguage().contentRemoved(sender, id);

    } else {
      mcqa.getLanguage().noContentToRemove(sender, id);
    }

    return true;
  }

}
