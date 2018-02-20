package jhi.flapjack.data;

import java.util.*;

public class FavAlleleManager extends XMLRoot
{
	private Map<String, int[]> favAlleles = new HashMap<>();
	private Map<String, int[]> unfavAlleles = new HashMap<>();

	public FavAlleleManager()
	{
	}

	public Map<String, int[]> getFavAlleles()
		{ return favAlleles; }

	public void setFavAlleles(Map<String, int[]> favAlleles)
		{ this.favAlleles = favAlleles; }

	public Map<String, int[]> getUnfavAlleles()
		{ return unfavAlleles; }

	public void setUnfavAlleles(Map<String, int[]> unfavAlleles)
		{ this.unfavAlleles = unfavAlleles; }

	public void addFavAllelesForMarker(String markerName, int[] favIndices)
	{
		favAlleles.put(markerName, favIndices);
	}

	public void addUnfavAllelesForMarker(String markerName, int[] unfavIndices)
	{
		unfavAlleles.put(markerName, unfavIndices);
	}
}
