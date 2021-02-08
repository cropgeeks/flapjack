// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

import java.util.*;

public class MarkerFavAlleles extends XMLRoot
{
	private Map<String, ArrayList<Integer>> markerToAlleles = new HashMap<>();

	public MarkerFavAlleles()
	{
	}


	// Methods required for XML serialization

	public Map<String, ArrayList<Integer>> getMarkerToAlleles()
	{
		return markerToAlleles;
	}

	public void setMarkerToAlleles(Map<String, ArrayList<Integer>> markerToAlleles)
	{
		this.markerToAlleles = markerToAlleles;
	}


	// Other methods

	public void addAllelesForMarker(String markerName, ArrayList<Integer> alleleIndices)
	{
		markerToAlleles.put(markerName, alleleIndices);
	}

	public ArrayList<Integer> allelesForMarker(String markerName)
	{
		return markerToAlleles.get(markerName);
	}
}