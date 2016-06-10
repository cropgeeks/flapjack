// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;

import jhi.flapjack.data.*;

public class MABCLineStats
{
	// What line are these stats associated with
	private LineInfo line;

	// One per chromosome...
	private ArrayList<ChrScore> chrScores = new ArrayList<>();
	private double rppTotal;
	private double genomeCoverage;

	// One per QTL
	private ArrayList<QTLScore> qtlScores = new ArrayList<>();

	public MABCLineStats(LineInfo line)
	{
		this.line = line;
	}

	public ArrayList<ChrScore> getChrScores()
		{ return chrScores; }

	public ArrayList<QTLScore> getQTLScores()
		{ return qtlScores; }

		public double getGenomeCoverage()
		{ return genomeCoverage; }

	public void setGenomeCoverage(double genomeCoverage)
		{ this.genomeCoverage = genomeCoverage; }

	public LineInfo getLineInfo()
		{ return line; }

	public double getRPPTotal()
		{ return rppTotal; }

	public void setRppTotal(double rppTotal)
		{ this.rppTotal = rppTotal; }


	public void updateAndAddGenomeCoverage(double value)
	{
		genomeCoverage += value;
	}

	public static class ChrScore
	{
		public GTView view;
		public double coverage, sumRP, sumDO;
	}

	public static class QTLScore
	{
		public QTLInfo qtl;
		public double drag;
		public boolean status = true;

		public QTLScore(QTLInfo qtl)
			{ this.qtl = qtl; }
	}
}