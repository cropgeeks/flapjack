package flapjack.gui.visualization;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

class QTLCanvas extends JPanel
{
	private GenotypeCanvas canvas;

	private BufferFactory bufferFactory;
	BufferedImage image;

	private int h = 25;

	// Scaling factor to convert between pixels and map positions
	private float xScale;

	QTLCanvas(GenotypeCanvas canvas)
	{
		this.canvas = canvas;

		setLayout(new BorderLayout());
		add(new Canvas2D());
	}

	void computeDimensions(int w1, int w2)
	{
		setBorder(BorderFactory.createEmptyBorder(5, w1, 0, w2));

		xScale = canvas.canvasW / canvas.view.getMapLength();

		createImage();
	}

	void updateView()
	{
		repaint();
	}

	void createImage()
	{
		image = null;

		if (bufferFactory != null)
			bufferFactory.killMe = true;

		bufferFactory = new BufferFactory(canvas.canvasW, h, false);
		bufferFactory.start();
	}

	BufferedImage createSavableImage()
	{
		BufferFactory tempFactory = new BufferFactory(canvas.canvasW, h, true);
		tempFactory.run();

		return tempFactory.buffer;
	}

	private void bufferAvailable(BufferedImage image)
	{
		this.image = image;
		repaint();
	}

	// Draws the loci at index i, optionally adding textual information
	private void drawLoci(Graphics2D g, int i)
	{
		Marker m = canvas.view.getMarker(i);

		// The position of the marker on the map
		int xMap = (int) (m.getPosition() * xScale);

		g.drawLine(xMap, 12, xMap, 22);
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

			// If the bg image is currently null, display some text instead
			if (image == null)
			{
				String str = RB.getString("gui.visualization.MapCanvas.buffer");
				int strW = g.getFontMetrics().stringWidth(str);

				g.drawString(str, (int)(getWidth()/2-strW/2), 25);

				return;
			}

			int w = getWidth();
			int x = canvas.pX2;
			if (canvas.canvasW < w)
				w = x = canvas.canvasW;

			// Cut out the area of the main buffer we want to draw
			BufferedImage image2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = image2.createGraphics();
			g2d.drawImage(image, 0, 0, w, h, canvas.pX1, 0, x, h, null);
			g2d.dispose();

			// And dump it to the screen
			g.drawImage(image2, 0, 0, null);

			// Translate the origin of the canvas so that we see (and draw) the
			// area appropriate to what the main canvas is viewing
			g.translate(0-canvas.pX1, 0);
		}
	}

	private class BufferFactory extends Thread
	{
		BufferedImage buffer;

		// isTempBuffer = true when a buffer is being made for saving as an image
		private boolean isTempBuffer = false;
		private boolean killMe = false;
		private int w, h;

		BufferFactory(int w, int h, boolean isTempBuffer)
		{
			this.w = w;
			this.h = h;
			this.isTempBuffer = isTempBuffer;
		}

		public void run()
		{
			setPriority(Thread.MIN_PRIORITY);

			// Run everything under try/catch conditions due to changes in the
			// view that may invalidate what this thread is trying to access
			try
			{
				createBuffer();
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				System.out.println("QTLCanvas: " + e.getMessage());
			}
		}

		private void createBuffer()
			throws ArrayIndexOutOfBoundsException
		{
			try
			{
				buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			}
			catch (Throwable t) { return; }

			Graphics2D g2d = buffer.createGraphics();

			drawCanvas(g2d);
			g2d.dispose();
		}

		private void drawCanvas(Graphics2D g)
			throws ArrayIndexOutOfBoundsException
		{
			if (isTempBuffer)
				g.setColor(Color.white);
			else
				g.setColor(getBackground());
			g.fillRect(0, 0, canvas.canvasW, h);

			// Draw the white rectangle representing the map
			g.setColor(Color.white);
			g.fillRect(0, 12, canvas.canvasW, 10);
			g.setColor(Color.lightGray);
			g.drawRect(0, 12, canvas.canvasW-1, 10);

			// Draw each marker
			int mkrCount = canvas.view.getMarkerCount();
			for (int i = 0; i < mkrCount && !killMe; i++)
				drawLoci(g, i);

			if (!killMe && !isTempBuffer)
				bufferAvailable(buffer);
		}
	}
}