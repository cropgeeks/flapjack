// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

public class SimpleColorState extends ColorState
{
	SimpleColorState(AlleleState state, Color c, int w, int h)
	{
		super(state, c, w, h);

		// This class is ONLY used to paint missing data, so we want to use the
		// same colour in both cases (Prefs.visColorBackground)
		image = createBuffer(color);
		imageUnderQTL = createBuffer(color);

		createUnselectedImage(w, h);
	}

	private BufferedImage createBuffer(Color c)
	{
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		g.setColor(c);

		Rectangle2D.Float r = new Rectangle2D.Float(0, 0, w, h);
		g.fill(r);

		if (Prefs.visHighlightHtZ || (Prefs.visHighlightGaps && c != Prefs.visColorBackground) || alpha < 200)
		{
			g.setPaint(new Color(20, 20, 20, alpha));
			g.fillRect(0, 0, w, h);
		}

		g.dispose();
		return image;
	}
}