// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * Helper class that runs a background thread to draw the map canvas (with anti-
 * aliased graphics) onto a separate buffer. When the buffer is ready, the
 * canvas is told to repaint itself using the new (prettier) image. Every main
 * repaint causes an instance of this class to run, but each new instance kills
 * off its predecessor, resulting in no impact to performance.
 */
class MapCanvasAAThread extends Thread
{
	private static MapCanvasAAThread previousThread;
	static boolean bufferAvailable = false;
	private Boolean killMe = false;

	private MapCanvas canvas;
	private int xS, xE;

	public MapCanvasAAThread(MapCanvas canvas, int xS, int xE)
	{
		this.canvas = canvas;
		this.xS = xS;
		this.xE = xE;

		bufferAvailable = false;

		// Cancel any previous rendering threads that might be running
		if (previousThread != null)
		{
			previousThread.killMe = true;
			previousThread.interrupt();
		}

		previousThread = this;
		start();
	}

	public void run()
	{
		setPriority(Thread.MIN_PRIORITY);

		try { Thread.sleep(333); }
		catch (InterruptedException e) {}

		if (killMe)
			return;

		Graphics2D g = canvas.getAntiAliasedBufferGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(canvas.getBackground());
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		canvas.render(g, xS, xE);
		g.dispose();

		if (killMe == false)
		{
			bufferAvailable = true;
			canvas.repaint();
		}
	}
}