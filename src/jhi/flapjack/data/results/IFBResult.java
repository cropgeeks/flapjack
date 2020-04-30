// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

import java.util.*;

public class IFBResult extends XMLRoot
{
	private ArrayList<IFBQTLScore> qtlScores = new ArrayList<>();

	public IFBResult()
	{
	}

	public ArrayList<IFBQTLScore> getQtlScores()
		{ return qtlScores; }

	public void setQtlScores(ArrayList<IFBQTLScore> qtlScores)
		{ this.qtlScores = qtlScores; }

}