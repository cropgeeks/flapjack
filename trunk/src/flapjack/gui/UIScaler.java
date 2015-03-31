// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import scri.commons.gui.*;

/**
 * Utility class designed to aid in coding for high DPI displays.
 */
public class UIScaler
{
	private static int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
	public static float UI = 1.0f;

	public static float init(float uiScale, boolean auto)
	{
		UI = uiScale;

		// Attempt to work out a scaling ratio
		if (auto)
		{
			// Detection doesn't seem to be possible on OS X - each system
			// returns a different DPI (probably correctly)
			if (SystemUtils.isMacOS())
				UI = 1.0f;

			// Windows/Linux *appear* to set a baseline of 96DPI though
			else
				UI = dpi / 96f;
		}

		System.out.println("DPI: " + dpi + ", scaling: " + UI + ", auto=" + auto);

		return UI;
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

	/**
	 * Attempts to set a suitable table/list/tree row cell height based on the
	 * current font scaling value.
	 */
	public static void setCellHeight(JComponent c)
	{
		if (UI == 1.0f)
			return;

		Font font = UIManager.getFont("Label.font");
		int height = c.getFontMetrics(font).getHeight();

		if (c instanceof JTable)
			((JTable)c).setRowHeight(height + scale(2));

		else if (c instanceof JTree)
			((JTree)c).setRowHeight(height + scale(2));
	}
}