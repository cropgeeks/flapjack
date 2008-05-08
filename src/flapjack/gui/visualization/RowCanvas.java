package flapjack.gui.visualization;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

class RowCanvas extends JPanel
{
	private GenotypeCanvas canvas;

	private int lineIndex = -1;

	// Starting index being displayed on the main canvas, and the number of boxes
	private int xIndex, xCount;

	RowCanvas(GenotypeCanvas canvas)
	{
		this.canvas = canvas;

		setLayout(new BorderLayout());
		add(new Canvas2D());
	}

	void computeDimensions(int w1, int w2)
	{
		setBorder(BorderFactory.createEmptyBorder(5, w1, 5, w2));
	}

	void updateOverviewSelectionBox(int xIndex, int xCount)
	{
		this.xIndex = xIndex;
		this.xCount = xCount;

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
			setBackground(Prefs.visColorBackground);
			setPreferredSize(new Dimension(0, 15));
		}

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);

			Graphics2D g = (Graphics2D) graphics;

			// Quit if the line index is out of bounds or beyond the canvas size
			if (lineIndex < 0 || lineIndex >= canvas.view.getLineCount())
				return;


			int boxTotalX = canvas.boxTotalX;

			// Scaling factors
			float xScale = getWidth() / (float) boxTotalX;

			// Width of each X element
			int xWidth = 1 + (int) ((xScale >= 1) ? xScale : 1);

			int lastX = -1;

			float x = 0;
			for (int xIndex = 0; xIndex < boxTotalX; xIndex++)
			{
				// This is where we save the time...
				if ((int)x != lastX)
				{
					g.setColor(canvas.cScheme.getColor(lineIndex, xIndex));
					g.fillRect((int)x, 0, xWidth, 15);

					lastX = (int)x;
				}

				x += xScale;
			}


			int x1 = (int) (xIndex*xScale);
			int x2 = (int) (xIndex*xScale + xCount*xScale);

			int cR = Prefs.visColorOverviewFill.getRed();
			int cG = Prefs.visColorOverviewFill.getGreen();
			int cB = Prefs.visColorOverviewFill.getBlue();
			g.setPaint(new Color(cR, cG, cB, 50));
			g.fillRect(x1, 0, x2-x1-1, 15-1);

			g.setColor(Prefs.visColorOverviewOutline);
			g.drawRect(x1, 0, x2-x1-1, 15-1);
		}
	}
}