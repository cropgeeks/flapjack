// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

class GraphCanvasAAThread extends Thread
{
	private static GraphCanvasAAThread previousThread;
	static boolean bufferAvailable = false;
	private Boolean killMe = false;

	private GraphCanvas canvas;
	private int xS, xE;

	public GraphCanvasAAThread(GraphCanvas canvas, int xS, int xE)
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