package flapjack.gui;

import java.io.*;

import com.install4j.api.launcher.*;
import com.install4j.api.update.*;

/**
 * Utility class that performs install4j updater actions on behalf of Flapjack.
 */
public class Install4j
{
	private static String URL = "http://bioinf.scri.ac.uk/flapjack/installers/updates.xml";

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
}