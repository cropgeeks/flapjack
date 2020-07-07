// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class AlleleFrequencyColorScheme extends ColorScheme
{
	private HashMap<String, ColorState> colors = new HashMap<>();

	private float thresholdFrequency;

	private HashMap<String, Integer> hashmap = new HashMap<>();

	/** Empty constructor that is ONLY used for color customization purposes. */
	public AlleleFrequencyColorScheme() {}

	public AlleleFrequencyColorScheme(GTView view, int w, int h)
	{
		super(view);

		// Copy the threshold value
		thresholdFrequency = view.getViewSet().getAlleleFrequencyThreshold();


		// Build a quick mapper between, eg "A" and the index of the frequency
		// of "A" in each marker's array
		int i = 0;
		for (String allele: Marker.alleles())
			hashmap.put(allele, i++);



		for (int s = 0; s < stateTable.size(); s++)
		{
			AlleleState state = stateTable.getAlleleState(s);

			// Use white for the default unknown state
			if (state.isUnknown())
				colors.put("", new SimpleColorState(state, Prefs.visColorBackground, w, h));

			// Homozygous states
			else if (state.isHomozygous())
			{
				colors.put(state.homzAllele() + "H", new HomozygousColorState(state, Prefs.visColorHiFreqState, w, h));
				colors.put(state.homzAllele() + "L", new HomozygousColorState(state, Prefs.visColorLoFreqState, w, h));
			}

			// Heterozygous states
			else
			{
				String[] alleles = state.getStates();

				colors.put(alleles[0]+"H"+alleles[1]+"H", new HeterozygeousColorState(state, Prefs.visColorHiFreqState, Prefs.visColorHiFreqState, Prefs.visColorHiFreqState, w, h));
				colors.put(alleles[0]+"H"+alleles[1]+"L", new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, Prefs.visColorHiFreqState, Prefs.visColorLoFreqState, w, h));
				colors.put(alleles[0]+"L"+alleles[1]+"H", new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, Prefs.visColorLoFreqState, Prefs.visColorHiFreqState, w, h));
				colors.put(alleles[0]+"L"+alleles[1]+"L", new HeterozygeousColorState(state, Prefs.visColorLoFreqState, Prefs.visColorLoFreqState, Prefs.visColorLoFreqState, w, h));
			}
		}
	}

	// Return the frequency of the given allele for the given marker
	private float getFrequency(int marker, String allele)
	{
		return view.getMarker(marker).frequencies()[hashmap.get(allele)];
	}

	private ColorState getState(int line, int marker)
	{
		// Get the genotype index
		int index = view.getState(line, marker);

		if (index == 0)
			return colors.get("");


		// Get the actual alleles, eg [A], or [A][T]
		String[] alleles = stateTable.getAlleleState(index).getStates();

		// Build a lookup key that will be of the form, eg:
		//  A- (for a low A)
		//  A+ (for a high A)
		//  A-/T+ (for a low A, high T heterozygote)
		//  and so on...

		String key = "";

		for (int i = 0; i < alleles.length; i++)
		{
			if (getFrequency(marker, alleles[i]) <= thresholdFrequency)
				key += alleles[i]+"L";
			else
				key += alleles[i]+"H";
		}

		return colors.get(key);
	}

	public BufferedImage getSelectedImage(int line, int marker, boolean underQTL)
		{ return getState(line, marker).getImage(underQTL); }

	public BufferedImage getUnselectedImage(int line, int marker, boolean underQTL)
		{ return getState(line, marker).getUnselectedImage(underQTL); }

	public Color getColor(int line, int marker)
		{ return getState(line, marker).getColor(); }

	public int getModel()
		{ return ALLELE_FREQUENCY; }

	public String toString()
		{ return RB.getString("gui.Actions.vizColorAlleleFreq"); }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.AlleleFrequencyColorScheme");
	}

	public ArrayList<ColorSummary> getColorSummaries()
	{
		ArrayList<ColorSummary> colors = new ArrayList<>();

		colors.add(new ColorSummary(Prefs.visColorLoFreqState,
			RB.getString("gui.visualization.colors.AlleleFrequencyColorScheme.loFreqState")));
		colors.add(new ColorSummary(Prefs.visColorHiFreqState,
			RB.getString("gui.visualization.colors.AlleleFrequencyColorScheme.hiFreqState")));

		return colors;
	}

	public void setColorSummaries(ArrayList<ColorSummary> colors)
	{
		Prefs.visColorLoFreqState = colors.get(0).color;
		Prefs.visColorHiFreqState = colors.get(1).color;
	}
}