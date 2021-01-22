// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui;

import java.awt.*;
import java.awt.desktop.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

import jhi.flapjack.gui.dialog.*;
import jhi.flapjack.io.*;

import scri.commons.io.*;
import scri.commons.gui.*;

public class Flapjack implements OpenFilesHandler
{
	private static File prefsFile = getPrefsFile();
	private static Prefs prefs = new Prefs();

	private static String[] initialProject;

	public static WinMain winMain;

	public static void main(String[] args)
		throws Exception
	{
		// Log some basic version/os information
		System.out.println("Flapjack " + Install4j.getVersion(Flapjack.class) + " on "
			+ System.getProperty("os.name")	+ " (" + System.getProperty("os.arch") + ")");

		new jhi.flapjack.gui.visualization.colors.WebsafePalette();

		Prefs.setDefaults();
		prefs.loadPreferences(prefsFile, Prefs.class);
		prefs.savePreferences(prefsFile, Prefs.class);

		Icons.initialize("/res/icons", ".png");
		RB.initialize(Prefs.localeText, "res.text.flapjack");
		if (Prefs.guiDecimalEnglish)
			Locale.setDefault(Locale.UK);

		setProxy();

		// Initialize JDBC->SQLite driver
		FlapjackUtils.initialiseSqlite();

		// Start the GUI (either with or without an initial project)
		if (args.length >= 1)
			initialProject = args;

		install4j();

		new Flapjack();
	}

	// Sets up the install4j environment to check for updates
	private static void install4j()
	{
		Install4j i4j = new Install4j("9131-7045-2417-5931", "418");

		i4j.setUser(Prefs.guiUpdateSchedule, Prefs.flapjackID, Prefs.rating);
		i4j.setURLs("http://bioinf.hutton.ac.uk/flapjack/installers/updates.xml",
				    "http://bioinf.hutton.ac.uk/flapjack/logs/flapjack.pl");

		i4j.doStartUpCheck(Flapjack.class);
	}

	Flapjack()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			UIManager.put("Table.selectionBackground", ((Color)UIManager.get("Table.background")).darker());

			// The default font size is usually stupidly large
			UIManager.put("TextArea.font", UIManager.get("TextField.font"));
			// I don't like focus highlights on tabs (interferes with icons)
			UIManager.put("TabbedPane.focus", new java.awt.Color(0, 0, 0, 0));

			UIManager.put("fjDialogBG", Color.white);
			UIManager.put("Slider.background", Color.white);
			UIManager.put("CheckBox.background", Color.white);
			UIManager.put("RadioButton.background", Color.white);

			UIManager.put("CheckBox.border", BorderFactory.createEmptyBorder(0,0,0,0));

			// Use the office look for Windows XP (but not for Vista/7/8)
			if (SystemUtils.isWindowsXP())
			{
				// Gives XP the same (nicer) grey background that Vista uses
				UIManager.put("Panel.background", new Color(240, 240, 240));
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
					winMain.mFile.handleDragDrop(initialProject);
			}

			public void windowIconified(WindowEvent e) {
				WinMainMenuBar.mWndFlapjack.setSelected(false);
			}

			public void windowDeiconified(WindowEvent e) {
				WinMainMenuBar.mWndFlapjack.setSelected(true);
			}
		});

		// Basic TaskDialog init
		TaskDialog.initialize(winMain, "Flapjack");
		// And then the additional stuff for error popups with logs, etc
		TaskDialog.initialize(FlapjackUtils.getLogFile(),
			RB.getString("gui.text.log"),
			RB.getString("gui.text.close"));

		winMain.setVisible(true);
	}

	public static void setProxy()
	{
		java.util.Properties p = System.getProperties();

		if (Prefs.proxyUse)
		{
			if (Prefs.proxySocks == false)
			{
				p.setProperty("http.proxyHost", Prefs.proxyAddress);
				p.setProperty("https.proxyHost", Prefs.proxyAddress);
				p.setProperty("http.proxyPort", "" + Prefs.proxyPort);
			}
			else
			{
				p.setProperty("socksProxyHost", Prefs.proxyAddress);
				p.setProperty("socksProxyPort", "" + Prefs.proxyPort);
			}

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
			p.remove("socksProxyHost");
			p.remove("socksProxyPort");
		}
	}

	private void shutdown()
	{
		Prefs.isFirstRun = false;
		prefs.savePreferences(prefsFile, Prefs.class);

		// Clear the cache of any files that might still exist - most stuff
		// should be gone from deleteOnExit() calls, but just in case...
		FileUtils.emptyDirectory(FlapjackUtils.getCacheDir(), true);

		System.exit(0);
	}

	public static File getPrefsFile()
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
		Desktop desktop = Desktop.getDesktop();

		// Register handlers to deal with the System menu about/quit options
        desktop.setAboutHandler(e -> osxAbout());
        desktop.setPreferencesHandler(e -> osxPreferences());
        desktop.setQuitHandler((e,r) -> osxShutdown());
		desktop.setOpenFileHandler(this);
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

	/** Deal with desktop-double clicking of registered files */
	public void openFiles(OpenFilesEvent e)
	{
		String[] paths = new String[e.getFiles().size()];
		for (int i = 0; i < paths.length; i++)
			paths[i] = e.getFiles().get(i).toString();

		// If Flapjack is already open, then open the file straight away
		if (winMain != null && winMain.isVisible())
		{
			// TODO: If we have project modified checks, do them here too
			winMain.mFile.fileOpen(new FlapjackFile(paths[0]));
		}

		// Otherwise, mark it for opening once Flapjack is ready
		else
			initialProject = new String[] { paths[0] };
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