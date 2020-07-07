// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
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

	// "Fake" marker score info
	private String markerName;
	private int markerStateCode;

	public IFBQTLScore()
	{
	}

	public IFBQTLScore(QTL qtl)
	{
		this.qtl = qtl;
	}

	// Used to store a "fake" marker score, for markers not under a QTL
	public IFBQTLScore(String markerName, int markerStateCode)
	{
		this.markerName = markerName;
		this.markerStateCode = markerStateCode;
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

	public String getMarkerName()
		{ return markerName; }

	public void setMarkerName(String markerName)
		{ this.markerName = markerName; }

	public int getMarkerStateCode()
		{ return markerStateCode; }

	public void setMarkerStateCode(int markerStateCode)
		{ this.markerStateCode = markerStateCode; }


	// Non-XML methods
	public String qtlGenotype()
	{
		String ref = properties.getAlleleName();
		String alt = properties.getAlleleNameAlt();

		switch (refAlleleMatchCount)
		{
			case -1: return "NA";
			case 0:  return alt + "/" + alt;    //   -/-
			case 1:  return ref + "/" + alt;    //   +/-
			case 2:  return ref + "/" + ref;    //   +/+
		}

		return alt + "/" + alt;
	}

	public String getMarkerAlleles(StateTable st)
	{
		AlleleState as = st.getAlleleState(markerStateCode);
		return as.toString();
	}
}