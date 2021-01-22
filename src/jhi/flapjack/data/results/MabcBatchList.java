// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;

import jhi.flapjack.data.*;

// Stores and tracks one or more runs/views associated with this analysis
public class MabcBatchList extends XMLRoot
{
	private ArrayList<MabcSummary> summaries = new ArrayList<>();

	// Tracks whether the table showing these results should autoresize
	private boolean autoResize = true;

	public MabcBatchList()
	{
	}

	// Methods required for XML serialization

	public ArrayList<MabcSummary> getSummaries()
		{ return summaries; }

	public void setSummaries(ArrayList<MabcSummary> summaries)
		{ this.summaries = summaries; }

	public boolean isAutoResize()
		{ return autoResize; }

	public void setAutoResize(boolean autoResize)
		{ this.autoResize = autoResize; }


	// Other methods

	public void add(ArrayList<GTViewSet> viewSets)
	{
		for (GTViewSet viewSet: viewSets)
			add(viewSet);
	}

	public void add(GTViewSet viewSet)
	{
		summaries.add(new MabcSummary(viewSet, this));
		viewSet.setMabcBatchList(this);
	}

	public int size()
		{ return summaries.size(); }
}