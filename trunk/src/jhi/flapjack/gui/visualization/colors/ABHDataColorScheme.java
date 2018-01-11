// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class ABHDataColorScheme extends ColorScheme
{
	protected ArrayList<ColorState> states = new ArrayList<>();

	/** Empty constructor that is ONLY used for color customization purposes. */
	public ABHDataColorScheme() {}

	public ABHDataColorScheme(GTView view, int w, int h)
	{
		super(view);

		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);
			ColorState c = null;

			// Use white for the default unknown state
			if (state.isUnknown())
				c = new SimpleColorState(state, Prefs.visColorBackground, w, h);

			// Homozygous states
			else if (state.isHomozygous())
			{
				if (state.homzAllele().equals("A"))
					c = new HomozygousColorState(state, Prefs.visColorABH_A, w, h);
				else if (state.homzAllele().equals("B"))
					c = new HomozygousColorState(state, Prefs.visColorABH_B, w, h);
				else if (state.homzAllele().equals("H"))
					c = new HomozygousColorState(state, Prefs.visColorABH_H, w, h);

				else
					c = new HomozygousColorState(state, Prefs.visColorABH_Other, w, h);
			}

			// Heterozygous states
/*			else
			{
				System.out.println("STATE: " + state.getRawData());

				if (state.getRawData().equals("A/B"))
					c = new HeterozygeousColorState(state, Prefs.visColorABH_B, Prefs.visColorABH_A, Prefs.visColorABH_Other, w, h);

				else
					c = new HeterozygeousColorState(state, Prefs.visColorABH_Other, Prefs.visColorABH_Other, Prefs.visColorABH_Other, w, h);
			}
*/
			states.add(c);
		}
	}

	public BufferedImage getSelectedImage(int line, int marker, boolean underQTL)
	{
		int state = view.getState(line, marker);
		return states.get(state).getImage(underQTL);
	}

	public BufferedImage getUnselectedImage(int line, int marker, boolean underQTL)
	{
		int state = view.getState(line, marker);
		return states.get(state).getUnselectedImage(underQTL);
	}

	public Color getColor(int line, int marker)
	{
		int state = view.getState(line, marker);
		return states.get(state).getColor();
	}

	public int getModel()
		{ return ABH_DATA; }

	public String toString()
		{ return RB.getString("gui.Actions.vizColorABHData"); }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.ABHColorScheme");
	}

	public ArrayList<ColorSummary> getColorSummaries()
	{
		ArrayList<ColorSummary> colors = new ArrayList<>();

		colors.add(new ColorSummary(Prefs.visColorABH_A,
			RB.getString("gui.visualization.colors.ABHColorScheme.a")));
		colors.add(new ColorSummary(Prefs.visColorABH_B,
			RB.getString("gui.visualization.colors.ABHColorScheme.b")));
		colors.add(new ColorSummary(Prefs.visColorABH_H,
			RB.getString("gui.visualization.colors.ABHColorScheme.h")));
		colors.add(new ColorSummary(Prefs.visColorABH_C,
			RB.getString("gui.visualization.colors.ABHColorScheme.c")));
		colors.add(new ColorSummary(Prefs.visColorABH_D,
			RB.getString("gui.visualization.colors.ABHColorScheme.d")));
		colors.add(new ColorSummary(Prefs.visColorABH_Other,
			RB.getString("gui.visualization.colors.ABHColorScheme.other")));

		return colors;
	}

	public void setColorSummaries(ArrayList<ColorSummary> colors)
	{
		Prefs.visColorABH_A = colors.get(0).color;
		Prefs.visColorABH_B = colors.get(1).color;
		Prefs.visColorABH_H = colors.get(2).color;
		Prefs.visColorABH_C = colors.get(3).color;
		Prefs.visColorABH_D = colors.get(4).color;
		Prefs.visColorABH_Other = colors.get(5).color;
	}
}