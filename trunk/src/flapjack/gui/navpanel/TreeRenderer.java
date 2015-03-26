// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.navpanel;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

import flapjack.gui.*;

import scri.commons.gui.*;

public class TreeRenderer extends JLabel implements TreeCellRenderer
{
	private static Color bColor = UIManager.getColor("Tree.selectionBackground");

	private boolean selected = false;

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean isExpanded, boolean leaf, int row,
			boolean hasFocus)
	{
		setText(value.toString());

		if (value instanceof DataSetNode)
			setIcon(isExpanded ? Icons.getIcon("FOLDEROPEN", Prefs.uiScale) : Icons.getIcon("FOLDER", Prefs.uiScale));
		else if (value instanceof VisualizationNode)
			setIcon(Icons.getIcon("VISUALIZATION", Prefs.uiScale));
		else if (value instanceof TraitsNode)
			setIcon(Icons.getIcon("TRAITS", Prefs.uiScale));
		else if (value instanceof BookmarkNode)
			setIcon(Icons.getIcon("BOOKMARK", Prefs.uiScale));
		else if (value instanceof SimMatrixNode)
			setIcon(Icons.getIcon(((SimMatrixNode)value).getIconName(), Prefs.uiScale));
		else if (value instanceof DendrogramNode)
			setIcon(Icons.getIcon("DENDROGRAM", Prefs.uiScale));

		this.selected = selected;

		if (selected)
			setForeground((Color) UIManager.get("Tree.selectionForeground"));
		else
			setForeground((Color) UIManager.get("Tree.foreground"));

		return this;
	}

	@Override
	public void paintComponent(Graphics g)
	{
		Icon icon = getIcon();

		int offset = 0;
		if (icon != null && getText() != null)
			offset = (icon.getIconWidth() + getIconTextGap());

		if (selected)
		{
			g.setColor(bColor);
			g.fillRect(offset, 0, 500 - offset, getHeight() - 1);
		}

		super.paintComponent(g);
	}
}