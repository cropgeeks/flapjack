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

	// TODO: These should be references in CASTOR
	private LineInfo parent1, parent2, expF1;
	private int ignoreParentsCount;
	private int familySize;

	public PedVerF1sSummary()
	{
	}

	public PedVerF1sSummary(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
		this.lines = viewSet.getLines();

		// Find the parents and F1
		for (LineInfo line: lines)
		{
			LineResults lr =  line.getResults();
			if (lr.getPedVerF1sResult().isP1())
				parent1 = line;
			else if (lr.getPedVerF1sResult().isP2())
				parent2 = line;
			else if (lr.getPedVerF1sResult().isF1())
				expF1 = line;
		}

		// Remove parents (and expF1) from the family size
		ignoreParentsCount = 2 + ((expF1 != null) ? 1 : 0);
		familySize = lines.size() - ignoreParentsCount;
	}

	// XML serialization methods

	public LineInfo getParent1()
		{ return parent1; }

	public void setParent1(LineInfo parent1)
		{ this.parent1 = parent1; }

	public LineInfo getParent2()
		{ return parent2; }

	public void setParent2(LineInfo parent2)
		{ this.parent2 = parent2; }

	public LineInfo getExpF1()
		{ return expF1; }

	public void setExpF1(LineInfo expF1)
		{ this.expF1 = expF1; }

	public int getFamilySize()
		{ return familySize; }

	public void setFamilySize(int familySize)
		{ this.familySize = familySize; }



	public String name()
	{
		return viewSet.getDataSet().getName() + " - " + viewSet.getName();
	}

	public String parent1()
	{
		return parent1.name();
	}

	public String parent2()
	{
		return parent2.name();
	}

	public float proportionSelected()
	{
		long selectedCount = lines.stream()
			.filter(line -> line != parent1)
			.filter(line -> line != parent2)
			.filter(line -> line != expF1)
			.filter(LineInfo::getSelected).count();

		return selectedCount / (float)familySize;
	}

	public PedVerF1sThresholds thresholds()
	{
		return lines.get(0).getResults().getPedVerF1sResult().getThresholds();
	}
}
