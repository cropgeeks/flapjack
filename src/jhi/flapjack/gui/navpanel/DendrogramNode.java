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

	private Dendrogram dendrogram;

	public DendrogramNode(GenotypePanel gPanel, Dendrogram dendrogram)
	{
		super(gPanel, dendrogram.getViewSet(), dendrogram.getTitle());

		this.dendrogram = dendrogram;

		panel = new DendrogramPanel(dendrogram);
	}

	public JPanel getPanel()
	{
		mapViewSet();

		return panel;
	}

	public Dendrogram getDendrogram()
		{ return dendrogram; }

	public void setDendrogram(Dendrogram dendrogram)
		{ this.dendrogram = dendrogram; }
}