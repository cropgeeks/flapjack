// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import com.install4j.api.launcher.*;
import com.install4j.api.update.*;

import scri.commons.gui.*;

/**
 * Utility class that performs install4j updater actions on behalf of Flapjack.
 */
public class Install4j
{
	private static String URL = "http://bioinf.scri.ac.uk/flapjack/installers/updates.xml";

	public static String VERSION = "x.xx.xx.xx";

	public static final int NEVER = 0;
	public static final int STARTUP = 1;
	public static final int DAILY = 2;
	public static final int WEEKLY = 3;
	public static final int MONTHLY = 4;

	/**
	 * install4j update check. This will only work when running under the full
	 * install4j environment, so expect exceptions everywhere else
	 */
	static void doStartUpCheck()
	{
		getVersion();
		pingServer();

		try
		{
			switch (Prefs.guiUpdateSchedule)
			{
				case STARTUP:
					UpdateScheduleRegistry.setUpdateSchedule(UpdateSchedule.ON_EVERY_START);
					break;
				case DAILY:
					UpdateScheduleRegistry.setUpdateSchedule(UpdateSchedule.DAILY);
					break;
				case WEEKLY:
					UpdateScheduleRegistry.setUpdateSchedule(UpdateSchedule.WEEKLY);
					break;
				case MONTHLY:
					UpdateScheduleRegistry.setUpdateSchedule(UpdateSchedule.MONTHLY);
					break;

				default:
					UpdateScheduleRegistry.setUpdateSchedule(UpdateSchedule.NEVER);
			}

			if (UpdateScheduleRegistry.checkAndReset() == false)
				return;

			UpdateDescriptor ud = UpdateChecker.getUpdateDescriptor(URL, ApplicationDisplayMode.GUI);

			if (ud.getPossibleUpdateEntry() != null)
				checkForUpdate(true);
		}
		catch (Exception e) {}
	}

	/**
	 * Shows the install4j updater app to check for updates and download/install
	 * any that are found.
	 */
	static void checkForUpdate(boolean block)
	{
		try
		{
			ApplicationLauncher.launchApplication("418", null, block, null);
		}
		catch (IOException e) {}
	}

	private static void getVersion()
	{
		// Attempt to get the version string from the jar's manifest
		VERSION = Flapjack.class.getPackage().getImplementationVersion();

		// If it's not found, we must be running the development version
		if (VERSION == null)
			VERSION = "x.xx.xx.xx";
	}

	static File getLicenceFile()
	{
		try
		{
			com.install4j.api.ApplicationRegistry.ApplicationInfo info =
				com.install4j.api.ApplicationRegistry.getApplicationInfoByDir(new File("."));

			File root = info.getInstallationDirectory();
			File licenceFolder = new File(root, "docs");

			return new File(licenceFolder, "Flapjack.html");
		}
		catch (Throwable e) { return new File("installer/docs/Flapjack.html"); }
	}

	private static void pingServer()
	{
		Runnable r = new Runnable() {
			public void run()
			{
				try
				{
					// Safely encode the URL's parameters
					String id = URLEncoder.encode(Prefs.flapjackID, "UTF-8");
					String version = URLEncoder.encode(VERSION, "UTF-8");
					String locale = URLEncoder.encode("" + Locale.getDefault(), "UTF-8");
					String os = URLEncoder.encode(System.getProperty("os.name"), "UTF-8");
					String user = URLEncoder.encode(System.getProperty("user.name"), "UTF-8");

					String addr = "http://bioinf.scri.ac.uk/cgi-bin/flapjack/flapjack.cgi"
						+ "?id=" + id
						+ "&version=" + version
						+ "&locale=" + locale
						+ "&rating=" + Prefs.rating
						+ "&os=" + os;

					// We DO NOT log usernames from non-SCRI addresses
					if (SystemUtils.isSCRIUser())
						addr += "&user=" + user;

					// Nudges the cgi script to log the fact that a version of
					// Flapjack has been run
					URL url = new URL(addr);
					HttpURLConnection c = (HttpURLConnection) url.openConnection();

					c.getResponseCode();
					c.disconnect();
				}
				catch (Exception e) {}
			}
		};

		// We run this in a separate thread to avoid any waits due to lack of an
		// internet connection or the server being non-responsive
		new Thread(r).start();
	}
}