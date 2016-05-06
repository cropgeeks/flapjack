package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

/**
 * Created by gs40939 on 04/05/2016.
 */
public class PedVerKnownParentsLineStats
{
	// What line are these stats associated with
	private LineInfo line;

	private int markerCount;
	private float percentMissing;
	private int heterozygousCount;
	private float percentHeterozygous;
	private float percentDeviationFromExpected;
	private int countP1Contained;
	private float percentP1Contained;
	private int countP2Contained;
	private float percentP2Contained;
	private int countAlleleMatchExpected;
	private float percentAlleleMatchExpected;

	public PedVerKnownParentsLineStats(LineInfo line)
	{

	}

	public LineInfo getLine()
	{
		return line;
	}

	public void setLine(LineInfo line)
	{
		this.line = line;
	}

	public int getMarkerCount()
	{
		return markerCount;
	}

	public void setMarkerCount(int markerCount)
	{
		this.markerCount = markerCount;
	}

	public float getPercentMissing()
	{
		return percentMissing;
	}

	public void setPercentMissing(float percentMissing)
	{
		this.percentMissing = percentMissing;
	}

	public int getHeterozygousCount()
	{
		return heterozygousCount;
	}

	public void setHeterozygousCount(int heterozygousCount)
	{
		this.heterozygousCount = heterozygousCount;
	}

	public float getPercentHeterozygous()
	{
		return percentHeterozygous;
	}

	public void setPercentHeterozygous(float percentHeterozygous)
	{
		this.percentHeterozygous = percentHeterozygous;
	}

	public float getPercentDeviationFromExpected()
	{
		return percentDeviationFromExpected;
	}

	public void setPercentDeviationFromExpected(float percentDeviationFromExpected)
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

	public float getPercentP1Contained()
	{
		return percentP1Contained;
	}

	public void setPercentP1Contained(float percentP1Contained)
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

	public float getPercentP2Contained()
	{
		return percentP2Contained;
	}

	public void setPercentP2Contained(float percentP2Contained)
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

	public float getPercentAlleleMatchExpected()
	{
		return percentAlleleMatchExpected;
	}

	public void setPercentAlleleMatchExpected(float percentAlleleMatchExpected)
	{
		this.percentAlleleMatchExpected = percentAlleleMatchExpected;
	}

	@Override
	public String toString()
	{
		return "PedVerKnownParentsLineStats{" +
			"line=" + line +
			", markerCount=" + markerCount +
			", percentMissing=" + percentMissing +
			", heterozygousCount=" + heterozygousCount +
			", percentHeterozygous=" + percentHeterozygous +
			", percentDeviationFromExpected=" + percentDeviationFromExpected +
			", countP1Contained=" + countP1Contained +
			", percentP1Contained=" + percentP1Contained +
			", countP2Contained=" + countP2Contained +
			", percentP2Contained=" + percentP2Contained +
			", countAlleleMatchExpected=" + countAlleleMatchExpected +
			", percentAlleleMatchExpected=" + percentAlleleMatchExpected +
			'}';
	}
}
