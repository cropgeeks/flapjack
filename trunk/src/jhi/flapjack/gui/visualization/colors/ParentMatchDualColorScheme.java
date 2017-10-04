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
		Color drk = new Color(0,176,240).darker();
		Color drk2 = new Color(255,255,0).darker();
		Color s1  = new Color(0,176,240);
		Color s2  = new Color(255,255,0);
		Color gsC = Prefs.visColorSimStateMissing;
		Color red = Prefs.visColorNucleotideG;

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
		Prefs.visColorSimStateMatchDark = colors.get(0).color;
		Prefs.visColorSimStateMatch = colors.get(1).color;
		Prefs.visColorSimStateNoMatch = colors.get(2).color;
		Prefs.visColorSimStateMissing = colors.get(3).color;
	}

	protected ColorState getState(int line, int marker)
	{
		int p1 = 0; // todo look up parent indices - they may have been hidden!!!
		int p2 = 1;

		int state = view.getState(line, marker);

		// Try to do the comparison
		if (p1 != -1 && p2 != -1)
		{
			int p1State = view.getState(p1, marker);
			int p2State = view.getState(p2, marker);

			// Parental lines
			// if the parent states match each other, return a greyscale state
			// otherwise return the appropriate state for that parent
			if (line == p1)
				return p1State == p2State ? gsStates.get(state) : p1States.get(state);
			else if (line == p2)
				return p2State == p1State ? gsStates.get(state) : p2States.get(state);

			// TODO: What do we do if a parent line has a het state?

			// Progeny lines
			// Parent 1 match
			if (state == p1State && state != p2State)
				return p1MatchStates.get(state);

			// Parent 2 match
			else if (state == p2State && state != p1State)
				return p2MatchStates.get(state);

			// Parent 1 and 2 match
			else if (state == p1State && state == p2State)
				return gsStates.get(state);

			// Anything else
			else if (state != 0)
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