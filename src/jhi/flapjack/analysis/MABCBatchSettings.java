package jhi.flapjack.analysis;

import jhi.flapjack.data.*;

public class MABCBatchSettings
{
	private GTViewSet viewSet;
	private int rpIndex;
	private int dpIndex;

	public MABCBatchSettings(GTViewSet viewSet, int rpIndex, int dpIndex)
	{
		this.viewSet = viewSet;
		this.rpIndex = rpIndex;
		this.dpIndex = dpIndex;
	}

	public GTViewSet getViewSet()
	{
		return viewSet;
	}

	public void setViewSet(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
	}

	public int getRpIndex()
	{
		return rpIndex;
	}

	public void setRpIndex(int rpIndex)
	{
		this.rpIndex = rpIndex;
	}

	public int getDpIndex()
	{
		return dpIndex;
	}

	public void setDpIndex(int dpIndex)
	{
		this.dpIndex = dpIndex;
	}
}
