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

	private GenotypeData data;
	private ChromosomeMap map;

	private Canvas2D mapCanvas;

	private BufferFactory bufferFactory;
	private BufferedImage image;

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
		add(mapCanvas = new Canvas2D());
	}

	void computeDimensions(int w1, int w2)
	{
		setBorder(BorderFactory.createEmptyBorder(5, w1, 0, w2));

		xScale = canvas.canvasW / map.getLength();

		image = null;

		if (bufferFactory != null)
			bufferFactory.killMe = true;

		bufferFactory = new BufferFactory(canvas.canvasW, h);
	}

	void updateLociIndices(int canvas1, int canvas2)
	{
		this.canvas1 = canvas1;
		this.canvas2 = canvas2;
	}

	void setChromosomeMap(ChromosomeMap map)
	{
		this.map = map;
	}

	void setLociIndex(int lociIndex)
	{
		if (this.lociIndex != lociIndex  && canvas.locked == false)
		{
			this.lociIndex = lociIndex;
			repaint();
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

			long s = System.nanoTime();


			Graphics2D g = (Graphics2D) graphics;

			if (map == null)
				return;

			if (image == null)
			{
				String str = RB.getString("gui.visualization.MapCanvas.buffer");
				int strW = g.getFontMetrics().stringWidth(str);

				g.drawString(str, (int)(getWidth()/2-strW/2), 25);

				return;
			}

			// Cut out the area of the main buffer we want to draw
			BufferedImage image2 = new BufferedImage(getWidth(), h, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = image2.createGraphics();
			g2d.drawImage(image, 0, 0, getWidth(), h, canvas.pX1, 0, canvas.pX2, h, null);
			g2d.dispose();

			// And dump it to the screen
			g.drawImage(image2, 0, 0, null);


			// Translate the origin of the canvas so that we see (and draw) the
			// area appropriate to what the main canvas is viewing
			g.translate(0-canvas.pX1, 0);

			// Change to red, and redraw the currently highlighted on
			if (lociIndex >=0 && lociIndex < map.countLoci())
			{
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(Color.red);
				drawLoci(g, lociIndex, true);
			}


			long e = System.nanoTime();
			System.out.println("MAP Render time: " + ((e-s)/1000000f) + "ms");
		}


	}

	private class BufferFactory extends Thread
	{
		private BufferedImage buffer;

		private boolean killMe = false;
		private int w, h;

		BufferFactory(int w, int h)
		{
			this.w = w;
			this.h = h;

			start();
		}

		public void run()
		{
//			setPriority(Thread.MIN_PRIORITY);

			try
			{
				buffer = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
			}
			catch (Throwable t) { return; }

			Graphics2D g2d = buffer.createGraphics();

			drawCanvas(g2d);
			g2d.dispose();
		}

		private void drawCanvas(Graphics2D g)
		{
			// Enable anti-aliased graphics to smooth the line jaggies
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g.setColor(getBackground());
			g.fillRect(0, 0, canvas.canvasW, h);

			// Draw the white rectangle representing the map
			g.setColor(Color.white);
			g.fillRect(0, 10, canvas.canvasW, 10);
			g.setColor(Color.lightGray);
			g.drawRect(0, 10, canvas.canvasW-1, 10);

			// Determine which markers to draw (to hopefully speed things up)
//			determineMarkers();

			map1 = 0;
			map2 = map.countLoci()-1;

			// Draw each marker
			for (int i = map1; i <= map2 && !killMe; i++)
				drawLoci(g, i, false);

			if (!killMe)
				bufferAvailable(buffer);
		}
	}

	private void bufferAvailable(BufferedImage image)
	{
		this.image = image;
		repaint();
	}

	private void drawLoci(Graphics2D g, int i, boolean showDetails)
		{
			Marker m = map.getMarkerByIndex(i);

			int xMap = (int) (m.getPosition() * xScale);
			int xBox = (int) (i * canvas.boxW + (canvas.boxW/2));

			if (showDetails)
			{
				String posStr = m.getName() + "  (" + d.format(m.getPosition()) + ")";
				int strWidth  = g.getFontMetrics().stringWidth(posStr);

				g.drawString(posStr, xMap-(int)(strWidth/2f), 8);
			}

			g.drawLine(xMap, 10, xMap, 20);
			g.drawLine(xMap, 20, xBox, h-5);
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