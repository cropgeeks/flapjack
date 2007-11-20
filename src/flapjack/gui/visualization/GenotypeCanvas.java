package flapjack.gui.visualization;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import flapjack.data.*;

class GenotypeCanvas extends JPanel
{
	private GenotypeDisplayPanel gdPanel;

	private DataSet dataSet;
	private ChromosomeMap map;

	// For faster rendering, maintain a local cache of the data to be drawn
	private Vector<GenotypeData> genotypeLines;

	// The number of lines and the number of markers being drawn
	int nLines, nMarkers;
	// Also referred to as:
	int boxTotalX, boxTotalY;
	// Width and height of the main drawing canvas
	int canvasW, canvasH;
	// Width and height of an allele "box"
	int boxW, boxH;
	// How many boxes will fit into the current screen size?
	int boxCountX, boxCountY;

	// These are the x and y pixel positions on the canvas that currently appear
	// in the top left corner of the current view
	int pX, pY;

	// Holds the current dimensions of the canvas in an AWT friendly format
	private Dimension dimension;

	GenotypeCanvas(GenotypeDisplayPanel gdPanel, DataSet dataSet, ChromosomeMap map)
	{
		this.gdPanel = gdPanel;
		this.dataSet = dataSet;
		this.map = map;

		setBackground(Color.white);
		setOpaque(false);

		cacheLines();
		computeDimensions();
	}

	private void cacheLines()
	{
		genotypeLines = new Vector<GenotypeData>(dataSet.countLines());

		for (int i = 0; i < dataSet.countLines(); i++)
		{
			Line line = dataSet.getLineByIndex(i);
			GenotypeData data = line.getGenotypeDataByMap(map);

			genotypeLines.add(data);
		}
	}

	// Compute canvas related dimensions that only change if the data or the
	// box-drawing size needs to be changed
	void computeDimensions()
	{
		Font font = new Font("Monospaced", Font.PLAIN, 11);
		FontMetrics fm = new java.awt.image.BufferedImage(1, 1,
			java.awt.image.BufferedImage.TYPE_INT_RGB).getGraphics()
			.getFontMetrics(font);

		boxW = 10;
		boxH = fm.getHeight();

		// Once we have suitable width/height values, the scrollbars can be made
		// to lock to those so we never have to draw less than a full box
		gdPanel.computeScrollbarAdjustmentValues(boxW, boxH);

		boxTotalY = nLines = genotypeLines.size();
		boxTotalX = nMarkers = map.countLoci();

		canvasW = (boxTotalX * boxW) + (boxW - 1);
		canvasH = boxTotalY * boxH;

		dimension = new Dimension(canvasW, canvasH);
		setSize(canvasW, canvasH);
	}

	// Compute real-time variables, that change as the viewpoint is moved across
	// the canvas
	void computeForRedraw(Dimension viewSize, Point viewPosition)
	{
		boxCountX = 1 + (int) ((float) viewSize.getWidth()  / boxW);
		boxCountY = 1 + (int) ((float) viewSize.getHeight() / boxH);

		pX = viewPosition.x;
		pY = viewPosition.y;

//		repaint();
	}

	public Dimension getSize()
		{ return dimension; }

	public Dimension getPreferredSize()
		{ return dimension; }

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// These are the index positions within the dataset that we'll start
		// drawing from
		int xIndexStart = pX / boxW;
		int yIndexStart = pY / boxH;

		// The end indices are calculated as the:
		//   (the start index) + (the number that can be drawn on screen)
		// with a check to set the end index to the last value in the array if
		// the calculated index would go out of bounds
		int xIndexEnd = xIndexStart + boxCountX;
		if (xIndexEnd >= boxTotalX)
			xIndexEnd = boxTotalX-1;

		int yIndexEnd = yIndexStart + boxCountY;
		if (yIndexEnd >= boxTotalY)
			yIndexEnd = boxTotalY-1;



		for (int yIndex = yIndexStart, y = (boxH*yIndexStart); yIndex <= yIndexEnd; yIndex++, y += boxH)
		{
			GenotypeData data = genotypeLines.get(yIndex);
			short[] loci = data.getLociData();


			for (int xIndex = xIndexStart, x = pX; xIndex <= xIndexEnd; xIndex++, x += boxW)
			{
				g.setColor(
					dataSet.getStateTable().getAlleleState(loci[xIndex]).getColor());

				g.fillRect(x, y, boxW, boxH);
//				g.drawString("" + loci[xIndex], x+2, y+boxH-3);
			}
		}
	}
}