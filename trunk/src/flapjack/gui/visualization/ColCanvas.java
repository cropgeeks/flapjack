package flapjack.gui.visualization;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import flapjack.data.*;

class ColCanvas extends JPanel
{
	private GenotypeCanvas canvas;

	private int lociIndex = -1;

	// Starting index being displayed on the main canvas, and the number of boxes
	private int yIndex, yCount;

	ColCanvas(GenotypeCanvas canvas)
	{
		this.canvas = canvas;

		setLayout(new BorderLayout());
		add(new Canvas2D());
	}

	void computeDimensions(int h1, int h2)
	{
		setBorder(BorderFactory.createEmptyBorder(0, 5, h2, 5));
	}

	void updateOverviewSelectionBox(int yIndex, int yCount)
	{
		this.yIndex = yIndex;
		this.yCount = yCount;
	}

	void setLociIndex(int lociIndex)
	{
		if (this.lociIndex != lociIndex && canvas.locked == false)
		{
			this.lociIndex = lociIndex;
			repaint();
		}
	}

	private class Canvas2D extends JPanel
	{
		Canvas2D()
		{
			setBackground(Color.white);
			setPreferredSize(new Dimension(45, 0));
		}

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);

			Graphics2D g = (Graphics2D) graphics;

			if (lociIndex == -1) // TODO: or is > than number of loci
				return;

			int boxTotalY = canvas.boxTotalY;

			// Scaling factors
			float yScale = getHeight() / (float) boxTotalY;

			// Width of each Y element
			int yHeight = 1 + (int) ((yScale >= 1) ? yScale : 1);

			int lastY = -1;

			float y = 0;
			for (int yIndex = 0; yIndex < boxTotalY; yIndex++)
			{
				// This is where we save the time...
				if ((int)y != lastY)
				{
					GenotypeData data = canvas.genotypeLines.get(yIndex);

					int x = 0;
					for (int xIndex = lociIndex-1; xIndex < lociIndex+2; xIndex++, x+=15)
					{
						int state = data.getState(xIndex);

						if (state > 0)
						{
							g.setColor(canvas.cTable.get(state).getColor());
							g.fillRect(x, (int)y, 15, yHeight);

							lastY = (int)y;
						}
					}
				}

				y += yScale;
			}


			int y1 = (int) (yIndex*yScale);
			int y2 = (int) (yIndex*yScale + yCount*yScale);

			g.setPaint(new Color(50, 50, 0, 50));
			g.fillRect(0, y1, 45-1, y2-y1-1);
			g.setColor(Color.red);
			g.drawRect(0, y1, 45-1, y2-y1-1);
		}
	}
}