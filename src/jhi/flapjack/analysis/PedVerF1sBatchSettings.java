// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import jhi.flapjack.data.*;

public class PedVerF1sBatchSettings
{
	private GTViewSet viewSet;
	private int parent1Index;
	private int parent2Index;

	public PedVerF1sBatchSettings(GTViewSet viewSet, int parent1Index, int parent2Index)
	{
		this.viewSet = viewSet;
		this.parent1Index = parent1Index;
		this.parent2Index = parent2Index;
	}

	public GTViewSet getViewSet()
	{
		return viewSet;
	}

	public void setViewSet(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
	}

	public int getParent1Index()
	{
		return parent1Index;
	}

	public void setParent1Index(int parent1Index)
	{
		this.parent1Index = parent1Index;
	}

	public int getParent2Index()
	{
		return parent2Index;
	}

	public void setParent2Index(int parent2Index)
	{
		this.parent2Index = parent2Index;
	}
}