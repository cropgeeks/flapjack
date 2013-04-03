// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import flapjack.data.*;
import flapjack.gui.*;

/**
 * Graphically represents 2-state heterozygeous alleles, on the assumption that
 * a single primary color is used to represent the state on the overview canvas,
 * but a split-view 2-color model used on the main canvas.
 */
public class HeterozygeousColorState extends ColorState
{
	HeterozygeousColorState(AlleleState state, Color primary, Color state1, Color state2, int w, int h)
	{
		super(state, primary, w, h);

		image = createBuffer(state1, state2);
		gsImage = createBuffer(getGreyScale(state1), getGreyScale(state2));

		createUnselectedImage(w, h);
	}

	private BufferedImage createBuffer(Color c1, Color c2)
	{
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		String s1 = state.getState(0);
		String s2 = state.getState(1);

		drawState(g, c1.brighter(), c1.darker(), s1, w, h, true);
		drawState(g, c2.brighter(), c2.darker(), s2, w, h, false);

		if (Prefs.visHighlightGaps)
		{
			g.setPaint(new Color(20, 20, 20, alpha));
			g.fillRect(0, 0, w, h);
		}

		g.dispose();

		return image;
	}

	private void drawState(Graphics2D g, Color c1, Color c2, String str, int w, int h, boolean lhs)
	{
		int[] x = null;
		int[] y = null;

		// Coordinates for a triangle from the top left hand corner
		if (lhs)
		{
			x = new int[] { 0, w, 0 };
			y = new int[] { 0, 0, h };
		}
		// Coordinates for a triangle from the bottom right hand corner
		else
		{
			x = new int[] { w, 0, w };
			y = new int[] { 0, h, h };
		}

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setPaint(new GradientPaint(0, 0, c1, w, h, c2));
		g.fill(new Polygon(x, y, 3));


		if (Prefs.visShowGenotypes && h >= 7 && Prefs.visLinkSliders)
		{
			Font font = g.getFont().deriveFont(Font.PLAIN, (h-h/2));
			g.setColor(Prefs.visColorText);
			g.setFont(font);

			// Horrible font calculations...
			FontMetrics fm = g.getFontMetrics();
			Rectangle2D bounds = fm.getStringBounds(str, g);

			// 1/2 the width of the string
			float strW2 = (float) (bounds.getWidth() / 2f);
			// 1/2 the height of the string
			float strH2 = (fm.getAscent()-fm.getDescent()) / 2f;

			// 1/4 of the box's width
			float w4 = w/4f;
			// 1/4 of the box's height
			float h4 = h/4f;


			// Draw the text at the centre of the 1st quadrant
			if (lhs)
				g.drawString(str, w4-strW2, h4+strH2+1);

			// Draw the text at the centre of the 4th quadrant
			else
				g.drawString(str, w4*3-strW2, h4*3+strH2-1);
		}
	}
}