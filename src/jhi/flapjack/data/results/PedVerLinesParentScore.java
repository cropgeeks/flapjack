// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

public class PedVerLinesParentScore extends XMLRoot
{
	private int dataParentMatch;
	private int matchParentCount;
	private double matchParentPercent;

	public int getDataParentMatch()
		{ return dataParentMatch; }

	public void setDataParentMatch(int dataParentMatch)
		{ this.dataParentMatch = dataParentMatch; }

	public int getMatchParentCount()
		{ return matchParentCount; }

	public void setMatchParentCount(int matchParentCount)
		{ this.matchParentCount = matchParentCount; }

	public double getMatchParentPercent()
		{ return matchParentPercent; }

	public void setMatchParentPercent(double matchParentPercent)
		{ this.matchParentPercent = matchParentPercent; }
}