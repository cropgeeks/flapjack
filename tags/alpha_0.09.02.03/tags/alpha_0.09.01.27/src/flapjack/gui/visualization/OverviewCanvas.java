package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.visualization.colors.*;

class OverviewCanvas extends JPanel
{
	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	private Canvas2D viewCanvas = new Canvas2D();

	private float boxX1, boxY1, boxX2, boxY2;

	private BufferFactory bufferFactory = null;
	private BufferedImage image = null;
	private int w, h;

	private Rectangle clip = null;

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

		if (w == 0 || h == 0 || gPanel == null || isVisible() == false)
			return;

		image = null;

		// Kill off any old image generation that might still be running...
		if (bufferFactory != null)
			bufferFactory.killMe = true;
		// Before starting a new one
		bufferFactory = new BufferFactory(w, h, false);

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
//		new ClipAnimator().start();

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

			gPanel.jumpToPosition(yIndex, xIndex, false);
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			if (image == null)
				return;

			if (clip != null)
				g.setClip(clip);

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
		private GTView view;
		private ColorScheme cScheme;

		private BufferedImage buffer;
		boolean killMe = false;
		boolean isExporting = false;

		private int w, h;
		private int boxTotalX, boxTotalY;
		private int xWidth, yHeight;
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
			view = canvas.view;
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
			catch (ArrayIndexOutOfBoundsException e)
			{
				System.out.println("OverviewCanvas: " + e.getMessage());
			}
		}

		private void createBuffer()
			throws ArrayIndexOutOfBoundsException
		{
			try
			{
				if (isExporting)
					buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
				else
					buffer = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED);
			}
			catch (Throwable t) { return; }


			// Scaling factors
			xScale = w / (float) boxTotalX;
			yScale = h / (float) boxTotalY;

			// Width of each X element (SEE NOTE BELOW)
			xWidth = 1 + (int) ((xScale >= 1) ? xScale : 1);
			// Height of each Y element
			yHeight = 1 + (int) ((yScale >= 1) ? yScale : 1);

			Graphics2D g = buffer.createGraphics();
			g.setColor(Prefs.visColorBackground);
			g.fillRect(0, 0, w, h);

			// What were the x and y positions of the last point drawn? If the next
			// point to be drawn ISN'T different, then we won't bother drawing it,
			// and will save a significant amount of time
			int lastX = -1;
			int lastY = -1;

			float y = 0;
			for (int yIndex = 0; yIndex < boxTotalY && !killMe; yIndex++)
			{
				float x = 0;
				for (int xIndex = 0; xIndex < boxTotalX && !killMe; xIndex++)
				{
					// This is where we save the time...
					if ((int)x != lastX || (int)y != lastY)
					{
						g.setColor(cScheme.getColor(yIndex, xIndex));
						g.fillRect((int)x, (int)y, xWidth, yHeight);

						lastX = (int)x;
						lastY = (int)y;
					}

					x += xScale;
				}

				y += yScale;
			}

			if (!killMe && !isExporting)
			{
				// Once complete, let the dialog know its image is ready
				bufferAvailable(buffer);
			}
		}

		// We use (1 +) to deal with integer roundoff that results in columns
		// being skipped due to overlaps: eg with width of 1.2:
		// 1.2 (1) 2.4 (2) 3.6 (3) 4.8 (4) 6.0 (6)
		// position 5 was skipped
	}

	private class ClipAnimator extends Thread
	{
		public void run()
		{
			for (int i = 0; i <= 30; i++)
			{
				int rectH = (int) (i*(h/30f));

				clip = new Rectangle(0, 0, w, rectH);
				viewCanvas.repaint();

				try { Thread.sleep(500/30); }
				catch (Exception e) {}
			}

			clip = null;
		}
	}
}