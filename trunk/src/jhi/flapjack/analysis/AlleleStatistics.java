// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;
import java.util.stream.Collectors;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

public class AlleleStatistics extends SimpleJob
{
	private GTViewSet viewSet;

	private ArrayList<long[]> results;

	// To deal with cases when the number of alleles breaks the 32bit INT limit
	private double max, prg;

	public AlleleStatistics(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
		max = viewSet.countAllAlleles(true);

		// Actual progress will be cast back to a scale from 0 to 50K
		maximum = 50000;
	}

	public ArrayList<long[]> getResults()
		{ return results; }

	public void runJob(int index)
		throws Exception
	{
		int viewCount = viewSet.getViews().size();

		results = new ArrayList<long[]>();

		// TODO: This could be multi-core optimized
		for (GTView view: viewSet.getViews())
			results.add(getStatistics(view));

//		results = viewSet.getViews().parallelStream().map(view -> getStatistics(view)).collect(Collectors.toCollection(ArrayList::new));
	}

	// Returns an array with each element being the total number of alleles for
	// that state (where each index is equivalent to a state in the state table.
	private long[] getStatistics(GTView view)
	{
		int stateCount = viewSet.getDataSet().getStateTable().size();

		// +1 because we use the last location to store the total count of
		// alleles within this view (chromosome)
		long[] statistics = new long[stateCount+1];

		view.cacheLines();

		for (int line = 0; line < view.lineCount() && okToRun; line++)
		{
			for (int marker = 0; marker < view.markerCount() && okToRun; marker++)
			{
				if (view.getMarker(marker).dummyMarker() == false)
				{
					int state = view.getState(line, marker);
					statistics[state]++;

					// Track the total
					statistics[statistics.length-1]++;
				}

				prg++;

				progress = (int) ((prg/max) * 50000f);
			}
		}

		return statistics;
	}
}