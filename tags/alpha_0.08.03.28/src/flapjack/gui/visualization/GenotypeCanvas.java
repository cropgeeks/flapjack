package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.lang.management.*;
import java.util.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.visualization.colors.*;

class GenotypeCanvas extends JPanel
{
	// Memory bean used for monitoring available memory
	private MemoryMXBean mxBean = ManagementFactory.getMemoryMXBean();

	private GenotypePanel gPanel;

	// The "view" being rendered
	private GTViewSet viewSet;
	GTView view;

	// The current color model
	ColorScheme cScheme;
	// Transparancy intensity for animation effects
	int alphaEffect = 0;

	boolean locked = false;

	// The total number of boxes (allele states) in the dataset
	int boxTotalX, boxTotalY;
	// Width and height of the main drawing canvas
	int canvasW, canvasH;
	// Width and height of an allele "box"
	int boxW, boxH;
	// How many boxes will fit into the current screen size?
	int boxCountX, boxCountY;

	// These are the x and y pixel positions on the canvas that currently appear
	// in the top left corner of the current view
	int pX1, pY1;
	// And bottom right hand corner
	int pX2, pY2;

	// Starting and ending indices of the x (marker) and y (line) data that will
	// be drawn during the next repaint operation
	private int xIndexStart, xIndexEnd;
	private int yIndexStart, yIndexEnd;

	// Holds the current dimensions of the canvas in an AWT friendly format
	private Dimension dimension = new Dimension();

	// The tooltip object
//	CanvasToolTip tt = new CanvasToolTip();

	private BufferFactory bufferFactory;
	// This buffer holds the entire view area (if possible)
	BufferedImage imageFull;
	// This buffer holds the current viewport (visible) area
	BufferedImage imageViewPort;

	MineSweeper mineSweeper;

	GenotypeCanvas(GenotypePanel gPanel)
	{
		this.gPanel = gPanel;

		setOpaque(false);
		setBackground(Color.white);

		new CanvasMouseListener(gPanel, this);

//		setToolTipText("");
	}

/*	public JToolTip createToolTip()
		{ return tt; }

	public String getToolTipText(MouseEvent e)
	{
		int xIndex = (int) (e.getPoint().x / boxW);
		int yIndex = (int) (e.getPoint().y / boxH);

		return (e.getPoint().x / boxW) + ", " + (e.getPoint().y / boxH)
			+ "    " + dataSet.getLineByIndex(yIndex) + " - "
			+ map + " - " + map.getMarkerByIndex(xIndex);
	}
*/

	void setView(GTViewSet viewSet, GTView view)
	{
		this.viewSet = viewSet;
		this.view = view;

		view.cacheLines();
	}

	// Compute canvas related dimensions that only change if the data or the
	// box-drawing size needs to be changed
	void computeDimensions(int size)
	{
		Font font = new Font("Monospaced", Font.PLAIN, size);
		FontMetrics fm = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
			.getGraphics().getFontMetrics(font);

		boxW = size*2;
		boxH = fm.getHeight();

		// Once we have suitable width/height values, the scrollbars can be made
		// to lock to those so we never have to draw less than a full box
		gPanel.setScrollbarAdjustmentValues(boxW, boxH);

		boxTotalX = view.getMarkerCount();
		boxTotalY = view.getLineCount();

		canvasW = (boxTotalX * boxW);// + (boxW);// - 1);
		canvasH = (boxTotalY * boxH);

		setSize(dimension = new Dimension(canvasW, canvasH));

		long s = System.currentTimeMillis();
		switch (viewSet.getColorScheme())
		{
			case ColorScheme.LINE_SIMILARITY:
				cScheme = new TwoColorScheme(view, boxW, boxH);
				break;

			case ColorScheme.LINE_SIMILARITY_GS:
				cScheme = new NucleotideGreyScaleColorScheme(view, boxW, boxH);
				break;

			case ColorScheme.SIMPLE_TWO_COLOR:
				cScheme = new SimpleTwoColorScheme(view, boxW, boxH);
				break;

			case ColorScheme.RANDOM:
				cScheme = new RandomColorScheme(view, boxW, boxH);
				break;

			default: // ColorScheme.NUCLEOTIDE
				cScheme = new NucleotideColorScheme(view, boxW, boxH);
		}
		System.out.println("Color scheme created in " + (System.currentTimeMillis()-s) + "ms");

		/////////////////////////

		resetBufferedState(true);
	}

	// Compute real-time variables, that change as the viewpoint is moved across
	// the canvas
	void computeForRedraw(Dimension viewSize, Point viewPosition)
	{
		boxCountX = 1 + (int) ((float) viewSize.getWidth()  / boxW);
		boxCountY = 1 + (int) ((float) viewSize.getHeight() / boxH);

		pX1 = viewPosition.x;
		pY1 = viewPosition.y;

		pX2 = pX1 + viewSize.width;
		pY2 = pY1 + viewSize.height;

		updateOverviewSelectionBox();
		repaint();
	}

	void updateOverviewSelectionBox()
	{
		gPanel.updateOverviewSelectionBox((pX1/boxW), boxCountX, (pY1/boxH), boxCountY);
	}

	// Called as the mouse moves over the canvas - we want to highlight this
	void setHighlightedIndices(int rowIndex, int colIndex)
	{
		if (rowIndex != view.selectedLine || colIndex != view.selectedMarker)
		{
			view.selectedLine = rowIndex;
			view.selectedMarker = colIndex;
//			repaint();
		}
	}

	public Dimension getPreferredSize()
		{ return dimension; }

	int getMarker(Point mousePoint)
		{ return mousePoint.x / boxW; }

	int getLine(Point mousePoint)
		{ return mousePoint.y / boxH; }

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;

		long s = System.nanoTime();

		renderViewport(g);

		// Post (main-canvas) rendering operations
		if (mineSweeper != null)
			mineSweeper.render(g);

		if (view.hideMarker != -1)
		{
			g.setPaint(new Color(255, 255, 255, alphaEffect));

			int mX = boxW * view.hideMarker;
			g.fillRect(mX, 0, boxW, canvasH);
		}

		// TODO: think about this - the image on screen really needs buffered at
		// this point, as constant repaints on a complicated canvas is too slow
		// (also see TODO: for renderViewport)
/*		if (highlightX != -1 || highlightY != -1)
		{
			g.setColor(Color.black);

			int mx1 = boxW * highlightX;
			int mx2 = mx1 + boxW - 1;
			int my1 = boxH * highlightY;
			int my2 = my1 + boxH - 1;

			g.drawLine(mx1, 0, mx1, canvasH);
			g.drawLine(mx2, 0, mx2, canvasH);
			g.drawLine(0, my1, canvasW, my1);
			g.drawLine(0, my2, canvasW, my2);
		}
*/

		long e = System.nanoTime();

		System.out.println("Render time: " + ((e-s)/1000000f) + "ms");
	}

	// TODO: this needs cached/cleared so only a genuine redraw causes it to
	// recreated. That way surface animation/popups/etc can use the existing
	// viewport buffer to help speed rendering
	private void renderViewport(Graphics2D g)
	{
		int w = pX2-pX1, h = pY2-pY1;

		if (canvasW < w) w = canvasW;
		if (canvasH < h) h = canvasH;

		// imageViewPort is (yet another) buffer that caches the visible area
		// What would have been drawn to screen is drawn to the buffer first
		imageViewPort = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D gImage = (Graphics2D) imageViewPort.getGraphics();

		if (imageFull == null)
		{
			gImage.translate(-pX1, -pY1);
			renderRegion(gImage);
		}
		else
			renderImage(gImage);

		gImage.dispose();
		g.drawImage(imageViewPort, pX1, pY1, null);
	}

	// This method takes the full back-buffered image (already pre-created by
	// this point) and cuts out a section of it that is pasted into a 2nd buffer
	// which is then drawn to the screen. The extra buffer is needed to reduce
	// a strange smearing effect that happens if you draw the cut-out section
	// directly to the screen
	private void renderImage(Graphics2D g)
	{
		// Width and height of the area to be copied (start by assuming we'll
		// copy everything that fits on screen
		int w = pX2-pX1, h = pY2-pY1;

		// Bottom right-hand corner of the source image we're copying from
		int x = pX2, y = pY2;

		// But modifiy for cases where the width/height of the canvas is smaller
		// than the current screen size
		if (canvasW < w) w = x = canvasW;
		if (canvasH < h) h = y = canvasH;

		// Now paste the crop onto the viewport buffer
		g.drawImage(imageFull, 0, 0, w, h, pX1, pY1, x, y, null);
	}

	void renderRegion(Graphics2D g)
	{
		// These are the index positions within the dataset that we'll start
		// drawing from
		xIndexStart = pX1 / boxW;
		yIndexStart = pY1 / boxH;

		// The end indices are calculated as the:
		//   (the start index) + (the number that can be drawn on screen)
		// with a check to set the end index to the last value in the array if
		// the calculated index would go out of bounds
		xIndexEnd = xIndexStart + boxCountX;
		if (xIndexEnd >= boxTotalX)
			xIndexEnd = boxTotalX-1;

		yIndexEnd = yIndexStart + boxCountY;
		if (yIndexEnd >= boxTotalY)
			yIndexEnd = boxTotalY-1;

		render(g, new ImageMonitor(), xIndexStart, xIndexEnd, yIndexStart, yIndexEnd);
	}

	void renderAll(Graphics2D g, ImageMonitor monitor)
	{
		xIndexStart = 0;
		xIndexEnd   = boxTotalX-1;
		yIndexStart = 0;
		yIndexEnd   = boxTotalY-1;

		render(g, monitor, xIndexStart, xIndexEnd, yIndexStart, yIndexEnd);
	}

	private void render(Graphics2D g, ImageMonitor monitor, int xS, int xE, int yS, int yE)
	{
//		g.setColor(Color.white);
//		g.fillRect(0, 0, canvasW, canvasH);

		for (int yIndex = yS, y = (boxH*yS); yIndex <= yE; yIndex++, y += boxH)
		{
			for (int xIndex = xS, x = (boxW*xS); xIndex <= xE; xIndex++, x += boxW)
			{
				if (monitor.killMe)
					break;

//				int state = view.getState(yIndex, xIndex);
//				int compState = view.getState(0, xIndex);

//				if (state > 0)
				{
//					if (state != compState || yIndex == 0)
					g.drawImage(cScheme.getImage(yIndex, xIndex), x, y, null);
//						g.drawImage(cTable.get(state).getImage(), x, y, null);
//					else
//						g.drawImage(cTable.get(state).getGSImage(), x, y, null);
				}
			}
		}
	}

	// Will stop the creation of any back-buffer, and optionally start working
	// on a new buffer (on the assumption that the view has changed in some way)
	void resetBufferedState(boolean createNewBuffer)
	{
		WinMainStatusBar.setRenderState(0);

		// TODO: do we want to be calling flush?
		if (imageFull != null)
			imageFull.flush();
		imageFull = null;

		if (bufferFactory != null)
		{
			bufferFactory.monitor.killMe = true;
			bufferFactory.interrupt();
		}

		if (createNewBuffer)
			bufferFactory = new BufferFactory();

		repaint();
	}

	private class BufferFactory extends Thread
	{
		private ImageMonitor monitor;
		private BufferedImage buffer;

		BufferFactory()
		{
			monitor = new ImageMonitor();
			start();
		}

		public void run()
		{
			setPriority(Thread.MIN_PRIORITY);

			// Wait for 2 seconds before starting anything - gives the user time
			// to stop arsing about with the interface
			try { Thread.sleep(2000); }
			catch (InterruptedException e) {}

			if (monitor.killMe || Prefs.visBackBuffer == false)
				return;

			System.runFinalization();
			System.gc();

			// Run everything under try/catch conditions due to changes in the
			// view that may invalidate what this thread is trying to access
			try
			{
				createBuffer();
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				System.out.println("GenotypeCanvas (buffer): " + e.getMessage());
				WinMainStatusBar.setRenderState(0);
			}
		}

		private void createBuffer()
			throws ArrayIndexOutOfBoundsException
		{
			// 3-bits per pixel or 1-bit per pixel depending on the image type
			int multiplier = 3;
			if (Prefs.visBackBufferType == BufferedImage.TYPE_BYTE_INDEXED)
				multiplier = 1;

			// Determine how much memory we need for the back buffer (in bytes)
			long bufferSize = (long)canvasW * (long)canvasH * multiplier;
			long available = mxBean.getHeapMemoryUsage().getMax()
				- mxBean.getHeapMemoryUsage().getUsed();

			System.out.println("RGB buffer requires: " + (bufferSize/1024f/1024f) + " MB ("
				+ (available/1024f/1024f) + " MB available)");

			if (bufferSize > 0.75 * available)
			{
				WinMainStatusBar.setRenderState(3);
				return;
			}

			try	{
				buffer = new BufferedImage(canvasW, canvasH, Prefs.visBackBufferType);
			}
			catch (Throwable t)	{
				// Catch out-of-memory errors
				WinMainStatusBar.setRenderState(4);
				return;
			}

			WinMainStatusBar.setRenderState(1);

			// Assuming everything is ok, draw the entire canvas onto the buffer
			Graphics2D g2d = buffer.createGraphics();
			renderAll(g2d, monitor);
			g2d.dispose();

			if (!monitor.killMe)
			{
				WinMainStatusBar.setRenderState(2);
				imageFull = buffer;
			}
		}
	}

	private static class ImageMonitor
	{
		boolean killMe = false;
	}
}