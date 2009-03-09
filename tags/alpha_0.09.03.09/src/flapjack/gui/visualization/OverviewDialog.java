package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

public class OverviewDialog extends JDialog
{
	public OverviewDialog(WinMain winMain, OverviewCanvas canvas)
	{
		super(winMain, RB.getString("gui.visualization.OverviewDialog.title"), false);

		setLayout(new BorderLayout());
		add(canvas);

		setSize(Prefs.guiOverviewWidth, Prefs.guiOverviewHeight);

		// Position on screen...
		if (Prefs.guiOverviewX == -9999 && Prefs.guiOverviewY == -9999)
			setLocationRelativeTo(winMain);
		else
			setLocation(Prefs.guiOverviewX, Prefs.guiOverviewY);

		addListeners();
	}

	private void addListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				Prefs.guiOverviewWidth = getSize().width;
				Prefs.guiOverviewHeight = getSize().height;
				Prefs.guiOverviewX = getLocation().x;
				Prefs.guiOverviewY = getLocation().y;
			}

			public void componentMoved(ComponentEvent e)
			{
				Prefs.guiOverviewX = getLocation().x;
				Prefs.guiOverviewY = getLocation().y;
			}

			public void componentShown(ComponentEvent e)
			{
				WinMainMenuBar.mVizOverview.setSelected(true);
			}

			public void componentHidden(ComponentEvent e)
			{
				WinMainMenuBar.mVizOverview.setSelected(false);
			}
		});
	}
}