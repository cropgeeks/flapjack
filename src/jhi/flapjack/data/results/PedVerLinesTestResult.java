package jhi.flapjack.data.results;

/**
 * Created by gs40939 on 01/06/2016.
 */
public class PedVerLinesTestResult
{
	private int testMarkerCount;
	private float testMarkerPresentPercentage;
	private int testHetCount;
	private float testHetPercentage;

	public PedVerLinesTestResult()
	{

	}

	public void set(int testMarkerCount, float testMarkerPresentPercentage, int testHetCount, float testHetPercentage)
	{
		this.testMarkerCount = testMarkerCount;
		this.testMarkerPresentPercentage = testMarkerPresentPercentage;
		this.testHetCount = testHetCount;
		this.testHetPercentage = testHetPercentage;
	}

	public int getTestMarkerCount()
	{
		return testMarkerCount;
	}

	public void setTestMarkerCount(int testMarkerCount)
	{
		this.testMarkerCount = testMarkerCount;
	}

	public float getTestMarkerPresentPercentage()
	{
		return testMarkerPresentPercentage;
	}

	public void setTestMarkerPresentPercentage(float testMarkerPresentPercentage)
	{
		this.testMarkerPresentPercentage = testMarkerPresentPercentage;
	}

	public int getTestHetCount()
	{
		return testHetCount;
	}

	public void setTestHetCount(int testHetCount)
	{
		this.testHetCount = testHetCount;
	}

	public float getTestHetPercentage()
	{
		return testHetPercentage;
	}

	public void setTestHetPercentage(float testHetPercentage)
	{
		this.testHetPercentage = testHetPercentage;
	}
}
