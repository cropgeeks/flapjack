package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class WinMainStatusBar extends JPanel
{
	private static AnimateThread animateThread;
	private static JLabel renderIcon;

	WinMainStatusBar()
	{
		renderIcon = new JLabel();
		setRenderState(0);

		JPanel renderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
		renderPanel.add(renderIcon);

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(1, 2, 2, 2));

		add(new LinePanel(), BorderLayout.NORTH);
		add(renderPanel, BorderLayout.EAST);

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