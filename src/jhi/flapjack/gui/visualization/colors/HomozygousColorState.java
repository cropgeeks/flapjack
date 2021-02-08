// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

public class HomozygousColorState extends ColorState
{
	HomozygousColorState(AlleleState state, Color c, int w, int h)
	{
		super(state, c, w, h);

		image = createBuffer(color, false);
		imageUnderQTL = createBuffer(color, true);

		createUnselectedImage(w, h);
	}

	private BufferedImage createBuffer(Color c, boolean isUnderQTL)
	{
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		Color c1 = c.brighter();
		Color c2 = c.darker();

		if (Prefs.visDisableGradients)
			g.setColor(c);
		else
			g.setPaint(new GradientPaint(0, 0, c1, w, h, c2));

		Rectangle2D.Float r = null;

//		RoundRectangle2D.Float r = new RoundRectangle2D.Float(1, 1, w-1, h-1, 7, 7);
//		Rectangle2D.Float r = new Rectangle2D.Float(0, 0, w, h);

//		if (w > 7 && h > 7)
//			r = new Rectangle2D.Float(1, 1, w-1, h-1);
//		else
			r = new Rectangle2D.Float(0, 0, w, h);

		g.fill(r);

		if (isUnderQTL)
		{
//			g.setPaint(new Color(20, 20, 20, 75));
//			g.fillRect(0, 0, w, h);
		}

		if (Prefs.visShowGenotypes && h >= 7 && Prefs.visLinkSliders)
		{
			String str = state.homzAllele();

			Font font = g.getFont().deriveFont(Font.PLAIN, h-3);
			g.setFont(font);
			FontMetrics fm = g.getFontMetrics();

			Rectangle2D bounds = fm.getStringBounds(str, g);

			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	//		if (isUnderQTL)
	//			g.setColor(Color.red);
	//		else
				g.setColor(Prefs.visColorText);
			g.drawString(str,
				(int)((float)w/2-bounds.getWidth()/2),
				h - fm.getMaxDescent());
		}


		if (Prefs.visHighlightHtZ || (Prefs.visHighlightGaps && c != Prefs.visColorBackground) || alpha < 200)
		{
			g.setPaint(new Color(20, 20, 20, alpha));
			g.fillRect(0, 0, w, h);
		}

		g.dispose();
		return image;
	}
}