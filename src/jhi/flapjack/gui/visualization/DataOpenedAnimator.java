// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.awt.geom.*;

import jhi.flapjack.gui.*;

public class DataOpenedAnimator extends Thread implements IOverlayRenderer
{
	private GenotypeCanvas canvas;

	// Position and size of the clip region
	private int x, y;
	private int w, h;

	public DataOpenedAnimator(GenotypePanel gPanel)
	{
		canvas = gPanel.canvas;

		start();
	}

	public void run()
	{
		canvas.overlays.addFirst(this);

		// This wait is needed as when Flapjack is first loaded, the canvas will
		// not have size values set properly until it is displayed
//		while (canvas.pX2 == 0)
			try { Thread.sleep(100); }
			catch (Exception e) {}

		// Half the width/height of the current display area
		int w2 = (canvas.pX2-canvas.pX1)/2;
		int h2 = (canvas.pY2-canvas.pY1)/2;

		// Increment size to grow the clip region
		int stepX = w2/22;
		int stepY = h2/22;

		for (int i = 0; i < 45; i++)
		{
			// Top-left corner of clip... from top-left of canvas (pX1) to its
			// middle point (+w2), minus the size of the clip area
			x = canvas.pX1 + w2 - (i*stepX);
			y = canvas.pY1 + h2 - (i*stepY);

			w = (i*stepX) * 2;
			h = (i*stepY) * 2;

			canvas.repaint();

			try { Thread.sleep(750/45); }
			catch (Exception e) {}
		}

		canvas.overlays.remove(this);
		canvas.repaint();
	}

	public void render(Graphics2D g)
	{
		// Clear the background as setClip() only works BEFORE a render, so it
		// will have no effect on elements already drawn
		g.setColor(Prefs.visColorBackground);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		// Set the clip, then redraw the buffer to the screen
		g.clip(new Ellipse2D.Float(x, y, w, h));
		g.drawImage(canvas.imageViewPort, canvas.pX1, canvas.pY1, null);
	}
}