// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.data;

/**
 * Holds information needed by Flapjack to associate the dataset with a database
 * (of some sort).
 */
public class DBAssociation extends XMLRoot
{
	private String lineSearch = "";
	private String markerSearch = "";

	public DBAssociation()
	{
	}


	// Methods required for XML serialization

	public String getLineSearch()
		{ return lineSearch; }

	public void setLineSearch(String lineSearch)
		{ this.lineSearch = lineSearch; }

	public String getMarkerSearch()
		{ return markerSearch; }

	public void setMarkerSearch(String markerSearch)
		{ this.markerSearch = markerSearch; }


	// Other methods

	public boolean isLineSearchEnabled()
	{
		return lineSearch.length() > 0;
	}

	public boolean isMarkerSearchEnabled()
	{
		return markerSearch.length() > 0;
	}
}