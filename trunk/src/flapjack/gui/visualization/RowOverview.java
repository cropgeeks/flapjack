package flapjack.gui.visualization;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import flapjack.data.*;

class RowOverview extends JPanel
{
	private GenotypeCanvas canvas;

	private GenotypeData data;

	RowOverview(GenotypeCanvas canvas)
	{
		this.canvas = canvas;

		setBackground(Color.white);
		setPreferredSize(new Dimension(0, 15));
	}

	void setGenotypeData(GenotypeData data)
	{
		if (this.data != data)
		{
			this.data = data;
			repaint();
		}
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
	}
}