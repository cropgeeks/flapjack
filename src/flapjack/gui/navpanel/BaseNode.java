package flapjack.gui.navpanel;

import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;

import flapjack.data.*;

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

	public abstract void setMenus();

	public abstract JComponent getPanel();

	public DataSet getDataSet()
		{ return dataSet; }
}