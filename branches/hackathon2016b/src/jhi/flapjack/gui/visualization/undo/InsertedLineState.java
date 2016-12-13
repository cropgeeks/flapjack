// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.undo;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.table.*;

public class InsertedLineState implements IUndoState
{
	private GTViewSet viewSet;
	private String menuStr;

	// The lines before the operation
	private ArrayList<LineInfo> undoLines;

	// The lines after the operation
	private ArrayList<LineInfo> redoLines;

	// Tracks any linked table's sort state
	private ArrayList<SortColumn> undoColumns, redoColumns;

	public InsertedLineState(GTViewSet viewSet, String menuStr)
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
		undoLines = viewSet.copyLines(LineInfo.VISIBLE);
		undoColumns = viewSet.tableHandler().getSortKeys();
	}

	public void applyUndoState()
	{
		viewSet.setLinesFromCopies(undoLines, null, null);
		viewSet.tableHandler().undoRedoApplySort(undoColumns);
	}

	public void createRedoState()
	{
		redoLines = viewSet.copyLines(LineInfo.VISIBLE);
		redoColumns = viewSet.tableHandler().getSortKeys();
	}

	public void applyRedoState()
	{
		viewSet.setLinesFromCopies(redoLines, null, null);
		viewSet.tableHandler().undoRedoApplySort(redoColumns);
	}
}