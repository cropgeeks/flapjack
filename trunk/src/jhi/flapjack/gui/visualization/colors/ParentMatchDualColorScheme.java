// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import scri.commons.gui.*;

public class ParentMatchDualColorScheme extends ColorScheme
{
	// List of comparison line/marker states (dark red by default)
	protected ArrayList<ColorState> p1States = new ArrayList<>();				// eg A matches A
	protected ArrayList<ColorState> p2States = new ArrayList<>();				// eg A matches A

	// States that exactly match the comparison
	protected ArrayList<ColorState> p1MatchStates = new ArrayList<>();			// eg A matches A, A/T matches A/T
	// States that don't exactly match the comparison
	protected ArrayList<ColorState> p2MatchStates = new ArrayList<>();			// eg A doesn't match T, A/T doesn't match C/G

	// Greyscale states for when the comparison state is missing
	protected ArrayList<ColorState> gsStates = new ArrayList<>();				// eg A could match MISSING, but we don't know
	protected ArrayList<ColorState> noMatchStates = new ArrayList<>();			// eg A could match MISSING, but we don't know

	/** Empty constructor that is ONLY used for color customization purposes. */
	public ParentMatchDualColorScheme() {}

	public ParentMatchDualColorScheme(GTView view, int w, int h)
	{
		super(view);

		// Shorthand names for the colours (makes the code below more readable)
		Color sHz = Prefs.visColorNucleotideHZ;
		Color drk =  Prefs.visParentSimilarity1;
		Color drk2 = Prefs.visParentSimilarity2;
		Color s1  = Prefs.visParentSimilarity1Match;
		Color s2  = Prefs.visParentSimilarity2Match;
		Color gsC = Prefs.visColorSimStateMissing;
		Color red = Prefs.visColorSimStateNoMatch;

		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);

			ColorState comp, comp2, mtchY, mtchN, gs, noMatch;

			// Use white for the default unknown state
			if (state.isUnknown())
				comp = comp2 = noMatch = mtchY = mtchN = gs = new SimpleColorState(state, Prefs.visColorBackground, w, h);

			// Homozygous states
			else if (state.isHomozygous())
			{
				comp  = new HomozygousColorState(state, drk, w, h);
				comp2  = new HomozygousColorState(state, drk2, w, h);

				mtchY = new HomozygousColorState(state, s1, w, h);
				mtchN = new HomozygousColorState(state, s2, w, h);
				gs    = new HomozygousColorState(state, gsC, w, h);
				noMatch = new HomozygousColorState(state, red, w, h);
			}

			// Heterozygous states
			else
			{
				comp  = new HeterozygeousColorState(state, sHz, drk, drk, w, h);
				comp2  = new HeterozygeousColorState(state, sHz, drk2, drk2, w, h);

				mtchY = new HeterozygeousColorState(state, sHz, s1, s1, w, h);
				mtchN = new HeterozygeousColorState(state, sHz, s2, s2, w, h);
				gs    = new HeterozygeousColorState(state, sHz, gsC, gsC, w, h);
				noMatch = new HeterozygeousColorState(state, sHz, red, red, w, h);
			}

			p1States.add(comp);
			p2States.add(comp2);
			p1MatchStates.add(mtchY);
			p2MatchStates.add(mtchN);
			gsStates.add(gs);
			noMatchStates.add(noMatch);
		}
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

			// If the child state contains alleles which can't be found in the
			// parents (and both parents are *not* missing) return a red state
			if (childState.allelesContainedInParents(p1AlleleState, p2AlleleState) == false && p1State != 0 && p2State != 0)
				return noMatchStates.get(state);

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
		{ return PARENT_DUAL; }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.ParentDualColorScheme");
	}

	public String toString()
		{ return RB.getString("gui.Actions.vizColorParentDual"); }

	public ArrayList<ColorSummary> getColorSummaries()
	{
		ArrayList<ColorSummary> colors = new ArrayList<>();

		colors.add(new ColorSummary(Prefs.visParentSimilarity1,
			RB.getString("gui.visualization.colors.ParentMatchDualColorScheme.parent1")));
		colors.add(new ColorSummary(Prefs.visParentSimilarity2,
			RB.getString("gui.visualization.colors.ParentMatchDualColorScheme.parent2")));
		colors.add(new ColorSummary(Prefs.visParentSimilarity1Match,
			RB.getString("gui.visualization.colors.ParentMatchDualColorScheme.state1")));
		colors.add(new ColorSummary(Prefs.visParentSimilarity2Match,
			RB.getString("gui.visualization.colors.ParentMatchDualColorScheme.state2")));
		colors.add(new ColorSummary(Prefs.visColorSimStateMissing,
			RB.getString("gui.visualization.colors.ParentMatchDualColorScheme.state3")));
		colors.add(new ColorSummary(Prefs.visColorSimStateNoMatch,
			RB.getString("gui.visualization.colors.ParentMatchDualColorScheme.state4")));

		return colors;
	}
}