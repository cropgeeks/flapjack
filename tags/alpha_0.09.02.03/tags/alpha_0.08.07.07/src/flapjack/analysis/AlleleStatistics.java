package flapjack.analysis;

import java.util.*;

import flapjack.data.*;

public class AlleleStatistics
{
	private GTViewSet viewSet;

	private int alleleCount = 0;
	private boolean isOK = true;

	private Vector<int[]> results;

	public AlleleStatistics(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
	}

	public int getAlleleCount()
		{ return alleleCount; }

	public void cancel()
		{ isOK = false; }

	public Vector<int[]> getResults()
		{ return results; }

	public Vector<int[]> computeStatistics()
	{
		StateTable stateTable = viewSet.getDataSet().getStateTable();

		int viewCount = viewSet.getViews().size();

		results = new Vector<int[]>(viewCount);

		// TODO: This could be multi-core optimized
		for (GTView view: viewSet.getViews())
			results.add(getStatistics(view));

		return results;
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

		for (int line = 0; line < view.getLineCount(); line++)
			for (int marker = 0; marker < view.getMarkerCount() && isOK; marker++)
			{
				int state = view.getState(line, marker);
				statistics[state]++;

				// Track the total
				statistics[statistics.length-1]++;
				alleleCount++;
			}

		return statistics;
	}
}