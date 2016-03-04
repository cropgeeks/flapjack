// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class MagicColorScheme extends ColorScheme
{
	protected ArrayList<ColorState> states = new ArrayList<ColorState>();

	private String[] colorIds = new String[] { "1", "2", "3", "4", "5", "6", "7", "8" };
	private Color[] colors = new Color[] { Prefs.visColorMagic1, Prefs.visColorMagic2,
		Prefs.visColorMagic3, Prefs.visColorMagic4, Prefs.visColorMagic5, Prefs.visColorMagic6,
		Prefs.visColorMagic7, Prefs.visColorMagic8 };

	/** Empty constructor that is ONLY used for color customization purposes. */
	public MagicColorScheme() {}

	public MagicColorScheme(GTView view, int w, int h)
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
				for (int j=0; j < colorIds.length; j++)
				{
					if (state.getRawData().equals(colorIds[j]))
					{
						c = new HomozygousColorState(state, colors[j], w, h);
						break;
					}

					// Use a fixed color for any further unknown states
					else
						c = new HomozygousColorState(state, Prefs.visColorNucleotideOther, w, h);
				}
			}

			// Heterozygous states
			else
			{
				c = new HeterozygeousColorState(state, Prefs.visColorNucleotideOther, Prefs.visColorNucleotideOther, Prefs.visColorNucleotideOther, w, h);
				for (int j=0; j < colorIds.length; j++)
				{
					for (int k=0; k < colorIds.length; k++)
					{
						if (state.getState(0).equals(colorIds[j]) && state.getState(1).equals(colorIds[k]))
						{
							c = new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, colors[j], colors[k], w, h);
						}
					}
				}
			}

			states.add(c);
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
		{ return MAGIC; }

	public String toString()
		{ return RB.getString("gui.Actions.vizColorMagic"); }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.MagicColorScheme");
	}

	@Override
	public ArrayList<ColorSummary> getColorSummaries()
	{
		ArrayList<ColorSummary> summaries = new ArrayList<>();

		summaries.add(new ColorSummary(Prefs.visColorMagic1,
			RB.getString("gui.visualization.colors.MagicColorScheme.parent1")));
		summaries.add(new ColorSummary(Prefs.visColorMagic2,
			RB.getString("gui.visualization.colors.MagicColorScheme.parent2")));
		summaries.add(new ColorSummary(Prefs.visColorMagic3,
			RB.getString("gui.visualization.colors.MagicColorScheme.parent3")));
		summaries.add(new ColorSummary(Prefs.visColorMagic4,
			RB.getString("gui.visualization.colors.MagicColorScheme.parent4")));
		summaries.add(new ColorSummary(Prefs.visColorMagic5,
			RB.getString("gui.visualization.colors.MagicColorScheme.parent5")));
		summaries.add(new ColorSummary(Prefs.visColorMagic6,
			RB.getString("gui.visualization.colors.MagicColorScheme.parent6")));
		summaries.add(new ColorSummary(Prefs.visColorMagic7,
			RB.getString("gui.visualization.colors.MagicColorScheme.parent7")));
		summaries.add(new ColorSummary(Prefs.visColorMagic8,
			RB.getString("gui.visualization.colors.MagicColorScheme.parent8")));
		summaries.add(new ColorSummary(Prefs.visColorNucleotideOther,
			RB.getString("gui.visualization.colors.NucleotideColorScheme.other")));

		return summaries;
	}

	@Override
	public void setColorSummaries(ArrayList<ColorSummary> colors)
	{
		Prefs.visColorMagic1 = colors.get(0).color;
		Prefs.visColorMagic2 = colors.get(1).color;
		Prefs.visColorMagic3 = colors.get(2).color;
		Prefs.visColorMagic4 = colors.get(3).color;
		Prefs.visColorMagic5 = colors.get(4).color;
		Prefs.visColorMagic6 = colors.get(5).color;
		Prefs.visColorMagic7 = colors.get(6).color;
		Prefs.visColorMagic8 = colors.get(7).color;
		Prefs.visColorNucleotideOther = colors.get(8).color;
	}
}