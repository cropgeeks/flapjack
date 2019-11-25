// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;

import jhi.flapjack.data.*;

// Stores and tracks one or more runs/views associated with this analysis
public class MabcBatchList extends XMLRoot
{
	private ArrayList<MabcSummary> summaries = new ArrayList<>();

	public MabcBatchList()
	{
	}

	public void add(ArrayList<GTViewSet> viewSets)
	{
		for (GTViewSet viewSet: viewSets)
			add(viewSet);
	}

	public void add(GTViewSet viewSet)
	{
		summaries.add(new MabcSummary(viewSet, this));
		viewSet._setMabcBatchList(this);
	}

	public int size()
		{ return summaries.size(); }

	public MabcSummary getSummary(int index)
		{ return summaries.get(index); }
}