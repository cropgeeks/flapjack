// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import java.awt.Color;

public class WebsafePalette
{
	private static Color[] colors;

	static
	{
		colors = new Color[216];

		int c = 0;

		for (int r = 0; r < 256; r += 51)
			for (int g = 0; g < 256; g += 51)
				for (int b = 0; b < 256; b += 51)
				{
					colors[c] = new Color(r, g, b);
					c++;
				}
	}

	public static int getColorCount()
	{
		return colors.length;
	}

	public static Color getColor(int index)
	{
		return colors[index % colors.length];
	}
}