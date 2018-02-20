package jhi.flapjack.data;

import java.util.*;

public class FavAlleleManager extends XMLRoot
{
	private Map<String, ArrayList<Integer>> favAlleles = new HashMap<>();
	private Map<String, ArrayList<Integer>> unfavAlleles = new HashMap<>();

	public FavAlleleManager()
	{
	}

	public Map<String, ArrayList<Integer>> getFavAlleles()
		{ return favAlleles; }

	public void setFavAlleles(Map<String, ArrayList<Integer>> favAlleles)
		{ this.favAlleles = favAlleles;	}

	public Map<String, ArrayList<Integer>> getUnfavAlleles()
		{ return unfavAlleles; }

	public void setUnfavAlleles(Map<String, ArrayList<Integer>> unfavAlleles)
		{ this.unfavAlleles = unfavAlleles; }

	public void addFavAllelesForMarker(String markerName, ArrayList<Integer> favIndices)
	{
		favAlleles.put(markerName, favIndices);
	}

	public void addUnfavAllelesForMarker(String markerName, ArrayList<Integer> unfavIndices)
	{
		unfavAlleles.put(markerName, unfavIndices);
	}
}
