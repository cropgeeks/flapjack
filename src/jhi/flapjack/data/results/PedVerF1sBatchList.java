// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;
import static jhi.flapjack.data.results.PedVerDecisions.*;

import jhi.flapjack.data.*;

// Stores and tracks one or more runs/views associated with this analysis
public class PedVerF1sBatchList extends XMLRoot
{
	private ArrayList<PedVerF1sSummary> summaries = new ArrayList<>();

	// Tracks whether the table showing these results should autoresize
	private boolean autoResize = true;

	private PedVerDecisions decisionMethod;

	public PedVerF1sBatchList()
	{
	}

	// Methods required for XML serialization

	public ArrayList<PedVerF1sSummary> getSummaries()
		{ return summaries; }

	public void setSummaries(ArrayList<PedVerF1sSummary> summaries)
		{ this.summaries = summaries; }

	public PedVerDecisions getDecisionMethod()
		{ return decisionMethod; }

	public void setDecisionMethod(PedVerDecisions decisionMethod)
		{ this.decisionMethod = decisionMethod; }

	public boolean isAutoResize()
		{ return autoResize; }

	public void setAutoResize(boolean autoResize)
		{ this.autoResize = autoResize; }


	// Other methods

	public PedVerF1sBatchList(int dedisionMethodIndex)
	{
		selectDecisionMethod(dedisionMethodIndex);
	}

	public void add(ArrayList<GTViewSet> viewSets)
	{
		for (GTViewSet viewSet: viewSets)
			add(viewSet);
	}

	public void add(GTViewSet viewSet)
	{
		summaries.add(new PedVerF1sSummary(viewSet, this));
		viewSet.setPedVerF1sBatchList(this);
	}

	public int size()
		{ return summaries.size(); }

	public void selectDecisionMethod(int decisionMethodIndex)
	{
		switch(decisionMethodIndex)
		{
			case SIMPLE_MODEL: 			decisionMethod = new PedVerDecisionsSimple();
										break;
			case INTERMEDIATE_MODEL:	decisionMethod = new PedVerDecisionsIntermediate();
										break;
			case DETAILED_MODEL: 		decisionMethod = new PedVerDecisionsDetailed();
										break;

			default:					decisionMethod =  new PedVerDecisionsSimple();
		}
	}
}