// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.table.*;

public class SortLinesByLineDataModel extends SortLines
{
	private TableRowSorter<LineDataTableModel> sorter;
	private LineDataTableModel.SortableColumn[] data;

	public SortLinesByLineDataModel(GTViewSet viewSet, TableRowSorter<LineDataTableModel> sorter, LineDataTableModel.SortableColumn[] data)
	{
		super(viewSet);

		this.sorter = sorter;
		this.data = data;
	}

	@Override
	protected ArrayList<LineInfo> doSort(GTView view)
	{
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();

		// Multi-column sort (handled by JTable) using "sort keys"
		for (LineDataTableModel.SortableColumn entry: data)
			sortKeys.add(new RowSorter.SortKey(entry.colIndex, entry.sortOrder));

		sorter.setSortKeys(sortKeys);
		sorter.sort();

		return view.getViewSet().getLines();
	}
}