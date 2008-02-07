package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

import flapjack.data.*;

class GenotypeCanvas extends JPanel
{
	private GenotypePanel gPanel;

	// The "view" being rendered
	GTView view;

//	DataSet dataSet;
//	ChromosomeMap map;

	// For faster rendering, maintain a local cache of the data to be drawn
//	Vector<GenotypeData> genotypeLines;

	// The current color model
	ColorTable cTable;

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

	// Holds the current dimensions of the canvas in an AWT friendly format
	private Dimension dimension = new Dimension();

	// Rendering mode: 0 (real-time), 1 (buffered), 2 (minesweeper)
	int renderMode = 0;

	// The tooltip object
	CanvasToolTip tt = new CanvasToolTip();

	private BufferedImage image = null;

	MineSweeper mineSweeper;

	GenotypeCanvas(GenotypePanel gPanel)
	{
		this.gPanel = gPanel;

		setOpaque(false);
		setBackground(Color.white);

		new CanvasMouseListener(this, gPanel);

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
		gPanel.computeScrollbarAdjustmentValues(boxW, boxH);

		boxTotalX = view.getMarkerCount();
		boxTotalY = view.getLineCount();

		canvasW = (boxTotalX * boxW);// + (boxW);// - 1);
		canvasH = (boxTotalY * boxH);

		setSize(dimension = new Dimension(canvasW, canvasH));

		cTable = new ColorTable(view.getStateTable(), boxW, boxH);

		/////////////////////////

		image = null;

		System.out.println("boxW = " + boxW + ", boxH = " + boxH);
		System.out.println("canvasW = " + canvasW + ", canvasH = " + canvasH);

		long bufferSize = (long)canvasW * (long)canvasH;
		System.out.println("Canvas buffer requires: " + (bufferSize/1024f/1024f) + " MB");

		repaint();
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


	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;

		long s = System.nanoTime();
		switch (renderMode)
		{
			case 0: renderRegion(g);
				break;
			case 1: renderImage(g);
				break;
			case 2: mineSweeper.render(g);
				break;
		}
		long e = System.nanoTime();

		System.out.println("Render time: " + ((e-s)/1000000f) + "ms");
	}

	private void renderImage(Graphics2D g)
	{
		if (image == null)
		{
	//		final GenotypeCanvas canvas = this;

	//		Runnable r = new Runnable() {
	//			public void run() {
	//				image = new BufferFactory(canvas, canvasW, canvasH).getImage();
	//			}
	//		};

			image = new BufferedImage(canvasW, canvasH, BufferedImage.TYPE_BYTE_INDEXED);

//			g.drawImage(imgBuffer, x, y, x+w, y+h, x, y, x+w, y+h, null);

			Graphics2D g2d = image.createGraphics();
			renderAll(g2d);
			g2d.dispose();
		}

		BufferedImage image2 = new BufferedImage(pX2-pX1, pY2-pY1, BufferedImage.TYPE_BYTE_INDEXED);
		Graphics2D g2d = image2.createGraphics();
		g2d.drawImage(image, 0, 0, pX2-pX1, pY2-pY1, pX1, pY1, pX2, pY2, null);
		g2d.dispose();


		System.out.println(pX1 + "," + pY1 + "-" + pX2 + "," + pY2);

		g.drawImage(image2, pX1, pY1, Color.white, null);
	}

	int xIndexStart, xIndexEnd;
	int yIndexStart, yIndexEnd;

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

		render(g, xIndexStart, xIndexEnd, yIndexStart, yIndexEnd);
	}

	void renderAll(Graphics2D g)
	{
		xIndexStart = 0;
		xIndexEnd = boxTotalX-1;
		yIndexStart = 0;
		yIndexEnd = boxTotalY-1;

//		render(g, 0, boxTotalX-1, 0, boxTotalY-1);

		render(g, xIndexStart, xIndexEnd, yIndexStart, yIndexEnd);
	}

	private void render(Graphics2D g, int xS, int xE, int yS, int yE)
	{
		StateTable table = view.getStateTable();

		g.setColor(Color.white);
		g.fillRect(0, 0, canvasW, canvasH);

		for (int yIndex = yS, y = (boxH*yS); yIndex <= yE; yIndex++, y += boxH)
		{
//			GenotypeData data = genotypeLines.get(yIndex);

			for (int xIndex = xS, x = (boxW*xS); xIndex <= xE; xIndex++, x += boxW)
			{
//				int state = data.getState(xIndex);

				int state = view.getState(yIndex, xIndex);

//				int compState = genotypeLines.get(10).getState(xIndex);

				if (state > 0)
				{
//					AlleleState as = table.getAlleleState(data.getState(xIndex));

//					g.setPaint(new GradientPaint(x, y, cTable.get(state).getBrightColor(), x+boxW, y+boxH, cTable.get(state).getDarkColor()));
//					g.setColor(table.getAlleleState(data.getState(xIndex)).getColor());

//					g.fillRect(x, y, boxW, boxH);
					g.drawImage(cTable.get(state).getImage(), x, y, null);

/*					if (y == 10 || state == compState)
					{
						g.drawImage(cTable.get(1).getImage(), x, y, null);
					}
					else
					{
						g.drawImage(cTable.get(2).getImage(), x, y, null);
					}
*/
//					g.setColor(Color.white);
//					g.drawRect(x, y, boxW, boxH);

//					if (dataSet.getStateTable().getAlleleState(loci[xIndex]).isHomozygous() == false)
//					{
//						g.setColor(Color.black);
//						g.drawRect(x, y, boxW, boxH);
//					}

//					g.setColor(Color.black);
//					g.drawString("" + data.getState(xIndex), x+2, y+boxH-3);
				}
			}
		}
	}
}