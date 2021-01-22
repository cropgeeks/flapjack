// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

public class MabcChrScore extends XMLRoot
{
	public GTView view;
	public double coverage;
	public double sumRP;
	public double sumDO;

	public MabcChrScore()
	{
	}

	public GTView getView()
	{
		return view;
	}

	public void setView(GTView view)
	{
		this.view = view;
	}

	public double getCoverage()
	{
		return coverage;
	}

	public void setCoverage(double coverage)
	{
		this.coverage = coverage;
	}

	public double getSumRP()
	{
		return sumRP;
	}

	public void setSumRP(double sumRP)
	{
		this.sumRP = sumRP;
	}

	public double getSumDO()
	{
		return sumDO;
	}

	public void setSumDO(double sumDO)
	{
		this.sumDO = sumDO;
	}
}