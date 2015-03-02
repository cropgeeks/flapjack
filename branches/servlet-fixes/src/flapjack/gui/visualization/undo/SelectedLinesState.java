// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization.undo;

import flapjack.data.*;

public class SelectedLinesState implements IUndoState
{
	private GTView view;
	private String menuStr;

	// The selected state of the lines before they were changed
	private boolean[] undoLines;

	// And the state after the selection was finished
	private boolean[] redoLines;

	public SelectedLinesState(GTView view)
	{
		this.view = view;
	}

	public SelectedLinesState(GTView view, String menuStr)
	{
		this.view = view;
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
		undoLines = new boolean[view.lineCount()];
		for (int i = 0; i < undoLines.length; i++)
			undoLines[i] = view.isLineSelected(i);
	}

	public void applyUndoState()
	{
		for (int i = 0; i < undoLines.length; i++)
			view.setLineState(i, undoLines[i]);
	}

	public void createRedoState()
	{
		redoLines = new boolean[view.lineCount()];
		for (int i = 0; i < redoLines.length; i++)
			redoLines[i] = view.isLineSelected(i);
	}

	public void applyRedoState()
	{
		for (int i = 0; i < redoLines.length; i++)
			view.setLineState(i, redoLines[i]);
	}
}