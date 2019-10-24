// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;
import jhi.flapjack.data.*;

// Stores and tracks one or more runs/views associated with this analysis
public class PedVerF1sSummary extends XMLRoot
{
	private GTViewSet viewSet;
	private ArrayList<LineInfo> lines;

	public PedVerF1sSummary()
	{
	}

	public PedVerF1sSummary(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
		this.lines = viewSet.getLines();
	}

	public String name()
	{
		return viewSet.getDataSet().getName() + " - " + viewSet.getName();
	}

	public String parent1()
	{
		for (LineInfo line: lines)
		{
			LineResults lr =  line.getResults();
			if (lr.getPedVerF1sResult().isP1())
				return line.name();
		}

		return null;
	}

	public String parent2()
	{
		for (LineInfo line: lines)
		{
			LineResults lr =  line.getResults();
			if (lr.getPedVerF1sResult().isP2())
				return line.name();
		}

		return null;
	}

	public int lineCount()
	{
		return lines.size();
	}

	public long selectedCount()
	{
		return lines.stream().filter(LineInfo::getSelected).count();
	}

	public PedVerF1sThresholds thresholds()
	{
		return lines.get(0).getResults().getPedVerF1sResult().getThresholds();
	}
}
