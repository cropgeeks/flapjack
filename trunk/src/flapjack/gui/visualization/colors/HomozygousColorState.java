package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.color.*;
import java.awt.geom.*;
import java.awt.image.*;

import flapjack.data.*;
import flapjack.gui.*;

public class HomozygousColorState extends ColorState
{
	HomozygousColorState(AlleleState state, Color c, int w, int h)
	{
		super(state, c, w, h);

		image = createBuffer(color);
		gsImage = createBuffer(getGreyScale(color));
	}

	private BufferedImage createBuffer(Color c)
	{
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		Color c1 = c.brighter();
		Color c2 = c.darker();

		g.setPaint(new GradientPaint(0, 0, c1, w, h, c2));

		Rectangle2D.Float r = null;

//		RoundRectangle2D.Float r = new RoundRectangle2D.Float(1, 1, w-1, h-1, 7, 7);
//		Rectangle2D.Float r = new Rectangle2D.Float(0, 0, w, h);

//		if (w > 7 && h > 7)
//			r = new Rectangle2D.Float(1, 1, w-1, h-1);
//		else
			r = new Rectangle2D.Float(0, 0, w, h);

		g.fill(r);


		if (Prefs.visShowGenotypes && h >= 7 && Prefs.visLinkSliders)
		{
			String str = state.toString();

			Font font = g.getFont().deriveFont(Font.PLAIN, h-3);
			g.setFont(font);
			FontMetrics fm = g.getFontMetrics();

			Rectangle2D bounds = fm.getStringBounds(str, g);

			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(Prefs.visColorText);
			g.drawString(str,
				(int)((float)w/2-bounds.getWidth()/2),
				h - fm.getMaxDescent());
		}


		if (Prefs.visHighlightHZ || alpha < 200)
		{
			g.setPaint(new Color(20, 20, 20, alpha));
			g.fillRect(0, 0, w, h);
		}

		g.dispose();
		return image;
	}
}