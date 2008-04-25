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

	public NavPanelMenu(JTree tree)
	{
		this.tree = tree;

		// Returns value for "CTRL" under most OSs, and the "apple" key for OS X
		menuShortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		createItems();
	}

	private void createItems()
	{
		dataRenameDataSet = WinMainMenuBar.getItem(Actions.dataRenameDataSet, KeyEvent.VK_N, 0, 0);
		dataDeleteDataSet = WinMainMenuBar.getItem(Actions.dataDeleteDataSet, KeyEvent.VK_R, 0, 0);

//		mLock = WinMainMenuBar.getCheckedItem(aLock, KeyEvent.VK_L, 0, 0, canvas.locked);
//		mShowGenotypes = WinMainMenuBar.getCheckedItem(Actions.vizOverlayGenotypes, KeyEvent.VK_O, KeyEvent.VK_G, menuShortcut, Prefs.visShowGenotypes);
//		mColorRandom = WinMainMenuBar.getItem(Actions.vizColorRandom, KeyEvent.VK_R, 0, 0);
//		mColorNucleotide = WinMainMenuBar.getItem(Actions.vizColorNucleotide, KeyEvent.VK_N, 0, 0);
	}

	void handlePopup(MouseEvent e)
	{
		// Check to see exactly what was clicked on
		TreePath path = tree.getPathForLocation(e.getX(), e.getY());

		if (path == null)
			return;
		tree.setSelectionPath(path);

		BaseNode node = (BaseNode) tree.getLastSelectedPathComponent();

		System.out.println(node);

		menu = new JPopupMenu();

		if (node instanceof DataSetNode)
		{
			menu.add(dataRenameDataSet);
			menu.add(dataDeleteDataSet);
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