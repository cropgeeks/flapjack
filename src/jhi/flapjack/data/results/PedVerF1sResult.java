// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
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
	private int countAlleleMatchExpected;
	private double percentAlleleMatchExpected;

	public PedVerF1sResult()
	{
	}

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

	public int getCountAlleleMatchExpected()
	{
		return countAlleleMatchExpected;
	}

	public void setCountAlleleMatchExpected(int countAlleleMatchExpected)
	{
		this.countAlleleMatchExpected = countAlleleMatchExpected;
	}

	public double getPercentAlleleMatchExpected()
	{
		return percentAlleleMatchExpected;
	}

	public void setPercentAlleleMatchExpected(double percentAlleleMatchExpected)
	{
		this.percentAlleleMatchExpected = percentAlleleMatchExpected;
	}
}