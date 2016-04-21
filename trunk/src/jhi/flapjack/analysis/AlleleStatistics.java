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
	private long alleleCount;

	// To deal with cases when the number of alleles breaks the 32bit INT limit
	private double prg;

	public AlleleStatistics(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
	}

	public long getAlleleCount()
		{ return alleleCount; }

	public ArrayList<long[]> getResults()
		{ return results; }

	public void runJob(int index)
		throws Exception
	{
		AnalysisSet as = new AnalysisSet(viewSet)
			.withViews(null)
			.withAllLines()
			.withAllMarkers();

		alleleCount = as.countAlleles();
		// Actual progress will be cast back to a scale from 0 to 50K
		maximum = 50000;

		results = new ArrayList<long[]>();

		// TODO: This could be multi-core optimized
		for (int i = 0; i < as.getViewCount(); i++)
			results.add(getStatistics(as, i));
	}

	// Returns an array with each element being the total number of alleles for
	// that state (where each index is equivalent to a state in the state table.
	private long[] getStatistics(AnalysisSet as, int chrIndex)
	{
		int stateCount = viewSet.getDataSet().getStateTable().size();

		// +1 because we use the last location to store the total count of
		// alleles within this view (chromosome)
		long[] statistics = new long[stateCount+1];

		for (int line = 0; line < as.getLines().size() && okToRun; line++)
		{
			for (int marker = 0; marker < as.getMarkers(chrIndex).size() && okToRun; marker++)
			{
				int state = as.getState(chrIndex, line, marker);
				statistics[state]++;

				// Track the total
				statistics[statistics.length-1]++;

				prg++;

				progress = (int) ((prg/(double)alleleCount) * 50000f);
			}
		}

		return statistics;
	}
}