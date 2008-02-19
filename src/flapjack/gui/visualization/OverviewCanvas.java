package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;

class OverviewCanvas extends JPanel
{
	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	private Canvas2D viewCanvas = new Canvas2D();

	private int boxX, boxY, boxW, boxH;

	private BufferFactory bufferFactory = null;
	private BufferedImage image = null;
	private int w, h;

	public OverviewCanvas(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;

		setLayout(new BorderLayout());
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

		// Work out the x/y position for the outline box
		boxX = (int) (bufferFactory.xScale * xIndex);
		boxY = (int) (bufferFactory.yScale * yIndex);

		// Work out the width/height for the outline box
		if (xW >= canvas.boxTotalX)
			boxW = viewCanvas.getWidth() - 1;
		else
			boxW = (int) (bufferFactory.xScale * xW);
		if (yH >= canvas.boxTotalY)
			boxH = viewCanvas.getHeight() - 1;
		else
			boxH = (int) (bufferFactory.yScale * yH + bufferFactory.yHeight);

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
		bufferFactory = new BufferFactory(w, h);

		repaint();
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

			int x = e.getX() - (int) (boxW / 2f);
			int y = e.getY() - (int) (boxH / 2f);

			// Compute mouse position (and adjust by wid/hgt of rectangle)
			int xIndex = (int) (x / bufferFactory.xScale);
			int yIndex = (int) (y / bufferFactory.yScale);

			gPanel.jumpToPosition(xIndex, yIndex);
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			if (image != null)
			{
				// Paint the image of the alignment
				g.drawImage(image, 0, 0, null);

				((Graphics2D) g).setPaint(new Color(255, 255, 255, 100));
				g.fillRect(0, 0, w, h);

				// Then draw the tracking rectangle
				((Graphics2D) g).setPaint(new Color(50, 50, 0, 50));
				g.fillRect(boxX, boxY, boxW, boxH);
				g.setColor(Color.red);
				g.drawRect(boxX, boxY, boxW, boxH);
			}

			else
			{
				String str = RB.getString("gui.visualization.OverviewDialog.buffer");
				int strWidth = g.getFontMetrics().stringWidth(str);

				g.setColor(Color.lightGray);
				g.drawString(str, (int) (getWidth() / 2f - strWidth / 2f),
						getHeight() / 2);
			}
		}
	}

	private class BufferFactory extends Thread
	{
		private GTView view;
		private ColorTable cTable;

		private BufferedImage buffer;
		boolean killMe = false;

		private int w, h;
		private int boxTotalX, boxTotalY;
		private int xWidth, yHeight;
		private float xScale, yScale;

		BufferFactory(int w, int h)
		{
			this.w = w;
			this.h = h;

			// Make private references to certain values now, as they MAY change
			// while the buffer is still being created, which creates a cock-up
			boxTotalX = canvas.boxTotalX;
			boxTotalY = canvas.boxTotalY;
			view = canvas.view;
			cTable = canvas.cTable;

			start();
		}

		public void run()
		{
			setPriority(Thread.MIN_PRIORITY);

			buffer = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED);
			Graphics2D g = buffer.createGraphics();


			// Scaling factors
			xScale = w / (float) boxTotalX;
			yScale = h / (float) boxTotalY;

			// Width of each X element (SEE NOTE BELOW)
			xWidth = 1 + (int) ((xScale >= 1) ? xScale : 1);
			// Height of each Y element
			yHeight = 1 + (int) ((yScale >= 1) ? yScale : 1);


			g.setColor(Color.white);
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
						int state = view.getState(yIndex, xIndex);

						if (state > 0)
						{
							g.setColor(cTable.get(state).getColor());
							g.fillRect((int)x, (int)y, xWidth, yHeight);

							lastX = (int)x;
							lastY = (int)y;
						}
					}

					x += xScale;
				}

				y += yScale;
			}

			if (!killMe)
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
}