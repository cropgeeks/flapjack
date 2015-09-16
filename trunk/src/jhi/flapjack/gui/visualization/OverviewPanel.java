// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import javax.swing.*;

import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class OverviewPanel extends JPanel
{
	public OverviewPanel()
	{
		setBackground(Prefs.visColorBackground);
		setLayout(new BorderLayout());
	}

	void removeCanvas()
	{
		removeAll();
		validate();
	}

	void addCanvas(OverviewCanvas canvas)
	{
		removeCanvas();

		add(new TitlePanel(
			RB.getString("gui.visualization.OverviewDialog.title")),
			BorderLayout.NORTH);

		add(canvas);
		validate();
	}
}