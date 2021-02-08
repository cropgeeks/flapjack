// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;

import jhi.flapjack.data.*;

public abstract class ColorState
{
	protected AlleleState state;

	// AWT representation of this color
	protected Color color;

	// Buffered image used to draw this allele to the canvas
	// This version is the normal view
	protected BufferedImage image;
	// And tweaked if the allele is under a QTL
	protected BufferedImage imageUnderQTL;

	// And this is the non-selected representation of it
	protected BufferedImage unSelImage;
	// And tweaked if the allele is under a q QTL
	protected BufferedImage unSelImageUnderQTL;

	// Width and height of the image
	protected int w, h;

	// Alpha transparency effect to (potentially) apply to the final image
	protected static int alpha = 200;

	ColorState(AlleleState state, Color c, int w, int h)
	{
		this.state = state;
		this.color = c;
		this.w = w;
		this.h = h;

//		if (color == null)
//			createRandomColor();
	}

	// Creates a copy of the main image, but with a faded overlay that can be
	// used to display non-selected allele states
	protected void createUnselectedImage(int w, int h)
	{
		// "Normal" unselected image
		unSelImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = unSelImage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.setPaint(new Color(255, 255, 255, 200));
		g.fillRect(0, 0, w, h);
		g.dispose();

		// And "under QTL" version of the unselected image
		unSelImageUnderQTL = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		g = unSelImageUnderQTL.createGraphics();
		g.drawImage(imageUnderQTL, 0, 0, null);
		g.setPaint(new Color(255, 255, 255, 200));
		g.fillRect(0, 0, w, h);
		g.dispose();
	}

	public BufferedImage getImage(boolean underQTL)
	{
		return underQTL ? imageUnderQTL : image;
	}

	public BufferedImage getUnselectedImage(boolean underQTL)
	{
		return underQTL ? unSelImageUnderQTL : unSelImage;
	}

	public Color getColor()
		{ return color; }

	public static void setAlpha(int newAlpha)
	{
		alpha = newAlpha;
	}
}