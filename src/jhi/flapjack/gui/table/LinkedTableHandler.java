// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.Actions;

import javax.swing.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.*;
import jhi.flapjack.gui.visualization.undo.*;

public class LinkedTableHandler extends XMLRoot implements ITableViewListener
{
	private GTViewSet viewSet;

	private LineDataTable table;
	private LineDataTableModel model;

	// Track various elements that will be saved as part of a project so the
	// table's state can be restored after a load
	private ArrayList<SortColumn> sortKeys;
	private SortColumn[] lastSort;
	private FilterColumn[] dialogFilter, tableFilter, lastSelect;
	private boolean autoResize = true;

	// Track the state of the table/lines before a sort (for undo functioanlity)
	private MovedLinesState undoSort;

	private boolean isChanging = false;

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

	public FilterColumn[] getDialogFilter()
		{ return dialogFilter; }

	public void setDialogFilter(FilterColumn[] dialogFilter)
		{ this.dialogFilter = dialogFilter; }

	public FilterColumn[] getTableFilter()
		{ return tableFilter; }

	public void setTableFilter(FilterColumn[] tableFilter)
		{ this.tableFilter = tableFilter; }

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
	public void copyTableToView()
	{
		if (table == null)
			return;

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
		updateTrackers();

		if (isChanging)
			return;

		isChanging = true;

		copyTableToView();


		Actions.projectModified();

		// Update the undo/redo state with the new order
		if (undoSort != null)
		{
			undoSort.createRedoState();
			GenotypePanel gPanel = Flapjack.winMain.getGenotypePanel();
			gPanel.addUndoState(undoSort);
		}

		isChanging = false;
	}

	public void tableFiltered()
	{
		updateTrackers();

		if (isChanging)
			return;

		isChanging = true;

		copyTableToView();

		isChanging = false;
	}

	private void updateTrackers()
	{
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

		// Track the table's filter settings for project saving
		dialogFilter = table.getDialogFilter();
		tableFilter = table.getTableFilter();
	}

	public ArrayList<LineInfo> linesForTable()
	{
		// Get the current list from the viewset
		ArrayList<LineInfo> lines = new ArrayList<>(viewSet.getLines());
		lines.addAll(viewSet.getHideLines());

		return lines;
	}

	public void copyViewToTable(boolean setModel)
	{
		if (table == null)
			return;

		if (isChanging)
			return;
		isChanging = true;

		// Get (all of) the lines from the view and apply to the model
		model.setLines(linesForTable());

		// Break the sort, but maintain the filters
		if (setModel)
		{
			table.setModel(model);
			table.filter();
		}
		// Don't break the sort or the filters
		else
			model.fireTableDataChanged();

		isChanging = false;
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

		table.setDialogFilter(dialogFilter);
		table.setTableFilter(tableFilter);
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

		table.filter();
	}

	public void tablePreSorted()
	{
		undoSort = new MovedLinesState(viewSet, "table sorted");
		undoSort.createUndoState();
	}

	public void undoRedoApplySort(ArrayList<SortColumn> sortKeys)
	{
		if (table == null)
			return;

		isChanging = true;

		// Clear any tracked (temp) undo state, because when we apply this sort
		// tableSorted() will be called, and we don't want to make another
		// undo/redo object, because we're already running one!!!
		undoSort = null;

		List<RowSorter.SortKey> keys = new ArrayList<>();

		// Multi-column sort (handled by JTable) using "sort keys"
		if (sortKeys != null)
			for (SortColumn entry: sortKeys)
				keys.add(new RowSorter.SortKey(entry.colIndex, entry.sortOrder));

		table.sorter().setSortKeys(keys);
		table.sorter().sort();

		isChanging = false;
	}

	public void undoRedoApplyFilter(FilterColumn[] filters)
	{
		if (table == null)
			return;

		isChanging = true;

		table.setTableFilter(filters);
		table.filter();

		isChanging = false;
	}
}