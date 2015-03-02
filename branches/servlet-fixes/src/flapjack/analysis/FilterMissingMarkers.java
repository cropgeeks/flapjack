// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.util.*;

import flapjack.data.*;

import scri.commons.gui.*;

public class FilterMissingMarkers extends SimpleJob
{
	private GTViewSet viewSet;
	private boolean allChromosomes;
	private int cutoff;

	public FilterMissingMarkers(GTViewSet viewSet, boolean allChromosomes, int cutoff)
	{
		this.viewSet = viewSet;
		this.allChromosomes = allChromosomes;
		this.cutoff = cutoff;

		// The total number of markers being processed is either:
		if (allChromosomes)
		{
			// A count of all markers across all chromosomes
			for (GTView view: viewSet.getViews())
				maximum += view.markerCount();
		}
		// Or just a count of markers in the currently active view
		else
		{
			int viewIndex = viewSet.getViewIndex();
			GTView current = viewSet.getViews().get(viewIndex);
			maximum = current.markerCount();
		}
	}

	public void runJob(int index)
		throws Exception
	{
		ArrayList<GTView> views = viewSet.getViews();

		for (GTView view: views)
		{
			// Skip if allChromosomes is false and it's not the current
			if (allChromosomes == false && view != views.get(viewSet.getViewIndex()))
				continue;

			view.cacheLines();

			// For each marker...
			for (int i = view.markerCount()-1; i >= 0 && okToRun; i--)
			{
				int allelesCount = 0;
				int missingCount = 0;

				// Count how many alleles are missing across all the lines...
				for (int j = 0; j < viewSet.getLines().size() && okToRun; j++)
				{
					if (view.getState(j, i) == 0)
						missingCount++;

					allelesCount++;
				}

//				System.out.println("Marker " + i + " missing " + missingCount + " / " + allelesCount + " ("
//					+ ((missingCount / (float)allelesCount)*100));

				// And if the percentage of missing ones is >= cutoff, then
				// remove it from the visible set
				if ((missingCount / (float)allelesCount)*100 >= cutoff)
					// But only so long as it doesn't remove all markers!!
					if (view.markerCount() > 1)
						view.hideMarker(i);

				progress++;
			}
		}
	}
}