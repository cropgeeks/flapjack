// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.util.*;

import flapjack.data.*;

import scri.commons.gui.*;

public class AlleleStatistics extends SimpleJob
{
	private GTViewSet viewSet;

	private ArrayList<int[]> results;

	public AlleleStatistics(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
		maximum = viewSet.countAllAlleles();
	}

	public ArrayList<int[]> getResults()
		{ return results; }

	public void runJob(int index)
		throws Exception
	{
		int viewCount = viewSet.getViews().size();

		results = new ArrayList<int[]>(viewCount);

		// TODO: This could be multi-core optimized
		for (GTView view: viewSet.getViews())
			results.add(getStatistics(view));
	}

	// Returns an array with each element being the total number of alleles for
	// that state (where each index is equivalent to a state in the state table.
	private int[] getStatistics(GTView view)
	{
		int stateCount = viewSet.getDataSet().getStateTable().size();

		// +1 because we use the last location to store the total count of
		// alleles within this view (chromosome)
		int[] statistics = new int[stateCount+1];

		view.cacheLines();

		for (int line = 0; line < view.lineCount(); line++)
			for (int marker = 0; marker < view.markerCount() && okToRun; marker++)
			{
				int state = view.getState(line, marker);
				statistics[state]++;

				// Track the total
				statistics[statistics.length-1]++;
				progress++;
			}

		return statistics;
	}
}