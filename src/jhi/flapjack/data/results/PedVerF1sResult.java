// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

public class PedVerF1sResult extends XMLRoot
{
	private int dataCount;
	private double percentData;
	private int heterozygousCount;
	private double percentHeterozygous;
	private double percentDeviationFromExpected;
	private double similarityToP1;
	private double similarityToP2;
	private double percentAlleleMatchExpected;
	private boolean p1;
	private boolean p2;
	private boolean f1;
	private double parent1Heterozygosity;
	private double parent2Heterozygosity;
	private double f1Heterozygosity;

	private PedVerF1sThresholds thresholds;

	public PedVerF1sResult()
	{
	}

	boolean isLineHet()
	{
		return percentHeterozygous >= thresholds.getHetThreshold();
	}

	boolean isAlleleMatchExpected()
	{
		return percentAlleleMatchExpected >= thresholds.getF1Threshold();
	}

	boolean isLikeP1()
	{
		return similarityToP1 >= 85;
	}

	boolean isLikeP2()
	{
		return similarityToP2 >= 85;
	}

	// We can only make decisions about lines if their parents are suitably inbred and the f1 is heterozygous
	boolean canDetermineLineType()
	{
		boolean parent1Inbred = parent1Heterozygosity <= thresholds.getParentHetThreshold();
		boolean parent2Inbred = parent2Heterozygosity <= thresholds.getParentHetThreshold();
		boolean f1Het = f1Heterozygosity >= thresholds.getF1isHetThreshold();

		return parent1Inbred && parent2Inbred && f1Het;
	}

	// Methods required for XML serialization

	public int getDataCount()
	{
		return dataCount;
	}

	public void setDataCount(int dataCount)
	{
		this.dataCount = dataCount;
	}

	public double getPercentData()
	{
		return percentData;
	}

	public void setPercentData(double percentData)
	{
		this.percentData = percentData;
	}

	public int getHeterozygousCount()
	{
		return heterozygousCount;
	}

	public void setHeterozygousCount(int heterozygousCount)
	{
		this.heterozygousCount = heterozygousCount;
	}

	public double getPercentHeterozygous()
	{
		return percentHeterozygous;
	}

	public void setPercentHeterozygous(double percentHeterozygous)
	{
		this.percentHeterozygous = percentHeterozygous;
	}

	public double getPercentDeviationFromExpected()
	{
		return percentDeviationFromExpected;
	}

	public void setPercentDeviationFromExpected(double percentDeviationFromExpected)
		{ this.percentDeviationFromExpected = percentDeviationFromExpected; }

	public double getSimilarityToP1()
		{ return similarityToP1; }

	public void setSimilarityToP1(double similarityToP1)
		{ this.similarityToP1 = similarityToP1; }

	public double getSimilarityToP2()
		{ return similarityToP2; }

	public void setSimilarityToP2(double similarityToP2)
		{ this.similarityToP2 = similarityToP2; }

	public double getPercentAlleleMatchExpected()
	{
		return percentAlleleMatchExpected;
	}

	public void setPercentAlleleMatchExpected(double percentAlleleMatchExpected)
		{ this.percentAlleleMatchExpected = percentAlleleMatchExpected; }

	public boolean isP1()
		{ return p1; }

	public void setP1(boolean p1)
		{ this.p1 = p1; }

	public boolean isP2()
		{ return p2; }

	public void setP2(boolean p2)
		{ this.p2 = p2; }

	public boolean isF1()
		{ return f1; }

	public void setF1(boolean f1)
		{ this.f1 = f1; }

	public PedVerF1sThresholds getThresholds()
		{ return thresholds; }

	public void setThresholds(PedVerF1sThresholds thresholds)
		{ this.thresholds = thresholds; }

	public double getParent1Heterozygosity()
		{ return parent1Heterozygosity; }

	public void setParent1Heterozygosity(double parent1Heterozygosity)
		{ this.parent1Heterozygosity = parent1Heterozygosity; }

	public double getParent2Heterozygosity()
		{ return parent2Heterozygosity; }

	public void setParent2Heterozygosity(double parent2Heterozygosity)
		{ this.parent2Heterozygosity = parent2Heterozygosity; }

	public double getF1Heterozygosity()
		{ return f1Heterozygosity; }

	public void setF1Heterozygosity(double f1Heterozygosity)
		{ this.f1Heterozygosity = f1Heterozygosity; }


}