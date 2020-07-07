// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.undo;

import java.util.*;

import jhi.flapjack.data.*;

public class SelectedMarkersState implements IUndoState
{
	private GTViewSet viewSet;
	private GTView view;
	private String menuStr;

	// The selected state of the markers before they were changed
	private ArrayList<boolean[]> undoMarkers;

	// And the state after the selection was finished
	private ArrayList<boolean[]> redoMarkers;

	public SelectedMarkersState(GTView view)
	{
		this.view = view;
		this.viewSet = view.getViewSet();
	}

	public SelectedMarkersState(GTView view, String menuStr)
	{
		this(view);
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
		undoMarkers = new ArrayList<boolean[]>(viewSet.chromosomeCount());

		for (GTView view: viewSet.getViews())
		{
			boolean[] states = new boolean[view.markerCount()];
			for (int i = 0; i < states.length; i++)
				states[i] = view.isMarkerSelected(i);

			undoMarkers.add(states);
		}
	}

	public void applyUndoState()
	{
		for (int v = 0; v < viewSet.chromosomeCount(); v++)
		{
			GTView view = viewSet.getViews().get(v);

			boolean[] states = undoMarkers.get(v);
			for (int i = 0; i < states.length; i++)
				view.setMarkerState(i, states[i]);
		}
	}

	public void createRedoState()
	{
		redoMarkers = new ArrayList<boolean[]>(viewSet.chromosomeCount());

		for (GTView view: viewSet.getViews())
		{
			boolean[] states = new boolean[view.markerCount()];
			for (int i = 0; i < states.length; i++)
				states[i] = view.isMarkerSelected(i);

			redoMarkers.add(states);
		}
	}

	public void applyRedoState()
	{
		for (int v = 0; v < viewSet.chromosomeCount(); v++)
		{
			GTView view = viewSet.getViews().get(v);

			boolean[] states = redoMarkers.get(v);
			for (int i = 0; i < states.length; i++)
				view.setMarkerState(i, states[i]);
		}
	}
}