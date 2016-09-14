// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.util.*;

import jhi.flapjack.data.*;

public class LinkedTableHandler implements ITableViewListener
{
	private GTViewSet viewSet;

	private LineDataTable table;
	private LineDataTableModel model;

	public LinkedTableHandler(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
	}

	public void linkTable(LineDataTable table, LineDataTableModel model)
	{
		this.table = table;
		this.model = model;

		table.addViewListener(this);
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
	}

	public void tableFiltered()
	{
		tableChanged();
	}

	public ArrayList<LineInfo> getLinesForTable()
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
		model.setLines(getLinesForTable());

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

	public LineDataTable getTable()
	{
		return table;
	}

	public LineDataTableModel getModel()
	{
		return model;
	}
}