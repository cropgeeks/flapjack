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
	private SortableColumn[] data;
	// And a list of the ones they actually *will* sort on
	private ArrayList<SortableColumn> rows;

	private String[] columnNames;

	SortDialogTableModel(SortableColumn[] data, SortableColumn[] lastUsed)
	{
		this.data = data;

		columnNames = new String[] {
			RB.getString("gui.table.SortDialog.col1"),
			RB.getString("gui.table.SortDialog.col2")
		};

		// Initialize the data we'll be showing
		rows = new ArrayList<SortableColumn>();

		if (lastUsed == null)
			rows.add(data[0].cloneMe());
		else
			for (SortableColumn entry: lastUsed)
				rows.add(entry.cloneMe());
	}

	SortableColumn[] getSortInfo()
	{
		return rows.toArray(new SortableColumn[] {});
	}

	@Override
	public String getColumnName(int col)
	{
		return columnNames[col];
	}

	public int getRowCount()
	{
		return rows.size();
	}

	public int getColumnCount()
	{
		return columnNames.length;
	}

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
		JComboBox<SortableColumn> combo = new JComboBox<>();

		for (int i = 0; i < data.length; i++)
			combo.addItem(data[i].cloneMe());

		return combo;
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
		SortableColumn entry = rows.get(row);

		if (col == 0)
		{
			entry.colIndex = ((SortableColumn)value).colIndex;
			entry.name = ((SortableColumn)value).name;
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