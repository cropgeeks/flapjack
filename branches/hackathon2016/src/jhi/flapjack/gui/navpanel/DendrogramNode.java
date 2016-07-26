// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import java.awt.image.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.dendrogram.*;

public class DendrogramNode extends BaseNode
{
	private DendrogramPanel panel;
	private String title;

	public DendrogramNode(DataSet dataSet, Dendrogram dendrogram)
	{
		super(dataSet);

		panel = new DendrogramPanel(dendrogram);
		title = dendrogram.getTitle();
	}

	public String toString()
	{
		return title;
	}

	public void setActions()
	{

	}

	public JPanel getPanel()
	{
		return panel;
	}
}