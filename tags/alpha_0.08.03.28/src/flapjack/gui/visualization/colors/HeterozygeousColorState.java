package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.color.*;
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
	}

	private BufferedImage createBuffer(Color c1, Color c2)
	{
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		String s1 = state.getState(0);
		String s2 = state.getState(1);

		drawState(g, c1.brighter(), c1.darker(), s1, (int)w/2, h);
		g.translate((int)w/2, 0);
		drawState(g, c2.brighter(), c2.darker(), s2, (int)w/2, h);

		g.dispose();

		return image;
	}

	private void drawState(Graphics2D g, Color c1, Color c2, String str, int w, int h)
	{
		g.setPaint(new GradientPaint(0, 0, c1, w, h, c2));

		Rectangle2D.Float r = null;

//		RoundRectangle2D.Float r = new RoundRectangle2D.Float(1, 1, w-1, h-1, 7, 7);
//		Rectangle2D.Float r = new Rectangle2D.Float(0, 0, w, h);

//		if (w > 7 && h > 7)
//			r = new Rectangle2D.Float(1, 1, w-1, h-1);
//		else
			r = new Rectangle2D.Float(0, 0, w, h);

		g.fill(r);


		if (Prefs.visShowGenotypes && h >= 7)
		{
			Font font = g.getFont().deriveFont(Font.PLAIN, h-3);
			g.setFont(font);
			FontMetrics fm = g.getFontMetrics();

			Rectangle2D bounds = fm.getStringBounds(str, g);

			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(Color.black);
			g.drawString(str,
				(int)((float)w/2-bounds.getWidth()/2),
				h - fm.getMaxDescent());
		}

//		g.fillRect(0, 0, w, h);

	}
}