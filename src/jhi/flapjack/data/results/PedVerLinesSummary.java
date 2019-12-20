// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;

import jhi.flapjack.data.*;

public class PedVerLinesSummary extends XMLRoot
{
	private GTViewSet viewSet;
	private ArrayList<LineInfo> lines;

	// TODO: These should be references in CASTOR
	private LineInfo parent1, parent2;
	private int familySize;

	// Don't save these!
	private Double percentDataAvg, percentHetAvg, similarityToParentsAvg;


	public PedVerLinesSummary()
	{
	}

	public PedVerLinesSummary(GTViewSet viewSet, PedVerLinesBatchList batchList)
	{
		this.viewSet = viewSet;
		this.lines = viewSet.getLines();

		// Find the parents
		for (LineInfo line: lines)
		{
			LineResults lr =  line.getLineResults();
			if (lr.getPedVerLinesResult().isP1())
				parent1 = line;
			else if (lr.getPedVerLinesResult().isP2())
				parent2 = line;
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

	public LineInfo getParent1()
		{ return parent1; }

	public void setParent1(LineInfo parent1)
		{ this.parent1 = parent1; }

	public LineInfo getParent2()
		{ return parent2; }

	public void setParent2(LineInfo parent2)
		{ this.parent2 = parent2; }

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
			.filter(LineInfo::getSelected).count();

		return selectedCount / (float)familySize;
	}

	public double percentDataAvg()
	{
		if (percentDataAvg == null)
		{
			percentDataAvg = lines.stream()
				.filter(line -> line != parent1)
				.filter(line -> line != parent2)
				.mapToDouble(line -> line.getLineResults().getPedVerLinesResult().getPercentData())
				.filter(value -> !Double.isNaN(value))
				.average()
				.orElse(Double.NaN);
		}

		return percentDataAvg;
	}

	public double percentHetAvg()
	{
		if (percentHetAvg == null)
		{
			percentHetAvg = lines.stream()
				.filter(line -> line != parent1)
				.filter(line -> line != parent2)
				.mapToDouble(line -> line.getLineResults().getPedVerLinesResult().getPercentHet())
				.filter(value -> !Double.isNaN(value))
				.average()
				.orElse(Double.NaN);
		}

		return percentHetAvg;
	}

	public double similarityToParentsAvg()
	{
		if (similarityToParentsAvg == null)
		{
			similarityToParentsAvg = lines.stream()
				.filter(line -> line != parent1)
				.filter(line -> line != parent2)
				.mapToDouble(line -> line.getLineResults().getPedVerLinesResult().getSimilarityToParents())
				.filter(value -> !Double.isNaN(value))
				.average()
				.orElse(Double.NaN);
		}

		return similarityToParentsAvg;
	}
}