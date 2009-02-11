package flapjack.analysis;

import java.util.regex.*;

import flapjack.data.*;

public class FindMarker extends StringFinder
{
	private GTViewSet viewSet;
	private GTView view;

	// Current marker index within the view
	private int index = 0;
	// Current view index across the viewSet
	private int viewIndex = 0;

	public FindMarker(GTViewSet viewSet, boolean findNext, boolean matchCase, boolean useRegex)
	{
		super(findNext, matchCase, useRegex);

		this.viewSet = viewSet;
		this.view = viewSet.getView(0);
	}

	public void setView(GTView view)
	{
		this.view = view;
	}

	protected int search(String str)
	{
		// Modify the starting index based on previous results
		if (foundMatch && findNext)
			index++;
		else if (foundMatch && !findNext)
			index--;

		// Maintain a count of the search. Once all lines have been looked at
		// it means we didn't find a match
		int searchCount = 0;

		while (searchCount < view.getMarkerCount())
		{
			// If we've reached the end of the data, reset to the start...
			if (index >= view.getMarkerCount())
				index = 0;
			// Or, if we're searching backwards and have reached the start...
			else if (index < 0)
				index = view.getMarkerCount()-1;

			Marker marker = view.getMarker(index);
			if (matches(marker.getName(), str))
				return index;

			searchCount++;

			// Move forward (or back) one index position
			if (findNext)
				index++;
			else
				index--;
		}

		return -1;
	}
}