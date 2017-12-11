// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class ParentTotalMatchColorScheme extends ColorScheme
{
	// List of comparison line/marker states (dark red by default)
	protected ArrayList<ColorState> p1States = new ArrayList<>();				// eg A matches A
	protected ArrayList<ColorState> p2States = new ArrayList<>();				// eg A matches A

	// States whose alleles match the parent alleles
	protected ArrayList<ColorState> matchStates = new ArrayList<>();			// eg parents are C and T and progeny is C, T, or C/T

	// States whose alleles mismatch the parent alleles in some way
	protected ArrayList<ColorState> noMatchStates = new ArrayList<>();			// eg parents are C and T and progeny is A, G, C/A, T/A

	// Greyscale states for when the comparison state is missing
	protected ArrayList<ColorState> gsStates = new ArrayList<>();				// eg A could match MISSING, but we don't know

	protected HashMap<String, ColorState> progenyStates = new HashMap<>();

	/** Empty constructor that is ONLY used for color customization purposes. */
	public ParentTotalMatchColorScheme() {}

	public ParentTotalMatchColorScheme(GTView view, int w, int h)
	{
		super(view);

		// Shorthand names for the colours (makes the code below more readable)
		Color sHz = Prefs.visColorNucleotideHZ;
		Color cp1 = Prefs.visParentSimilarity1;
		Color cp2 = Prefs.visParentSimilarity2;
		Color stateMatch = Prefs.visColorSimStateMatch;
		Color stateNoMatch = Prefs.visColorSimStateNoMatch;
		Color gsC = Prefs.visColorSimStateMissing;

		// Build a set of arraylists containing colour states for each state in
		// the state table for each colour we need to be able to display
		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);

			ColorState comp, comp2, match, noMatch, grey;

			// Use white for the default unknown state
			if (state.isUnknown())
				comp = comp2 = match = noMatch = grey = new SimpleColorState(state, Prefs.visColorBackground, w, h);

			else if (state.isHomozygous())
			{
				comp = new HomozygousColorState(state, cp1, w, h);
				comp2 = new HomozygousColorState(state, cp2, w, h);
				match = new HomozygousColorState(state, stateMatch, w, h);
				noMatch = new HomozygousColorState(state, stateNoMatch, w, h);
				grey = new HomozygousColorState(state, gsC, w, h);
			}
			else
			{
				comp = new HeterozygeousColorState(state, sHz, cp1, cp1, w, h);
				comp2 = new HeterozygeousColorState(state, sHz, cp2, cp2, w, h);
				match = new HeterozygeousColorState(state, sHz, stateMatch, stateMatch, w, h);
				noMatch = new HeterozygeousColorState(state, sHz, stateNoMatch, stateNoMatch, w, h);
				grey = new HeterozygeousColorState(state, sHz, gsC, gsC, w, h);
			}

			p1States.add(comp);
			p2States.add(comp2);
			matchStates.add(match);
			noMatchStates.add(noMatch);
			gsStates.add(grey);
		}

		// We need to build a map of the states which relate to a pair of parent
		// states to a progeny state. The keys for the map are the indices of
		// each parent state and the progeny state in the statetable, separated
		// by colons. Each map value is a color state which can be used for
		// stamping on the canvas. To do this we loop over the statetable three
		// times (once for each parent, then once for the progeny).
		for (int i=0; i < stateTable.size(); i++)
		{
			// Grab the AlleleState for the first parent
			AlleleState p1State = stateTable.getAlleleState(i);

			for (int j=0; j < stateTable.size(); j++)
			{
				// Grab the AlleleState for the second parent
				AlleleState p2State = stateTable.getAlleleState(j);

				for (int k=0; k < stateTable.size(); k++)
				{
					// Grab the AlleleState for the progeny
					AlleleState progState = stateTable.getAlleleState(k);
					// Construct the key for the map of progeny ColorStates
					String key = i + ":" + j + ":" + k;

					if (progState.isUnknown())
						progenyStates.put(key, matchStates.get(k));

					// If neither parent has allele data at this location,
					// progeny alleles should be grey
					else if (p1State.isUnknown() && p2State.isUnknown())
						progenyStates.put(key, gsStates.get(k));

					// If either parent has missing data, or the progeny state's
					// alleles are fully contained in the parent alleles: green
					else if (p1State.isUnknown() || p2State.isUnknown() || progState.allelesContainedInParents(p1State, p2State))
						progenyStates.put(key, matchStates.get(k));

					// In all other cases we color the allele red
					else
						progenyStates.put(key, noMatchStates.get(k));
				}
			}
		}
	}

	public void setColorSummaries(ArrayList<ColorSummary> colors)
	{
		Prefs.visParentSimilarity1 = colors.get(0).color;
		Prefs.visParentSimilarity2 = colors.get(1).color;
		Prefs.visColorSimStateMatch = colors.get(2).color;
		Prefs.visColorSimStateNoMatch = colors.get(3).color;
		Prefs.visColorSimStateMissing = colors.get(4).color;
	}

	protected ColorState getState(int line, int marker)
	{
		int p1 = view.getViewSet().getComparisonLineIndex();
		int p2 = view.getViewSet().getComparisonLineIndex2();

		int state = view.getState(line, marker);

		// Try to do the comparison
		if (p1 != -1 && p2 != -1)
		{
			int p1State = view.getState(p1, marker);
			int p2State = view.getState(p2, marker);

			// Parental lines
			// if the parent states matchStates each other, return a greyscale state
			// otherwise return the appropriate state for that parent
			if (line == p1)
				return p1States.get(state);
			else if (line == p2)
				return p2States.get(state);

			return progenyStates.get(p1State+":"+p2State+":"+state);
		}

		// If it's not the same, or we can't do a comparison...
		return gsStates.get(state);
	}

	public BufferedImage getSelectedImage(int line, int marker, boolean underQTL)
	{
		return getState(line, marker).getImage(underQTL);
	}

	public BufferedImage getUnselectedImage(int line, int marker, boolean underQTL)
	{
		return getState(line, marker).getUnselectedImage(underQTL);
	}

	public Color getColor(int line, int marker)
	{
		return getState(line, marker).getColor();
	}

	public int getModel()
		{ return PARENT_TOTAL; }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.ParentTotalMatchColorScheme");
	}

	public String toString()
		{ return RB.getString("gui.Actions.vizColorParentTotal"); }

	public ArrayList<ColorSummary> getColorSummaries()
	{
		ArrayList<ColorSummary> colors = new ArrayList<>();

		colors.add(new ColorSummary(Prefs.visParentSimilarity1,
			RB.getString("gui.visualization.colors.ParentTotalMatchColorScheme.parent1")));
		colors.add(new ColorSummary(Prefs.visParentSimilarity2,
			RB.getString("gui.visualization.colors.ParentTotalMatchColorScheme.parent2")));
		colors.add(new ColorSummary(Prefs.visColorSimStateMatch,
			RB.getString("gui.visualization.colors.ParentTotalMatchColorScheme.state1")));
		colors.add(new ColorSummary(Prefs.visColorSimStateNoMatch,
			RB.getString("gui.visualization.colors.ParentTotalMatchColorScheme.state2")));
		colors.add(new ColorSummary(Prefs.visColorSimStateMissing,
			RB.getString("gui.visualization.colors.ParentTotalMatchColorScheme.state3")));

		return colors;
	}
}