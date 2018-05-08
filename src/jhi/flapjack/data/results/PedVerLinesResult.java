// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

import java.util.*;

public class PedVerLinesResult extends XMLRoot
{
	private int dataCount;
	private double percentData;
	private int hetCount;
	private double percentHet;
	private int dataTotalMatch;
	private int totalMatch;
	private double percentTotalMatch;

	// One for each parent
	private ArrayList<PedVerLinesParentScore> parentScores = new ArrayList<>();

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

	public ArrayList<PedVerLinesParentScore> getParentScores()
		{  return parentScores; }

	public void setParentScores(ArrayList<PedVerLinesParentScore> parentScores)
		{ this.parentScores = parentScores; }

	public int getDataTotalMatch()
		{ return dataTotalMatch; }

	public void setDataTotalMatch(int dataTotalMatch)
		{ this.dataTotalMatch = dataTotalMatch; }

	public int getTotalMatch()
		{ return totalMatch; }

	public void setTotalMatch(int totalMatch)
		{ this.totalMatch = totalMatch; }

	public double getPercentTotalMatch()
		{ return percentTotalMatch; }

	public void setPercentTotalMatch(double percentTotalMatch)
		{ this.percentTotalMatch = percentTotalMatch; }
}