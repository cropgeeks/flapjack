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

	// Direct reference to an identified MarkerProperties object we can use for
	// IFB calculations
	private MarkerProperties properties;

	// MolecularBreedingValue
	private double molecularBreedingValue;
	// WeightedMBV
	private double weightedMolecularBreedingValue;

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

	public MarkerProperties getProperties()
		{ return properties; }

	public void setProperties(MarkerProperties properties)
		{ this.properties = properties; }

	public double getMolecularBreedingValue()
		{ return molecularBreedingValue; }

	public void setMolecularBreedingValue(double molecularBreedingValue)
		{ this.molecularBreedingValue = molecularBreedingValue; }

	public double getWeightedMolecularBreedingValue()
		{ return weightedMolecularBreedingValue; }

	public void setWeightedMolecularBreedingValue(double weightedMolecularBreedingValue)
		{ this.weightedMolecularBreedingValue = weightedMolecularBreedingValue; }


	// Non-XML methods
	public String qtlGenotype()
	{
		String str = properties.getAlleleName();

		switch (refAlleleMatchCount)
		{
			case 0: return "-/-";
			case 1: return str + "/-";
			case 2: return str + "/" + str;
		}

		return "-/-";
	}
}