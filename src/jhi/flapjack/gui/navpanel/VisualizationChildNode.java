// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import jhi.flapjack.data.GTViewSet;
import jhi.flapjack.gui.Actions;
import jhi.flapjack.gui.visualization.GenotypePanel;

// Base class for all nodes that hang directly below a VisualizationNode (i.e.
// the main graphical view). This class' sole purpose is to ensure that the
// GenotypePanel (that renders the view) has had the GTViewSet associated
// with this child node correctly set.
public abstract class VisualizationChildNode extends BaseNode
{
	protected GenotypePanel gPanel;
	protected GTViewSet viewSet;

	protected String name;

	VisualizationChildNode(GenotypePanel gPanel, GTViewSet viewSet, String name)
	{
		super(viewSet.getDataSet());

		this.gPanel = gPanel;
		this.viewSet = viewSet;
		this.name = name;
	}

	protected void mapViewSet()
	{
		gPanel.setViewSet(viewSet);
	}

	public String toString()
	{
		return name;
	}

	public void setActions()
	{
		Actions.viewRenameView.setEnabled(true);
	}

	public GTViewSet getViewSet()
		{ return viewSet; }

	public void setViewSet(GTViewSet viewSet)
		{ this.viewSet = viewSet; }

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }
}