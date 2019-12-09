// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;

import jhi.flapjack.data.*;

public class MabcSummary extends XMLRoot
{
	private GTViewSet viewSet;
	private ArrayList<LineInfo> lines;

	// TODO: These should be references in CASTOR
	private LineInfo dp, rp;
	private int familySize;

	// Don't save these!
	private Double percentDataAvg, rppTotalAvg, qtlStatusCountAvg;


	public MabcSummary()
	{
	}

	public MabcSummary(GTViewSet viewSet, MabcBatchList batchList)
	{
		this.viewSet = viewSet;
		this.lines = viewSet.getLines();

		// Find the parents
		for (LineInfo line: lines)
		{
			LineResults lr =  line.getResults();
			if (lr.getMabcResult().isIsDP())
				dp = line;
			if (lr.getMabcResult().isIsRP())
				rp = line;
		}

		// Remove parents from the family size
		familySize = lines.size() - 2;
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

	public LineInfo getDP()
		{ return dp; }

	public void setDP(LineInfo dp)
		{ this.dp = dp; }

	public LineInfo getRP()
		{ return rp; }

	public void setRP(LineInfo rp)
		{ this.rp = rp; }

	public int getFamilySize()
		{ return familySize; }

	public void setFamilySize(int familySize)
		{ this.familySize = familySize; }


	public String name()
	{
		return viewSet.getDataSet().getName() + " - " + viewSet.getName();
	}

	public String dp()
	{
		return dp.name();
	}

	public String rp()
	{
		return rp.name();
	}

	public float proportionSelected()
	{
		long selectedCount = lines.stream()
			.filter(line -> line != dp)
			.filter(line -> line != rp)
			.filter(LineInfo::getSelected).count();

		return selectedCount / (float)familySize;
	}

	public double percentDataAvg()
	{
		if (percentDataAvg == null)
		{
			percentDataAvg = lines.stream()
				.filter(line -> line != rp)
				.filter(line -> line != dp)
				.mapToDouble(line -> line.getResults().getMabcResult().getPercentData())
				.filter(value -> !Double.isNaN(value))
				.average()
				.orElse(Double.NaN);
		}

		return percentDataAvg;
	}

	public double rppTotalAvg()
	{
		if (rppTotalAvg == null)
		{
			rppTotalAvg = lines.stream()
				.filter(line -> line != rp)
				.filter(line -> line != dp)
				.mapToDouble(line -> line.getResults().getMabcResult().getRppTotal())
				.filter(value -> !Double.isNaN(value))
				.average()
				.orElse(Double.NaN);
		}

		return rppTotalAvg;
	}

	public double qtlStatusCountAvg()
	{
		if (qtlStatusCountAvg == null)
		{
			qtlStatusCountAvg = lines.stream()
				.filter(line -> line != rp)
				.filter(line -> line != dp)
				.mapToDouble(line -> line.getResults().getMabcResult().getQtlStatusCount())
				.filter(value -> !Double.isNaN(value))
				.average()
				.orElse(Double.NaN);
		}

		return qtlStatusCountAvg;
	}
}