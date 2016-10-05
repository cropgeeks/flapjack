// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.undo;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.table.*;

public class HidLinesState implements IUndoState
{
	private GTViewSet viewSet;
	private String menuStr;

	// The visible lines before the operation
	private ArrayList<LineInfo> undoVisible;
	// The hidden lines before the operation
	private ArrayList<LineInfo> undoHidden;
	// The linked table filtered lines before the operation
	private ArrayList<LineInfo> undoFiltered;

	// The visible lines after the operation
	private ArrayList<LineInfo> redoVisible;
	// The hidden lines after the operation
	private ArrayList<LineInfo> redoHidden;
	// The linked table filtered lines after the operation
	private ArrayList<LineInfo> redoFiltered;

	// Tracks any linked table's sort state
	private ArrayList<SortColumn> undoSort, redoSort;
	private FilterColumn[] undoFilter, redoFilter;

	public HidLinesState(GTViewSet viewSet, String menuStr)
	{
		this.viewSet = viewSet;
		this.menuStr = menuStr;
	}

	public String getMenuString()
		{ return menuStr; }

	public void setMenuString(String menuStr)
		{ this.menuStr = menuStr; }

	public GTView getView()
		{ return null; }

	public void createUndoState()
	{
		undoVisible = viewSet.copyLines(LineInfo.VISIBLE);
		undoHidden = viewSet.copyLines(LineInfo.HIDDEN);
		undoFiltered  = viewSet.copyLines(LineInfo.FILTERED);

		undoSort = viewSet.tableHandler().getSortKeys();
		undoFilter = viewSet.tableHandler().getTableFilter();
	}

	public void applyUndoState()
	{
		viewSet.setLinesFromCopies(undoVisible, undoHidden, undoFiltered);
		viewSet.tableHandler().undoRedoApplySort(undoSort);
		viewSet.tableHandler().undoRedoApplyFilter(undoFilter);
	}

	public void createRedoState()
	{
		redoVisible = viewSet.copyLines(LineInfo.VISIBLE);
		redoHidden = viewSet.copyLines(LineInfo.HIDDEN);
		redoFiltered  = viewSet.copyLines(LineInfo.FILTERED);

		redoSort = viewSet.tableHandler().getSortKeys();
		redoFilter = viewSet.tableHandler().getTableFilter();
	}

	public void applyRedoState()
	{
		viewSet.setLinesFromCopies(redoVisible, redoHidden, redoFiltered);
		viewSet.tableHandler().undoRedoApplySort(redoSort);
		viewSet.tableHandler().undoRedoApplyFilter(redoFilter);
	}
}