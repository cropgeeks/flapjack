// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
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

	// Additional stats
	private int dataCount;
	private double percentData;
	private int heterozygousCount;
	private double percentHeterozygous;

	private boolean isRP;
	private boolean isDP;

	private MABCThresholds thresholds;

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

	public int getDataCount()
		{ return dataCount; }

	public void setDataCount(int dataCount)
		{ this.dataCount = dataCount; }

	public double getPercentData()
		{ return percentData; }

	public void setPercentData(double percentData)
		{ this.percentData = percentData; }

	public int getHeterozygousCount()
		{ return heterozygousCount; }

	public void setHeterozygousCount(int heterozygousCount)
		{ this.heterozygousCount = heterozygousCount; }

	public double getPercentHeterozygous()
		{ return percentHeterozygous; }

	public void setPercentHeterozygous(double percentHeterozygous)
		{ this.percentHeterozygous = percentHeterozygous; }

	public boolean isIsRP()
		{ return isRP; }

	public void setIsRP(boolean isRP)
		{ this.isRP = isRP; }

	public boolean isIsDP()
		{ return isDP; }

	public void setIsDP(boolean isDP)
		{ this.isDP = isDP; }

	public MABCThresholds getThresholds()
		{ return thresholds; }

	public void setThresholds(MABCThresholds thresholds)
		{ this.thresholds = thresholds; }

	// Other methods

	public void updateAndAddGenomeCoverage(double value)
	{
		genomeCoverage += value;
	}

	public String calculateDecisionString()
	{
		if (isRP)
			return "Recurrent parent";

		else if (isDP)
			return "Donor parent";

		else if (percentData >= thresholds.getPercData() && (rppTotal*100) >= thresholds.getRppTotal()
			&& qtlStatusCount >= thresholds.getQtlAlleleCount())
			return "Select";

		else
			return "No decision";
	}
}