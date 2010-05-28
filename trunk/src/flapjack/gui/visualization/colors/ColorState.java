// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;

import flapjack.data.*;

public abstract class ColorState
{
	protected AlleleState state;

	// AWT representation of this color
	protected Color color;
	protected Color gsColor;

	// Buffered image used to draw this allele to the canvas
	// This version is the normal view
	protected BufferedImage image;
	// And this is the greyscale representation of it
	protected BufferedImage gsImage;
	// And this is the non-selected representation of it
	protected BufferedImage unSelImage;

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

		gsColor = getGreyScale(color);
	}

	protected Color getGreyScale(Color color)
	{
		// Cheap and simple conversion - average of the three colours
//		int gs = (int) ((color.getRed()+color.getGreen()+color.getBlue())/3);

		// Luminance conversion - reflects human vision better (apparently)
		int gs = (int) (0.3*color.getRed()+0.59*color.getGreen()+0.11*color.getBlue());

		return new Color(gs, gs, gs);


		// For future reference: color-convert op that modifies an existing
		// image and changes it to greyscale - SLOW

//		ColorConvertOp op = new ColorConvertOp(
//			ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
//		image = op.filter(image, null);
	}

	// Creates a copy of the main image, but with a faded overlay that can be
	// used to display non-selected allele states
	protected void createUnselectedImage(int w, int h)
	{
		unSelImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = unSelImage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.setPaint(new Color(255, 255, 255, 200));
		g.fillRect(0, 0, w, h);
		g.dispose();
	}

	public BufferedImage getImage()
		{ return image; }

	public BufferedImage getGreyScaleImage()
		{ return gsImage; }

	public BufferedImage getUnselectedImage()
		{ return unSelImage; }

	public Color getColor()
		{ return color; }

	public Color getGreyScaleColor()
		{ return gsColor; }

	public static void setAlpha(int newAlpha)
	{
		alpha = newAlpha;
	}
}