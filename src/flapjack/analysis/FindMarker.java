// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.util.*;

import flapjack.data.*;

public class FindMarker extends StringFinder
{
	private GTViewSet viewSet;
	private GTView view;

	private boolean searchAllChromosomes;

	// Current marker index within the view
	private int index = 0;
	// Current view index across the viewSet
	private int viewIndex = 0;

	public FindMarker(GTViewSet viewSet, boolean searchAllChromosomes, boolean matchCase, boolean useRegex)
	{
		super(matchCase, useRegex);

		this.viewSet = viewSet;
		this.searchAllChromosomes = searchAllChromosomes;

		if (searchAllChromosomes)
			viewIndex = 0;
		else
			viewIndex = viewSet.getViewIndex();

		view = viewSet.getView(viewIndex);
	}

	public LinkedList<Result> search(String str)
	{
		LinkedList<Result> results = new LinkedList<>();

		// Maintain a count of the search. Once all markers have been looked at
		// it means we didn't find a match
		int searchCount = 0;

		int max = view.markerCount();
		if (searchAllChromosomes)
			max = viewSet.countAllMarkers();

		while (searchCount < max)
		{
			// If we've reached the end of this view, move on to the next one...
			if (index >= view.markerCount())
			{
				view = viewSet.getView(++viewIndex);
				index = 0;
			}

			Marker marker = view.getMarker(index);
			if (!marker.dummyMarker() && matches(marker.getName(), str))
				results.add(new Result(marker, view.getChromosomeMap()));

			searchCount++;
			index++;
		}

		return results;
	}

	public static class Result
	{
		public Marker marker;
		public ChromosomeMap map;

		Result(Marker marker, ChromosomeMap map)
		{
			this.marker = marker;
			this.map = map;
		}
	}
}