// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import javax.swing.*;

import flapjack.gui.*;

class ColCanvas extends JPanel
{
	private GenotypeCanvas canvas;

	private int markerIndex = -1;

	// Index of the top-most visible line being displayed on the main canvas
	private int lineIndex;
	// How many lines are currently on screen
	private int lineCount;

	private int w = 45;

	ColCanvas(GenotypeCanvas canvas)
	{
		this.canvas = canvas;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(1, 5, 0, 5));
		add(new Canvas2D());
	}

	void updateOverviewSelectionBox(int lineIndex, int lineCount)
	{
		this.lineIndex = lineIndex;
		this.lineCount = lineCount;

		repaint();
	}

	void setMarkerIndex(int markerIndex)
	{
		if (this.markerIndex != markerIndex && canvas.locked == false)
		{
			this.markerIndex = markerIndex;
			repaint();
		}
	}

	private class Canvas2D extends JPanel
	{
		Canvas2D()
		{
			setPreferredSize(new Dimension(w, 0));
		}

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics;

			// Calculate the required offset and width
			int height = canvas.pY2-canvas.pY1+1;

			// Paint the background
			g.setColor(Prefs.visColorBackground);
			g.fillRect(0, 0, w, height);

			// Quit if the line index is out of bounds or beyond the canvas size
			if (markerIndex < 0 || markerIndex >= canvas.view.getMarkerCount())
				return;

			// Scaling factors
			float yScale = canvas.boxTotalY / (float) height;

			// For every pixel of the overview...
			for (int y = 0; y < height; y++)
			{
				// What line should be drawn on this (x) row?
				int rowIndex = (int) (yScale * y);

				int x = 0;
				for (int xIndex = markerIndex-1; xIndex < markerIndex+2; xIndex++, x+=15)
				{
					if (xIndex < 0 || xIndex >= canvas.view.getMarkerCount())
						continue;

					g.setColor(canvas.cScheme.getColor(rowIndex, xIndex));
					g.fillRect(x, y, w/3, 1);
				}
			}


			// Determine the boundary of the outline
			yScale = height / (float) canvas.boxTotalY;

			float y1 = lineIndex * yScale;
			float y2 = y1 + lineCount * yScale;
			if (lineCount > canvas.boxTotalY || y2 >= height)
				y2 = height-1;

			// Draw the outline fill
			int cR = Prefs.visColorOverviewFill.getRed();
			int cG = Prefs.visColorOverviewFill.getGreen();
			int cB = Prefs.visColorOverviewFill.getBlue();
			g.setPaint(new Color(cR, cG, cB, 50));
			g.fillRect(0, Math.round(y1), w-1, Math.round(y2-y1));

			g.setColor(Prefs.visColorOverviewOutline);
			g.drawRect(0, Math.round(y1), w-1, Math.round(y2-y1));
		}
	}
}