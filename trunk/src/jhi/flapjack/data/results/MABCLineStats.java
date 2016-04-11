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
	private ArrayList<Double> sumRP = new ArrayList<>();
	private ArrayList<Double> sumDO = new ArrayList<>();

	private double rppTotal;
	private double coverage;


	// One per QTL
	private ArrayList<QTLScore> qtlScores = new ArrayList<>();

	public MABCLineStats(LineInfo line, int chrCount)
	{
		this.line = line;

		for (int i = 0; i < chrCount; i++)
		{
			sumRP.add(0.0);
			sumDO.add(0.0);
		}
	}

	public ArrayList<QTLScore> getQTLScores()
		{ return qtlScores; }

	// TODO: Lose - coverage only needs to be stored ONCE, not for every line
	public double getCoverage()
		{ return coverage; }

	public void setCoverage(double coverage)
		{ this.coverage = coverage; }
	/////////////////////////////////////////////////////////////////////////

	public LineInfo getLineInfo()
		{ return line; }

	public double getRPPTotal()
		{ return rppTotal; }

	public void setRppTotal(double rppTotal)
		{ this.rppTotal = rppTotal; }

	public ArrayList<Double> getSumRP()
		{ return sumRP; }

	public ArrayList<Double> getSumDO()
		{ return sumDO; }

	// Fudge to do a += on values held in the ArrayList
	public void updateRP(int index, double value)
	{
		sumRP.set(index, sumRP.get(index) + value);
	}

	public void updateDO(int index, double value)
	{
		sumDO.set(index, sumDO.get(index) + value);
	}

	public String toString()
	{
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();

		String str = "";
		for (double d: sumRP)
			str += nf.format(d) + "\t";
		str += nf.format(rppTotal);

		return str;
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