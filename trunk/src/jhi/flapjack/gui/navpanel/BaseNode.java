// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.visualization.GenotypePanel;

/**
 * Abstract base class for all nodes displayed in the navigation tree. Ensures
 * that all nodes we add have support for updating the menus and the right-hand
 * side panel whenever the tree's selection changes.
 */
public abstract class BaseNode extends DefaultMutableTreeNode
{
	protected DataSet dataSet;

	BaseNode(DataSet dataSet)
	{
		this.dataSet = dataSet;
	}

	public abstract void setActions();

	public abstract JComponent getPanel();

	public DataSet getDataSet()
		{ return dataSet; }
}

// Base class for all nodes that hang directly below a VisualizationNode (i.e.
// the main graphical view). This class' sole purpose is to ensure that the
// GenotypePanel (that renders the view) has had the GTViewSet associated
// with this child node correctly set.
abstract class VisualizationChildNode extends BaseNode
{
	protected GenotypePanel gPanel;
	protected GTViewSet viewSet;

	VisualizationChildNode(GenotypePanel gPanel, GTViewSet viewSet)
	{
		super(viewSet.getDataSet());

		this.gPanel = gPanel;
		this.viewSet = viewSet;
	}

	protected void mapViewSet()
	{
		gPanel.setViewSet(viewSet);
	}
}