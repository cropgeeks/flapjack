package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

import java.util.*;

public class PedVerLinesResult extends XMLRoot
{
	private int dataCount;
	private int markerCount;
	private int missingCount;
	private double percentMissing;
	private int hetCount;
	private double percentHet;
	private int dataTotalMatch;
	private int totalMatch;
	private double percentTotalMatch;

	// One for each parent
	private ArrayList<PedVerLinesParentScore> parentScores = new ArrayList<>();

	public int getDataCount()
		{ return dataCount; }

	public void setDataCount(int dataCount)
		{  this.dataCount = dataCount; }

	public int getMarkerCount()
		{  return markerCount; }

	public void setMarkerCount(int markerCount)
		{  this.markerCount = markerCount; }

	public int getMissingCount()
		{  return missingCount; }

	public void setMissingCount(int missingCount)
		{ this.missingCount = missingCount; }

	public double getPercentMissing()
		{ return percentMissing; }

	public void setPercentMissing(double percentMissing)
		{  this.percentMissing = percentMissing; }

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
