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

		new flapjack.gui.visualization.colors.WebsafePalette();

		Prefs.setDefaults();
		prefs.loadPreferences(prefsFile, Prefs.class);

		Icons.initialize("/res/icons", ".png");
		RB.initialize(Prefs.localeText, "res.text.flapjack");

		Install4j.doStartUpCheck();

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

			// The default font size is usually stupidly large
			UIManager.put("TextArea.font", UIManager.get("TextField.font"));
			// I don't like focus highlights on tabs (interferes with icons)
			UIManager.put("TabbedPane.focus", new java.awt.Color(0, 0, 0, 0));

			UIManager.put("fjDialogBG", Color.white);
			UIManager.put("Slider.background", Color.white);
			UIManager.put("CheckBox.background", Color.white);
			UIManager.put("RadioButton.background", Color.white);

			// Use the office look for Windows (but not for Vista)
			if (SystemUtils.isWindows() && !SystemUtils.isWindowsVista())
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

				if (Install4j.displayUpdate)
					FlapjackUtils.visitURL("http://bioinf.scri.ac.uk/flapjack/help/whats-new.shtml");
			}

			public void windowIconified(WindowEvent e) {
				WinMainMenuBar.mWndFlapjack.setSelected(false);
			}

			public void windowDeiconified(WindowEvent e) {
				WinMainMenuBar.mWndFlapjack.setSelected(true);
			}
		});

		TaskDialog.initialize(winMain, "Flapjack");

//		new SCRIDialog();
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
