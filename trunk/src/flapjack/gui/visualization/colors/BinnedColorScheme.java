// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class BinnedColorScheme extends ColorScheme
{
	protected ArrayList<ColorState> states = new ArrayList<>();

	/** Empty constructor that is ONLY used for color customization purposes. */
	public BinnedColorScheme() {}

	public BinnedColorScheme(GTView view, int w, int h)
	{
		super(view);

		// Initialize the colors
		Color col1 = Prefs.visColorBinnedLow;
		int[] c1 = new int[] { col1.getRed(), col1.getGreen(), col1.getBlue() };
		Color col2 = Prefs.visColorBinnedHigh;
		int[] c2 = new int[] { col2.getRed(), col2.getGreen(), col2.getBlue() };

		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);

			// Use white for the default unknown state
			if (state.isUnknown())
				states.add(new SimpleColorState(state, Prefs.visColorBackground, w, h));

			else
			{
				try
				{
					int bin = Integer.parseInt(state.getRawData());
					float f = (bin+1)/10f;

					float f1 = (float) (1.0 - f);
					float f2 = (float) f;

					Color color = new Color(
	          				(int) (f1 * c1[0] + f2 * c2[0]),
	      					(int) (f1 * c1[1] + f2 * c2[1]),
	      					(int) (f1 * c1[2] + f2 * c2[2]));

					states.add(new HomozygousColorState(state, color, w, h));
				}
				// If we can't "parse" the state as a number, then blank it
				catch (NumberFormatException e)
				{
					states.add(new HomozygousColorState(state, Prefs.visColorSimple2Other, w, h));
				}
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
		{ return BINNED_10; }

	public String toString()
		{ return RB.getString("gui.Actions.vizColorBinned"); }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.BinnedColorScheme");
	}

	public ArrayList<ColorSummary> getColorSummaries()
	{
		ArrayList<ColorSummary> colors = new ArrayList<>();

		colors.add(new ColorSummary(Prefs.visColorBinnedLow, "Low"));
//			RB.getString("gui.visualization.colors.SimpleTwoColorScheme.state1")));
		colors.add(new ColorSummary(Prefs.visColorBinnedHigh, "High"));
//			RB.getString("gui.visualization.colors.SimpleTwoColorScheme.state2")));
		colors.add(new ColorSummary(Prefs.visColorBinnedOther, "Other"));

		return colors;
	}

	public void setColorSummaries(ArrayList<ColorSummary> colors)
	{
		Prefs.visColorBinnedLow = colors.get(0).color;
		Prefs.visColorBinnedHigh = colors.get(1).color;
		Prefs.visColorBinnedOther = colors.get(2).color;
	}
}