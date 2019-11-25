// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;

import jhi.flapjack.data.*;

// Stores and tracks one or more runs/views associated with this analysis
public class ForwardBreedingBatchList extends XMLRoot
{
	private ArrayList<ForwardBreedingSummary> summaries = new ArrayList<>();

	public ForwardBreedingBatchList()
	{
	}

	public void add(ArrayList<GTViewSet> viewSets)
	{
		for (GTViewSet viewSet: viewSets)
			add(viewSet);
	}

	public void add(GTViewSet viewSet)
	{
		summaries.add(new ForwardBreedingSummary(viewSet, this));
		viewSet._setForwardBreedingBatchList(this);
	}

	public int size()
		{ return summaries.size(); }

	public ForwardBreedingSummary getSummary(int index)
		{ return summaries.get(index); }
}