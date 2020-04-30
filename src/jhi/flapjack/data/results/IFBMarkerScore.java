// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

public class IFBMarkerScore extends XMLRoot
{
	// A reference to the marker this score relates to
	private MarkerProperties properties;

	// A count of how many alleles (for the owning line) match this marker's ref
	private int refAlleleMatchCount;

	public IFBMarkerScore()
	{
	}

	public IFBMarkerScore(MarkerProperties properties)
	{
		this.properties = properties;
	}

	public MarkerProperties getProperties()
		{ return properties; }

	public void setProperties(MarkerProperties properties)
		{ this.properties = properties; }

	public int getRefAlleleMatchCount()
		{ return refAlleleMatchCount; }

	public void setRefAlleleMatchCount(int refAlleleMatchCount)
		{ this.refAlleleMatchCount = refAlleleMatchCount; }
}