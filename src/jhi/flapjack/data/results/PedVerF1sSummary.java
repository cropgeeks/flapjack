// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;
import jhi.flapjack.data.*;

// Stores and tracks one or more runs/views associated with this analysis
public class PedVerF1sSummary extends XMLRoot
{
	private GTViewSet viewSet;

	public PedVerF1sSummary()
	{
	}

	public PedVerF1sSummary(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
	}

	public String getName()
	{
		return viewSet.getDataSet().getName() + " - " + viewSet.getName();
	}
}
