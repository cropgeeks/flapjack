// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;

import jhi.flapjack.data.*;

// Stores and tracks one or more runs/views associated with this analysis
public class IFBBatchList extends XMLRoot
{
	private ArrayList<IFBSummary> summaries = new ArrayList<>();

	// Tracks whether the table showing these results should autoresize
	private boolean autoResize = true;

	public IFBBatchList()
	{
	}


	// Methods required for XML serialization

	public ArrayList<IFBSummary> getSummaries()
		{ return summaries; }

	public void setSummaries(ArrayList<IFBSummary> summaries)
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
		summaries.add(new IFBSummary(viewSet, this));
		viewSet.setIFBBatchList(this);
	}

	public int size()
		{ return summaries.size(); }
}