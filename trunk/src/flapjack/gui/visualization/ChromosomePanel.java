// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class ChromosomePanel extends JPanel
{
	private GTViewSet viewSet;

	private JScrollPane sp;
	private ChromosomeCanvas canvas;

	public ChromosomePanel()
	{
		canvas = new ChromosomeCanvas();
		sp = new JScrollPane(canvas);

		setLayout(new BorderLayout());
		add(sp);
	}

	public void setViewSet(GTViewSet viewSet)
	{
		this.viewSet = viewSet;

		canvas.setView(viewSet);
	}
}