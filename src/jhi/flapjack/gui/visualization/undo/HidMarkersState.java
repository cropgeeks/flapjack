// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.undo;

import java.util.*;

import jhi.flapjack.data.*;

public class HidMarkersState implements IUndoState
{
	// The viewset being tracked
	private GTViewSet viewSet;
	// And the active view at the time the state was made
	private GTView view;
	private String menuStr;

	// The visible markers before the operation
	private ArrayList<MarkerInfo[]> undoVisible;
	// The hidden markers before the operation
	private ArrayList<MarkerInfo[]> undoHidden;

	// The visible markers after the operation
	private ArrayList<MarkerInfo[]> redoVisible;
	// The hidden markers after the operation
	private ArrayList<MarkerInfo[]> redoHidden;

	public HidMarkersState(GTView view, String menuStr)
	{
		this.view = view;
		this.viewSet = view.getViewSet();
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
		undoVisible = new ArrayList<MarkerInfo[]>(viewSet.chromosomeCount());
		undoHidden = new ArrayList<MarkerInfo[]>(viewSet.chromosomeCount());

		for (GTView view: viewSet.getViews())
		{
			undoVisible.add(view.getMarkersAsArray(true));
			undoHidden.add(view.getMarkersAsArray(false));
		}
	}

	public void applyUndoState()
	{
		for (int i = 0; i < viewSet.chromosomeCount(); i++)
		{
			GTView view = viewSet.getViews().get(i);
			view.setMarkersFromArray(undoVisible.get(i), true);
			view.setMarkersFromArray(undoHidden.get(i), false);
		}
	}

	public void createRedoState()
	{
		redoVisible = new ArrayList<MarkerInfo[]>(viewSet.chromosomeCount());
		redoHidden = new ArrayList<MarkerInfo[]>(viewSet.chromosomeCount());

		for (GTView view: viewSet.getViews())
		{
			redoVisible.add(view.getMarkersAsArray(true));
			redoHidden.add(view.getMarkersAsArray(false));
		}
	}

	public void applyRedoState()
	{
		for (int i = 0; i < viewSet.chromosomeCount(); i++)
		{
			GTView view = viewSet.getViews().get(i);
			view.setMarkersFromArray(redoVisible.get(i), true);
			view.setMarkersFromArray(redoHidden.get(i), false);
		}
	}
}