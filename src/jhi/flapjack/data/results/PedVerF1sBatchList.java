// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;
import jhi.flapjack.data.*;

// Stores and tracks one or more runs/views associated with this analysis
public class PedVerF1sBatchList extends XMLRoot
{
	public String info = "Batch run: " + new Date();

	private ArrayList<PedVerF1sSummary> summaries = new ArrayList<>();

	public PedVerF1sBatchList()
	{
	}

	public void add(ArrayList<GTViewSet> viewSets)
	{
		for (GTViewSet viewSet: viewSets)
			add(viewSet);
	}

	public void add(GTViewSet viewSet)
	{
		summaries.add(new PedVerF1sSummary(viewSet));
		viewSet._setPedVerF1sBatchList(this);
	}

	public int size()
		{ return summaries.size(); }

	public PedVerF1sSummary getSummary(int index)
		{ return summaries.get(index); }
}
