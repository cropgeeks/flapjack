package flapjack.gui.visualization;

import java.awt.*;
import java.awt.image.*;
import java.text.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

class MapCanvas extends JPanel
{
	private DecimalFormat d = new DecimalFormat("0.0");

	private GenotypeCanvas canvas;

	private BufferFactory bufferFactory;
	BufferedImage image;

	private int h = 55;

	// Scaling factor to convert between pixels and map positions
	private float xScale;

	// The loci index of the marker currently under the mouse on the main canvas
	private int lociIndex = -1;

	// Starting and ending indices of the loci currently being displayed on the
	// main canvas
	private int canvas1, canvas2;

	// Starting and ending indices of the loci we want to display line data for,
	// which may not be the same as only those visible
	private int map1, map2;


	MapCanvas(GenotypeCanvas canvas)
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

	void updateLociIndices(int canvas1, int canvas2)
	{
		this.canvas1 = canvas1;
		this.canvas2 = canvas2;

		repaint();
	}

	void setLociIndex(int lociIndex)
	{
		if (this.lociIndex != lociIndex  && canvas.locked == false)
		{
			this.lociIndex = lociIndex;
			repaint();
		}
	}

	private void bufferAvailable(BufferedImage image)
	{
		this.image = image;
		repaint();
	}

	// Draws the loci at index i, optionally adding textual information
	private void drawLoci(Graphics2D g, int i, boolean showDetails)
	{
		Marker m = canvas.view.getMarker(i);

		// The position of the marker on the map
		int xMap = (int) (m.getPosition() * xScale);
		// Its position on the main canvas (its "box" representation)
		int xBox = (int) (i * canvas.boxW + (canvas.boxW/2));

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

			g.drawString(str, leftPos, 8);
		}

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

			g.setFont(new Font("Dialog", Font.PLAIN, 11));

			// If the bg image is currently null, display some text instead
			if (image == null)
			{
				String str = RB.getString("gui.visualization.MapCanvas.buffer");
				int strW = g.getFontMetrics().stringWidth(str);

				g.drawString(str, (int)(getWidth()/2-strW/2), 25);

				return;
			}

//			int w = canvas.pX2-canvas.pX1+1;
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

			// Change to red, and redraw the currently highlighted one
			if (lociIndex >=0 && lociIndex < canvas.view.getMarkerCount())
			{
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(Color.red);
				drawLoci(g, lociIndex, true);
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

			// Draw the white rectangle representing the map
			g.setColor(Color.white);
			g.fillRect(0, 12, canvas.canvasW, 10);
			g.setColor(Color.lightGray);
			g.drawRect(0, 12, canvas.canvasW-1, 10);

			// Determine which markers to draw (to hopefully speed things up)
//			determineMarkers();

			map1 = 0;
			map2 = canvas.view.getMarkerCount()-1;

			// Draw each marker
			for (int i = map1; i <= map2 && !killMe; i++)
				drawLoci(g, i, false);

			if (!killMe && !isTempBuffer)
				bufferAvailable(buffer);
		}
	}


	/**
	 * Optimises the start and end indices for the map based on which markers
	 * are currently visible and/or which map positions for markers (which may
	 * be offscreen) are currently visible.
	 *
	 * Not currently used while back-buffering the entire map anyway
	 */
/*	private void determineMarkers()
	{
		// Map position leftmost visible on the screen
		float pos1 = canvas1*canvas.boxW/xScale;
		// Map position rightmost visible on the screen
		float pos2 = canvas2*canvas.boxW/xScale;

		map1 = -1;
		map2 = -1;

		for (int i = 0; i < map.countLoci(); i++)
			if (map1 == -1 && map.getMarkerByIndex(i).getPosition() >= pos1)
				map1 = i;

		for (int i = map.countLoci()-1; i >= 0; i--)
			if (map2 == -1 && map.getMarkerByIndex(i).getPosition() <= pos2)
				map2 = i;

		map1 = canvas1 < map1 ? canvas1 : map1;
		map2 = canvas2 > map2 ? canvas2 : map2;
	}
*/
}