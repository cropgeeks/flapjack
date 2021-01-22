// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

import java.util.*;

public class FBResult extends XMLRoot
{
	private int dataCount;
	private double percentData;
	private int heterozygousCount;
	private double percentHeterozygous;
	private ArrayList<String> haplotypeNames = new ArrayList<>();
	private ArrayList<Double> haplotypePartialMatch = new ArrayList<>();
	private ArrayList<Integer> haplotypeAlleleCounts = new ArrayList<>();
	private ArrayList<Double> haplotypeMatch = new ArrayList<>();
	private ArrayList<Double> haplotypeWeight = new ArrayList<>();
	private double averageWeightedHapMatch;
	private double averageHapMatch;

	public FBResult()
	{
	}

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

	public ArrayList<String> getHaplotypeNames()
		{ return haplotypeNames; }

	public void setHaplotypeNames(ArrayList<String> haplotypeNames)
		{ this.haplotypeNames = haplotypeNames; }

	public ArrayList<Double> getHaplotypePartialMatch()
		{ return haplotypePartialMatch; }

	public void setHaplotypePartialMatch(ArrayList<Double> haplotypePartialMatch)
		{ this.haplotypePartialMatch = haplotypePartialMatch; }

	public ArrayList<Integer> getHaplotypeAlleleCounts()
		{ return haplotypeAlleleCounts; }

	public void setHaplotypeAlleleCounts(ArrayList<Integer> haplotypeAlleleCounts)
		{ this.haplotypeAlleleCounts = haplotypeAlleleCounts; }

	public ArrayList<Double> getHaplotypeMatch()
		{ return haplotypeMatch; }

	public void setHaplotypeMatch(ArrayList<Double> haplotypeMatch)
		{ this.haplotypeMatch = haplotypeMatch; }

	public ArrayList<Double> getHaplotypeWeight()
		{ return haplotypeWeight; }

	public void setHaplotypeWeight(ArrayList<Double> haplotypeWeight)
		{ this.haplotypeWeight = haplotypeWeight; }

	public double getAverageWeightedHapMatch()
		{ return averageWeightedHapMatch; }

	public void setAverageWeightedHapMatch(double averageWeightedHapMatch)
		{ this.averageWeightedHapMatch = averageWeightedHapMatch; }

	public double getAverageHapMatch()
		{ return averageHapMatch; }

	public void setAverageHapMatch(double averageHapMatch)
		{ this.averageHapMatch = averageHapMatch; }
}