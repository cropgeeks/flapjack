// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;
import java.util.stream.*;

import jhi.flapjack.data.*;

public class IFBSummary extends XMLRoot
{
	private GTViewSet viewSet;
	private ArrayList<LineInfo> lines;

	// TODO: These should be references in CASTOR
	private int familySize;

	// Don't save these!
//	private Double percentDataAvg, percentHetAvg, similarityToP1Avg,
//		similarityToP2Avg, percentAlleleMatchExpectedAvg;


	public IFBSummary()
	{
	}

	public IFBSummary(GTViewSet viewSet, IFBBatchList batchList)
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

	public double minWeightedMBVSelected()
	{
		return lines.stream()
			.filter(LineInfo::getSelected)
			.filter(li -> li.getLineResults().getIFBResult().isMbvValid())
			.mapToDouble(li -> li.getLineResults().getIFBResult().getWmbvTotal())
			.min()
			.orElse(Double.NaN);
	}

	public double maxWeightedMBVSelected()
	{
		return lines.stream()
			.filter(LineInfo::getSelected)
			.filter(li -> li.getLineResults().getIFBResult().isMbvValid())
			.mapToDouble(li -> li.getLineResults().getIFBResult().getWmbvTotal())
			.max()
			.orElse(Double.NaN);
	}

	public double avgWeightedMBVSelected()
	{
		return lines.stream()
			.filter(LineInfo::getSelected)
			.filter(li -> li.getLineResults().getIFBResult().isMbvValid())
			.mapToDouble(li -> li.getLineResults().getIFBResult().getWmbvTotal())
			.average()
			.orElse(Double.NaN);
	}

	// Same as the above, but this time on Non Missing (NM) data
	public double minWeightedMBVSelectedNM()
	{
		return lines.stream()
			.filter(LineInfo::getSelected)
			.mapToDouble(li -> li.getLineResults().getIFBResult().getWmbvTotal2())
			.min()
			.orElse(Double.NaN);
	}

	public double maxWeightedMBVSelectedNM()
	{
		return lines.stream()
			.filter(LineInfo::getSelected)
			.mapToDouble(li -> li.getLineResults().getIFBResult().getWmbvTotal2())
			.max()
			.orElse(Double.NaN);
	}

	public double avgWeightedMBVSelectedNM()
	{
		return lines.stream()
			.filter(LineInfo::getSelected)
			.mapToDouble(li -> li.getLineResults().getIFBResult().getWmbvTotal2())
			.average()
			.orElse(Double.NaN);
	}

	// Provides the proportion of:
	//  - selected lines
	//  - where the QTL (in question), has a refAlleleMatchCount > 0
	public double getQTLFreq(String qtlName)
	{
		// Get the index of the QTL for this dataset
		// This makes it faster when iterating over all lines (below) as we
		// don't need to search for it every time
		int index = lines.get(0).getLineResults().getIFBResult().qtlScoreIndexByName(qtlName);

		if (index == -1)
			return Double.NaN;

		List<LineInfo> selectedLines = lines.stream()
			.filter(LineInfo::getSelected)
			.collect(Collectors.toList());

		// Total number of selected lines
		double total = selectedLines.size();

		// Count of (those) lines having positive allele match to reference
		double count = selectedLines.stream()
			.filter(li -> {
				IFBQTLScore r = li.getLineResults().getIFBResult().getQtlScores().get(index);
				return r == null ? false : r.getRefAlleleMatchCount() > 0;
			})
			.count();

		// Return as a proportion
		return count / total;


//		return lines.stream()
//			.filter(LineInfo::getSelected)
//			.filter(li -> li.getLineResults().getIFBResult().qtlScoreByByName(qtlName) != null)
//			.filter(li -> li.getLineResults().getIFBResult().qtlScoreByByName(qtlName).getRefAlleleMatchCount() > 0)
//			.count();

	}
}