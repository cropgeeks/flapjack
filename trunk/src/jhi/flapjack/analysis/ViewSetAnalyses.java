// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;

// Methods that operate on the GTViewSet class, that may take some time to run
public class ViewSetAnalyses
{
	/**
	 * Returns a new GTView that holds a cloned chromosome containing only the
	 * markers from the original view that actually have data for the *selected*
	 * lines in the original view.
	 */
	public static void createCustomMap(GTView view)
	{
		GTViewSet viewSet = view.getViewSet();

		// Start by making a new ChromosomeMap to hold the markers of interest
		String name = view.getChromosomeMap().getName() + " (" + new Date() + ")";
		ChromosomeMap newMap = new ChromosomeMap(name);
		newMap.setLength(view.getChromosomeMap().getLength());

		// And get a reference to the line information for the view-set
		ArrayList<LineInfo> lines = viewSet.getLines();


		// For each marker...
		for (int i = 0; i < view.getMarkers().size(); i++)
		{
			// Look across the SELECTED set of lines, and if *any one* of that
			// set has data for this marker, then include it
			for (int j = 0; j < lines.size(); j++)
				if (lines.get(j).getSelected() && view.getState(j, i) != 0)
				{
					newMap.addMarker(view.getMarker(i));
					break;
				}
		}

		newMap.sort();

		GTView newView = new GTView(viewSet, newMap, true);
		viewSet.getCustomMaps().add(viewSet, newView, view);
	}
}