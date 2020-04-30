// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;

import jhi.flapjack.data.*;

public class IFBQTLScore extends XMLRoot
{
	// A reference to the qtl this score relates to
	private QTL qtl;

	// A list of underlying markers that are associated with the QTL
	private ArrayList<IFBMarkerScore> markerScores = new ArrayList<>();

	// A count of how many alleles (for the owning line) match this marker's ref
	// This is a consensus call across all markers that lie under this QTL
	private int refAlleleMatchCount;

	// Display string for the reference allele
	private String alleleName = "-";

	public IFBQTLScore()
	{
	}

	public IFBQTLScore(QTL qtl)
	{
		this.qtl = qtl;
	}

	public QTL getQtl()
		{ return qtl; }

	public void setQtl(QTL qtl)
		{ this.qtl = qtl; }

	public ArrayList<IFBMarkerScore> getMarkerScores()
		{ return markerScores; }

	public void setMarkerScores(ArrayList<IFBMarkerScore> markerScores)
		{ this.markerScores = markerScores; }

	public int getRefAlleleMatchCount()
		{ return refAlleleMatchCount; }

	public void setRefAlleleMatchCount(int refAlleleMatchCount)
		{ this.refAlleleMatchCount = refAlleleMatchCount; }

	public String getAlleleName()
		{ return alleleName; }

	public void setAlleleName(String alleleName)
		{ this.alleleName = alleleName; }
}