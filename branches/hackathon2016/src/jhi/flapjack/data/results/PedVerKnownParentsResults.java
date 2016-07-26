package jhi.flapjack.data.results;

import java.util.*;

/**
 * Created by gs40939 on 04/05/2016.
 */
public class PedVerKnownParentsResults
{
	private int f1MarkerCount;
	private int f1HeterozygousCount;
	private float f1PercentHeterozygous;

	private ArrayList<PedVerKnownParentsLineStats> lineStats;

	public PedVerKnownParentsResults(int f1MarkerCount, int f1HeterozygousCount, float f1PercentHeterozygous, ArrayList<PedVerKnownParentsLineStats> lineStats)
	{
		this.f1MarkerCount = f1MarkerCount;
		this.f1HeterozygousCount = f1HeterozygousCount;
		this.f1PercentHeterozygous = f1PercentHeterozygous;
		this.lineStats = lineStats;
	}

	public int getF1MarkerCount()
	{
		return f1MarkerCount;
	}

	public void setF1MarkerCount(int f1MarkerCount)
	{
		this.f1MarkerCount = f1MarkerCount;
	}

	public int getF1HeterozygousCount()
	{
		return f1HeterozygousCount;
	}

	public void setF1HeterozygousCount(int f1HeterozygousCount)
	{
		this.f1HeterozygousCount = f1HeterozygousCount;
	}

	public float getF1PercentHeterozygous()
	{
		return f1PercentHeterozygous;
	}

	public void setF1PercentHeterozygous(float f1PercentHeterozygous)
	{
		this.f1PercentHeterozygous = f1PercentHeterozygous;
	}

	public ArrayList<PedVerKnownParentsLineStats> getLineStats()
	{
		return lineStats;
	}

	public void setLineStats(ArrayList<PedVerKnownParentsLineStats> lineStats)
	{
		this.lineStats = lineStats;
	}

	public int size()
	{
		return lineStats.size();
	}
}
