// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;

public class ChromosomePanel extends JPanel
{
	private GTViewSet viewSet;

	private JScrollPane sp;
	private ChromosomeCanvas canvas;

	private ChromosomeCanvasKey key;

	public ChromosomePanel()
	{
		canvas = new ChromosomeCanvas();
		sp = new JScrollPane(canvas);
		sp.getVerticalScrollBar().setUnitIncrement(10);
		sp.getVerticalScrollBar().setBlockIncrement(10);

		key = new ChromosomeCanvasKey(canvas);
		canvas.setKey(key);

		setLayout(new BorderLayout());
		add(sp);
		add(key, BorderLayout.SOUTH);
	}

	public void setViewSet(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
		canvas.setView(viewSet);
	}
}