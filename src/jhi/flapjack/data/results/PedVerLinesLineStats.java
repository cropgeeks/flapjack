package jhi.flapjack.data.results;

import java.util.*;

/**
 * Created by gs40939 on 30/05/2016.
 */
public class PedVerLinesLineStats
{
	private int markerCount;
	private float missingPerc;
	private int hetCount;
	private float hetPerc;
	private int matchCount;
	private float matchPerc;

	private ArrayList<Integer> chrMatchCount = new ArrayList<>();

	// Holds a reference to the test line's results (obtainable via any line)
	private PedVerLinesResults testResults;

	public int getMarkerCount()
		{ return markerCount; }

	public void setMarkerCount(int markerCount)
		{ this.markerCount = markerCount; }

	public float getMissingPerc()
		{ return missingPerc; }

	public void setMissingPerc(float missingPerc)
		{ this.missingPerc = missingPerc; }

	public int getHetCount()
		{ return hetCount; }

	public void setHetCount(int hetCount)
		{ this.hetCount = hetCount; }

	public float getHetPerc()
		{ return hetPerc; }

	public void setHetPerc(float hetPerc)
		{ this.hetPerc = hetPerc; }

	public int getMatchCount()
		{ return matchCount; }

	public void setMatchCount(int matchCount)
		{ this.matchCount = matchCount; }

	public float getMatchPerc()
		{ return matchPerc; }

	public void setMatchPerc(float matchPerc)
		{ this.matchPerc = matchPerc; }

	public ArrayList<Integer> getChrMatchCount()
		{ return chrMatchCount; }

	public void setChrMatchCount(ArrayList<Integer> chrMatchCount)
		{ this.chrMatchCount = chrMatchCount; }

	public PedVerLinesResults getPedVerLinesResults()
		{ return testResults; }

	public void setPedVerLinesResults(PedVerLinesResults testResults)
		{ this.testResults = testResults; }
}
