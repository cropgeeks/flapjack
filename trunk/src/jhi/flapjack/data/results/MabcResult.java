// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;

import jhi.flapjack.data.*;

public class MabcResult extends XMLRoot
{
	// What line are these stats associated with
	private LineInfo line;

	// One per chromosome...
	private ArrayList<MabcChrScore> chrScores = new ArrayList<>();
	private double rppTotal;
	private double genomeCoverage;

	// One per QTL
	private ArrayList<MabcQtlScore> qtlScores = new ArrayList<>();
	// Count of qtl (status=1) (sum of status count)
	private int qtlStatusCount;

	public MabcResult()
	{
	}

	public MabcResult(LineInfo line)
	{
		this.line = line;
	}


	// Methods required for XML serialization

	public ArrayList<MabcChrScore> getChrScores()
		{ return chrScores; }

	public void setChrScores(ArrayList<MabcChrScore> chrScores)
		{ this.chrScores = chrScores; }

	public ArrayList<MabcQtlScore> getQtlScores()
		{ return qtlScores; }

	public void setQtlScores(ArrayList<MabcQtlScore> qtlScores)
		{ this.qtlScores = qtlScores; }

	public int getQtlStatusCount()
		{ return qtlStatusCount; }

	public void setQtlStatusCount(int qtlStatusCount)
		{ this.qtlStatusCount = qtlStatusCount; }

	public double getGenomeCoverage()
		{ return genomeCoverage; }

	public void setGenomeCoverage(double genomeCoverage)
		{ this.genomeCoverage = genomeCoverage; }

	public LineInfo getLineInfo()
		{ return line; }

	public void setLineInfo(LineInfo line)
		{ this.line = line; }

	public double getRppTotal()
		{ return rppTotal; }

	public void setRppTotal(double rppTotal)
		{ this.rppTotal = rppTotal; }


	// Other methods

	public void updateAndAddGenomeCoverage(double value)
	{
		genomeCoverage += value;
	}
}