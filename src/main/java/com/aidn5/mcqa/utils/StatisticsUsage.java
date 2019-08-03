package com.aidn5.mcqa.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * My little boy :)
 * </p>
 * 
 * This helps me monitoring the usage of my own programs. It gives me statistics
 * like how many people are using the program.
 * 
 * <p>
 * <b>By the statistics, I can know:</b>
 * <ul>
 * <li>Whether I should continue developing/maintaining new features (when the
 * program is used by many people)</li>
 * 
 * <li>How to divide my time and focus on the programs, which are used by the
 * most.</li>
 * </ul>
 * 
 * 
 * <p>
 * <b>Data which is sent: </b>
 * <ul>
 * <li><u><b>appId</b></u>: The plugin/mod/program's id. It is sent to the
 * server to know which program is refereed to</li>
 * 
 * <li><u><b>appVersion</b></u>: The plugin/mod/program's version. It is sent to
 * the server to know which version is used the most.</li>
 * 
 * <li><u><b>vmName</b></u>: The machine/server/client's name that is running
 * it. Every platform has it's own codebase and logic (so the program). It's
 * easier to only maintain these platforms (which are used the most) instead of
 * all of them.</li>
 * 
 * <li><u><b>vmVersion</b></u>: Machine/Server/Client's version. same as vmName!
 * Every version has its own logic and codebase too!</li>
 * 
 * <li><u><b>userId</b></u>: An unique ID is used to avoid duplicated requests.
 * No worries, I can't monitor you just by knowing an anonymous-UUID/user-UUID.
 * </ul>
 * 
 * <p>
 * <b>Usage example:</b>
 * <code>#registerMCServerPlugin("awesomePlugin", "1.0", "Bukkit", "1.12.2")</code>
 * 
 * 
 * @author aidn5
 * @version 1.3
 * 
 * @see #registerMCClient(String, String, String, String, UUID)
 * @see #registerMCServer(String, String, String, String)
 * 
 * @license http://opensource.org/licenses/gpl-3.0.html GNU Public License
 * 
 * @category
 */
public class StatisticsUsage {
	private static final String serverHostName = "http://statistics.aidn5.epizy.com/?";

	/**
	 * After creating the object, wait (n) milliseconds before starting
	 * 
	 * @see #runEvery
	 */
	private static final long delayAtStartup = TimeUnit.SECONDS.toMillis(5);
	/**
	 * Check every (n) milliseconds, whether it's the time to inform the server
	 * about the statistic
	 * 
	 * @see #delayAtStartup
	 */
	private static final long runEvery = TimeUnit.SECONDS.toMillis(60);

	/**
	 * If the informing process succeed with no errors (on client and server side).
	 * use this time as to how far should the schedule be to the next run
	 * 
	 * @see #onFailRepeatAfter
	 */
	private static final long onSuccessRepeatAfter = TimeUnit.MINUTES.toMillis(55);
	/**
	 * If there is a failure in the process of informing the server (client or
	 * server side error), make the next schedule using this time
	 * 
	 * @see #onSuccessRepeatAfter
	 */
	private static final long onFailRepeatAfter = TimeUnit.SECONDS.toMillis(30);

	/**
	 * The pool where the repeated schedule saved
	 * 
	 * @see #fetcher
	 */
	private final Timer pool = new Timer(true);
	/**
	 * extended {@link TimerTask} serves as a holder to the server-informing task.
	 * Registered in {@link #pool} by {@link #StatisticMod(String, String)}
	 * 
	 * @see #pool
	 */
	private final StatisticUpdater fetcher = new StatisticUpdater();
	/**
	 * When should the next schedule run (Unix timestamp)
	 * 
	 * @see StatisticUpdater
	 */
	private long nextRun = -1;

	/**
	 * The encoded data to send to the server
	 */
	private final String payload;

	/**
	 * @see StatisticsUsage
	 */
	public static void registerMCClient(String modId, String modVersion, String mcName, String mcVersion,
			UUID playerUuid) {
		// Capitalize the first letter and lower case the rest
		String mcNameCap = mcName.substring(0, 1).toUpperCase() + mcName.substring(1).toLowerCase();

		new StatisticsUsage(modId, modVersion, "MC-" + mcNameCap + "-Client", mcVersion, playerUuid.toString());
	}

	/**
	 * @param serverType
	 *            e.g. Bukkit, Paper, Forge, Vanilla
	 * @see StatisticsUsage
	 */
	public static void registerMCServer(String pluginId, String pluginVersion, String serverType, String serverVerion) {
		// Capitalize the first letter and lower case the rest
		String mcNameCap = serverType.substring(0, 1).toUpperCase() + serverType.substring(1).toLowerCase();

		new StatisticsUsage(pluginId, pluginVersion, "MC-" + mcNameCap + "-Server", serverVerion,
				UUID.randomUUID().toString());
	}

	/**
	 * create a payload from the provided data and save it to be used by
	 * Web-protocol and then start daemon thread
	 * 
	 * @param appId
	 *            the program id
	 * @param appVersion
	 *            the program version
	 * @param vmName
	 *            the machine/server/client's name that is running it
	 * @param vmVersion
	 *            the version of the machine
	 * @param userId
	 *            An unique ID is used to avoid duplicated requests.
	 *
	 * @see StatisticsUsage
	 */
	private StatisticsUsage(String appId, String appVersion, String vmName, String vmVersion, String userId) {
		StringBuilder payloadBuilder = new StringBuilder(250);

		payloadBuilder.append("&appId=").append(appId);
		payloadBuilder.append("&appVersion=").append(appVersion);

		payloadBuilder.append("&vmName=").append(vmName);
		payloadBuilder.append("&vmVersion=").append(vmVersion);

		payloadBuilder.append("&userId=").append(userId);

		this.payload = payloadBuilder.toString();


		this.pool.schedule(this.fetcher, delayAtStartup, runEvery);
	}


	/**
	 * extended {@link TimerTask} serves as a holder to the server-informing task.
	 * Registered in {@link #pool} by
	 * {@link StatisticsUsage#NewStatisticer(String, String, String, String, String)}
	 * 
	 * @author aidn5
	 * @see #fetcher
	 */
	private final class StatisticUpdater extends TimerTask {

		// synchronized is used to avoid running the code twice at the same time.
		@Override
		public synchronized void run() {
			if (nextRun > System.currentTimeMillis()) return;

			InputStream in = null;

			try {
				URL url = new URL(serverHostName + payload);
				in = url.openStream();

				ByteArrayOutputStream result = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int length;
				while ((length = in.read(buffer)) != -1) {
					result.write(buffer, 0, length);
				}

				String response = result.toString("UTF-8");
				if (response.toLowerCase().contains("true")) {
					nextRun = System.currentTimeMillis() + onSuccessRepeatAfter;
				} else {
					nextRun = System.currentTimeMillis() + onFailRepeatAfter;
				}
			} catch (Throwable e) {
				nextRun = System.currentTimeMillis() + onFailRepeatAfter;

			} finally {
				try {
					in.close();
				} catch (Exception ignored) {}
			}
		}
	}
}
