package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.gui.dialog.*;

import scri.commons.gui.*;

import apple.dts.samplecode.osxadapter.*;

public class Flapjack
{
	private static File prefsFile = new File(System.getProperty("user.home"), ".flapjack.xml");
	private static Prefs prefs = new Prefs();

	public static WinMain winMain;

	public static void main(String[] args)
		throws Exception
	{
		// OS X: This has to be set before anything else
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Flapjack");

		Prefs.setDefaults();
		prefs.loadPreferences(prefsFile, Prefs.class);

		Install4j.doStartUpCheck();

		Icons.initialize();
		RB.initialize();

		// Start the GUI (either with or without an initial project)
		if (args.length == 1 && args[0] != null)
			new Flapjack(new File(args[0]));
		else
			new Flapjack(null);
	}

	Flapjack(final File initialProject)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("TextArea.font", UIManager.get("TextField.font"));

			// Use the office look for Windows (but not for Vista)
			if (SystemUtils.isWindows() && !SystemUtils.isWindowsVista())
			{
				UIManager.setLookAndFeel("org.fife.plaf.Office2003.Office2003LookAndFeel");

				UIManager.put("OptionPane.errorIcon", Icons.WINERROR);
				UIManager.put("OptionPane.informationIcon", Icons.WININFORMATION);
				UIManager.put("OptionPane.warningIcon", Icons.WINWARNING);
				UIManager.put("OptionPane.questionIcon", Icons.WINQUESTION);
			}

			// Keep Apple happy...
			else if (SystemUtils.isMacOS())
				handleOSXStupidities();

//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch (Exception e) {}

		Thread.setDefaultUncaughtExceptionHandler(new ErrorDialog());

		winMain = new WinMain();

		winMain.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				if (winMain.okToExit() == false)
					return;

				shutdown();
			}

			public void windowOpened(WindowEvent e)
			{
				// Do we want to open an initial project?
				if (initialProject != null)
					winMain.mFile.fileOpen(initialProject);
			}

			public void windowIconified(WindowEvent e) {
				WinMainMenuBar.mWndFlapjack.setSelected(false);
			}

			public void windowDeiconified(WindowEvent e) {
				WinMainMenuBar.mWndFlapjack.setSelected(true);
			}
		});

		MsgBox.initialize(winMain, "Flapjack");
		TaskDialog.initialize(winMain, "Flapjack");

		winMain.setVisible(true);
	}

	private void shutdown()
	{
		Prefs.isFirstRun = false;
		prefs.savePreferences(prefsFile, Prefs.class);

		System.exit(0);
	}


	// --------------------------------------------------
	// Methods required for better native support on OS X

	private void handleOSXStupidities()
	{
		try
		{
			// Register handlers to deal with the System menu about/quit options
			OSXAdapter.setPreferencesHandler(this,
				getClass().getDeclaredMethod("osxPreferences", (Class[])null));
			OSXAdapter.setAboutHandler(this,
				getClass().getDeclaredMethod("osxAbout", (Class[])null));
			OSXAdapter.setQuitHandler(this,
				getClass().getDeclaredMethod("osxShutdown", (Class[])null));

			// Dock the menu bar at the top of the screen
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		catch (Exception e) {}
	}

	/** "Preferences" on the OS X system menu. */
	public void osxPreferences()
	{
		winMain.mHelp.helpPrefs();
	}

	/** "About Flapjack" on the OS X system menu. */
	public void osxAbout()
	{
		winMain.mHelp.helpAbout();
	}

	/** "Quit Flapjack" on the OS X system menu. */
	public boolean osxShutdown()
	{
		if (winMain.okToExit() == false)
			return false;

		shutdown();
		return true;
	}

	static void osxMinimize()
	{
		winMain.setExtendedState(Frame.ICONIFIED);
	}

	static void osxZoom()
	{
		if (winMain.getExtendedState() == Frame.NORMAL)
			winMain.setExtendedState(Frame.MAXIMIZED_BOTH);
		else
			winMain.setExtendedState(Frame.NORMAL);
	}

	static void osxFlapjack()
	{
		if (Prefs.guiWinMainMaximized)
			winMain.setExtendedState(Frame.MAXIMIZED_BOTH);
		else
			winMain.setExtendedState(Frame.NORMAL);

		WinMainMenuBar.mWndFlapjack.setSelected(true);
	}
}