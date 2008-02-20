package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.lang.management.*;
import java.util.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

class GenotypeCanvas extends JPanel
{
	// Memory bean used for monitoring available memory
	private MemoryMXBean mxBean = ManagementFactory.getMemoryMXBean();

	private GenotypePanel gPanel;

	// The "view" being rendered
	GTView view;

	// The current color model
	ColorTable cTable;
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
	CanvasToolTip tt = new CanvasToolTip();

	private BufferFactory bufferFactory;
	private BufferedImage imageFull;

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

	void setView(GTView view)
	{
		this.view = view;
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

		cTable = new ColorTable(view.getStateTable(), boxW, boxH);

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

		if (imageFull == null)
			renderRegion(g);
		else
			renderImage(g);

		// Post (main-canvas) rendering operations
		if (mineSweeper != null)
			mineSweeper.render(g);

		if (view.hideMarker != -1)
		{
			g.setPaint(new Color(255, 255, 255, alphaEffect));

			int mX = boxW * view.hideMarker;
			g.fillRect(mX, 0, boxW, canvasH);
		}

		long e = System.nanoTime();

//		System.out.println("Render time: " + ((e-s)/1000000f) + "ms");
	}

	private void renderImage(Graphics2D g)
	{
		BufferedImage imageCrop = new BufferedImage(pX2-pX1, pY2-pY1, Prefs.guiBackBufferType);
		Graphics2D g2d = imageCrop.createGraphics();
		g2d.drawImage(imageFull, 0, 0, pX2-pX1, pY2-pY1, pX1, pY1, pX2, pY2, null);
		g2d.dispose();

		g.drawImage(imageCrop, pX1, pY1, Color.white, null);
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
		g.setColor(Color.white);
		g.fillRect(0, 0, canvasW, canvasH);

		for (int yIndex = yS, y = (boxH*yS); yIndex <= yE; yIndex++, y += boxH)
		{
			for (int xIndex = xS, x = (boxW*xS); xIndex <= xE; xIndex++, x += boxW)
			{
				if (monitor.killMe)
					break;

				int state = view.getState(yIndex, xIndex);
//				int compState = view.getState(0, xIndex);

				if (state > 0)
				{
//					if (state != compState || yIndex == 0)
						g.drawImage(cTable.get(state).getImage(), x, y, null);
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

			if (monitor.killMe)
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
			// Determine how much memory we need for the back buffer (in bytes)
			long bufferSize = (long)canvasW * (long)canvasH * 3;
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
				buffer = new BufferedImage(canvasW, canvasH, Prefs.guiBackBufferType);
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