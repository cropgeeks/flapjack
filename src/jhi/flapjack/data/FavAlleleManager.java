// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

import java.util.*;

public class FavAlleleManager extends XMLRoot
{
	private Map<String, ArrayList<Integer>> favAlleles = new HashMap<>();
	private Map<String, ArrayList<Integer>> unfavAlleles = new HashMap<>();

	private Map<String, MarkerFavAlleles> haplotypeAlleleManager = new HashMap<>();

	// Stores (possible) alternative names for each marker
	private Map<String, String> altNames = new HashMap<>();

	public FavAlleleManager()
	{
	}


	// Methods required for XML serialization

	public Map<String, ArrayList<Integer>> getFavAlleles()
		{ return favAlleles; }

	public void setFavAlleles(Map<String, ArrayList<Integer>> favAlleles)
		{ this.favAlleles = favAlleles;	}

	public Map<String, ArrayList<Integer>> getUnfavAlleles()
		{ return unfavAlleles; }

	public void setUnfavAlleles(Map<String, ArrayList<Integer>> unfavAlleles)
		{ this.unfavAlleles = unfavAlleles; }

	public Map<String, String> getAltNames()
		{ return altNames; }

	public void setAltNames(Map<String, String> alternateNames)
		{ this.altNames = alternateNames; }

	public Map<String, MarkerFavAlleles> getHaplotypeAlleleManager()
		{ return haplotypeAlleleManager; }

	public void setHaplotypeAlleleManager(Map<String, MarkerFavAlleles> haplotypeAlleleManager)
		{ this.haplotypeAlleleManager = haplotypeAlleleManager; }


	// Other methods

	public void addFavAllelesForMarker(String markerName, ArrayList<Integer> favIndices)
	{
		favAlleles.put(markerName, favIndices);
	}

	public void addUnfavAllelesForMarker(String markerName, ArrayList<Integer> unfavIndices)
	{
		unfavAlleles.put(markerName, unfavIndices);
	}

	public void addHaplotypeAlleles(String haplotypeName, String markerName, ArrayList<Integer> alleleIndices)
	{
		haplotypeAlleleManager.putIfAbsent(haplotypeName, new MarkerFavAlleles());
		haplotypeAlleleManager.get(haplotypeName).addAllelesForMarker(markerName, alleleIndices);
	}

	public ArrayList<Integer> haplotypeAlleles(String haplotypeName, String markerName)
	{
		return haplotypeAlleleManager.get(haplotypeName).allelesForMarker(markerName);
	}
}