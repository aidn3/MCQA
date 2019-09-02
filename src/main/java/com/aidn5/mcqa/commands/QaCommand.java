
package com.aidn5.mcqa.commands;

import com.aidn5.mcqa.Mcqa;
import com.aidn5.mcqa.core.content.Content;
import com.aidn5.mcqa.core.database.IContentsIterator;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class QaCommand implements CommandExecutor {
  private final Mcqa mcqa;

  public QaCommand(Mcqa mcqa) {
    this.mcqa = mcqa;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    final List<Content> contents = new ArrayList<Content>(20);

    IContentsIterator iterator = mcqa.getDatabase().searchForContents(false, 0, args);
    int count = 0;
    while (iterator.hasNext()) {
      if (count >= 10) {
        break;
      }
      contents.add(iterator.next());
      count++;
    }

    if (contents.size() == 0) {
      mcqa.getLanguage().noContent(sender);
      return true;
    }

    mcqa.getLanguage().showSearch(sender, contents);
    return true;
  }
}
