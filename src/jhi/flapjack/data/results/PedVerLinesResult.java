// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

public class PedVerLinesResult extends XMLRoot
{
	private int dataCount;
	private double percentData;
	private int hetCount;
	private double percentHet;
	private double similarityToP1;
	private double similarityToP2;
	private double similarityToParents;
	private boolean p1;
	private boolean p2;

	// Removed but left in for compatibility with old projects
	private double percentTotalMatch;

	public int getDataCount()
		{  return dataCount; }

	public void setDataCount(int dataCount)
		{  this.dataCount = dataCount; }

	public double getPercentData()
		{ return percentData; }

	public void setPercentData(double percentData)
		{  this.percentData = percentData; }

	public int getHetCount()
		{  return hetCount; }

	public void setHetCount(int hetCount)
		{  this.hetCount = hetCount; }

	public double getPercentHet()
		{  return percentHet; }

	public void setPercentHet(double percentHet)
		{  this.percentHet = percentHet; }

	public double getSimilarityToP1()
	{
		return similarityToP1;
	}

	public void setSimilarityToP1(double similarityToP1)
	{
		this.similarityToP1 = similarityToP1;
	}

	public double getSimilarityToP2()
	{
		return similarityToP2;
	}

	public void setSimilarityToP2(double similarityToP2)
	{
		this.similarityToP2 = similarityToP2;
	}

	public double getSimilarityToParents()
	{
		return similarityToParents;
	}

	public void setSimilarityToParents(double similarityToParents)
	{
		this.similarityToParents = similarityToParents;
	}

	// Removed but left in for compatibility with old projects
	public double getPercentTotalMatch()
	{ return percentTotalMatch; }

	public void setPercentTotalMatch(double percentTotalMatch)
	{ this.percentTotalMatch = percentTotalMatch; }

	public boolean isP1()
		{ return p1; }

	public void setP1(boolean p1)
		{ this.p1 = p1; }

	public boolean isP2()
		{ return p2; }

	public void setP2(boolean p2)
		{ this.p2 = p2; }
}