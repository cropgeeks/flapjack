// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.gui.*;

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
		SwingUtils.positionWindow(
			this, null, Prefs.guiOverviewX, Prefs.guiOverviewY);

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
				WinMainMenuBar.mViewOverview.setSelected(true);
			}

			public void componentHidden(ComponentEvent e)
			{
				WinMainMenuBar.mViewOverview.setSelected(false);
			}
		});
	}
}