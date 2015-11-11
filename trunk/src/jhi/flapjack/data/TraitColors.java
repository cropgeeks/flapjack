// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

import java.awt.*;
import java.util.*;

import jhi.flapjack.gui.*;

public class TraitColors extends XMLRoot
{
	private HashMap<String,Integer> colors = new HashMap<>();

	public TraitColors()
	{
	}


	// Methods required for XML serialization

	public HashMap<String,Integer> getColors()
		{ return colors; }

	public void setColors(HashMap<String,Integer> colors)
		{ this.colors = colors; }


	// Other methods

	public void clear()
	{
		colors.clear();
	}

	public void put(String key, Color value)
	{
		colors.put(key, value.getRGB());
	}

	public Color get(String key)
	{
		Integer rgb = colors.get(key);

		if (rgb != null)
			return new Color(rgb);
		else
			return null;
	}

	// Returns a LOW value for use with the heatmap
	public Color queryLow()
	{
		if (colors.size() > 0 && colors.containsKey("FLAPJACK_LW"))
			return get("FLAPJACK_LW");

		return Prefs.visColorHeatmapLow;

	}

	// Returns a HIGH value for use with the heatmap
	public Color queryHigh()
	{
		if (colors.size() > 0 && colors.containsKey("FLAPJACK_HG"))
			return get("FLAPJACK_HG");

		return Prefs.visColorHeatmapHigh;

	}

	Color displayColor(Trait trait, float value, float normal)
	{
		// Start by looking for a custom colour for the specific category
		if (colors.size() > 0)
		{
			// TODO: Should we be looking for custom colours for numericals?
			if (trait.traitIsNumerical() == false)
			{
				String key = trait.getCategories().get((int)value);
				if (colors.containsKey(key))
					return get(key);
			}
		}

		// If that fails, then work out a gradiant paint somewhere on the
		// low/high scale
		Color col1 = queryLow();
		int[] c1 = new int[] { col1.getRed(), col1.getGreen(), col1.getBlue() };
		Color col2 = queryHigh();
		int[] c2 = new int[] { col2.getRed(), col2.getGreen(), col2.getBlue() };

		float f1 = 1f - normal;
		float f2 = normal;

		Color color = new Color(
			(int) (f1 * c1[0] + f2 * c2[0]),
			(int) (f1 * c1[1] + f2 * c2[1]),
			(int) (f1 * c1[2] + f2 * c2[2]));

		return color;
	}
}