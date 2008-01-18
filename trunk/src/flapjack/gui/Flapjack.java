package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class Flapjack
{
	private static File prefsFile = new File(System.getProperty("user.home"), "flapjack.xml");
	private static Prefs prefs = new Prefs();

	public static WinMain winMain;

	public static int DATASET = 1;

	public static void main(String[] args)
		throws Exception
	{
		if (args.length == 1 && args[0].equals("de"))
			RB.locale = Locale.GERMAN;
		else if (args.length == 1 && args[0].equals("pirate"))
			RB.locale = new Locale("en", "GB", "Pirate");

		else if (args.length > 1)
		{
			DATASET = Integer.parseInt(args[0]);
			flapjack.gui.visualization.GenotypePanel.mapIndex = Integer.parseInt(args[1]) - 1;
		}

		prefs.loadPreferences(prefsFile, Prefs.class);

		Icons.initialize();
		RB.initialize();

		new Flapjack();
	}

	Flapjack()
	{
		try
		{
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			UIManager.setLookAndFeel("org.fife.plaf.Office2003.Office2003LookAndFeel");
		}
		catch (Exception e) {}

		winMain = new WinMain();

		winMain.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				shutdown();
			}

			public void windowOpened(WindowEvent e)
			{
			}
		});

		winMain.setVisible(true);
	}

	private void shutdown()
	{
		prefs.isFirstRun = false;
		prefs.savePreferences(prefsFile, Prefs.class);

		System.exit(0);
	}
}