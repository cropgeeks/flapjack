// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class RandomColorScheme extends ColorScheme
{
	private int type;

	private ArrayList<ColorState> states = new ArrayList<ColorState>();

	/** Constructor that is ONLY used for color customization purposes. */
	public RandomColorScheme(int type)
	{
		this.type = type;
	}

	public RandomColorScheme(int type, GTView view, int w, int h)
	{
		super(view);
		this.type = type;

		// Temp storage for the colors as we "invent" them
		Hashtable<String, Color> hashtable = new Hashtable<String, Color>();

		// Use white for the default unknown state
		AlleleState state = stateTable.getAlleleState(0);
		states.add(new SimpleColorState(state, Prefs.visColorBackground, w, h));

		int seed = view.getViewSet().getRandomColorSeed();
		float colorsNeeded = stateTable.calculateUniqueStateCount();

		float colorSpacing = 0, fColor = 0;

		if (type == RANDOM)
		{
			colorSpacing = 1 / colorsNeeded;
			fColor = 1 / 50000f * seed;
		}
		else if (type == RANDOM_WSP)
		{
			colorSpacing = WebsafePalette.getColorCount() / colorsNeeded;
			fColor = seed;
		}


		// And random colors for everything else
		for (int i = 1; i < stateTable.size(); i++)
		{
			state = stateTable.getAlleleState(i);

			if (state.isHomozygous())
			{
				Color color = hashtable.get(state.toString());
				if (color == null)
				{
					if (type == RANDOM)
						color = Color.getHSBColor(fColor, 0.5f, 1);
					else if (type == RANDOM_WSP)
						color = WebsafePalette.getColor((int)fColor);
					fColor += colorSpacing;

					hashtable.put(state.toString(), color);
				}

				states.add(new HomozygousColorState(state, color, w, h));
			}
			else
			{
				// Get the color for the first het half
				Color c1 = hashtable.get(state.getState(0));
				if (c1 == null)
				{
					if (type == RANDOM)
						c1 = Color.getHSBColor(fColor, 0.5f, 1);
					else if (type == RANDOM_WSP)
						c1 = WebsafePalette.getColor((int)fColor);
					fColor += colorSpacing;

					hashtable.put(state.getState(0), c1);
				}

				// Get the color for the second het half
				Color c2 = hashtable.get(state.getState(1));
				if (c2 == null)
				{
					if (type == RANDOM)
						c2 = Color.getHSBColor(fColor, 0.5f, 1);
					else if (type == RANDOM_WSP)
						c2 = WebsafePalette.getColor((int)fColor);
					fColor += colorSpacing;

					hashtable.put(state.getState(1), c2);
				}

				states.add(new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, c1, c2, w, h));
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
	{
		if (type == ColorScheme.RANDOM)
			return RANDOM;
		else
			return RANDOM_WSP;
	}

	public String toString()
	{
		if (type == RANDOM)
			return RB.getString("gui.Actions.vizColorRandom");
		else
			return RB.getString("gui.Actions.vizColorRandomWSP");
	}

	public String getDescription()
	{
		if (type == RANDOM)
			return RB.getString("gui.visualization.colors.RandomColorScheme");
		else
			return RB.getString("gui.visualization.colors.RandomColorSchemeWSP");
	}

	public ArrayList<ColorSummary> getColorSummaries()
		{ return new ArrayList<ColorSummary>(); }

	public void setColorSummaries(ArrayList<ColorSummary> colors) {}
}