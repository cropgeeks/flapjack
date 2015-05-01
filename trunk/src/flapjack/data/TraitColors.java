// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.data;

import java.awt.*;
import java.util.*;

import flapjack.gui.*;

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

	Color displayColor(float value, float normal)
	{
		Color col1 = Prefs.visColorHeatmapLow;
		int[] c1 = new int[] { col1.getRed(), col1.getGreen(), col1.getBlue() };
		Color col2 = Prefs.visColorHeatmapHigh;
		int[] c2 = new int[] { col2.getRed(), col2.getGreen(), col2.getBlue() };

		float f1 = 1f - normal;
		float f2 = normal;

		Color color = new Color(
			(int) (f1 * c1[0] + f2 * c2[0]),
			(int) (f1 * c1[1] + f2 * c2[1]),
			(int) (f1 * c1[2] + f2 * c2[2]));

		// DON'T LOOK UP HASHTABLE IF IT'S EMPTY

		return color;
	}
}