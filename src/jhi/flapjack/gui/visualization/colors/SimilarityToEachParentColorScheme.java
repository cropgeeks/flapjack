// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

/*
 * A colour scheme which colours two parent lines (defaults are parent 1 is blue, parent 2 is yellow) ambiguous parental
 * genotypes are coloured grey. Ambiguous genotypes are genotypes where the parent genotypes match, or either parental
 * genotype is missing or heterozygous. For progeny lines they are coloured depending on whether they match a parental
 * genotype (e.g.match parent1 is blue) this is done one an allele match basis so if one half matches parent 1 and one
 * half matches parent2 the first half is coloured blue and the second half is coloured yellow.
 */
public class SimilarityToEachParentColorScheme extends ColorScheme
{
	// Lists of alleles for parent 1 and parent 2
	protected ArrayList<ColorState> p1States = new ArrayList<>();
	protected ArrayList<ColorState> p2States = new ArrayList<>();

	// States that exactly match the comparison
	protected ArrayList<ColorState> p1MatchStates = new ArrayList<>();
	// States that don't exactly match the comparison
	protected ArrayList<ColorState> p2MatchStates = new ArrayList<>();

	// Heterozygous states which match p1 + p2 and p2 + p1 respectively
	protected ArrayList<ColorState> hetP1P2States = new ArrayList<>();
	protected ArrayList<ColorState> hetP2P1States = new ArrayList<>();

	// States which match one of the parents, but are a mismatch on another
	protected ArrayList<ColorState> hetP1MismatchStates = new ArrayList<>();
	protected ArrayList<ColorState> hetMismatchP1States = new ArrayList<>();
	protected ArrayList<ColorState> hetP2MismatchStates = new ArrayList<>();
	protected ArrayList<ColorState> hetMismatchP2States = new ArrayList<>();

	// Greyscale states for when the comparison state is missing
	protected ArrayList<ColorState> gsStates = new ArrayList<>();
	protected ArrayList<ColorState> noMatchStates = new ArrayList<>();

	// A lookup table which disambiguates heterozygous genotypes allowing us to match against their homozygous allele equiavalents
	protected int[][] lookupTable;

	/** Empty constructor that is ONLY used for color customization purposes. */
	public SimilarityToEachParentColorScheme() {}

	public SimilarityToEachParentColorScheme(GTView view, int w, int h)
	{
		super(view);

		// Shorthand names for the colours (makes the code below more readable)
		Color sHz = Prefs.visColorNucleotideHZ;
		Color p1 =  Prefs.visParentSimilarity1;
		Color p2 = Prefs.visParentSimilarity2;
		Color mP1  = Prefs.visParentSimilarity1Match;
		Color mP2  = Prefs.visParentSimilarity2Match;
		Color gsC = Prefs.visColorSimStateMissing;
		Color red = Prefs.visColorSimStateNoMatch;

		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);

			ColorState parent1, parent2, matchP1, matchP2, hetP1P2, hetP2P1, hetP1Mis, hetMisP1, hetP2Mis, hetMisP2, gs, noMatch;

			// Use white for the default unknown state
			if (state.isUnknown())
			{
				parent1 = parent2 = noMatch = matchP1 = matchP2 = hetP1P2 = hetP2P1 = hetP1Mis = hetMisP1 = hetP2Mis
					= hetMisP2 = gs = new SimpleColorState(state, Prefs.visColorBackground, w, h);
			}
			// Homozygous states
			else if (state.isHomozygous())
			{
				parent1 = new HomozygousColorState(state, p1, w, h);
				parent2 = new HomozygousColorState(state, p2, w, h);

				matchP1 = new HomozygousColorState(state, mP1, w, h);
				matchP2 = new HomozygousColorState(state, mP2, w, h);

				hetP1P2 = null;
				hetP2P1 = null;
				hetP1Mis = null;
				hetMisP1 = null;
				hetP2Mis = null;
				hetMisP2 = null;

				gs = new HomozygousColorState(state, gsC, w, h);
				noMatch = new HomozygousColorState(state, red, w, h);
			}

			// Heterozygous states
			else
			{
				parent1 = new HeterozygeousColorState(state, sHz, p1, p1, w, h);
				parent2 = new HeterozygeousColorState(state, sHz, p2, p2, w, h);

				matchP1 = new HeterozygeousColorState(state, sHz, mP1, mP1, w, h);
				matchP2 = new HeterozygeousColorState(state, sHz, mP2, mP2, w, h);

				hetP1P2 = new HeterozygeousColorState(state, sHz, mP1, mP2, w, h);
				hetP2P1 = new HeterozygeousColorState(state, sHz, mP2, mP1, w, h);
				hetP1Mis = new HeterozygeousColorState(state, sHz, mP1, red, w, h);
				hetMisP1 = new HeterozygeousColorState(state, sHz, red, mP1, w, h);
				hetP2Mis = new HeterozygeousColorState(state, sHz, mP2, red, w, h);
				hetMisP2 = new HeterozygeousColorState(state, sHz, red, mP2, w, h);

				gs = new HeterozygeousColorState(state, sHz, gsC, gsC, w, h);
				noMatch = new HeterozygeousColorState(state, sHz, red, red, w, h);
			}

			p1States.add(parent1);
			p2States.add(parent2);
			p1MatchStates.add(matchP1);
			p2MatchStates.add(matchP2);

			hetP1P2States.add(hetP1P2);
			hetP2P1States.add(hetP2P1);
			hetP1MismatchStates.add(hetP1Mis);
			hetMismatchP1States.add(hetMisP1);
			hetP2MismatchStates.add(hetP2Mis);
			hetMismatchP2States.add(hetMisP2);

			gsStates.add(gs);
			noMatchStates.add(noMatch);
		}

		createLookupTable();
	}

	public void setColorSummaries(ArrayList<ColorSummary> colors)
	{
		Prefs.visParentSimilarity1 = colors.get(0).color;
		Prefs.visParentSimilarity2 = colors.get(1).color;
		Prefs.visParentSimilarity1Match = colors.get(2).color;
		Prefs.visParentSimilarity2Match = colors.get(3).color;
		Prefs.visColorSimStateMissing = colors.get(4).color;
		Prefs.visColorSimStateNoMatch = colors.get(5).color;
	}

	protected ColorState getState(int line, int marker)
	{
		int p1 = view.getViewSet().getComparisonLineIndex();
		int p2 = view.getViewSet().getComparisonLineIndex2();

		int state = view.getState(line, marker);

		AlleleState childState = stateTable.getAlleleState(state);

		// Try to do the comparison
		if (p1 != -1 && p2 != -1)
		{
			int p1State = view.getState(p1, marker);
			int p2State = view.getState(p2, marker);
			AlleleState p1AlleleState = stateTable.getAlleleState(p1State);
			AlleleState p2AlleleState = stateTable.getAlleleState(p2State);

			// Parental lines
			// if the parent states match each other, either are missing, or
			// either are heterozygous return a greyscale state otherwise return
			// the appropriate state for that parent
			if (line == p1)
				return parentStatesAmbiguous(p1State, p2State) ? gsStates.get(state) : p1States.get(state);
			else if (line == p2)
				return parentStatesAmbiguous(p1State, p2State) ? gsStates.get(state) : p2States.get(state);

			// The heterozygous genotypes require a lookup table. The lookup table allows us to query each allele in a
			// genotype. This allows us to check if an allele in a het matches either the parent 1 genotype, or the
			// parent 2 genotype.
			if (!childState.isHomozygous())
			{
				boolean p1MatchAllele1 = lookupTable[state][0] == p1State;
				boolean p2MatchAllele1 = lookupTable[state][0] == p2State;
				boolean p1MatchAllele2 = lookupTable[state][1] == p1State;
				boolean p2MatchAllele2 = lookupTable[state][1] == p2State;

				if (p1MatchAllele1 && p2MatchAllele2)
					return hetP1P2States.get(state);

				else if (p1MatchAllele1 && !p2MatchAllele2)
					return hetP1MismatchStates.get(state);

				else if (!p1MatchAllele1 && p2MatchAllele2)
					return  hetMismatchP2States.get(state);

				else if (p2MatchAllele1 && p1MatchAllele2)
					return hetP2P1States.get(state);

				else if (p2MatchAllele1 && !p1MatchAllele2)
					return hetP2MismatchStates.get(state);

				else if (!p2MatchAllele1 && p1MatchAllele2)
					return hetMismatchP1States.get(state);
			}
			else
			{
				// If there is ambiguity because of the parental alleles, return a
				// grey state
				if (parentStatesAmbiguous(p1State, p2State))
					return gsStates.get(state);

				// Child state matches parent 1 state return a brighter version of
				// the parent 1 colour
				if (state == p1State)
					return p1MatchStates.get(state);

					// Child state matches parent 2 state return a brighter version of
					// the parent 2 colour
				else if (state == p2State)
					return p2MatchStates.get(state);

				// If the child state contains alleles which can't be found in the
				// parents (and both parents are *not* missing) return a red state
				if (childState.allelesContainedInParents(p1AlleleState, p2AlleleState) == false && p1State != 0 && p2State != 0)
					return noMatchStates.get(state);
			}
		}

		// If it's not met any of our conditions fall back on greyscale
		return gsStates.get(state);
	}

	// If the parent states match each other, or if either parent state is
	// missing, or heterozygous we have an ambiguous set of parent states
	private boolean parentStatesAmbiguous(int p1, int p2)
	{
		return p1 == p2  || p1 == 0 || p2 == 0 || stateTable.isHet(p1) || stateTable.isHet(p2);
	}

	private void createLookupTable()
	{
		// Create a lookup table which has two slots for each genotype in the state table this allows us to reconstitute
		// heterozygous alleles into a form where each half can be easily compared to homozygous alleles
		int count = stateTable.size();
		lookupTable = new int[count][2];

		// Prefill the array with -1s which will be used to denote states which can't be found in the statetable
		for (int i = 0; i < count; i++)
			for (int j = 0; j < 2; j++)
				lookupTable[i][j] = -1;

		// Iterate over the state table creating the two slot array entry for each state in the table
		for (int i = 0; i < count; i++)
		{
			for (int j = 0; j < 2; j++)
			{
				// Get the string values of the allele states (e.g. 'A', or 'A''T')
				AlleleState state = stateTable.getAlleleState(i);
				String[] stringAlleles = state.getStates();

				// We may only hav a hom allele so we can't assume we have two strings here
				if (j < stringAlleles.length)
				{
					// Make a temp state to check against the statetable
					AlleleState newState = new AlleleState(stringAlleles[j], "/");
					int stateCode = -1;
					for (int k = 0; k < stateTable.size(); k++)
						if (stateTable.getAlleleState(k).matches(newState))
							stateCode = k;

					lookupTable[i][j] = stateCode;

					// If this was a homozygous genotype manually add its second allele
					if (stringAlleles.length == 1)
						lookupTable[i][1] = stateCode;
				}
			}
		}
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
		{ return SIMILARITY_TO_EACH_PARENT; }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.SimilarityToEachParentColorScheme");
	}

	public String toString()
		{ return RB.getString("gui.Actions.vizColorSimilarityToEachParent"); }

	public ArrayList<ColorSummary> getColorSummaries()
	{
		ArrayList<ColorSummary> colors = new ArrayList<>();

		colors.add(new ColorSummary(Prefs.visParentSimilarity1,
			RB.getString("gui.visualization.colors.SimilarityToEachParentColorScheme.parent1")));
		colors.add(new ColorSummary(Prefs.visParentSimilarity2,
			RB.getString("gui.visualization.colors.SimilarityToEachParentColorScheme.parent2")));
		colors.add(new ColorSummary(Prefs.visParentSimilarity1Match,
			RB.getString("gui.visualization.colors.SimilarityToEachParentColorScheme.state1")));
		colors.add(new ColorSummary(Prefs.visParentSimilarity2Match,
			RB.getString("gui.visualization.colors.SimilarityToEachParentColorScheme.state2")));
		colors.add(new ColorSummary(Prefs.visColorSimStateMissing,
			RB.getString("gui.visualization.colors.SimilarityToEachParentColorScheme.state3")));
		colors.add(new ColorSummary(Prefs.visColorSimStateNoMatch,
			RB.getString("gui.visualization.colors.SimilarityToEachParentColorScheme.state4")));

		return colors;
	}
}