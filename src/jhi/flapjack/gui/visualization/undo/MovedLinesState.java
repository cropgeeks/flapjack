// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
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
	private Line undoComparisonLine, undoComparisonLine2;
	private int undoComparisonLineIndex, undoComparisonLineIndex2;

	// And the state after the movement was finished
	private ArrayList<LineInfo> redoLines;
	// The comparisonLine (and index) after the move
	private Line redoComparisonLine, redoComparisonLine2;
	private int redoComparisonLineIndex, redoComparisonLineIndex2;

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
		undoComparisonLine2 = viewSet.getComparisonLine2();
		undoComparisonLineIndex = viewSet.getComparisonLineIndex();
		undoComparisonLineIndex2 = viewSet.getComparisonLineIndex2();

		undoColumns = viewSet.tableHandler().getSortKeys();
	}

	public void applyUndoState()
	{
		viewSet.setLinesFromCopies(undoLines, null, null);
		viewSet.setComparisonLine(undoComparisonLine);
		viewSet.setComparisonLine2(undoComparisonLine2);
		viewSet.setComparisonLineIndex(undoComparisonLineIndex);
		viewSet.setComparisonLineIndex2(undoComparisonLineIndex2);

		viewSet.tableHandler().undoRedoApplySort(undoColumns);
	}

	public void createRedoState()
	{
		redoLines = viewSet.copyLines(LineInfo.VISIBLE);
		redoComparisonLine = viewSet.getComparisonLine();
		redoComparisonLine2 = viewSet.getComparisonLine2();
		redoComparisonLineIndex = viewSet.getComparisonLineIndex();
		redoComparisonLineIndex2 = viewSet.getComparisonLineIndex2();

		redoColumns = viewSet.tableHandler().getSortKeys();
	}

	public void applyRedoState()
	{
		viewSet.setLinesFromCopies(redoLines, null, null);
		viewSet.setComparisonLine(redoComparisonLine);
		viewSet.setComparisonLine2(redoComparisonLine2);
		viewSet.setComparisonLineIndex(redoComparisonLineIndex);
		viewSet.setComparisonLineIndex2(redoComparisonLineIndex2);

		viewSet.tableHandler().undoRedoApplySort(redoColumns);
	}
}