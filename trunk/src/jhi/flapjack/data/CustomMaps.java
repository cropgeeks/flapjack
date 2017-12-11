// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

import java.util.*;

public class CustomMaps extends XMLRoot
{
	// A list of custom, additional GTViews (holding maps) that can be applied
	// to the Chromosomes panel view
	private ArrayList<GTView> customViews = new ArrayList<>();

	// The panel will display the original (real) set of chromosomes, plus any
	// custom ones, but in a user-specified order. This list holds both the
	// original maps, and the custom maps in that order. This object is saved to
	// XML using "bind-xml reference" to minimize the required size
	private ArrayList<GTView> allViews = new ArrayList<>();

	public CustomMaps()
	{
	}


	// Methods required for XML serialization

	public ArrayList<GTView> getCustomViews()
		{ return customViews; }

	public void setCustomViews(ArrayList<GTView> customViews)
		{ this.customViews = customViews; }

	public ArrayList<GTView> getAllViews()
		{ return allViews; }

	public void setAllViews(ArrayList<GTView> allViews)
		{ this.allViews = allViews; }


	// Other methods

	public void add(GTViewSet viewSet, GTView newView, GTView cloneOf)
	{
		initForDisplay(viewSet);

		customViews.add(newView);

		int index = allViews.indexOf(cloneOf) + 1;
		allViews.add(index, newView);
	}

	// This is dealing with the fact existing projects will not have this object
	// instantiated, so we copy over the references to the existing chromosomes
	public void initForDisplay(GTViewSet viewSet)
	{
		if (allViews.size() == 0)
		{
			for (GTView view: viewSet.getViews())
				if (view.getChromosomeMap().isSpecialChromosome() == false)
					allViews.add(view);
		}
	}
}