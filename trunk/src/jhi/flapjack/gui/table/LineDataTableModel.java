// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.awt.*;
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

	public SortableColumn[] getSortableColumns()
	{
		SortableColumn[] cols = new SortableColumn[columnNames.length];
		for (int i = 0; i < cols.length; i++)
			cols[i] = new SortableColumn(i, columnNames[i], SortOrder.DESCENDING);

		return cols;
	}

	public static class SortableColumn
	{
		// The index of this column in the model
		public int colIndex;
		// Its name
		public String name;
		// Whether to sort this column's data ascending or decending
		public SortOrder sortOrder;

		private SortableColumn(int colIndex, String name, SortOrder sortOrder)
		{
			this.colIndex = colIndex;
			this.name = name;
			this.sortOrder = sortOrder;
		}

		SortableColumn cloneMe()
		{
			return new SortableColumn(colIndex, name, sortOrder);
		}

		public String toString()
			{ return name; }
	}
}