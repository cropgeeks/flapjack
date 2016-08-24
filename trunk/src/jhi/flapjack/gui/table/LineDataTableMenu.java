// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import javax.swing.*;

import scri.commons.gui.*;

public class LineDataTableMenu
{
	private LineDataTable table;

	LineDataTableMenu(LineDataTable table)
	{
		this.table = table;
	}

	public JPopupMenu getExportMenu()
	{
		JPopupMenu menu = new JPopupMenu();
		createExportMenu(menu);
		return menu;
	}

	private void createExportMenu(JComponent menu)
	{
		JMenuItem mExportAll = new JMenuItem("Export all lines...");
		mExportAll.addActionListener(e -> table.exportData(false));

		JMenuItem mExportSelected = new JMenuItem("Export selected lines...");
		mExportSelected.addActionListener(e -> table.exportData(true));

		menu.add(mExportAll);
		menu.add(mExportSelected);
	}

	public JPopupMenu getFilterMenu()
	{
		JPopupMenu menu = new JPopupMenu();
		createFilterMenu(menu);
		return menu;
	}

	private void createFilterMenu(JComponent menu)
	{
		JMenuItem mFilter = new JMenuItem("Filter...");
		mFilter.setIcon(Icons.getIcon("FILTER"));
		mFilter.addActionListener(e -> table.filterDialog());

		JMenuItem reset = new JMenuItem("Reset filters");
		reset.addActionListener(e -> table.resetFilters());

		menu.add(mFilter);
		menu.add(reset);
	}

	public JPopupMenu getSelectMenu()
	{
		JPopupMenu menu = new JPopupMenu();
		createSelectMenu(menu, false);
		return menu;
	}

	private void createSelectMenu(JComponent menu, boolean allOptions)
	{
		LineDataTableModel model = table.getLineDataTableModel();

		final JMenuItem mSelect = new JMenuItem();
		mSelect.setText("Auto select...");
		mSelect.setIcon(Icons.getIcon("AUTOSELECT"));
		mSelect.addActionListener(event -> table.selectDialog());

		final JMenuItem mSelectAll = new JMenuItem();
		mSelectAll.setText("Select all");
		mSelectAll.addActionListener(event -> model.setLineStates(true));

		final JMenuItem mSelectNone = new JMenuItem();
		mSelectNone.setText("Select none");
		mSelectNone.addActionListener(event -> model.setLineStates(false));

		final JMenuItem mSelectInvert = new JMenuItem();
		mSelectInvert.setIcon(Icons.getIcon("INVERT"));
		mSelectInvert.setText("Invert selection");
		mSelectInvert.addActionListener(event -> model.setLineStates(null));

		boolean enabled = table.getSelectionModel().getMinSelectionIndex() != -1;

		final JMenuItem mSelectHighlightedAll = new JMenuItem();
		mSelectHighlightedAll.setText("Select highlighted");
		mSelectHighlightedAll.addActionListener(event -> table.selectHighlighted(true));
		mSelectHighlightedAll.setEnabled(enabled);

		final JMenuItem mSelectHighlightedNone = new JMenuItem();
		mSelectHighlightedNone.setText("Deselect highlighted");
		mSelectHighlightedNone.addActionListener(event -> table.selectHighlighted(false));
		mSelectHighlightedNone.setEnabled(enabled);

		final JMenuItem mSelectHighlightedInvert = new JMenuItem();
		mSelectHighlightedInvert.setText("Invert highlighted");
		mSelectHighlightedInvert.addActionListener(event -> table.selectHighlighted(null));
		mSelectHighlightedInvert.setEnabled(enabled);

		menu.add(mSelect);
		if (menu instanceof JMenu) ((JMenu)menu).addSeparator();
		else ((JPopupMenu)menu).addSeparator();
		menu.add(mSelectAll);
		menu.add(mSelectNone);
		menu.add(mSelectInvert);
		if (allOptions)
		{
			if (menu instanceof JMenu) ((JMenu)menu).addSeparator();
			else ((JPopupMenu)menu).addSeparator();
			menu.add(mSelectHighlightedAll);
			menu.add(mSelectHighlightedNone);
			menu.add(mSelectHighlightedInvert);
		}
	}

	public JPopupMenu createPopupMenu()
	{
		JPopupMenu menu = new JPopupMenu();

		// Top level menus
		final JMenu menuFilter = new JMenu();
		menuFilter.setText("Filter");
		createFilterMenu(menuFilter);

		final JMenu menuExport = new JMenu();
		menuExport.setText(RB.getString("gui.mabc.MabcPanel.export"));
//		menuExport.setIcon(Icons.getIcon("EXPORTTRAITS"));
		createExportMenu(menuExport);

		final JMenu menuSelect = new JMenu();
		menuSelect.setText("Select");
		createSelectMenu(menuSelect, true);


		// And any other actual menu items
		final JMenuItem mCopy = new JMenuItem();
		mCopy.setText(RB.getString("gui.mabc.MabcPanel.copy"));
		mCopy.setIcon(Icons.getIcon("COPY"));
		mCopy.addActionListener(e -> table.copyTableToClipboard());

		final JMenuItem mSort = new JMenuItem();
		mSort.setText("Sort...");
		mSort.setIcon(Icons.getIcon("SORT"));
		mSort.addActionListener(e -> table.sortDialog());


		menu.add(menuSelect);
		menu.add(mCopy);
		menu.addSeparator();
		menu.add(menuFilter);
		menu.add(mSort);
		menu.add(menuExport);

		return menu;
	}
}