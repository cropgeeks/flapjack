// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;

import jhi.flapjack.data.*;

// Stores and tracks one or more runs/views associated with this analysis
public class PedVerLinesBatchList extends XMLRoot
{
	private ArrayList<PedVerLinesSummary> summaries = new ArrayList<>();

	public PedVerLinesBatchList()
	{
	}

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

	public PedVerLinesSummary getSummary(int index)
		{ return summaries.get(index); }
}