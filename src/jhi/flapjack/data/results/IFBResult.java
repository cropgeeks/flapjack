// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
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

	// Only set to true if we have a valid refAlleleCount for each QTL
	private boolean mbvValid;

	// Holds sums of MBV/wMBV across all QTL
	private double mbvTotal;
	private double wmbvTotal;
	// Holds sums of MBV/wMBV adjusted for QTL with no missing data
	private double mbvTotal2;
	private double wmbvTotal2;

	// A count of how many valid QTLs were used to get the MBV - this might not
	// be the same as mbvScores.size() as some of them could be NaN
	private int qtlsUsedForMBV;

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

	public boolean isMbvValid()
		{ return mbvValid; }

	public void setMbvValid(boolean mbvValid)
		{ this.mbvValid = mbvValid; }

	public double getMbvTotal()
		{ return mbvTotal; }

	public void setMbvTotal(double mbvTotal)
		{ this.mbvTotal = mbvTotal; }

	public double getWmbvTotal()
		{ return wmbvTotal; }

	public void setWmbvTotal(double wmbvTotal)
		{ this.wmbvTotal = wmbvTotal; }

	public double getMbvTotal2()
		{ return mbvTotal2; }

	public void setMbvTotal2(double mbvTotal2)
		{ this.mbvTotal2 = mbvTotal2; }

	public double getWmbvTotal2()
		{ return wmbvTotal2; }

	public void setWmbvTotal2(double wmbvTotal2)
		{ this.wmbvTotal2 = wmbvTotal2; }

	public int getQtlsUsedForMBV()
		{ return qtlsUsedForMBV; }

	public void setQtlsUsedForMBV(int qtlsUsedForMBV)
		{ this.qtlsUsedForMBV = qtlsUsedForMBV; }

	// Attempts to return the index of the IFBQTLScore object that matches the
	// given QTL name
	public int qtlScoreIndexByName(String name)
	{
		for (int i = 0; i < qtlScores.size(); i++)
			if (qtlScores.get(i).getQtl().getName().equals(name))
				return i;

		return -1;
	}
}