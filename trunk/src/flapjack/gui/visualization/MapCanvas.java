package flapjack.gui.visualization;

import java.awt.*;
import java.awt.image.*;
import java.text.*;
import javax.swing.*;

import flapjack.data.*;

class MapCanvas extends JPanel
{
	private DecimalFormat d = new DecimalFormat("0.0");

	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	private BufferFactory bufferFactory;
	BufferedImage image;

	private int h = 55;

	// Scaling factor to convert between pixels and map positions
	private float xScale;

	// The index of the marker currently under the mouse on the main canvas
	private int markerIndex = -1;

	MapCanvas(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		add(new Canvas2D());
	}

	void createImage()
	{
		image = null;

		if (bufferFactory != null)
		{
			bufferFactory.killMe = true;
			bufferFactory.interrupt();
		}

		// Thread off the image creation...
		bufferFactory = new BufferFactory(canvas.canvasW, h, false);
		bufferFactory.start();
	}

	BufferedImage createSavableImage()
	{
		// Note that this *doesn't* happen in a new thread as the assumption is
		// that this will be called by a threaded process anyway
		BufferFactory tempFactory = new BufferFactory(canvas.canvasW, h, true);
		tempFactory.run();

		return tempFactory.buffer;
	}

	void updateView()
	{
		repaint();
	}

	void setMarkerIndex(int markerIndex)
	{
		if (this.markerIndex != markerIndex  && canvas.locked == false)
		{
			this.markerIndex = markerIndex;
			repaint();
		}
	}

	private void bufferAvailable(BufferedImage image)
	{
		this.image = image;
		repaint();
	}

	// Draws the marker at index i, optionally adding textual information
	private void drawMarker(Graphics2D g, int i, boolean showDetails)
	{
		Marker m = canvas.view.getMarker(i);

		// The position of the marker on the map
		int xMap = Math.round(m.getPosition() * xScale);
		// Its position on the main canvas (its "box" representation)
		int xBox = Math.round(i * canvas.boxW + (canvas.boxW/2));

		if (showDetails)
		{
			String str = m.getName() + "  (" + d.format(m.getPosition()) + ")";
			int strWidth  = g.getFontMetrics().stringWidth(str);

			// Work out where the left and right hand edges of the text will be
			int leftPos = xMap-(int)(strWidth/2f);
			int rghtPos = xMap+(int)(strWidth/2f);

			// If we're offscreen to the left, adjust...
			if (leftPos < canvas.pX1)
				leftPos = canvas.pX1;
			// Similarly if we're offscreen to the right...
			if (rghtPos > canvas.pX2)
				leftPos = canvas.pX2-strWidth;

			g.setColor(Color.red);
			g.drawString(str, leftPos, 8);
		}

//		else if (canvas.view.isMarkerSelected(i))
		else
			g.setColor(Color.lightGray);
//		else
//			g.setPaint(new Color(192, 192, 192, 50));

		g.drawLine(xMap, 12, xMap, 22);
		g.drawLine(xMap, 22, xBox, h-5);
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

			// Calculate the required offset and width
			int xOffset = gPanel.traitCanvas.getPanelWidth()
				+ gPanel.listPanel.getPanelWidth() + 1;
			int width = (canvas.pX2-canvas.pX1);

			g.setClip(xOffset, 0, width, h);
			g.translate(xOffset, 0);


			if (image == null)
				return;

			int w = width;
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

			// Change to red, and redraw the currently highlighted one
			if (markerIndex >=0 && markerIndex < canvas.view.getMarkerCount())
			{
				// Translate the origin of the canvas so that we see (and draw)
				// the area appropriate to what the main canvas is viewing
				g.translate(0-canvas.pX1, 0);

				g.setFont(new Font("Dialog", Font.PLAIN, 11));
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(Color.red);
				drawMarker(g, markerIndex, true);
			}
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
			setName("MapCanvas BufferFactory");

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
				System.out.println("MapCanvas: " + e.getMessage());
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
			// Enable anti-aliased graphics to smooth the line jaggies
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if (isTempBuffer)
				g.setColor(Color.white);
			else
				g.setColor(getBackground());
			g.fillRect(0, 0, canvas.canvasW, h);

			int mkrCount = canvas.view.getMarkerCount();
			xScale = canvas.canvasW / canvas.view.mapLength();

			// Draw the white rectangle representing the map
			g.setColor(Color.white);
			g.fillRect(0, 12, canvas.canvasW, 10);
			g.setColor(Color.lightGray);
			g.drawRect(0, 12, canvas.canvasW-1, 10);

			// Draw each marker
			for (int i = 0; i < mkrCount && !killMe; i++)
				drawMarker(g, i, false);

			if (!killMe && !isTempBuffer)
				bufferAvailable(buffer);
		}
	}
}