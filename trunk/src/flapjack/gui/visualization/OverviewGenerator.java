package flapjack.gui.visualization;

import java.awt.*;
import java.awt.image.*;

import flapjack.data.*;

class OverviewGenerator extends Thread
{
	private OverviewDialog dialog;

	private GenotypeDisplayPanel gdPanel;

	// Width and height of the image to be created
	private int w, h;

	private BufferedImage image;

	// Give up drawing if set to true
	boolean killMe = false;

	float xScale, yScale;

	int xWidth, yHeight;

	OverviewGenerator(OverviewDialog dialog, GenotypeDisplayPanel gdPanel, int w, int h)
	{
		this.dialog = dialog;
		this.gdPanel = gdPanel;
		this.w = w;
		this.h = h;

		setPriority(Thread.MIN_PRIORITY);
		start();
	}

	BufferedImage getImage()
	{
		return image;
	}

	@Override
	public void run()
	{
		image = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED);
		Graphics2D g = image.createGraphics();

		g.setColor(Color.white);
		g.fillRect(0, 0, w, h);

		GenotypeCanvas canvas = gdPanel.canvas;

		int boxTotalX = canvas.boxTotalX;
		int boxTotalY = canvas.boxTotalY;


		// Scaling factors
		xScale = w / (float) boxTotalX;
		yScale = h / (float) boxTotalY;

		// Width of each X element (SEE NOTE BELOW)
		xWidth = 1 + (int) ((xScale >= 1) ? xScale : 1);
		// Height of each Y element
		yHeight = 1 + (int) ((yScale >= 1) ? yScale : 1);

//		StateTable table = canvas.dataSet.getStateTable();

		// What were the x and y positions of the last point drawn? If the next
		// point to be drawn ISN'T different, then we won't bother drawing it,
		// and will save a significant amount of time
		int lastX = -1;
		int lastY = -1;

		float y = 0;
		for (int yIndex = 0; yIndex < boxTotalY && !killMe; yIndex++)
		{
			GenotypeData data = canvas.genotypeLines.get(yIndex);

			float x = 0;
			for (int xIndex = 0; xIndex < boxTotalX && !killMe; xIndex++)
			{
				// This is where we save the time...
				if ((int)x != lastX || (int)y != lastY)
				{
					int state = data.getState(xIndex);

					if (state > 0)
					{
//						g.setColor(table.getAlleleState(data.getState(xIndex)).getColor());
						g.setColor(canvas.cTable.get(state).getColor());
						g.fillRect((int)x, (int)y, xWidth, yHeight);

						lastX = (int)x;
						lastY = (int)y;
					}
				}

				x += xScale;
			}

			y += yScale;
		}

		if (!killMe)
		{
			// Once complete, let the dialog know its image is ready
			dialog.imageAvailable(this);
		}
	}

	// We use (1 +) do deal with integer roundoff that results in columns
	// being skipped due to overlaps: eg with width of 1.2:
	// 1.2 (1) 2.4 (2) 3.6 (3) 4.8 (4) 6.0 (6)
	// position 5 was skipped
}