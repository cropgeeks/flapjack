// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

class MiniMapCanvas extends JPanel
{
	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	private BufferFactory bufferFactory;
	private BufferedImage image;
	private int h = 11;

	private int w;

	MiniMapCanvas(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		add(new Canvas2D());

		// This panel has to detect changes to its size, and recreate the image
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				createImage();
			}
		});
	}

	// Called if the data changes or the view resizes
	void createImage()
	{
		image = null;

		if (bufferFactory != null)
		{
			bufferFactory.killMe = true;
			bufferFactory.interrupt();
		}

		w = canvas.pX2 - canvas.pX1 + 1;

		// Thread off the image creation...
		bufferFactory = new BufferFactory(w, h);
		bufferFactory.start();

		repaint();
	}

	private void bufferAvailable(BufferedImage image)
	{
		this.image = image;
		repaint();
	}

	private void drawMap(Graphics2D g, boolean forBuffer, Boolean killMe)
	{
		// Starting and ending indexes to draw
		int xS, xE;

		float xScale = (w-1) / canvas.view.mapLength();

		if (forBuffer)
		{
			g.setColor(Color.lightGray);
			g.drawLine(0, 5, w-1, 5);

			// Draw all the markers
			xS = 0;
			xE = canvas.view.markerCount();
		}
		else
		{
			g.setColor(Color.red);

			// Or highlight the ones that are onscreen in the main view
			xS = canvas.pX1 / canvas.boxW;
			xE = xS + canvas.boxCountX;

			if (xE > canvas.boxTotalX)
				xE = canvas.boxTotalX;
		}

		for (int i = xS; i < xE; i++)
		{
			Marker m = canvas.view.getMarker(i);

			int pos = (int) (m.getPosition() * xScale);
			g.drawLine(pos, 2, pos, 8);
		}
	}

	private class Canvas2D extends JPanel
	{
		Canvas2D()
		{
			setPreferredSize(new Dimension(0, h));
		}

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics;

			if (image == null)
				return;

			// Calculate the required offset and width
			int xOffset = gPanel.traitCanvas.getPanelWidth()
				+ gPanel.listPanel.getPanelWidth() + 1;
			g.translate(xOffset, 0);

			// Draw the full map (from the back buffer)
			g.drawImage(image, 0, 0, null);

			// Then overlay the markers that are onscreen
			drawMap(g, false, true);
		}
	}

	private class BufferFactory extends Thread
	{
		BufferedImage buffer;

		private boolean killMe = false;
		private int w, h;

		BufferFactory(int w, int h)
		{
			this.w = w;
			this.h = h;
		}

		public void run()
		{
			setPriority(Thread.MIN_PRIORITY);
			setName("MiniMapCanvas BufferFactory");

			try { Thread.sleep(500); }
			catch (InterruptedException e) {}

			if (killMe)
				return;

			// Run everything under try/catch conditions due to changes in the
			// view that may invalidate what this thread is trying to access
			try
			{
				buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

				Graphics2D g2d = buffer.createGraphics();
				g2d.setColor(getBackground());
				g2d.fillRect(0, 0, w, h);

				drawMap(g2d, true, killMe);
				g2d.dispose();

				if (!killMe)
					bufferAvailable(buffer);
			}
			catch (Exception e)
			{
				System.out.println("MiniMapCanvas: " + e);
			}
		}
	}
}