// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

// Used to store additional GOBii/QTL properties about each marker

import java.util.*;

public class MarkerProperties extends XMLRoot
{
	public static final int NA = 0;
	public static final int ADDITIVE = 1;
	public static final int DOMINANT = 2;

	// A reference to the Marker these properties relate to
	private Marker marker;

	private ArrayList<Integer> favAlleles = new ArrayList<>();
	private ArrayList<Integer> unfavAlleles = new ArrayList<>();

	private String platform;
	private String alleleName;
	private boolean priorityMarker;
	private boolean breedingValue;
	private int model = NA;
	private double subEffect;
	private double relWeight;

	public MarkerProperties()
	{
	}

	public MarkerProperties(Marker marker)
	{
		this.marker = marker;
	}


	// Methods required for XML serialization

	public Marker getMarker()
		{ return marker; }

	public void setMarker(Marker marker)
		{ this.marker = marker; }

	public ArrayList<Integer> getFavAlleles()
		{ return favAlleles; }

	public void setFavAlleles(ArrayList<Integer> favAlleles)
		{ this.favAlleles = favAlleles; }

	public ArrayList<Integer> getUnfavAlleles()
		{ return unfavAlleles; }

	public void setUnfavAlleles(ArrayList<Integer> unfavAlleles)
		{ this.unfavAlleles = unfavAlleles; }

	public String getPlatform()
		{ return platform; }

	public void setPlatform(String platform)
		{ this.platform = platform; }

	public String getAlleleName()
		{ return alleleName; }

	public void setAlleleName(String alleleName)
		{ this.alleleName = alleleName; }

	public boolean isPriorityMarker()
		{ return priorityMarker; }

	public void setPriorityMarker(boolean priorityMarker)
		{ this.priorityMarker = priorityMarker; }

	public boolean isBreedingValue()
		{ return breedingValue; }

	public void setBreedingValue(boolean breedingValue)
		{ this.breedingValue = breedingValue; }

	public int getModel()
		{ return model; }

	public void setModel(int model)
		{ this.model = model; }

	public double getSubEffect()
		{ return subEffect; }

	public void setSubEffect(double subEffect)
		{ this.subEffect = subEffect; }

	public double getRelWeight()
		{ return relWeight; }

	public void setRelWeight(double relWeight)
		{ this.relWeight = relWeight; }
}
