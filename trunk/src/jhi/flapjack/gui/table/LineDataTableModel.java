// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.awt.*;
import java.util.*;
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
			Class c = getObjectColumnClass(i);

			if (c == Double.class || c == Integer.class || c == Boolean.class)
				cols.add(new FilterColumn(i, c, columnNames[i], FilterColumn.NONE));
		}

		return cols.toArray(new FilterColumn[] {});
	}

	void setLineStates(Boolean state, boolean visibleOnly)
	{
		for (LineInfo line: lines)
		{
			if (visibleOnly && line.getVisibility() == LineInfo.VISIBLE)
			{
			// Set the line's state to either true or false
			if (state != null)
				line.setSelected(state);
			// Or toggle it
			else
				line.setSelected(!line.getSelected());
		}
		}

		fireTableRowsUpdated(0, lines.size()-1);
	}

	void selectLines(FilterColumn[] data, boolean visibleOnly)
	{
		// For every line...
		for (int i = 0; i < getRowCount(); i++)
		{
			LineInfo line = (LineInfo) getObjectAt(i, 0);

			// We only want to apply these states to visible lines
			if (visibleOnly && line.getVisibility() != LineInfo.VISIBLE)
				continue;

			// For every filter
			for (FilterColumn selectFilter: data)
			{
				if (selectFilter.disabled())
					continue;

				Object value = getObjectAt(i, selectFilter.colIndex);
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

	// Returns a count of how many lines are both visible and selected
	public long visAndSelCount()
	{
		return lines.stream()
			.filter(li -> li.getVisibility() == LineInfo.VISIBLE)
			.filter(li -> li.getSelected())
			.count();
	}

	public Object getValueForLine(LineInfo lineInfo, int col)
	{
		int index = lines.indexOf(lineInfo);

		if (index == -1)
			return null;

		return getValueAt(index, col);
	}

	@Override
	public Class getColumnClass(int col)
	{
		return CellData.class;
	}

	@Override
	public Object getValueAt(int row, int col)
	{
		return new CellData(lines.get(row), getObjectAt(row, col));
	}

	public abstract Object getObjectAt(int row, int col);

	public abstract Class getObjectColumnClass(int col);
}