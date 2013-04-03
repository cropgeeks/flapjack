// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

import flapjack.gui.dialog.*;
import flapjack.io.*;

import scri.commons.file.*;
import scri.commons.gui.*;

import apple.dts.samplecode.osxadapter.*;

public class Flapjack
{
	private static File prefsFile = getPrefsFile();
	private static Prefs prefs = new Prefs();

	private static FlapjackFile initialProject;

	public static WinMain winMain;

	public static void main(String[] args)
		throws Exception
	{
		// OS X: This has to be set before anything else
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Flapjack");

		new flapjack.gui.visualization.colors.WebsafePalette();

		Prefs.setDefaults();
		prefs.loadPreferences(prefsFile, Prefs.class);
		prefs.savePreferences(prefsFile, Prefs.class);

		Icons.initialize("/res/icons", ".png");
		RB.initialize(Prefs.localeText, "res.text.flapjack");
		if (Prefs.guiDecimalEnglish)
			Locale.setDefault(Locale.UK);

		setProxy();

		Install4j.doStartUpCheck();

		// Start the GUI (either with or without an initial project)
		if (args.length == 1 && args[0] != null)
			initialProject = new FlapjackFile(args[0]);

		new Flapjack();
	}

	Flapjack()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			// The default font size is usually stupidly large
			UIManager.put("TextArea.font", UIManager.get("TextField.font"));
			// I don't like focus highlights on tabs (interferes with icons)
			UIManager.put("TabbedPane.focus", new java.awt.Color(0, 0, 0, 0));

			UIManager.put("fjDialogBG", Color.white);
			UIManager.put("Slider.background", Color.white);
			UIManager.put("CheckBox.background", Color.white);
			UIManager.put("RadioButton.background", Color.white);

			// Use the office look for Windows (but not for Vista or 7)
			if (SystemUtils.isWindows() && !SystemUtils.isWindowsVista() && !SystemUtils.isWindows7())
			{
				UIManager.setLookAndFeel("org.fife.plaf.Office2003.Office2003LookAndFeel");

				// Gives XP the same (nicer) grey background that Vista uses
				UIManager.put("Panel.background", new Color(240, 240, 240));

				// Overrides the JOptionPane dialogs with better icons
				UIManager.put("OptionPane.errorIcon", Icons.getIcon("WINERROR"));
				UIManager.put("OptionPane.informationIcon", Icons.getIcon("WININFORMATION"));
				UIManager.put("OptionPane.warningIcon", Icons.getIcon("WINWARNING"));
				UIManager.put("OptionPane.questionIcon", Icons.getIcon("WINQUESTION"));
			}

			// Keep Apple happy...
			else if (SystemUtils.isMacOS())
				handleOSXStupidities();

//			for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels())
//				if (laf.getName().equals("Nimbus"))
//					UIManager.setLookAndFeel(laf.getClassName());
		}
		catch (Exception e) {}

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
				long thirtyDays = 2592000000L; // This is 30 days
				long thirtyDaysAgo = System.currentTimeMillis() - thirtyDays;

				if (!Prefs.isFirstRun &&
					Long.valueOf(Prefs.visColorSeed) < thirtyDaysAgo)
				{
					Prefs.visColorSeed = "" + System.currentTimeMillis();
					// Force a re-save on prefs so that multiple Tablet launches
					// (without closing any of them) don't show the dialog every
					// time
					prefs.savePreferences(prefsFile, Prefs.class);
					new CitationDialog();
				}

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

		TaskDialog.initialize(winMain, "Flapjack");

		winMain.setVisible(true);
	}

	public static void setProxy()
	{
		java.util.Properties p = System.getProperties();

		if (Prefs.proxyUse)
		{
			p.setProperty("http.proxyHost", Prefs.proxyAddress);
			p.setProperty("http.proxyPort", "" + Prefs.proxyPort);

			Authenticator.setDefault(new Authenticator()
			{
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(Prefs.proxyUsername,
						Prefs.proxyPassword.toCharArray());
				}
			});
		}
		else
		{
			p.remove("http.proxyHost");
			p.remove("http.proxyPort");
		}
	}

	private void shutdown()
	{
		Prefs.isFirstRun = false;
		prefs.savePreferences(prefsFile, Prefs.class);

		System.exit(0);
	}

	private static File getPrefsFile()
	{
		// Ensure the .scri-bioinf folder exists
		File fldr = new File(System.getProperty("user.home"), ".scri-bioinf");
		fldr.mkdirs();

		// This is the file we really want
		File file = new File(fldr, "flapjack.xml");
		// So if it exists, just use it
		if (file.exists())
			return file;

		// If not, see if the "old" (pre 21/06/2010) file is available
		File old = new File(System.getProperty("user.home"), ".flapjack.xml");
		if (old.exists())
			try { FileUtils.copyFile(old, file, true); }
			catch (IOException e) {}

		return file;
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
			OSXAdapter.setFileHandler(this,
				getClass().getDeclaredMethod("osxOpen", new Class[] { String.class }));

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

	public void osxOpen(String path)
	{
		// If Tablet is already open, then open the file straight away
		if (winMain != null && winMain.isVisible())
		{
			// TODO: If we have project modified checks, do them here too
			winMain.mFile.fileOpen(new FlapjackFile(path));
		}

		// Otherwise, mark it for opening once Flapjack is ready
		else
			initialProject = new FlapjackFile(path);
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