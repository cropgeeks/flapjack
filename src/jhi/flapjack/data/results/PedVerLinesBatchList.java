// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;

import jhi.flapjack.data.*;

// Stores and tracks one or more runs/views associated with this analysis
public class PedVerLinesBatchList extends XMLRoot
{
	private ArrayList<PedVerLinesSummary> summaries = new ArrayList<>();

	// Tracks whether the table showing these results should autoresize
	private boolean autoResize = true;

	public PedVerLinesBatchList()
	{
	}


	// Methods required for XML serialization

	public ArrayList<PedVerLinesSummary> getSummaries()
		{ return summaries; }

	public void setSummaries(ArrayList<PedVerLinesSummary> summaries)
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
		summaries.add(new PedVerLinesSummary(viewSet, this));
		viewSet._setPedVerLinesBatchList(this);
	}

	public int size()
		{ return summaries.size(); }
}