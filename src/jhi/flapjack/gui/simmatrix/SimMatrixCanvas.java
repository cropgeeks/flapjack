// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.simmatrix;

import java.awt.*;
import java.awt.image.*;
import java.util.concurrent.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;

public class SimMatrixCanvas extends JPanel
{
	private SimMatrixPanel sPanel;
	private SimMatrix matrix;

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
	// These positions represent the bottom right hand corner of the visible
	// data onscreen (pX2 == pX2Max when more data is offscreen)
	int pX2, pY2;
	// And bottom right hand corner
	int pX2Max, pY2Max;

	// Objects for multicore rendering
	private int cores = Runtime.getRuntime().availableProcessors();
	private ExecutorService executor;
	private Future[] tasks;

	// This buffer holds the current viewport (visible) area
	BufferedImage imageViewPort;

	boolean redraw = true;

	int[][] colors;

	Point pCenter;
	Point bCenter;

	private GTViewSet viewSet;


	public SimMatrixCanvas(SimMatrixPanel sPanel, SimMatrix matrix)
	{
		this.sPanel = sPanel;
		this.matrix = matrix;
		this.viewSet = viewSet;

		setOpaque(false);

		// Prepare the background threads that will do the main painting
		executor = Executors.newFixedThreadPool(cores);
		tasks = new Future[cores];
	}

	// Compute canvas related dimensions that only change if the data or the
	// box-drawing size needs to be changed
	void onResize(int sizeX, int sizeY)
	{
		boxW = sizeX;
		boxH = sizeY;

		boxTotalX = matrix.size();
		boxTotalY = matrix.size();

		canvasW = (boxTotalX * boxW);
		canvasH = (boxTotalY * boxH);

		setPreferredSize(new Dimension(canvasW, canvasH));
		resetBufferedState(true);
	}

	// Compute real-time variables, that change as the viewpoint is moved across
	// the canvas
	void onRedraw(Dimension viewSize, Point viewPosition)
	{
		boxCountX = 1 + (int) ((float) viewSize.getWidth()  / boxW);
		boxCountY = 1 + (int) ((float) viewSize.getHeight() / boxH);

		pX1 = viewPosition.x;
		pX2 = pX2Max = pX1 + viewSize.width -1;

		pY1 = viewPosition.y;
		pY2 = pY2Max = pY1 + viewSize.height - 1;

		// Adjust for canvases that are smaller than the window size
		if (pX2 >= canvasW)
			pX2 = canvasW - 1;
		if (pY2 >= canvasH)
			pY2 = canvasH - 1;

		// Track the center of the view
		int pCenterX = pX1 + ((pX2-pX1+1)/2);
		int pCenterY = pY1 + ((pY2-pY1+1)/2);
		pCenter = new Point(pCenterX, pCenterY);
		bCenter = new Point(pCenterX/boxW, pCenterY/boxH);

		redraw = true;
		repaint();
	}

	// Will stop the creation of any back-buffer, and optionally start working
	// on a new buffer (on the assumption that the view has changed in some way)
	void resetBufferedState(boolean createNewBuffer)
	{
		redraw = true;
		repaint();
	}

	@Override
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;
//		g.fillRect(0, 0, canvasW, canvasH);

		renderViewport(g);
	}

	private void renderViewport(Graphics2D g)
	{
		if (redraw)
		{

			// What size of viewport buffer do we need?
			int w = pX2-pX1+1, h = pY2-pY1+1;

			// Only make a new buffer if we really really need to, as this has
			// a noticeable effect on performance
			if (imageViewPort == null ||
				imageViewPort.getWidth() != w || imageViewPort.getHeight() != h)
			{
				imageViewPort = (BufferedImage) createImage(w, h);
			}

			Graphics2D[] graphics = new Graphics2D[cores];
			for (int i = 0; i < graphics.length; i++)
			{
				graphics[i] = imageViewPort.createGraphics();
				graphics[i].translate(-pX1, -pY1);
			}

			// Draw what should be visible onto the screen buffer...
			renderRegion(graphics);

			for (int i = 0; i < graphics.length; i++)
				graphics[i].dispose();
		}

		g.drawImage(imageViewPort, pX1, pY1, null);
		redraw = false;
	}

	private void renderRegion(Graphics2D[] g)
	{
		// These are the index positions within the dataset that we'll start
		// drawing from
		int xS = pX1 / boxW;
		int yS = pY1 / boxH;

		// The end indices are calculated as the:
		//   (the start index) + (the number that can be drawn on screen)
		// with a check to set the end index to the last value in the array if
		// the calculated index would go out of bounds
		int xE = xS + boxCountX;
		if (xE >= boxTotalX)
			xE = boxTotalX-1;

		int yE = yS + boxCountY;
		if (yE >= boxTotalY)
			yE = boxTotalY-1;

		render(g, xS, xE, yS, yE);
	}

	private void render(Graphics2D[] g, int xS, int xE, int yS, int yE)
	{
//		long s = System.currentTimeMillis();
		try
		{
			// Paint the lines using multiple cores...
			for (int i = 0; i < tasks.length; i++)
				tasks[i] = executor.submit(new LinePainter(g[i], xS, xE, yS+i, yE));
			for (Future task: tasks)
				task.get();
		}
		catch (Exception e) {}
//		System.out.println("Time for render: " + (System.currentTimeMillis() - s));
	}

	private final class LinePainter implements Runnable
	{
		private Graphics2D g;
		private int xS;
		private int xE;
		private int yS;
		private int yE;

		LinePainter(Graphics2D g, int xS, int xE, int yS, int yE)
		{
			this.g = g;
			this.xS = xS;
			this.xE = xE;
			this.yS = yS;
			this.yE = yE;
		}

		@Override
		public void run()
		{
			Color col1 = Color.blue;
			int[] c1 = new int[] { col1.getRed(), col1.getGreen(), col1.getBlue() };
			Color col2 = Color.white;
			int[] c2 = new int[] { col2.getRed(), col2.getGreen(), col2.getBlue() };

			for (int row = yS, y = (boxH*yS); row <= yE; row += cores, y += boxH*cores)
			{
				for (int xIndex = xS, x = (boxW*xS); xIndex <= xE; xIndex++, x += boxW)
				{
					float f = 0;

					if (xIndex <= row)
						f = matrix.valueAt(row, xIndex);
					else
						f = matrix.valueAt(xIndex, row);

					float f1 = (float) (1.0 - f);
					float f2 = (float) f;

					g.setColor(new Color(
	          			(int) (f1 * c1[0] + f2 * c2[0]),
	      				(int) (f1 * c1[1] + f2 * c2[1]),
	      				(int) (f1 * c1[2] + f2 * c2[2])));

//	      			g.setColor(new Color((int)(f*255), (int)(f*255), (int)(f*255)));

					g.fillRect(x, y, boxW, boxH);
				}
			}
		}
	}
}