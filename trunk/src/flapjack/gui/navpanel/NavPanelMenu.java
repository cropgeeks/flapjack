package flapjack.gui.navpanel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import flapjack.gui.*;

public class NavPanelMenu extends MouseInputAdapter
{
	private JTree tree;

	private JPopupMenu menu = new JPopupMenu();
	private int menuShortcut;

	private JMenuItem dataRenameDataSet;
	private JMenuItem dataDeleteDataSet;
	private JMenuItem vizNewView;
	private JMenuItem vizRenameView;
	private JMenuItem vizDeleteView;

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
		vizNewView = WinMainMenuBar.getItem(Actions.vizNewView, "gui.Actions.vizNewView", 0, 0);
		vizRenameView = WinMainMenuBar.getItem(Actions.vizRenameView, "gui.Actions.vizRenameView", 0, 0);
		vizDeleteView = WinMainMenuBar.getItem(Actions.vizDeleteView, "gui.Actions.vizDeleteView", 0, 0);
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
			menu.add(vizNewView);
		}

		else if (node instanceof VisualizationNode)
		{
			menu.add(vizNewView);
			menu.add(vizRenameView);
			menu.add(vizDeleteView);
		}

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