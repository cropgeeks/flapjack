// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.undo;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.table.*;

public class MovedLinesState implements IUndoState
{
	private GTViewSet viewSet;
	private String menuStr;

	// The state (that is, "order") of the lines before they were moved about
	private ArrayList<LineInfo> undoLines;
	// The comparisonLine (and index) before the move
	private Line undoComparisonLine;
	private int undoComparisonLineIndex;

	// And the state after the movement was finished
	private ArrayList<LineInfo> redoLines;
	// The comparisonLine (and index) after the move
	private Line redoComparisonLine;
	private int redoComparisonLineIndex;

	// Tracks any linked table's sort state
	private ArrayList<SortColumn> undoColumns, redoColumns;


	public MovedLinesState(GTViewSet viewSet, String menuStr)
	{
		this.viewSet = viewSet;
		this.menuStr = menuStr;
	}

	public GTView getView()
		{ return null; }

	public String getMenuString()
	{
		return menuStr;
	}

	public void createUndoState()
	{
		undoLines = viewSet.copyLines(LineInfo.VISIBLE);
		undoComparisonLine = viewSet.getComparisonLine();
		undoComparisonLineIndex = viewSet.getComparisonLineIndex();

		undoColumns = viewSet.tableHandler().getSortKeys();
	}

	public void applyUndoState()
	{
		viewSet.setLinesFromCopies(undoLines, null, null);
		viewSet.setComparisonLine(undoComparisonLine);
		viewSet.setComparisonLineIndex(undoComparisonLineIndex);

		viewSet.tableHandler().undoRedoApplySort(undoColumns);
	}

	public void createRedoState()
	{
		redoLines = viewSet.copyLines(LineInfo.VISIBLE);
		redoComparisonLine = viewSet.getComparisonLine();
		redoComparisonLineIndex = viewSet.getComparisonLineIndex();

		redoColumns = viewSet.tableHandler().getSortKeys();
	}

	public void applyRedoState()
	{
		viewSet.setLinesFromCopies(redoLines, null, null);
		viewSet.setComparisonLine(redoComparisonLine);
		viewSet.setComparisonLineIndex(redoComparisonLineIndex);

		viewSet.tableHandler().undoRedoApplySort(redoColumns);
	}
}