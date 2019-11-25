// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;
import static jhi.flapjack.data.results.PedVerDecisions.*;

import jhi.flapjack.data.*;

// Stores and tracks one or more runs/views associated with this analysis
public class PedVerF1sBatchList extends XMLRoot
{
	private ArrayList<PedVerF1sSummary> summaries = new ArrayList<>();

	private PedVerDecisions decisionMethod;

	public PedVerF1sBatchList()
	{
	}

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
		viewSet._setPedVerF1sBatchList(this);
	}

	public int size()
		{ return summaries.size(); }

	public PedVerF1sSummary getSummary(int index)
		{ return summaries.get(index); }

	public PedVerDecisions getDecisionMethod()
	{
		return decisionMethod;
	}

	public void setDecisionMethod(PedVerDecisions decisionMethod)
	{
		this.decisionMethod = decisionMethod;
	}

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
