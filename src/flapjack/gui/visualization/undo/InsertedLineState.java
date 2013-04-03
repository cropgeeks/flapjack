// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization.undo;

import flapjack.data.*;

public class InsertedLineState implements IUndoState
{
	private GTViewSet viewSet;
	private String menuStr;

	// The lines before the operation
	private LineInfo[] undoLines;

	// The lines after the operation
	private LineInfo[] redoLines;

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
		undoLines = viewSet.getLinesAsArray(true);
	}

	public void applyUndoState()
	{
		viewSet.setLinesFromArray(undoLines, true);
	}

	public void createRedoState()
	{
		redoLines = viewSet.getLinesAsArray(true);
	}

	public void applyRedoState()
	{
		viewSet.setLinesFromArray(redoLines, true);
	}
}