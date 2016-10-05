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
	private LineInfo[] undoLines;
	// The comparisonLine (and index) before the move
	private Line undoComparisonLine;
	private int undoComparisonLineIndex;

	// And the state after the movement was finished
	private LineInfo[] redoLines;
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
		undoLines = viewSet.getLinesAsArray(true);
		undoComparisonLine = viewSet.getComparisonLine();
		undoComparisonLineIndex = viewSet.getComparisonLineIndex();

		undoColumns = viewSet.tableHandler().getSortKeys();
	}

	public void applyUndoState()
	{
		viewSet.setLinesFromArray(undoLines, true);
		viewSet.setComparisonLine(undoComparisonLine);
		viewSet.setComparisonLineIndex(undoComparisonLineIndex);

		viewSet.tableHandler().copyViewToTable(true);
		viewSet.tableHandler().undoRedoApplySort(undoColumns);
	}

	public void createRedoState()
	{
		redoLines = viewSet.getLinesAsArray(true);
		redoComparisonLine = viewSet.getComparisonLine();
		redoComparisonLineIndex = viewSet.getComparisonLineIndex();

		redoColumns = viewSet.tableHandler().getSortKeys();
	}

	public void applyRedoState()
	{
		viewSet.setLinesFromArray(redoLines, true);
		viewSet.setComparisonLine(redoComparisonLine);
		viewSet.setComparisonLineIndex(redoComparisonLineIndex);

		viewSet.tableHandler().copyViewToTable(true);
		viewSet.tableHandler().undoRedoApplySort(redoColumns);
	}
}