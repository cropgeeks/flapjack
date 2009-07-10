package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.lang.management.*;
import java.util.*;
import javax.swing.*;

import scri.commons.gui.*;

public class WinMainStatusBar extends JPanel
{
	private JLabel tipsLabel, helpLabel;
	private Vector<String> helpHints = new Vector<String>();

	private static AnimateThread animateThread;
	private static JLabel renderIcon;
	private JLabel threadLabel;

	private int cores = Runtime.getRuntime().availableProcessors();
	private ThreadMXBean threads = ManagementFactory.getThreadMXBean();

	WinMainStatusBar()
	{
		// Scan the properties file looking for tip strings to add
		for (int i = 1; i < 1000; i++)
		{
			if (RB.exists("gui.StatusBar.help" + i) == false)
				break;

			// Format them based on shortcuts for OS X or Windows/Linux
			if (SystemUtils.isMacOS())
				helpHints.add(RB.format(
					"gui.StatusBar.help" + i, RB.getString("gui.StatusBar.cmnd")));
			else
				helpHints.add(RB.format(
					"gui.StatusBar.help" + i, RB.getString("gui.StatusBar.ctrl")));
		}

		tipsLabel = new JLabel(RB.getString("gui.StatusBar.helpText"));
		helpLabel = new JLabel();
		JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
		helpPanel.add(tipsLabel);
		helpPanel.add(helpLabel);

		threadLabel = new JLabel();

		renderIcon = new JLabel();
		setRenderState(0);

		JPanel renderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
		renderPanel.add(threadLabel);
		renderPanel.add(renderIcon);

		Color lineColor = new JMenuBar().getBackground();
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(1, 0, 0, 0, lineColor),
			BorderFactory.createEmptyBorder(1, 2, 2, 2)));

		add(helpPanel, BorderLayout.WEST);
		add(renderPanel, BorderLayout.EAST);


//		setVisible(Prefs.gui_statusbar_visible);

		// Start the timer for the tips animation
		javax.swing.Timer tipsTimer = new javax.swing.Timer(30000,
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new TipsThread().start();
		}});

		tipsTimer.setInitialDelay(0);
		tipsTimer.start();


		// Start the timer for thread monitoring
		javax.swing.Timer threadsTimer = new javax.swing.Timer(500,
			new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					int current = threads.getThreadCount();
					threadLabel.setText("Threads: " + current + " Cores: " + cores);
				}
		});

		threadsTimer.setInitialDelay(0);
		threadsTimer.start();
	}

	public static void setRenderState(int state)
	{
		if (animateThread != null)
			animateThread.interrupt();

		// TODO: decide on fate for this icon (and translate the tooltips)
		switch (state)
		{
			case 0:
				renderIcon.setIcon(Icons.getIcon("GREYBLOB"));
				renderIcon.setToolTipText(RB.getString("gui.WinMainStatusBar.tip0"));
				break;
			case 1:
				animateThread = new AnimateThread();
				renderIcon.setToolTipText(RB.getString("gui.WinMainStatusBar.tip1"));
				break;
			case 2:
				renderIcon.setIcon(Icons.getIcon("BLUEBLOB"));
				renderIcon.setToolTipText(RB.getString("gui.WinMainStatusBar.tip2"));
				break;
			case 3:
				renderIcon.setIcon(Icons.getIcon("REDBLOB"));
				renderIcon.setToolTipText(RB.getString("gui.WinMainStatusBar.tip3"));
				break;
			case 4:
				renderIcon.setIcon(Icons.getIcon("REDBLOB"));
				renderIcon.setToolTipText(RB.getString("gui.WinMainStatusBar.tip4"));
				break;
		}
	}

	int bgColor = new JPanel().getBackground().getRed();

	private class TipsThread extends Thread
	{
		public void run()
		{
			float step = (bgColor / 15f);
			float fontColor = 0;

			// Fade from black to bgColor
			for (int i = 0; i < 16 || fontColor < bgColor; i++)
			{
				int c = (int) fontColor;
				helpLabel.setForeground(new Color(c, c, c));

				fontColor += step;

				try { Thread.sleep(100); }
				catch (Exception ex) {}
			}

			// Then wait for a second
			try { Thread.sleep(1000); }
			catch (Exception ex) {}

			// Before picking a new help string...
			int index = new Random().nextInt(helpHints.size());
			helpLabel.setText(helpHints.get(index));
			fontColor = bgColor;

			// ...and then fadding the font back to black
			for (int i = 0; i < 16 || fontColor > 0f; i++)
			{
				int c = (int) fontColor;
				helpLabel.setForeground(new Color(c, c, c));

				fontColor -= step;

				try { Thread.sleep(100); }
				catch (Exception ex) {}
			}
		}
	}

	private static class AnimateThread extends Thread
	{
		AnimateThread() { start(); }

		public void run()
		{
			try
			{
				while (true)
				{
					renderIcon.setIcon(Icons.getIcon("BLUEBLOB"));
					Thread.sleep(750);
					renderIcon.setIcon(Icons.getIcon("GREYBLOB"));
					Thread.sleep(750);
				}
			}
			catch (InterruptedException e) {}
		}
	}
}