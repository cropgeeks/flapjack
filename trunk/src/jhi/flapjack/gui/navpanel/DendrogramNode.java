// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import javax.swing.*;

import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.dendrogram.*;
import jhi.flapjack.gui.visualization.*;

public class DendrogramNode extends VisualizationChildNode
{
	private DendrogramPanel panel;
	private String title;

	public DendrogramNode(GenotypePanel gPanel, Dendrogram dendrogram)
	{
		super(gPanel, dendrogram.getViewSet());

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
		mapViewSet();

		return panel;
	}
}