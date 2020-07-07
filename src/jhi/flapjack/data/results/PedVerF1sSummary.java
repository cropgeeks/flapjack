// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;

import jhi.flapjack.data.*;
import static jhi.flapjack.data.results.PedVerDecisions.*;

public class PedVerF1sSummary extends XMLRoot
{
	private GTViewSet viewSet;
	private ArrayList<LineInfo> lines;

	// TODO: These should be references in CASTOR
	private LineInfo parent1, parent2, expF1;
	private int familySize;

	// Don't save these!
	private Double percentDataAvg, percentHetAvg, similarityToP1Avg,
		similarityToP2Avg, percentAlleleMatchExpectedAvg;

	private PedVerDecisions decider;

	public PedVerF1sSummary()
	{
	}

	public PedVerF1sSummary(GTViewSet viewSet, PedVerF1sBatchList batchList)
	{
		this.viewSet = viewSet;
		this.lines = viewSet.getLines();
		this.decider = batchList.getDecisionMethod();

		// Find the parents and F1
		for (LineInfo line: lines)
		{
			LineResults lr =  line.getLineResults();
			if (lr.getPedVerF1sResult().isP1())
				parent1 = line;
			else if (lr.getPedVerF1sResult().isP2())
				parent2 = line;
			else if (lr.getPedVerF1sResult().isF1())
				expF1 = line;
		}

		// Remove parents (and expF1) from the family size
		int ignoreParentsCount = 2 + ((expF1 != null) ? 1 : 0);
		familySize = lines.size() - ignoreParentsCount;
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
		return lines.get(0).getLineResults().getPedVerF1sResult().getThresholds();
	}

	public double percentDecisionTrueF1s()
	{
		return (lines.stream()
			.filter(line -> line != parent1)
			.filter(line -> line != parent2)
			.filter(line -> line != expF1)
			.filter(line -> decider.getDecision(line.getLineResults().getPedVerF1sResult()) == TRUE_F1)
			.count() / (float) familySize) * 100;
	}

	public double percentDecisionUndecidedHybrid()
	{
		return (lines.stream()
			.filter(line -> line != parent1)
			.filter(line -> line != parent2)
			.filter(line -> line != expF1)
			.filter(line -> decider.getDecision(line.getLineResults().getPedVerF1sResult()) == UNDECIDED_HYBRID)
			.count() / (float) familySize) * 100;
	}

	public double percentDecisionUndecidedInbred()
	{
		return (lines.stream()
			.filter(line -> line != parent1)
			.filter(line -> line != parent2)
			.filter(line -> line != expF1)
			.filter(line -> decider.getDecision(line.getLineResults().getPedVerF1sResult()) == UNDECIDED_INBRED)
			.count() / (float) familySize) * 100;
	}

	public double percentDecisionNoDecision()
	{
		return (lines.stream()
			.filter(line -> line != parent1)
			.filter(line -> line != parent2)
			.filter(line -> line != expF1)
			.filter(line -> decider.getDecision(line.getLineResults().getPedVerF1sResult()) == NO_DECISION)
			.count() / (float) familySize) * 100;
	}

	public double percentDecisionLikeP1()
	{
		return (lines.stream()
			.filter(line -> line != parent1)
			.filter(line -> line != parent2)
			.filter(line -> line != expF1)
			.filter(line -> decider.getDecision(line.getLineResults().getPedVerF1sResult()) == LIKE_P1)
			.count() / (float) familySize) * 100;
	}

	public double percentDecisionLikeP2()
	{
		return (lines.stream()
			.filter(line -> line != parent1)
			.filter(line -> line != parent2)
			.filter(line -> line != expF1)
			.filter(line -> decider.getDecision(line.getLineResults().getPedVerF1sResult()) == LIKE_P2)
			.count() / (float) familySize) * 100;
	}

	public double percentDataAvg()
	{
		if (percentDataAvg == null)
		{
			percentDataAvg = lines.stream()
				.filter(line -> line != parent1)
				.filter(line -> line != parent2)
				.filter(line -> line != expF1)
				.mapToDouble(line -> line.getLineResults().getPedVerF1sResult().getPercentData())
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
				.filter(line -> line != expF1)
				.mapToDouble(line -> line.getLineResults().getPedVerF1sResult().getPercentHeterozygous())
				.filter(value -> !Double.isNaN(value))
				.average()
				.orElse(Double.NaN);
		}

		return percentHetAvg;
	}

	public double similarityToP1Avg()
	{
		if (similarityToP1Avg == null)
		{
			similarityToP1Avg = lines.stream()
				.filter(line -> line != parent1)
				.filter(line -> line != parent2)
				.filter(line -> line != expF1)
				.mapToDouble(line -> line.getLineResults().getPedVerF1sResult().getSimilarityToP1())
				.filter(value -> !Double.isNaN(value))
				.average()
				.orElse(Double.NaN);
		}

		return similarityToP1Avg;
	}

	public double similarityToP2Avg()
	{
		if (similarityToP2Avg == null)
		{
			return lines.stream()
				.filter(line -> line != parent1)
				.filter(line -> line != parent2)
				.filter(line -> line != expF1)
				.mapToDouble(line -> line.getLineResults().getPedVerF1sResult().getSimilarityToP2())
				.filter(value -> !Double.isNaN(value))
				.average()
				.orElse(Double.NaN);
		}

		return similarityToP2Avg;
	}

	public double percentAlleleMatchExpectedAvg()
	{
		if (percentAlleleMatchExpectedAvg == null)
		{
			return lines.stream()
				.filter(line -> line != parent1)
				.filter(line -> line != parent2)
				.filter(line -> line != expF1)
				.mapToDouble(line -> line.getLineResults().getPedVerF1sResult().getPercentAlleleMatchExpected())
				.filter(value -> !Double.isNaN(value))
				.average()
				.orElse(Double.NaN);
		}

		return percentAlleleMatchExpectedAvg;
	}
}