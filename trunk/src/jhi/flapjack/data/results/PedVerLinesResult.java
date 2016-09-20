package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

import java.util.*;

public class PedVerLinesResult extends XMLRoot
{
	private int markerCount;
	private double missingPerc;
	private int hetCount;
	private double hetPerc;
	private int matchCount;
	private double matchPerc;

	private ArrayList<Integer> chrMatchCount = new ArrayList<>();

	public int getMarkerCount()
		{ return markerCount; }

	public void setMarkerCount(int markerCount)
		{ this.markerCount = markerCount; }

	public double getMissingPerc()
		{ return missingPerc; }

	public void setMissingPerc(double missingPerc)
		{ this.missingPerc = missingPerc; }

	public int getHetCount()
		{ return hetCount; }

	public void setHetCount(int hetCount)
		{ this.hetCount = hetCount; }

	public double getHetPerc()
		{ return hetPerc; }

	public void setHetPerc(double hetPerc)
		{ this.hetPerc = hetPerc; }

	public int getMatchCount()
		{ return matchCount; }

	public void setMatchCount(int matchCount)
		{ this.matchCount = matchCount; }

	public double getMatchPerc()
		{ return matchPerc; }

	public void setMatchPerc(double matchPerc)
		{ this.matchPerc = matchPerc; }

	public ArrayList<Integer> getChrMatchCount()
		{ return chrMatchCount; }

	public void setChrMatchCount(ArrayList<Integer> chrMatchCount)
		{ this.chrMatchCount = chrMatchCount; }
}
