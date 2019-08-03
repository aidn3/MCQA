package com.aidn5.mcqa;

import java.io.File;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.aidn5.mcqa.commands.QAADDCommand;
import com.aidn5.mcqa.commands.QACOPYCommand;
import com.aidn5.mcqa.commands.QACommand;
import com.aidn5.mcqa.commands.QADELCommand;
import com.aidn5.mcqa.commands.QAGETCommand;
import com.aidn5.mcqa.core.interfaces.PermissionInterface;
import com.aidn5.mcqa.core.interfaces.StorageInterface;
import com.aidn5.mcqa.core.storage.SQLiteConnection;
import com.aidn5.mcqa.defaults.DefaultPermissionAdapter;
import com.aidn5.mcqa.utils.StatisticsUsage;
import com.aidn5.mcqa.utils.TutorialContent;

public class MCQA extends JavaPlugin {
	static {
		StatisticsUsage.registerMCServer(PluginConfig.ID, PluginConfig.VERSION, "Bukkit", Bukkit.getBukkitVersion());
	}

	private String dbPath;
	private StorageInterface StorageInterface;
	private PermissionInterface perms = new DefaultPermissionAdapter();

	@Override
	public void onEnable() {
		getDataFolder().mkdirs();

		dbPath = getDataFolder().getAbsolutePath() + File.separator + "QA.db";
		if (StorageInterface == null) initDefaultDatabase();


		this.getCommand("qa").setExecutor(new QACommand(this));
		this.getCommand("qaadd").setExecutor(new QAADDCommand(this));
		this.getCommand("qaget").setExecutor(new QAGETCommand(this));
		this.getCommand("qadel").setExecutor(new QADELCommand(this));

		QACOPYCommand qacopy = new QACOPYCommand(this);
		this.getCommand("qacopy").setExecutor(qacopy);
		this.getCommand("qacopy").setTabCompleter(qacopy);

		System.out.println(getDataFolder().getAbsolutePath());
	}

	@Override
	public void onDisable() {
		if (StorageInterface != null) {
			StorageInterface.closeConnection();
		}
	}

	public StorageInterface getStorage() {
		return StorageInterface;
	}

	public PermissionInterface getPerms() {
		return perms;
	}

	public void setPerms(PermissionInterface perms) {
		this.perms = Objects.requireNonNull(perms);
	}

	public void setStorageInterface(StorageInterface storageInterface) {
		StorageInterface = Objects.requireNonNull(storageInterface);
	}

	private void initDefaultDatabase() {
		// on-new: add the tutorial contents about the plugin
		// to help the admin, when they test the plugin with "/qa"
		// (which is get all saved contents)

		// save whether the file exists.
		// We can't check after we create the file by SQLiteConnection(String)
		boolean isNew = new File(dbPath).length() == 0;

		if (dbPath != null) {
			StorageInterface = new SQLiteConnection(dbPath);
			if (isNew) StorageInterface.addContent(TutorialContent.getTutorialContent());
		} else {
			StorageInterface = new SQLiteConnection();
			StorageInterface.addContent(TutorialContent.getTutorialContent());
		}
	}
}
