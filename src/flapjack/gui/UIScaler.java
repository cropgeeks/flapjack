// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.awt.*;

/**
 * Utility class designed to aid in coding for high DPI displays.
 */
public class UIScaler
{
	public static float UI = 1.0f;

	UIScaler(float UI)
	{
		this.UI = UI;

		System.out.println("DPI: " + Toolkit.getDefaultToolkit().getScreenResolution());
	}

	/**
	 * Takes a Font and returns a new Font suitably scaled for the current
	 * environment. Note that on Windows (at least) anything using the System
	 * L&F will normally have fonts scaled automatically (in JLabels, etc) but
	 * any custom fonts (eg used with Graphics2D) will need rescaled.
	 */
	public static Font getFont(Font font)
	{
		return font.deriveFont(font.getStyle(), Math.round(font.getSize() * UI));
	}

	public static int scale(int value)
	{
		return (UI == 1.0f) ? value: Math.round(value * UI);
	}
}