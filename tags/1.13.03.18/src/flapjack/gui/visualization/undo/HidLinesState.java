// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization.undo;

import flapjack.data.*;

public class HidLinesState implements IUndoState
{
	private GTViewSet viewSet;
	private String menuStr;

	// The visible lines before the operation
	private LineInfo[] undoVisible;
	// The hidden lines before the operation
	private LineInfo[] undoHidden;

	// The visible lines after the operation
	private LineInfo[] redoVisible;
	// The hidden lines after the operation
	private LineInfo[] redoHidden;

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
		undoVisible = viewSet.getLinesAsArray(true);
		undoHidden  = viewSet.getLinesAsArray(false);
	}

	public void applyUndoState()
	{
		viewSet.setLinesFromArray(undoVisible, true);
		viewSet.setLinesFromArray(undoHidden, false);
	}

	public void createRedoState()
	{
		redoVisible = viewSet.getLinesAsArray(true);
		redoHidden  = viewSet.getLinesAsArray(false);
	}

	public void applyRedoState()
	{
		viewSet.setLinesFromArray(redoVisible, true);
		viewSet.setLinesFromArray(redoHidden, false);
	}
}