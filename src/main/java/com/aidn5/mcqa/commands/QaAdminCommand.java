
package com.aidn5.mcqa.commands;

import com.aidn5.mcqa.Mcqa;
import com.aidn5.mcqa.McqaPermission;
import com.aidn5.mcqa.utils.Book;
import com.aidn5.mcqa.utils.TutorialContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QaAdminCommand implements CommandExecutor, TabCompleter {
  private final Mcqa mcqa;

  public QaAdminCommand(Mcqa mcqa) {
    this.mcqa = mcqa;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (args[0].equalsIgnoreCase("book")) {
      // can't give console an item without an inventory.
      if (!(sender instanceof Player)) {
        mcqa.getLanguage().mustBePlayerWithInventory(sender);
      }

      if (!sender.hasPermission(McqaPermission.ADMIN_BOOK)) {
        mcqa.getLanguage().noPermissionToUse(sender);
        return true;
      }

      final Player player = (Player) sender;

      ItemStack book = Book.createBook(TutorialContent.getTutorialContent(), 0, true, true);
      player.getInventory().addItem(book);

      Book.showBook(book, player);

      mcqa.getLanguage().tutorialBookReceived(sender);

      return true;

    } else if (args[0].equalsIgnoreCase("reload")) {

      if (!sender.hasPermission(McqaPermission.ADMIN_RELOAD)) {
        mcqa.getLanguage().noPermissionToUse(sender);
        return true;
      }

      Bukkit.getPluginManager().disablePlugin(mcqa);
      Bukkit.getPluginManager().enablePlugin(mcqa);

      if (Bukkit.getPluginManager().isPluginEnabled(mcqa)) {
        mcqa.getLanguage().pluginReloaded(sender);

      } else {
        mcqa.getLanguage().pluginNotReloaded(sender);
      }

      return true;

    } else if (args[0].equalsIgnoreCase("help")) {
      mcqa.getLanguage().helpHelp(sender);
      mcqa.getLanguage().helpBook(sender);
      mcqa.getLanguage().helpInfo(sender);
      mcqa.getLanguage().helpReload(sender);
      mcqa.getLanguage().helpMigrate(sender);

      return true;

    } else if (args[0].equalsIgnoreCase("info")) {
      mcqa.getLanguage().featureDisabled(sender);
      if ("A".equalsIgnoreCase("a")) {
        return true;
      }

    } else if (args[0].equalsIgnoreCase("migrate")) {
      mcqa.getLanguage().featureDisabled(sender);
      if ("A".equalsIgnoreCase("a")) {
        return true;
      }

      if (args.length == 1 || args.length > 3) {
        mcqa.getLanguage().helpMigrate(sender);
        return true;
      }
    }

    mcqa.getLanguage().helpHelp(sender);
    mcqa.getLanguage().helpBook(sender);
    mcqa.getLanguage().helpInfo(sender);
    mcqa.getLanguage().helpReload(sender);
    mcqa.getLanguage().helpMigrate(sender);

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias,
      String[] args) {
    List<String> tabs = new ArrayList<>();

    if (args.length == 1) {
      List<String> all = Arrays
          .asList("book", "reload", "migrate", "info", "help");

      for (String tab : all) {
        if (tab.startsWith(args[0])) {
          tabs.add(tab);
        }
      }

      return tabs;

    } else if (args[0].equalsIgnoreCase("migrate") && args.length < 4) {
      // "/qa migrate [from] <to>"
      return Arrays.asList("json", "sqlite", "memory", "mysql");
    }

    return tabs;
  }


}
