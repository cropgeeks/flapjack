// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;

public abstract class LineDataTableModel extends AbstractTableModel
{
	protected DataSet dataSet;
	protected String[] columnNames;

	@Override
	public String getColumnName(int col)
	{
	    return columnNames[col];
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	public Color getDisplayColor(int row, int col)
	{
		return null;
	}

	// Returns a list of all columns (because we can sort on any column)
	public SortFilterColumn[] getSortableColumns()
	{
		SortFilterColumn[] cols = new SortFilterColumn[columnNames.length];
		for (int i = 0; i < cols.length; i++)
			cols[i] = new SortFilterColumn(i, columnNames[i]);

		return cols;
	}

	// Returns only those columns that make sense for filtering (by numbers)
	public SortFilterColumn[] getFilterableColumns()
	{
		ArrayList<SortFilterColumn> cols = new ArrayList<>();

		for (int i = 0; i < getColumnCount(); i++)
		{
			Class c = getColumnClass(i);

			if (c == Double.class || c == Integer.class || c == Boolean.class)
				cols.add(new SortFilterColumn(i, columnNames[i]));
		}

		return cols.toArray(new SortFilterColumn[] {});
	}
}