// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import javax.swing.*;

import flapjack.gui.*;

class RowCanvas extends JPanel
{
	private GenotypeCanvas canvas;
	private GenotypePanel gPanel;

	private int lineIndex = -1;

	// Index of the left-most visible marker being displayed on the main canvas
	private int markerIndex;
	// How many markers are currently on screen
	private int markerCount;

	private int h = 15;

	RowCanvas(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		add(new Canvas2D());
	}

	void updateOverviewSelectionBox(int markerIndex, int markerCount)
	{
		this.markerIndex = markerIndex;
		this.markerCount = markerCount;

		repaint();
	}

	void setLineIndex(int lineIndex)
	{
		if (this.lineIndex != lineIndex && canvas.locked == false)
		{
			this.lineIndex = lineIndex;
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
			Graphics2D g = (Graphics2D) graphics;

			// Calculate the required offset and width
			int xOffset = gPanel.traitCanvas.getPanelWidth()
				+ gPanel.listPanel.getPanelWidth() + 1;
			int width = (canvas.pX2-canvas.pX1+1);
			g.translate(xOffset, 0);

			// Paint the background
			g.setColor(Prefs.visColorBackground);
			g.fillRect(0, 0, width, h);

			// Quit if the line index is out of bounds or beyond the canvas size
			if (lineIndex < 0 || lineIndex >= canvas.view.lineCount())
				return;

			// Scaling factors
			float xScale = canvas.boxTotalX / (float) width;

			// For every pixel of the overview...
			for (int x = 0; x < width; x++)
			{
				// What marker should be drawn on this (y) row?
				int mrkIndex = (int) (xScale * x);

				g.setColor(canvas.cScheme.getColor(lineIndex, mrkIndex));
				g.fillRect(x, 0, 1, h);
			}


			// Determine the boundary of the outline
			xScale = width / (float) canvas.boxTotalX;

			// Draw the outline fill
			float x1 = markerIndex * xScale;
			float x2 = x1 + (markerCount * xScale);
			if (markerCount > canvas.boxTotalX || x2 >= width)
				x2 = width-1;

			int cR = Prefs.visColorOverviewFill.getRed();
			int cG = Prefs.visColorOverviewFill.getGreen();
			int cB = Prefs.visColorOverviewFill.getBlue();
			g.setPaint(new Color(cR, cG, cB, 50));
			g.fillRect(Math.round(x1), 0, Math.round(x2-x1), h-1);

			g.setColor(Prefs.visColorOverviewOutline);
			g.drawRect(Math.round(x1), 0, Math.round(x2-x1), h-1);
		}
	}
}