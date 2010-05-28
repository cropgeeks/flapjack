// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.visualization.undo;

import flapjack.data.*;

public class HidMarkersState implements IUndoState
{
	private GTView view;
	private String menuStr;

	// The visible markers before the operation
	private MarkerInfo[] undoVisible;
	// The hidden markers before the operation
	private MarkerInfo[] undoHidden;

	// The visible markers after the operation
	private MarkerInfo[] redoVisible;
	// The hidden markers after the operation
	private MarkerInfo[] redoHidden;

	public HidMarkersState(GTView view, String menuStr)
	{
		this.view = view;
		this.menuStr = menuStr;
	}

	public String getMenuString()
		{ return menuStr; }

	public void setMenuString(String menuStr)
		{ this.menuStr = menuStr; }

	public GTView getView()
		{ return view; }

	public void createUndoState()
	{
		undoVisible = view.getMarkersAsArray(true);
		undoHidden  = view.getMarkersAsArray(false);
	}

	public void applyUndoState()
	{
		view.setMarkersFromArray(undoVisible, true);
		view.setMarkersFromArray(undoHidden, false);
	}

	public void createRedoState()
	{
		redoVisible = view.getMarkersAsArray(true);
		redoHidden  = view.getMarkersAsArray(false);
	}

	public void applyRedoState()
	{
		view.setMarkersFromArray(redoVisible, true);
		view.setMarkersFromArray(redoHidden, false);
	}
}