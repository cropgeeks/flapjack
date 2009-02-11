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
			int xOffset = gPanel.listPanel.getPanelWidth() + 1;
			int width = (canvas.pX2-canvas.pX1);
			g.translate(xOffset, 0);

			// Paint the background
			g.setColor(Prefs.visColorBackground);
			g.fillRect(0, 0, width, 15);

			// Quit if the line index is out of bounds or beyond the canvas size
			if (lineIndex < 0 || lineIndex >= canvas.view.getLineCount())
				return;


			int boxTotalX = canvas.boxTotalX;

			// Scaling factors
			float xScale = width / (float) boxTotalX;

			// Width of each X element
			int xWidth = 1 + Math.round((xScale >= 1) ? xScale : 1);

			int lastX = -1;

			float x = 0;
			for (int xIndex = 0; xIndex < boxTotalX; xIndex++)
			{
				// This is where we save the time...
				if ((int)x != lastX)
				{
					g.setColor(canvas.cScheme.getColor(lineIndex, xIndex));
					g.fillRect(Math.round(x), 0, xWidth, h);

					lastX = (int)x;
				}

				x += xScale;
			}


			// Draw the outline fill
			float x1 = markerIndex * xScale;
			float x2 = x1 + (markerCount * xScale);
			if (markerCount > boxTotalX || x2 > width)
				x2 = width;

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