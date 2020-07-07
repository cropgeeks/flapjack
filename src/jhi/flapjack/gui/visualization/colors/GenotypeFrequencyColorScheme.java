// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class GenotypeFrequencyColorScheme extends ColorScheme
{
	private ArrayList<ColorState> hiStates = new ArrayList<>();
	private ArrayList<ColorState> loStates = new ArrayList<>();

	private float thresholdFrequency;

	/** Empty constructor that is ONLY used for color customization purposes. */
	public GenotypeFrequencyColorScheme() {}

	public GenotypeFrequencyColorScheme(GTView view, int w, int h)
	{
		super(view);

		// Copy the threshold value
		thresholdFrequency = view.getViewSet().getAlleleFrequencyThreshold();

		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);
			ColorState cHi = null;
			ColorState cLo = null;

			// Use white for the default unknown state
			if (state.isUnknown())
				cHi = cLo = new SimpleColorState(state, Prefs.visColorBackground, w, h);

			// Homozygous states
			else if (state.isHomozygous())
			{
				cHi = new HomozygousColorState(state, Prefs.visColorHiFreqState, w, h);
				cLo = new HomozygousColorState(state, Prefs.visColorLoFreqState, w, h);
			}

			// Heterozygous states
			else
			{
				cHi = new HeterozygeousColorState(state, Prefs.visColorHiFreqState, Prefs.visColorHiFreqState, Prefs.visColorHiFreqState, w, h);
				cLo = new HeterozygeousColorState(state, Prefs.visColorLoFreqState, Prefs.visColorLoFreqState, Prefs.visColorLoFreqState, w, h);
			}

			hiStates.add(cHi);
			loStates.add(cLo);
		}
	}

	private ColorState getState(int line, int marker)
	{
		int state = view.getState(line, marker);

		if (view.getMarker(marker).frequencies()[state] <= thresholdFrequency)
			return loStates.get(state);

		else
			return hiStates.get(state);
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