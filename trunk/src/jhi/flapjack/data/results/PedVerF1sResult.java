// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
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
	private int countP1Contained;
	private double percentP1Contained;
	private int countP2Contained;
	private double percentP2Contained;
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
	{
		this.percentDeviationFromExpected = percentDeviationFromExpected;
	}

	public int getCountP1Contained()
	{
		return countP1Contained;
	}

	public void setCountP1Contained(int countP1Contained)
	{
		this.countP1Contained = countP1Contained;
	}

	public double getPercentP1Contained()
	{
		return percentP1Contained;
	}

	public void setPercentP1Contained(double percentP1Contained)
	{
		this.percentP1Contained = percentP1Contained;
	}

	public int getCountP2Contained()
	{
		return countP2Contained;
	}

	public void setCountP2Contained(int countP2Contained)
	{
		this.countP2Contained = countP2Contained;
	}

	public double getPercentP2Contained()
	{
		return percentP2Contained;
	}

	public void setPercentP2Contained(double percentP2Contained)
	{
		this.percentP2Contained = percentP2Contained;
	}

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