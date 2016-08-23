// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.table.*;

import jhi.flapjack.data.*;

public abstract class LineDataTableModel extends AbstractTableModel
{
	protected DataSet dataSet;
	protected String[] columnNames;

	// A list of lines being shown. This list (although containing the same
	// LineInfo objects as a GTViewSet's list) is *not* the same list from the
	// view...
	protected ArrayList<LineInfo> lines;

	public void setLines(ArrayList<LineInfo> lines)
	{
		this.lines = lines;
	}

	public ArrayList<LineInfo> getLines()
		{ return lines; }

//	public ArrayList<LineInfo> getFilteredLines()
//	{
//		return lines.stream().filter(LineInfo::getFiltered)
//			.collect(Collectors.toCollection(ArrayList::new));
//	}

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
	public SortColumn[] getSortableColumns()
	{
		SortColumn[] cols = new SortColumn[columnNames.length];
		for (int i = 0; i < cols.length; i++)
			cols[i] = new SortColumn(i, columnNames[i]);

		return cols;
	}

	// Returns only those columns that make sense for filtering (by numbers)
	public FilterColumn[] getFilterableColumns()
	{
		ArrayList<FilterColumn> cols = new ArrayList<>();

		for (int i = 0; i < getColumnCount(); i++)
		{
			Class c = getColumnClass(i);

			if (c == Double.class || c == Integer.class || c == Boolean.class)
				cols.add(new FilterColumn(i, c, columnNames[i], FilterColumn.NONE));
		}

		return cols.toArray(new FilterColumn[] {});
	}

	void selectLines(FilterColumn[] data)
	{
		// For every line...
		for (int i = 0; i < getRowCount(); i++)
		{
			LineInfo line = (LineInfo) getValueAt(i, 0);

			// For every filter
			for (FilterColumn selectFilter: data)
			{
				if (selectFilter.disabled())
					continue;

				Object value = getValueAt(i, selectFilter.colIndex);
				// If the value matches, we can set its state to true
				if (selectFilter.matches(value))
					line.setSelected(true);
				// But any false match on any filter, will deselect the line
				// and quit without even bothering to check remaining filters
				else
				{
					line.setSelected(false);
					break;
				}
			}
		}

		fireTableDataChanged();
	}

	void clearAllFilters()
	{
		// When we clear the filters on the table, we only want to restore lines
		// that were filtered out by the table - any that were hidden manually
		// by the user need to stay hidden
		for (LineInfo line: lines)
			if (line.getVisibility() == LineInfo.FILTERED)
				line.setVisibility(LineInfo.VISIBLE);
	}
}