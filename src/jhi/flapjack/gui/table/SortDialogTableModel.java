// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

class SortDialogTableModel extends AbstractTableModel
{
	// An array of every possible column the user can sort on
	private SortFilterColumn[] data;
	// And a list of the ones they actually *will* sort on
	private ArrayList<SortFilterColumn> rows;

	private String[] columnNames;

	SortDialogTableModel(SortFilterColumn[] data, SortFilterColumn[] lastUsed)
	{
		this.data = data;

		columnNames = new String[] {
			RB.getString("gui.table.SortDialog.col1"),
			RB.getString("gui.table.SortDialog.col2")
		};

		// Initialize the data we'll be showing
		rows = new ArrayList<SortFilterColumn>();

		if (lastUsed == null)
			rows.add(data[0].cloneMe());
		else
			for (SortFilterColumn entry: lastUsed)
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
		{
			return rows.get(row).name;
		}
		else
			return rows.get(row).sortOrder == SortOrder.ASCENDING;
	}

	@Override
	public Class getColumnClass(int col)
	{
		if (col == 0)
			return String.class;

		return Boolean.class;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return true;
	}

	JComboBox getComboBox()
	{
		JComboBox<SortFilterColumn> combo = new JComboBox<>();

		for (int i = 0; i < data.length; i++)
			combo.addItem(data[i].cloneMe());

		return combo;
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
		SortFilterColumn entry = rows.get(row);

		if (col == 0)
		{
			entry.colIndex = ((SortFilterColumn)value).colIndex;
			entry.name = ((SortFilterColumn)value).name;
		}
		else
		{
			if (entry.sortOrder == SortOrder.ASCENDING)
				entry.sortOrder = SortOrder.DESCENDING;
			else
				entry.sortOrder = SortOrder.ASCENDING;
		}

		fireTableCellUpdated(row, col);
	}

	void addRow()
	{
		rows.add(data[0].cloneMe());

		fireTableDataChanged();
	}

	void deleteRow(int row)
	{
		rows.remove(row);
		fireTableDataChanged();
	}
}