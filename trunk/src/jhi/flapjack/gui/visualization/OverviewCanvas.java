// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.colors.*;

class OverviewCanvas extends JPanel
{
	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	private Canvas2D viewCanvas = new Canvas2D();

	private float boxX1, boxY1, boxX2, boxY2;

	private BufferFactory bufferFactory = null;
	private BufferedImage image = null;
	private int w, h;

	public OverviewCanvas(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;

		setLayout(new BorderLayout());
		setBackground((Color)UIManager.get("Tree.background"));
		add(viewCanvas);

		addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				createImage();
			}
		});
	}

	void updateOverviewSelectionBox(int xIndex, int xW, int yIndex, int yH)
	{
		if (bufferFactory == null)
			return;

		// Work out the x1/y1 position for the outline box
		boxX1 = bufferFactory.xScale * xIndex;
		boxY1 = bufferFactory.yScale * yIndex;

		// Work out the x2 position for the outline box
		boxX2 = boxX1 + xW * bufferFactory.xScale;
		if (xW > canvas.boxTotalX || boxX2 > viewCanvas.getWidth())
			boxX2 = viewCanvas.getWidth();

		// Work out the y2 position for the outline box
		boxY2 = boxY1 + yH * bufferFactory.yScale;
		if (yH > canvas.boxTotalY || boxY2 > viewCanvas.getHeight())
			boxY2 = viewCanvas.getHeight();

		repaint();
	}

	void createImage()
	{
		w = viewCanvas.getSize().width;
		h = viewCanvas.getSize().height;

		if (gPanel == null || isVisible() == false)
			return;

		image = null;

		// Kill off any old image generation that might still be running...
		if (bufferFactory != null)
			bufferFactory.killMe = true;
		// Before starting a new one
		bufferFactory = new BufferFactory(w>0 ? w:1, h>0 ? h:1, false);

		repaint();
	}

	public BufferedImage exportImage(int w, int h)
	{
		BufferFactory tmpFactory = new BufferFactory(w, h, true);

		tmpFactory.run();

		return tmpFactory.buffer;
	}

	private void bufferAvailable(BufferedImage image)
	{
		this.image = image;

		// Force the main canvas to send its view size dimensions so we can draw
		// the highlighting box on top of the new back buffer's image
		gPanel.forceOverviewUpdate();
	}

	private class Canvas2D extends JPanel
	{
		Canvas2D()
		{
			setOpaque(false);

			addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)
					{ processMouse(e); }

				public void mousePressed(MouseEvent e)
					{ processMouse(e); }

				public void mouseReleased(MouseEvent e)
					{ processMouse(e); }
			});

			addMouseMotionListener(new MouseMotionAdapter()
			{
				public void mouseDragged(MouseEvent e)
					{ processMouse(e); }
			});
		}

		private void processMouse(MouseEvent e)
		{
			if (gPanel == null)
				return;

			int x = e.getX() - (int) ((boxX2-boxX1) / 2f);
			int y = e.getY() - (int) ((boxY2-boxY1) / 2f);

			// Compute mouse position (and adjust by wid/hgt of rectangle)
			int xIndex = (int) (x / bufferFactory.xScale);
			int yIndex = (int) (y / bufferFactory.yScale);

			gPanel.getController().moveTo(yIndex, xIndex, false);
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			if (image == null)
				return;

			// Paint the image of the alignment
			g.drawImage(image, 0, 0, null);

			((Graphics2D) g).setPaint(new Color(255, 255, 255, 50));
			g.fillRect(0, 0, w, h);

			// Then draw the tracking rectangle
			int cR = Prefs.visColorOverviewFill.getRed();
			int cG = Prefs.visColorOverviewFill.getGreen();
			int cB = Prefs.visColorOverviewFill.getBlue();
			((Graphics2D) g).setPaint(new Color(cR, cG, cB, 50));

			int boxW = Math.round(boxX2-boxX1) - 1;
			int boxH = Math.round(boxY2-boxY1) - 1;
			g.fillRect(Math.round(boxX1), Math.round(boxY1), boxW, boxH);
			g.setColor(Prefs.visColorOverviewOutline);
			g.drawRect(Math.round(boxX1), Math.round(boxY1), boxW, boxH);
		}
	}

	private class BufferFactory extends Thread
	{
		private ColorScheme cScheme;

		private BufferedImage buffer;
		boolean killMe = false;
		boolean isExporting = false;

		private int w, h;
		private int boxTotalX, boxTotalY;
		private float xScale, yScale;

		BufferFactory(int w, int h, boolean isExporting)
		{
			this.w = w;
			this.h = h;
			this.isExporting = isExporting;

			// Make private references to certain values now, as they MAY change
			// while the buffer is still being created, which creates a cock-up
			boxTotalX = canvas.boxTotalX;
			boxTotalY = canvas.boxTotalY;
			cScheme = canvas.cScheme;

			if (!isExporting)
				start();
		}

		public void run()
		{
			setPriority(Thread.MIN_PRIORITY);
			setName("OverviewCanvas BufferFactory");

			try { Thread.sleep(500); }
			catch (InterruptedException e) {}

			if (killMe)
				return;

			// Run everything under try/catch conditions due to changes in the
			// view that may invalidate what this thread is trying to access
			try
			{
				createBuffer();
			}
			catch (Exception e)
			{
				System.out.println("OverviewCanvas: " + e);
			}
		}

		private void createBuffer()
		{
			try
			{
				if (isExporting)
					buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
				else
					buffer = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED);
			}
			catch (Throwable t) { return; }

			// Scaling factors for drawing...
			xScale = boxTotalX / (float) w;
			yScale = boxTotalY / (float) h;

			Graphics2D g = buffer.createGraphics();
			g.setColor(Prefs.visColorBackground);
			g.fillRect(0, 0, w, h);


			// Loop over every pixel that makes up the overview...
			for (int y = 0; y < h && !killMe; y++)
			{
				for (int x = 0; x < w && !killMe; x++)
				{
					// Working out where each pixel maps to in the data...
					int dataX = (int) (x * xScale);
					int dataY = (int) (y * yScale);

					// Then finding and drawing that data
					g.setColor(cScheme.getColor(dataY, dataX));
					g.drawLine(x, y, x, y);
				}
			}

			// Scaling factors for mouse/mapping
			xScale = w / (float) boxTotalX;
			yScale = h / (float) boxTotalY;

			if (!killMe && !isExporting)
			{
				// Once complete, let the dialog know its image is ready
				bufferAvailable(buffer);
			}
		}
	}
}