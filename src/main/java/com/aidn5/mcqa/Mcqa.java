
package com.aidn5.mcqa;

import com.aidn5.mcqa.commands.QaAddCommand;
import com.aidn5.mcqa.commands.QaAdminCommand;
import com.aidn5.mcqa.commands.QaCommand;
import com.aidn5.mcqa.commands.QaCopyCommand;
import com.aidn5.mcqa.commands.QaDelCommand;
import com.aidn5.mcqa.commands.QaGetCommand;
import com.aidn5.mcqa.core.DatabaseFactory;
import com.aidn5.mcqa.core.database.IMcqaDatabase;
import com.aidn5.mcqa.language.Language;
import com.google.common.base.Charsets;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Mcqa extends JavaPlugin {
  private static Mcqa mcqa;
  public Logger logger;

  public static Mcqa getMcqa() {
    return mcqa;
  }

  private IMcqaDatabase database;
  private String databaseType;
  private Language language;

  private long shortestContent = 32;
  private long longestContent = 32000;
  private long shortestCategory = 3;
  private long longestCategory = 16;
  private long shortestQuestion = 4;
  private long longestQuestion = 128;

  private static boolean placeHolderApi = false;

  @Override
  public void onEnable() {
    logger = getLogger();
    mcqa = this;

    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      logger.config("found PlaceholderAPI.");
      placeHolderApi = true;
    }

    saveDefaultConfig();


    // initiate language file
    final File languageFile = new File(getDataFolder(), "language.yml");
    if (!languageFile.exists()) {
      saveResource("language.yml", false);
    }

    final FileConfiguration languageConfig = YamlConfiguration.loadConfiguration(languageFile);
    final InputStream defConfigStream = getResource("language.yml");

    languageConfig.setDefaults(YamlConfiguration
        .loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));

    language = new Language(languageConfig);


    // initiate database
    try {
      final String dbType = getConfig().getString("Config.Database.Type");

      if (dbType.equalsIgnoreCase("Memory")) {
        logger.info("Enabling Memory database...");
        logger.warning("DATA WILL BE LOST AFTER RESTARTING!");

        database = DatabaseFactory.getMemory(true);

      } else if (dbType.equalsIgnoreCase("Json")) {
        logger.info("Enabling Json database...");
        database = DatabaseFactory.getJson(getDataFolder(), true);

      } else if (dbType.equalsIgnoreCase("Byte")) {
        logger.info("Enabling Byte database...");
        database = DatabaseFactory.getByte(getDataFolder(), true);

      } else {
        throw new RuntimeException("No known database driver has been selected.");
      }

    } catch (Exception e) {
      throw new RuntimeException("Could not init the DB.", e);
    }


    // initiate commands
    this.getCommand("qa").setExecutor(new QaCommand(this));
    this.getCommand("qaadd").setExecutor(new QaAddCommand(this));
    this.getCommand("qaget").setExecutor(new QaGetCommand(this));
    this.getCommand("qadel").setExecutor(new QaDelCommand(this));

    QaCopyCommand qacopy = new QaCopyCommand(this);
    this.getCommand("qacopy").setExecutor(qacopy);
    this.getCommand("qacopy").setTabCompleter(qacopy);

    QaAdminCommand qaadmin = new QaAdminCommand(this);
    this.getCommand("qaadmin").setExecutor(qaadmin);
    this.getCommand("qaadmin").setTabCompleter(qaadmin);

    // initiate variables
    shortestContent = getConfig().getInt("Config.Answer.ShortestLength");
    longestContent = getConfig().getInt("Config.Answer.LongestLength");
    shortestQuestion = getConfig().getInt("Config.Question.ShortestLength");
    longestQuestion = getConfig().getInt("Config.Question.LongestLength");
    shortestCategory = getConfig().getInt("Config.Category.ShortestLength");
    longestCategory = getConfig().getInt("Config.Category.LongestLength");
  }

  @Override
  public void onDisable() {
    if (database != null) {
      try {
        database.closeConnection();
      } catch (Exception e) {
        e.printStackTrace();
      }

      databaseType = null;
      database = null;
    }
  }

  public IMcqaDatabase getDatabase() {
    return database;
  }

  public String getDatabaseType() {
    return databaseType;
  }

  public Language getLanguage() {
    return language;
  }

  public long getShortestContent() {
    return shortestContent;
  }

  public long getLongestContent() {
    return longestContent;
  }

  public long getShortestCategory() {
    return shortestCategory;
  }

  public long getLongestCategory() {
    return longestCategory;
  }

  public long getShortestQuestion() {
    return shortestQuestion;
  }

  public long getLongestQuestion() {
    return longestQuestion;
  }

  public static boolean placeHolderApi() {
    return placeHolderApi;
  }
}
