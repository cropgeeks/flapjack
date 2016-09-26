// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import jhi.flapjack.gui.*;

public class NavPanelMenu extends MouseInputAdapter
{
	private JTree tree;

	private JPopupMenu menu = new JPopupMenu();
	private int menuShortcut;

	private JMenuItem dataRenameDataSet;
	private JMenuItem dataDeleteDataSet;
	private JMenuItem viewNewView;
	private JMenuItem viewRenameView;
	private JMenuItem viewDeleteView;
	private JMenuItem viewDeleteBookmark;

	public NavPanelMenu(JTree tree)
	{
		this.tree = tree;

		// Returns value for "CTRL" under most OSs, and the "apple" key for OS X
		menuShortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		createItems();
	}

	private void createItems()
	{
		dataRenameDataSet = WinMainMenuBar.getItem(Actions.dataRenameDataSet, "gui.Actions.dataRenameDataSet", 0, 0);
		dataDeleteDataSet = WinMainMenuBar.getItem(Actions.dataDeleteDataSet, "gui.Actions.dataDeleteDataSet", 0, 0);
		viewNewView = WinMainMenuBar.getItem(Actions.viewNewView, "gui.Actions.viewNewView", 0, 0);
		viewRenameView = WinMainMenuBar.getItem(Actions.viewRenameView, "gui.Actions.viewRenameView", 0, 0);
		viewDeleteView = WinMainMenuBar.getItem(Actions.viewDeleteView, "gui.Actions.viewDeleteView", 0, 0);
		viewDeleteBookmark = WinMainMenuBar.getItem(Actions.viewDeleteBookmark, "gui.Actions.viewDeleteBookmark", 0, 0);
	}

	void handlePopup(MouseEvent e)
	{
		// Check to see exactly what was clicked on
		TreePath path = tree.getPathForLocation(e.getX(), e.getY());

		if (path == null)
			return;
		tree.setSelectionPath(path);

		BaseNode node = (BaseNode) tree.getLastSelectedPathComponent();

		menu = new JPopupMenu();

		if (node instanceof DataSetNode)
		{
			menu.add(dataRenameDataSet);
			menu.add(dataDeleteDataSet);
			menu.addSeparator();
			menu.add(viewNewView);
		}

		else if (node instanceof VisualizationNode)
		{
			menu.add(viewNewView);
			menu.add(viewRenameView);
			menu.add(viewDeleteView);
		}

		else if (node instanceof VisualizationChildNode && !(node instanceof BookmarkNode))
			menu.add(viewRenameView);

		else if (node instanceof BookmarkNode)
			menu.add(viewDeleteBookmark);

		else
			return;

		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.isPopupTrigger())
			handlePopup(e);
	}

	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger())
			handlePopup(e);
	}
}