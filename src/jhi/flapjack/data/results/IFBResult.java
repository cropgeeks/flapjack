// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

import java.util.*;

public class IFBResult extends XMLRoot
{
	// All QTLs
	private ArrayList<IFBQTLScore> qtlScores = new ArrayList<>();
	// Just the ones having mbv values
	private ArrayList<IFBQTLScore> mbvScores = new ArrayList<>();

	// 'Fake' (markers not under a QTL) information
	private ArrayList<IFBQTLScore> mkrScores = new ArrayList<>();

	private double mbvTotal;
	private double wmbvTotal;

	public IFBResult()
	{
	}

	public ArrayList<IFBQTLScore> getQtlScores()
		{ return qtlScores; }

	public void setQtlScores(ArrayList<IFBQTLScore> qtlScores)
		{ this.qtlScores = qtlScores; }

	public ArrayList<IFBQTLScore> getMbvScores()
		{ return mbvScores; }

	public void setMbvScores(ArrayList<IFBQTLScore> mbvScores)
		{ this.mbvScores = mbvScores; }

	public ArrayList<IFBQTLScore> getMkrScores()
		{ return mkrScores; }

	public void setMkrScores(ArrayList<IFBQTLScore> mkrScores)
		{ this.mkrScores = mkrScores; }

	public double getMbvTotal()
		{ return mbvTotal; }

	public void setMbvTotal(double mbvTotal)
		{ this.mbvTotal = mbvTotal; }

	public double getWmbvTotal()
		{ return wmbvTotal; }

	public void setWmbvTotal(double wmbvTotal)
		{ this.wmbvTotal = wmbvTotal; }
}