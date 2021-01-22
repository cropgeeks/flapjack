// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import javax.swing.*;
import javax.swing.tree.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.*;

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