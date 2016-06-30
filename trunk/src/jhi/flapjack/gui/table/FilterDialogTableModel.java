// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

class FilterDialogTableModel extends AbstractTableModel
{
	// An array of every possible column the user can sort on
	private SortFilterColumn[] data;
	// And a list of the ones they actually *will* sort on
	private ArrayList<SortFilterColumn> rows;

	private String[] columnNames;

	FilterDialogTableModel(SortFilterColumn[] data, SortFilterColumn[] lastUsed)
	{
		this.data = data;

		columnNames = new String[] {
			RB.getString("gui.table.FilterDialog.col1"),
			RB.getString("gui.table.FilterDialog.col2"),
			RB.getString("gui.table.FilterDialog.col3")
		};

		// Initialize the data we'll be showing
		rows = new ArrayList<SortFilterColumn>();

		// Shall we add fresh columns, or the ones from last time?
		SortFilterColumn[] colsToAdd = data;
		if (lastUsed != null) colsToAdd = lastUsed;

		for (SortFilterColumn entry: colsToAdd)
			rows.add(entry.cloneMe());
	}

	SortFilterColumn[] getResults()
	{
		return rows.toArray(new SortFilterColumn[] {});
	}

	@Override
	public String getColumnName(int col)
	{
		return columnNames[col];
	}

	@Override
	public int getRowCount()
	{
		return rows.size();
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int row, int col)
	{
		if (col == 0)
			return rows.get(row).name;

		else if (col == 1)
			return rows.get(row).filter;

		else
			return rows.get(row).value;
	}

	@Override
	public Class getColumnClass(int col)
	{
		if (col == 0)
			return String.class;

		else if (col == 1)
			return SortFilterColumn.Filter.class;

		else
			return String.class;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		// We don't want to allow editing of the first column
		return col > 0;
	}

	// Creates a list of possible filters that can be selected from
	JComboBox getFilterComboBox()
	{
		JComboBox<SortFilterColumn.Filter> combo = new JComboBox<>();

		for (SortFilterColumn.Filter filter: SortFilterColumn.getFilters())
			combo.addItem(filter);

		return combo;
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
		SortFilterColumn entry = rows.get(row);

		if (col == 1)
		{
			entry.filter.type = ((SortFilterColumn.Filter)value).type;
		}
		else if (col == 2)
			entry.value = (String) value;

		fireTableCellUpdated(row, col);
	}
}