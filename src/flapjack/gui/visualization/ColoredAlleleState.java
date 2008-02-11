package flapjack.gui.visualization;

import java.awt.*;
import java.awt.color.*;
import java.awt.geom.*;
import java.awt.image.*;

import flapjack.data.*;

class ColoredAlleleState
{
	private AlleleState state;

	// AWT representation of this color, plus brighter and darker versions
	private Color color, colorB, colorD;
	private Color gsColor;

	// Buffered image used to draw this allele to the canvas
	// This version is the normal view
	private BufferedImage image;
	// And this is the greyscale representation of it
	private BufferedImage gsImage;

	// Width and height of the image
	private int w, h;

	ColoredAlleleState(AlleleState state, int w, int h)
	{
		this.state = state;
		this.w = w;
		this.h = h;

		// Don't create colors et al for the UNKNOWN state
		if (state.toString().length() > 0)
		{
			createColor();

			image = createBuffer(colorB, colorD);
			gsImage = createBuffer(getGSColor(colorB), getGSColor(colorD));
		}
	}

	private Color getGSColor(Color color)
	{
		// Cheap and simple conversion - average of the three colours
//		int gs = (int) ((color.getRed()+color.getGreen()+color.getBlue())/3);

		// Luminance conversion - reflects human vision better (apparently)
		int gs = (int) (0.3*color.getRed()+0.59*color.getGreen()+0.11*color.getBlue());

		return new Color(gs, gs, gs);


		// For future reference: color-convert op that modifies an existing
		// image and changes it to grayscale - SLOW

//		ColorConvertOp op = new ColorConvertOp(
//			ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
//		image = op.filter(image, null);
	}

	private BufferedImage createBuffer(Color c1, Color c2)
	{
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

//		g.setColor(Color.white);
//		g.fillRect(0, 0, w, h);

		g.setPaint(new GradientPaint(0, 0, c1, w, h, c2));

		Rectangle2D.Float r = null;

//		RoundRectangle2D.Float r = new RoundRectangle2D.Float(1, 1, w-1, h-1, 7, 7);
//		Rectangle2D.Float r = new Rectangle2D.Float(0, 0, w, h);

//		if (w > 7 && h > 7)
//			r = new Rectangle2D.Float(1, 1, w-1, h-1);
//		else
			r = new Rectangle2D.Float(0, 0, w, h);

		g.fill(r);


		if (true == false && h >= 10)
		{
			//Font font = new Font("Monospaced", Font.PLAIN, h);
			Font font = g.getFont().deriveFont(Font.PLAIN, h-3);
			g.setFont(font);
			FontMetrics fm = g.getFontMetrics();

			Rectangle2D bounds = fm.getStringBounds(state.toString(), g);

			g.setColor(Color.black);
			g.drawString(state.toString(),
				(int)((float)w/2-bounds.getWidth()/2),
				h - fm.getMaxDescent());
		}

//		r = new Rectangle2D.Float(0, 0, w-1, h-1);
//		g.setColor(Color.black);
//		g.draw(r);

//		g.fillRect(0, 0, w, h);
		g.dispose();

		return image;
	}

	public BufferedImage getImage()
		{ return image; }

	public BufferedImage getGSImage()
		{ return gsImage; }

	private void createColor()
	{
		int value = 0;
		for (int i = 0; i < state.toString().length(); i++)
			value += state.toString().charAt(i);

		java.util.Random rnd = new java.util.Random(value+555);

		int r = rnd.nextInt(255);
		int g = rnd.nextInt(255);
		int b = rnd.nextInt(255);

		color = new Color(r, g, b);
		colorB = color.brighter();
		colorD = color.darker();

		gsColor = getGSColor(color);
	}

	public Color getColor()
		{ return color; }

	public Color getBrightColor()
		{ return colorB; }

	public Color getDarkColor()
		{ return colorD; }
}