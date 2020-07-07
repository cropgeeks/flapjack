// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;

import jhi.flapjack.data.*;

public class FBSummary extends XMLRoot
{
	private GTViewSet viewSet;
	private ArrayList<LineInfo> lines;

	// TODO: These should be references in CASTOR
	private int familySize;

	// Don't save these!
//	private Double percentDataAvg, percentHetAvg, similarityToP1Avg,
//		similarityToP2Avg, percentAlleleMatchExpectedAvg;


	public FBSummary()
	{
	}

	public FBSummary(GTViewSet viewSet, FBBatchList batchList)
	{
		this.viewSet = viewSet;
		this.lines = viewSet.getLines();

		familySize = lines.size();
	}

	// XML serialization methods

	public GTViewSet getViewSet()
		{ return viewSet; }

	public void setViewSet(GTViewSet viewSet)
		{ this.viewSet = viewSet; }

	public ArrayList<LineInfo> getLines()
		{ return lines; }

	public void setLines(ArrayList<LineInfo> lines)
		{ this.lines = lines; }

	public int getFamilySize()
		{ return familySize; }

	public void setFamilySize(int familySize)
		{ this.familySize = familySize; }


	public String name()
	{
		return viewSet.getDataSet().getName() + " - " + viewSet.getName();
	}


	public float proportionSelected()
	{
		long selectedCount = lines.stream()
			.filter(LineInfo::getSelected).count();

		return selectedCount / (float)familySize;
	}
}