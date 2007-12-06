package flapjack.gui.visualization;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import flapjack.data.*;

class RowPanel extends JPanel
{
	private GenotypeCanvas canvas;

	private GenotypeData data;

	private RowCanvas rowCanvas = new RowCanvas();

	// Starting index being displayed on the main canvas, and the number of boxes
	private int xIndex, xCount;

	RowPanel(GenotypeCanvas canvas)
	{
		this.canvas = canvas;

		setLayout(new BorderLayout());
		add(rowCanvas);
	}

	void computeDimensions(int w1, int w2)
	{
		System.out.println("border: "+  w1 +", " + w2);

		setBorder(BorderFactory.createEmptyBorder(5, w1, 5, w2));
	}

	void updateOverviewSelectionBox(int xIndex, int xCount)
	{
		this.xIndex = xIndex;
		this.xCount = xCount;
	}

	void setGenotypeData(GenotypeData data)
	{
		if (this.data != data && canvas.locked == false)
		{
			this.data = data;
			repaint();
		}
	}

	private class RowCanvas extends JPanel
	{
		RowCanvas()
		{
			setBackground(Color.white);
			setPreferredSize(new Dimension(0, 15));
		}

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);

			Graphics2D g = (Graphics2D) graphics;

			if (data == null)
				return;

			int boxTotalX = data.countLoci();

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
					int state = data.getState(xIndex);

					if (state > 0)
					{
						g.setColor(canvas.cTable.get(state).getColor());
						g.fillRect((int)x, 0, xWidth, 15);

						lastX = (int)x;
					}
				}

				x += xScale;
			}


			int x1 = (int) (xIndex*xScale);
			int x2 = (int) (xIndex*xScale + xCount*xScale);

			g.setPaint(new Color(50, 50, 0, 50));
			g.fillRect(x1, 0, x2-x1-1, 15-1);
			g.setColor(Color.red);
			g.drawRect(x1, 0, x2-x1-1, 15-1);
		}
	}
}