// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.util.*;
import javax.swing.table.*;

import scri.commons.gui.*;

class FilterDialogTableModel extends AbstractTableModel
{
	// An array of every possible column the user can sort on
	private FilterColumn[] data;
	// And a list of the ones they actually *will* sort on
	private ArrayList<FilterColumn> rows;

	private String[] columnNames;

	FilterDialogTableModel(FilterColumn[] data, FilterColumn[] lastUsed)
	{
		this.data = data;

		columnNames = new String[] {
			RB.getString("gui.table.FilterDialog.col1"),
			RB.getString("gui.table.FilterDialog.col2"),
			RB.getString("gui.table.FilterDialog.col3")
		};

		// Initialize the data we'll be showing
		rows = new ArrayList<FilterColumn>();

		// Shall we add fresh columns, or the ones from last time?
		FilterColumn[] colsToAdd = data;
		if (lastUsed != null) colsToAdd = lastUsed;

		for (FilterColumn entry: colsToAdd)
			rows.add(entry);
	}

	FilterColumn[] getResults()
	{
		return rows.toArray(new FilterColumn[] {});
	}

	@Override
	public String getColumnName(int col)
	{
		return columnNames[col];
	}

	// Override used for toggling between 'filter' or 'criteria' display
	void setColumnName(int col, String name)
	{
		columnNames[col] = name;
		fireTableStructureChanged();
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
			return rows.get(row);

		else
			return rows.get(row).value;
	}

	@Override
	public Class getColumnClass(int col)
	{
		if (col == 0)
			return String.class;

		else if (col == 1)
			return FilterColumn.class;

		else
			return String.class;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		// We don't want to allow editing of the first column
		return col == 1 || (col == 2 && !needsBooleanFilter(row));
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
		FilterColumn entry = rows.get(row);

		if (col == 1)
		{
			entry.filter = ((FilterColumn)value).filter;
		}
		else if (col == 2)
		{
			// Allow the user to clear the cell
			if (((String)value).isEmpty())
				entry.value = null;
			else
			{
				// But don't allow them to type something that isn't a number
				try {
					Double.parseDouble((String)value);
					entry.value = (String) value;
				} catch (Exception e) {}
			}
		}

		fireTableCellUpdated(row, col);
	}

	// Returns true if the column (in the original table) represented by the row
	// in the FilterDialog's view should be using a Boolean filter (true/false)
	// rather than the numerical (less than, greater than, etc) filter
	boolean needsBooleanFilter(int row)
	{
		return rows.get(row).colClass == Boolean.class;
	}

	void clear()
	{
		for (FilterColumn entry: rows)
		{
			entry.filter = FilterColumn.NONE;
			entry.value = null;
		}

		fireTableDataChanged();
	}
}