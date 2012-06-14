// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class SimpleTwoColorScheme extends ColorScheme
{
	protected ArrayList<ColorState> states = new ArrayList<ColorState>();

	/** Empty constructor that is ONLY used for color customization purposes. */
	public SimpleTwoColorScheme() {}

	public SimpleTwoColorScheme(GTView view, int w, int h)
	{
		super(view);

		String s1 = null, s2 = null;

		// Search for the first two homozygous states and store their values
		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);

			if (!state.isUnknown() && state.isHomozygous() && s1 == null)
				s1 = state.getState(0);
			else if (!state.isUnknown() && state.isHomozygous() && s2 == null)
				s2 = state.getState(0);
		}

		// Now scan all the states, assigning them colours
		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);

			// Use white for the default unknown state
			if (state.isUnknown())
				states.add(new SimpleColorState(state, Prefs.visColorBackground, w, h));

			else if (state.isHomozygous() && state.getState(0).equals(s1))
				states.add(new HomozygousColorState(state, Prefs.visColorSimple2State1, w, h));
			else if (state.isHomozygous() && state.getState(0).equals(s2))
				states.add(new HomozygousColorState(state, Prefs.visColorSimple2State2, w, h));

			// Use the "other" colour for any remaining homozygous states
			else if (state.isHomozygous())
				states.add(new HomozygousColorState(state, Prefs.visColorSimple2Other, w, h));

			// Attempt to properly colour heterozygous states
			else
			{
				Color c1 = Prefs.visColorSimple2Other;
				Color c2 = Prefs.visColorSimple2Other;

				// Try to match either of the two alleles with the two coloured
				// heterozygous states
				if (state.getState(0).equals(s1))
					c1 = Prefs.visColorSimple2State1;
				else if (state.getState(0).equals(s2))
					c1 = Prefs.visColorSimple2State2;

				if (state.getState(1).equals(s1))
					c2 = Prefs.visColorSimple2State1;
				else if (state.getState(1).equals(s2))
					c2 = Prefs.visColorSimple2State2;

				states.add(new HeterozygeousColorState(state, Prefs.visColorSimple2Other, c1, c2, w, h));
			}
		}
	}

	public BufferedImage getSelectedImage(int line, int marker)
	{
		int state = view.getState(line, marker);
		return states.get(state).getImage();
	}

	public BufferedImage getUnselectedImage(int line, int marker)
	{
		int state = view.getState(line, marker);
		return states.get(state).getUnselectedImage();
	}

	public Color getColor(int line, int marker)
	{
		int state = view.getState(line, marker);
		return states.get(state).getColor();
	}

	public int getModel()
		{ return SIMPLE_TWO_COLOR; }

	public String toString()
		{ return RB.getString("gui.Actions.vizColorSimple2Color"); }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.SimpleTwoColorScheme");
	}

	public ArrayList<ColorSummary> getColorSummaries()
	{
		ArrayList<ColorSummary> colors = new ArrayList<ColorSummary>();

		colors.add(new ColorSummary(Prefs.visColorSimple2State1,
			RB.getString("gui.visualization.colors.SimpleTwoColorScheme.state1")));
		colors.add(new ColorSummary(Prefs.visColorSimple2State2,
			RB.getString("gui.visualization.colors.SimpleTwoColorScheme.state2")));
		colors.add(new ColorSummary(Prefs.visColorSimple2Other,
			RB.getString("gui.visualization.colors.SimpleTwoColorScheme.other")));

		return colors;
	}

	public void setColorSummaries(ArrayList<ColorSummary> colors)
	{
		Prefs.visColorSimple2State1 = colors.get(0).color;
		Prefs.visColorSimple2State2 = colors.get(1).color;
		Prefs.visColorSimple2Other = colors.get(2).color;
	}
}