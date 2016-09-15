// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.Actions;

import javax.swing.*;

public class LinkedTableHandler extends XMLRoot implements ITableViewListener
{
	private GTViewSet viewSet;

	private LineDataTable table;
	private LineDataTableModel model;

	private ArrayList<SortColumn> sortKeys;
	private SortColumn[] lastSort;
	private FilterColumn[] lastFilter, lastSelect;
	private boolean isFiltered;

	private boolean autoResize = true;

	public LinkedTableHandler()
	{
	}

	public LinkedTableHandler(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
	}


	// Methods required for XML serialization

	public GTViewSet getViewSet()
		{ return viewSet; }

	public void setViewSet(GTViewSet viewSet)
		{ this.viewSet = viewSet; }

	public ArrayList<SortColumn> getSortKeys()
		{ return sortKeys; }

	public void setSortKeys(ArrayList<SortColumn> sortKeys)
		{ this.sortKeys = sortKeys; }

	public boolean isAutoResize()
		{ return autoResize; }

	public void setAutoResize(boolean autoResize)
		{ this.autoResize = autoResize; }

	public FilterColumn[] getLastFilter()
		{ return lastFilter; }

	public void setLastFilter(FilterColumn[] lastFilter)
		{ this.lastFilter = lastFilter; }

	public boolean isFiltered()
		{ return isFiltered; }

	public void setFiltered(boolean filtered)
		{ isFiltered = filtered; }

	public SortColumn[] getLastSort()
		{ return lastSort; }

	public void setLastSort(SortColumn[] lastSort)
		{ this.lastSort = lastSort; }

	public FilterColumn[] getLastSelect()
		{ return lastSelect; }

	public void setLastSelect(FilterColumn[] lastSelect)
		{ this.lastSelect = lastSelect; }


	// Other methods

	public void linkTable(LineDataTable table, LineDataTableModel model)
	{
		this.table = table;
		this.model = model;

		table.addViewListener(this);

		doPostLoadOperations();
	}

	// Mirror the table's list of lines back to the ViewSet
	private void tableChanged()
	{
		viewSet.getLines().clear();
		viewSet.getHideLines().clear();

		// Anything still visible in the table should be visible in the view
		for (int i = 0; i < table.getRowCount(); i++)
		{
			int col0 = table.convertColumnIndexToView(0);

			((LineInfo)table.getObjectAt(i, col0)).setVisibility(LineInfo.VISIBLE);
			viewSet.getLines().add((LineInfo)table.getObjectAt(i, col0));
		}


		// Anything filtered, should be hidden in the view. We do this by making
		// a clone of the model's entire list, then filtering on the viewable
		// set from above
		ArrayList<LineInfo> hideLines = new ArrayList<>(model.getLines());
		hideLines.removeAll(viewSet.getLines());
		viewSet.getHideLines().addAll(hideLines);

		// We need to make sure any newly filtered lines have the corect state
		// set on them (that doesn't override a manually set hidden state)
		for (LineInfo lineInfo: hideLines)
			if (lineInfo.getVisibility() == LineInfo.VISIBLE)
				lineInfo.setVisibility(LineInfo.FILTERED);
	}

	public void tableSorted()
	{
		tableChanged();

		Actions.projectModified();

		// Convert table sort keys to castor safe SortColumns (but only if we
		// have any active sort keys
		if (table.getRowSorter().getSortKeys().isEmpty())
			sortKeys = null;
		else
			sortKeys = new ArrayList<SortColumn>();

		// Track the sort settings for project saving
		for (RowSorter.SortKey key : table.getRowSorter().getSortKeys())
			sortKeys.add(new SortColumn(key.getColumn(), key.getSortOrder()));

		// Track the table's last sort/select settings for project saving
		lastSort = table.getLastSort();
		lastSelect = table.getLastSelect();
	}

	public void tableFiltered()
	{
		tableChanged();

		// Track the table's filter settings for project saving
		lastFilter = table.getLastFilter();
		isFiltered = table.isFiltered();
	}

	public ArrayList<LineInfo> linesForTable()
	{
		// Get the current list from the viewset
		ArrayList<LineInfo> lines = new ArrayList<>(viewSet.getLines());
		lines.addAll(viewSet.getHideLines());

		return lines;
	}

	public void viewChanged(boolean setModel)
	{
		if (table == null)
			return;

		// Get (all of) the lines from the view and apply to the model
		model.setLines(linesForTable());

		// Break the sort, but maintain the filters
		if (setModel)
		{
			table.setModel(model);
			table.reapplyFilter();
		}
		// Don't break the sort or the filters
		else
			model.fireTableDataChanged();
	}

	public LineDataTable table()
	{
		return table;
	}

	public LineDataTableModel model()
	{
		return model;
	}

	private void doPostLoadOperations()
	{
		table.autoResize(autoResize, true);

		table.setLastFilter(lastFilter);
		table.setFiltered(isFiltered);
		table.setLastSort(lastSort);
		table.setLastSelect(lastSelect);

		if (sortKeys != null)
		{
			List<RowSorter.SortKey> keys = new ArrayList<>();

			// Multi-column sort (handled by JTable) using "sort keys"
			for (SortColumn entry: sortKeys)
				keys.add(new RowSorter.SortKey(entry.colIndex, entry.sortOrder));

			table.sorter().setSortKeys(keys);
			table.sorter().sort();
		}

		table.reapplyFilter();
	}
}