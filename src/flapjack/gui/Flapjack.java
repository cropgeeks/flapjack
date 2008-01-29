package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import flapjack.io.ProjectSerializer;

import scri.commons.gui.*;

public class Flapjack
{
	private static File prefsFile = new File(System.getProperty("user.home"), "flapjack.xml");
	private static Prefs prefs = new Prefs();

	public static WinMain winMain;

	public static void main(String[] args)
		throws Exception
	{
		if (args.length == 1 && args[0].equals("de"))
			RB.locale = Locale.GERMAN;
		else if (args.length == 1 && args[0].equals("pirate"))
			RB.locale = new Locale("en", "GB", "Pirate");


		prefs.loadPreferences(prefsFile, Prefs.class);

		Icons.initialize();
		RB.initialize();

		new Flapjack();
	}

	Flapjack()
	{
		try
		{
			if (SystemUtils.isWindows())
			{
				UIManager.setLookAndFeel("org.fife.plaf.Office2003.Office2003LookAndFeel");

				UIManager.put("OptionPane.errorIcon", Icons.WINERROR);
				UIManager.put("OptionPane.informationIcon", Icons.WININFORMATION);
				UIManager.put("OptionPane.warningIcon", Icons.WINWARNING);
				UIManager.put("OptionPane.questionIcon", Icons.WINQUESTION);
			}
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
			}
		});

		MsgBox.initialize(winMain, "Flapjack");
		TaskDialog.initialize(winMain, "Flapjack");

		winMain.setVisible(true);
	}

	private void shutdown()
	{
		prefs.isFirstRun = false;
		prefs.savePreferences(prefsFile, Prefs.class);

		System.exit(0);
	}
}