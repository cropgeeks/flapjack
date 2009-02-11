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
	private void drawMarker(Graphics2D g, int i, boolean showDetails, boolean highlight)
	{
		Marker m = canvas.view.getMarker(i);

		// The position of the marker on the map
		int xMap = Math.round(m.getPosition() * xScale);
		// Its position on the main canvas (its "box" representation)
		int xBox = Math.round(i * canvas.boxW + (canvas.boxW/2));

		if (showDetails || highlight)
			g.setColor(Color.red);
		else
			g.setColor(Color.lightGray);

		if (showDetails)
		{
			String str = m.getName() + "  (" + d.format(m.getPosition()) + ")";
			int strWidth = g.getFontMetrics().stringWidth(str);

			g.drawString(str, getPosition(xMap, strWidth), 8);
		}

		g.drawLine(xMap, 12, xMap, 22);
		g.drawLine(xMap, 22, xBox, h-5);
	}

	// Displays a feature's information on the canvas (because the map canvas
	// has the "space" on it for text). Also scans every marker under a feature
	// and highlights their link-lines so the associations can be seen
	private void drawFeatureDetails(Graphics2D g)
	{
		g.translate(0-canvas.pX1, 0);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Feature f = QTLCanvas.mouseOverFeature;
		float min = f.getMin();
		float max = f.getMax();

		// Where should it be drawn
		int pos = Math.round(xScale * (min + ((max-min)/2)));

		// And what should be drawn
		String str = f.getName() + "  (";
		if (f instanceof QTL)
			str += d.format(((QTL)f).getPosition()) + ": ";
		str += d.format(min) + "-" + d.format(max) + ")";
		int strWidth = g.getFontMetrics().stringWidth(str);

		g.setColor(Color.red);
		g.drawString(str, getPosition(pos, strWidth), 8);

		// Now see which markers are "under" this feature, and highlight them
		int mkrCount = canvas.view.getMarkerCount();
		for (int i = 0; i < mkrCount; i++)
		{
			Marker m = canvas.view.getMarker(i);

			// Is this marker under the QTL?
			if (m.getPosition() >= min && m.getPosition() <= max)
				drawMarker(g, i, false, true);
		}
	}

	// Computes the best position to draw a string onscreen, assuming an optimum
	// start position that *may* be adjusted if the text ends up partially drawn
	// offscreen on either the LHS or the RHS
	private int getPosition(int pos, int strWidth)
	{
		// Work out where the left and right hand edges of the text will be
		int leftPos = pos-(int)(strWidth/2f);
		int rghtPos = pos+(int)(strWidth/2f);

		// If we're offscreen to the left, adjust...
		if (leftPos < canvas.pX1)
			leftPos = canvas.pX1+1;
		// Similarly if we're offscreen to the right...
		if (rghtPos > canvas.pX2)
			leftPos = canvas.pX2-strWidth-1;

		return leftPos;
	}

	private class Canvas2D extends JPanel
	{
		private Font FONT = new Font("Dialog", Font.PLAIN, 11);

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
			g.setFont(FONT);

			// Change to red, and redraw the currently highlighted one
			if (markerIndex >=0 && markerIndex < canvas.view.getMarkerCount())
			{
				// Translate the origin of the canvas so that we see (and draw)
				// the area appropriate to what the main canvas is viewing
				g.translate(0-canvas.pX1, 0);

				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(Color.red);
				drawMarker(g, markerIndex, true, true);
			}

			if (QTLCanvas.mouseOverFeature != null)
				drawFeatureDetails(g);
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
				drawMarker(g, i, false, false);

			if (!killMe && !isTempBuffer)
				bufferAvailable(buffer);
		}
	}
}