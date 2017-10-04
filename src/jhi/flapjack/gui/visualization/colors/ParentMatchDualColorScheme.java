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

	// States that match on the first het allele
	protected ArrayList<ColorState> het1States = new ArrayList<>();				// eg A/T matches A or A/G
	// States that match on the second het allele
	protected ArrayList<ColorState> het2States = new ArrayList<>();				// eg A/T matches T or C/T

	// Greyscale states for when the comparison state is missing
	protected ArrayList<ColorState> gsStates = new ArrayList<>();				// eg A could match MISSING, but we don't know
	protected ArrayList<ColorState> p1p2MatchStates = new ArrayList<>();		// eg A could match MISSING, but we don't know
	protected ArrayList<ColorState> noMatchStates = new ArrayList<>();		// eg A could match MISSING, but we don't know

	protected int[][] lookupTable;


	/** Empty constructor that is ONLY used for color customization purposes. */
	public ParentMatchDualColorScheme() {}

	public ParentMatchDualColorScheme(GTView view, int w, int h)
	{
		super(view);

		// Shorthand names for the colours (makes the code below more readable)
		Color sHz = Prefs.visColorNucleotideHZ;
		Color drk = new Color(0,176,240).darker();
		Color drk2 = new Color(255,255,0).darker();
		Color s1  = new Color(0,176,240);
		Color s2  = new Color(255,255,0);
		Color gsC = Prefs.visColorSimStateMissing;
		Color flesh = new Color(218, 155, 255);
		Color red = Prefs.visColorNucleotideG;

		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);

			ColorState comp, comp2, bthPar, mtchY, mtchN, het1, het2, gs, noMatch;

			// Use white for the default unknown state
			if (state.isUnknown())
				comp = comp2 = bthPar = noMatch = mtchY = mtchN = het1 = het2 = gs = new SimpleColorState(state, Prefs.visColorBackground, w, h);

			// Homozygous states
			else if (state.isHomozygous())
			{
				comp  = new HomozygousColorState(state, drk, w, h);
				comp2  = new HomozygousColorState(state, drk2, w, h);

				mtchY = new HomozygousColorState(state, s1, w, h);
				mtchN = new HomozygousColorState(state, s2, w, h);
				het1  = null;
				het2  = null;
				gs    = new HomozygousColorState(state, gsC, w, h);
				bthPar = new HomozygousColorState(state, flesh, w, h);
				noMatch = new HomozygousColorState(state, red, w, h);
			}

			// Heterozygous states
			else
			{
				comp  = new HeterozygeousColorState(state, sHz, drk, drk, w, h);
				comp2  = new HeterozygeousColorState(state, sHz, drk2, drk2, w, h);

				mtchY = new HeterozygeousColorState(state, sHz, s1, s1, w, h);
				mtchN = new HeterozygeousColorState(state, sHz, s2, s2, w, h);
				het1  = new HeterozygeousColorState(state, sHz, s1, s2, w, h);
				het2  = new HeterozygeousColorState(state, sHz, s2, s1, w, h);
				gs    = new HeterozygeousColorState(state, sHz, gsC, gsC, w, h);
				bthPar = new HeterozygeousColorState(state, sHz, flesh, flesh, w, h);
				noMatch = new HeterozygeousColorState(state, sHz, red, red, w, h);
			}

			p1States.add(comp);
			p2States.add(comp2);
			p1MatchStates.add(mtchY);
			p2MatchStates.add(mtchN);
			het1States.add(het1);
			het2States.add(het2);
			gsStates.add(gs);
			p1p2MatchStates.add(bthPar);
			noMatchStates.add(noMatch);
		}

		createLookupTable();
	}

	public void setColorSummaries(ArrayList<ColorSummary> colors)
	{
		Prefs.visColorSimStateMatchDark = colors.get(0).color;
		Prefs.visColorSimStateMatch = colors.get(1).color;
		Prefs.visColorSimStateNoMatch = colors.get(2).color;
		Prefs.visColorSimStateMissing = colors.get(3).color;
	}

	private void createLookupTable()
	{
		// Make a lookup table suitable for an all-by-all comparison of states
		// This is so we can, for example, compare A against A/T and decide that
		// A is a partial match and should be drawn in the same colour
		// Or (another example), comparing A/T against A, again a partial match
		// so draw A/T with the A half in red, T half in green (het1 state)

		int count = stateTable.size();
		lookupTable = new int[count][count];

		for (int i = 0; i < count; i++)
		{
			for (int j = 0; j < count; j++)
			{
				// Start by assuming no match - we can override later...
				lookupTable[i][j] = 2;

				// Perfect match... (does homoz<->homoz comparisons too)
				if (i == j)
				{
					lookupTable[i][j] = 1;
					continue;
				}

				AlleleState ai = stateTable.getAlleleState(i);
				AlleleState aj = stateTable.getAlleleState(j);

				// Comparing a blank against something else
				if (i == 0 || j == 0)
				{
					lookupTable[i][j] = 5;
				}

				// i is homoz, j is hetez
				else if (ai.isHomozygous() && !aj.isHomozygous())
				{
					// match
					if (ai.getState(0).equals(aj.getState(0)) || ai.getState(0).equals(aj.getState(1)))
						lookupTable[i][j] = 1;
				}

				// i is hetez, j is homoz
				else if (!ai.isHomozygous() && aj.isHomozygous())
				{
					// het1 match
					if (ai.getState(0).equals(aj.getState(0)))
						lookupTable[i][j] = 3;
					// het2 match
					else if (ai.getState(1).equals(aj.getState(0)))
						lookupTable[i][j] = 4;
				}

				// i and j both hetez
				else if (!ai.isHomozygous() && !aj.isHomozygous())
				{
					// A/T matches T/A ?
					if (ai.matches(aj))
						lookupTable[i][j] = 1;

					// het1 match  (eg A/T half matches A/G or G/A)
					else if (ai.getState(0).equals(aj.getState(0)) || ai.getState(0).equals(aj.getState(1)))
						lookupTable[i][j] = 3;
					// het2 match  (eg A/T half matches T/G or C/T)
					else if (ai.getState(1).equals(aj.getState(0)) || ai.getState(1).equals(aj.getState(1)))
						lookupTable[i][j] = 4;
				}
			}
		}
	}

	protected ColorState getState(int line, int marker)
	{
		int p1 = 0; // todo look up parent indices - they may have been hidden!!!
		int p2 = 1;

		int state = view.getState(line, marker);

		// If it's the index line, return the darker version
		if (line == p1)
			return p1States.get(state);
		else if (line == p2)
			return p2States.get(state);

		// Try to do the comparison
		if (p1 != -1 && p2 != -1)
		{
			int p1State = view.getState(p1, marker);
			int p2State = view.getState(p2, marker);

			int matchType = 0;
			int matchState = 0;

			if (p1State == 0 || p2State == 0)
				return gsStates.get(state);

			if (state == p1State && state != p2State)
			{
				matchType = 1;
				matchState = p1State;
			}
			else if (state == p2State && state != p1State)
			{
				matchType = 2;
				matchState = p2State;
			}
			else if (state == p1State && state == p2State)
			{
				matchType = 3;
			}
			else if (state != 0)
				matchType = 4;

			if (matchType == 1)
			{
				switch (lookupTable[state][p1State])
				{
					case 1: return p1MatchStates.get(state);
//					case 2: return mtchStatesN.get(state);
					case 3: return het1States.get(state);
					case 4: return het2States.get(state);
					case 5: return gsStates.get(state);
				}
			}
			else if (matchType == 2)
			{
				switch (lookupTable[state][p2State])
				{
					case 1: return p2MatchStates.get(state);
//					case 2: return mtchStatesN.get(state);
					case 3: return het1States.get(state);
					case 4: return het2States.get(state);
					case 5: return gsStates.get(state);
				}
			}
			else if (matchType == 3)
				return p1p2MatchStates.get(state);
			else if (matchType == 4)
				return noMatchStates.get(state);
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

/*		colors.add(new ColorSummary(Prefs.visColorSimStateMatchDark,
			RB.getString("gui.visualization.colors.LineSimilarityColorScheme.state1Dark")));
		colors.add(new ColorSummary(Prefs.visColorSimStateMatch,
			RB.getString("gui.visualization.colors.LineSimilarityColorScheme.state1")));
		colors.add(new ColorSummary(Prefs.visColorSimStateNoMatch,
			RB.getString("gui.visualization.colors.LineSimilarityColorScheme.state2")));
		colors.add(new ColorSummary(Prefs.visColorSimStateMissing,
			RB.getString("gui.visualization.colors.LineSimilarityColorScheme.state3")));
*/
		return colors;
	}
}