package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.color.*;
import java.awt.geom.*;
import java.awt.image.*;

import flapjack.data.*;
import flapjack.gui.*;

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

	// Width and height of the image
	protected int w, h;

	// Alpha transparency effect to (potentially) apply to the final image
	protected int alpha = 200;

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

	public BufferedImage getImage()
		{ return image; }

	public BufferedImage getGreyScaleImage()
		{ return gsImage; }

	public Color getColor()
		{ return color; }

	public Color getGreyScaleColor()
		{ return gsColor; }
}