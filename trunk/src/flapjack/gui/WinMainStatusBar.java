package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.lang.management.*;
import java.util.*;
import javax.swing.*;

import scri.commons.gui.*;

public class WinMainStatusBar extends JPanel implements Runnable
{
	private JLabel tipsLabel, helpLabel;
	private Vector<String> helpHints = new Vector<String>();

	private static AnimateThread animateThread;
	private static JLabel renderIcon;
	private JLabel threadLabel;

	WinMainStatusBar()
	{
		try
		{
			String ctrl = RB.getString("gui.StatusBar.ctrl");
			String cmd  = RB.getString("gui.StatusBar.cmd");

			int count = Integer.parseInt(RB.getString("gui.StatusBar.helpCount"));

			for (int i = 1; i <= count; i++)
			{
				String txt = ("gui.StatusBar.help" + i);
				if (SystemUtils.isMacOS())
					helpHints.add(RB.format(txt, cmd));
				else
					helpHints.add(RB.format(txt, ctrl));
			}
		}
		catch (Exception e) { System.out.println(e); }

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

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(1, 2, 2, 2));

		add(helpPanel, BorderLayout.WEST);
		add(new LinePanel(), BorderLayout.NORTH);
		add(renderPanel, BorderLayout.EAST);

		new Thread(this).start();
		new ThreadMonitor().start();

//		setVisible(Prefs.gui_statusbar_visible);
	}

	public static void setRenderState(int state)
	{
		if (animateThread != null)
			animateThread.interrupt();

		// TODO: decide on fate for this icon (and translate the tooltips)
		switch (state)
		{
			case 0:
				renderIcon.setIcon(Icons.GREYBLOB);
				renderIcon.setToolTipText(RB.getString("gui.WinMainStatusBar.tip0"));
				break;
			case 1:
				animateThread = new AnimateThread();
				renderIcon.setToolTipText(RB.getString("gui.WinMainStatusBar.tip1"));
				break;
			case 2:
				renderIcon.setIcon(Icons.BLUEBLOB);
				renderIcon.setToolTipText(RB.getString("gui.WinMainStatusBar.tip2"));
				break;
			case 3:
				renderIcon.setIcon(Icons.REDBLOB);
				renderIcon.setToolTipText(RB.getString("gui.WinMainStatusBar.tip3"));
				break;
			case 4:
				renderIcon.setIcon(Icons.REDBLOB);
				renderIcon.setToolTipText(RB.getString("gui.WinMainStatusBar.tip4"));
				break;
		}
	}

	int bgColor = new JPanel().getBackground().getRed();
	float fontColor;

	public void run()
	{
		Thread.currentThread().setName("StatusBar Tips");

		Random rnd = new Random();

		float step = (bgColor / 15f);

		while (true)
		{
			fontColor = bgColor;

			int index = rnd.nextInt(helpHints.size());
			helpLabel.setText(helpHints.get(index));

			// Fade from bgColor to black
			for (int i = 0; i < 16 || fontColor > 0f; i++)
			{
				int c = (int) fontColor;
				helpLabel.setForeground(new Color(c, c, c));

				fontColor -= step;

				try { Thread.sleep(100); }
				catch (Exception e) {}
			}

			try { Thread.sleep(25000); }
			catch (Exception e) {}

			fontColor = 0;

			// Fade from black to bgColor
			for (int i = 0; i < 16 || fontColor < bgColor; i++)
			{
				int c = (int) fontColor;
				helpLabel.setForeground(new Color(c, c, c));

				fontColor += step;

				try { Thread.sleep(100); }
				catch (Exception e) {}
			}

			try { Thread.sleep(1000); }
			catch (Exception e) {}
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
					renderIcon.setIcon(Icons.BLUEBLOB);
					Thread.sleep(750);
					renderIcon.setIcon(Icons.GREYBLOB);
					Thread.sleep(750);
				}
			}
			catch (InterruptedException e) {}
		}
	}

	private class ThreadMonitor extends Thread
	{
		public void run()
		{
			setPriority(Thread.MIN_PRIORITY);
			setName("Flapjack Thread Monitor");

			int cores = Runtime.getRuntime().availableProcessors();
			ThreadMXBean threads = ManagementFactory.getThreadMXBean();

			while (true)
			{
				int current = threads.getThreadCount();
				threadLabel.setText("Threads: " + current + " Cores: " + cores);

				try { Thread.sleep(500); }
				catch (Exception e) {}
			}
		}
	}

	private static class LinePanel extends JPanel
	{
		LinePanel()
		{
			setBackground(new JMenuBar().getBackground());
		}

		public Dimension getPreferredSize()
			{ return new Dimension(20, 1); }
	}
}