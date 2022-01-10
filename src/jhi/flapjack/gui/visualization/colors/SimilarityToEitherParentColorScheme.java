// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class SimilarityToEitherParentColorScheme extends ColorScheme
{
	// States whose alleles match the parent alleles
	private ArrayList<ColorState> matchStates = new ArrayList<>();

	// States whose alleles mismatch the parent alleles completely
	private ArrayList<ColorState> noMatchStates = new ArrayList<>();

	private ArrayList<ColorState> het1MatchStates = new ArrayList<>();
	private ArrayList<ColorState> het2MatchStates = new ArrayList<>();

	// Greyscale states for when the comparison state is missing
	private ArrayList<ColorState> gsStates = new ArrayList<>();

	// A lookup table which disambiguates heterozygous genotypes allowing us to match against their homozygous allele equiavalents
	private int[][] lookupTable;

	/** Empty constructor that is ONLY used for color customization purposes. */
	public SimilarityToEitherParentColorScheme() {}

	public SimilarityToEitherParentColorScheme(GTView view, int w, int h)
	{
		super(view);

		// Shorthand names for the colours (makes the code below more readable)
		Color sHz = Prefs.visColorNucleotideHZ;
		Color stateMatch = Prefs.visColorSimStateMatch;
		Color stateNoMatch = Prefs.visColorSimStateNoMatch;
		Color gsC = Prefs.visColorSimStateMissing;

		// Build a set of arraylists containing colour states for each state in
		// the state table for each colour we need to be able to display
		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);

			ColorState match, noMatch, grey, het1Match, het2Match;

			// Use white for the default unknown state
			if (state.isUnknown())
				match = noMatch = grey = het1Match = het2Match = new SimpleColorState(state, Prefs.visColorBackground, w, h);

			else if (state.isHomozygous())
			{
				match = new HomozygousColorState(state, stateMatch, w, h);
				noMatch = new HomozygousColorState(state, stateNoMatch, w, h);
				grey = new HomozygousColorState(state, gsC, w, h);
				het1Match = null;
				het2Match = null;
			}
			else
			{
				match = new HeterozygeousColorState(state, sHz, stateMatch, stateMatch, w, h);
				noMatch = new HeterozygeousColorState(state, sHz, stateNoMatch, stateNoMatch, w, h);
				grey = new HeterozygeousColorState(state, sHz, gsC, gsC, w, h);
				het1Match = new HeterozygeousColorState(state, sHz, stateMatch, stateNoMatch, w, h);
				het2Match = new HeterozygeousColorState(state, sHz, stateNoMatch, stateMatch, w, h);
			}

			matchStates.add(match);
			noMatchStates.add(noMatch);
			gsStates.add(grey);
			het1MatchStates.add(het1Match);
			het2MatchStates.add(het2Match);
		}

		lookupTable = stateTable.createAlleleLookupTable();
	}

	public void setColorSummaries(ArrayList<ColorSummary> colors)
	{
		Prefs.visColorSimStateMatch = colors.get(0).color;
		Prefs.visColorSimStateNoMatch = colors.get(1).color;
		Prefs.visColorSimStateMissing = colors.get(2).color;
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
			AlleleState p1AlleleState = stateTable.getAlleleState(p1State);
			AlleleState p2AlleleState = stateTable.getAlleleState(p2State);

			// Parental lines return a gresyscale version of the state for each parent line
			if (line == p1 || line == p2)
				return gsStates.get(state);

			else if (state == p1State || state == p2State)
				return matchStates.get(state);

			if (lookupTable[state][0] == -1 || lookupTable[state][1] == -1)
				return noMatchStates.get(state);

			AlleleState allele1State = stateTable.getAlleleState(lookupTable[state][0]);
			AlleleState allele2State = stateTable.getAlleleState(lookupTable[state][1]);


			if (allele1State != null && allele2State != null)
			{
				if ((allele1State.matchesAnyAllele(p1AlleleState) || allele1State.matchesAnyAllele(p2AlleleState)) && (allele2State.matchesAnyAllele(p2AlleleState) || allele2State.matchesAnyAllele(p1AlleleState)))
					return matchStates.get(state);
			}

			if (allele1State != null && (allele1State.matchesAnyAllele(p1AlleleState) || allele1State.matchesAnyAllele(p2AlleleState)))
			{
				return het1MatchStates.get(state);
			}
			else if (allele2State != null && (allele2State.matchesAnyAllele(p1AlleleState) || allele2State.matchesAnyAllele(p2AlleleState)))
			{
				return het2MatchStates.get(state);
			}
		}

		// If it's not the same, or we can't do a comparison...
		return noMatchStates.get(state);
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
		{ return SIMILARITY_TO_EITHER_PARENT; }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.SimilarityToEitherParentColorScheme");
	}

	public String toString()
		{ return RB.getString("gui.Actions.vizColorSimilarityToEitherParent"); }

	public ArrayList<ColorSummary> getColorSummaries()
	{
		ArrayList<ColorSummary> colors = new ArrayList<>();

		colors.add(new ColorSummary(Prefs.visColorSimStateMatch,
			RB.getString("gui.visualization.colors.SimilarityToEitherParentColorScheme.state1")));
		colors.add(new ColorSummary(Prefs.visColorSimStateNoMatch,
			RB.getString("gui.visualization.colors.SimilarityToEitherParentColorScheme.state2")));
		colors.add(new ColorSummary(Prefs.visColorSimStateMissing,
			RB.getString("gui.visualization.colors.SimilarityToEitherParentColorScheme.state3")));

		return colors;
	}
}