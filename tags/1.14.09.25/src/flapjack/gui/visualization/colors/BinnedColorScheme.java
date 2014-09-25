// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
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

		create(w, h);
	}

	/**
	 * Special constructor to be used *only* by the io.binning.CreateImage code
	 * that needs to use this colour scheme externally to Flapjack
	 */
	public BinnedColorScheme(StateTable stateTable, int w, int h)
	{
		this.stateTable = stateTable;

		create(w, h);
	}

	private void create(int w, int h)
	{
		// Initialize the colors
		Color col1 = Prefs.visColorBinnedLow;
		int[] c1 = new int[] { col1.getRed(), col1.getGreen(), col1.getBlue() };
		Color col2 = Prefs.visColorBinnedHigh;
		int[] c2 = new int[] { col2.getRed(), col2.getGreen(), col2.getBlue() };

		// Scan the state table to determine how many bins there are. This needs
		// to be the maximum value found in the table (on the offchance some
		// bins weren't represented, eg 0, 1, 2, 3...5 (missing 4)).
		Integer binCount = null;
		for (int i = 1; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);
			try
			{
				int bin = Integer.parseInt(state.getRawData());
				if (binCount == null || bin > binCount)
					binCount = bin;
			}
			catch (NumberFormatException e) {}
		}

		// As the bins are zero-indexed, add one to the max value found
		if (binCount != null)
			binCount++;

		// Or, if we didn't find any numbers, failsafe with the size of the
		// table, minus 1 to ignore the empty state at the start
		else
			binCount = stateTable.size() - 1;



		// Now apply colours for each of the states found
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

					// We want bin=0 to map to f=0 and bin=9 to map to f=1 so we
					// use 1/binCount-1 for the range rather than 1/binCount:
					// eg:
					//     1/binCount   = 0.1:   0 * 0.1   = 0;  9 * 0.1 = 0.9
					//     1/binCount-1 = 0.111: 0 * 0.111 = 0;  9 * 0.111 = 1
					float f = bin * (1 / ((float)binCount-1));

					float f1 = (float) (1.0 - f);
					float f2 = (float) f;

					Color color = new Color(
	          				(int) (f1 * c1[0] + f2 * c2[0]),
	      					(int) (f1 * c1[1] + f2 * c2[1]),
	      					(int) (f1 * c1[2] + f2 * c2[2]));

					states.add(new HomozygousColorState(state, color, w, h));
				}
				// If we can't parse the state as a number, then blank it
				catch (Exception e)
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

	// Only used by the io.binning.CreateImage code
	public Color getColor(int bin)
	{
		// Offset by 1 to ignore the empty state
		return states.get(bin+1).getColor();
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