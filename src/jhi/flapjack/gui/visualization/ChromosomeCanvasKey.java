// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

import scri.commons.gui.*;

public class ChromosomeCanvasKey extends JPanel
{
	private boolean redraw;

	// This buffer holds the current viewport (visible) area
	private BufferedImage imageViewPort;

	private Dimension dimension = new Dimension();

	private ChromosomeCanvas chromCanvas;
	private Canvas2D canvas2D;

	ChromosomeCanvasKey(ChromosomeCanvas chromCanvas)
	{
		this.chromCanvas = chromCanvas;

		// This panel has to detect changes to its size, and recreate the image
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				redraw();
			}
		});

//		dimension = new Dimension(getWidth(), 32);

		setBackground(Color.WHITE);

		setLayout(new BorderLayout(5, 5));
		add(new JLabel(RB.getString("gui.visualization.ChromosomeCanvasKey.label")), BorderLayout.NORTH);
		add(canvas2D = new Canvas2D());
		add(new JLabel(), BorderLayout.SOUTH);
	}

	public void redraw()
	{
		redraw = true;
		canvas2D.repaint();
	}

	private class Canvas2D extends JPanel
	{
		Canvas2D()
		{
			setBorder(BorderFactory.createLineBorder(Color.GRAY));
		}

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);

			Graphics2D g = (Graphics2D) graphics;

			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());

			if (redraw)
			{
				// What size of viewport buffer do we need?
				int w = getWidth();
				int h = getHeight();

				// Only make a new buffer if we really really need to, as this has
				// a noticeable effect on performance
				if (imageViewPort == null ||
					imageViewPort.getWidth() != w || imageViewPort.getHeight() != h)
				{
					imageViewPort = (BufferedImage) createImage(w, h);
				}

				Graphics2D gImage = imageViewPort.createGraphics();

				renderCanvas(gImage, w, h);
				gImage.dispose();
			}

			g.drawImage(imageViewPort, 0, 0, null);
			redraw = false;
		}

		private void renderCanvas(Graphics2D g, int w, int h)
		{
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, w, h);

			for (int i=0; i < w; i++)
			{
				int alpha = 0 + (int) (((255-0) * (255*(i/ (float)w))) / 255f);
				g.setColor(new Color(70, 116, 162, alpha));
				g.drawLine(i, 0, i, h);
			}

			g.setColor(Color.BLACK);
			String low = RB.format("gui.visualization.ChromosomeCanvasKey.low",
				chromCanvas.minMarkerCount());
			g.drawString(low, 5, 12);

			g.setColor(Color.WHITE);
			String high = RB.format("gui.visualization.ChromosomeCanvasKey.high",
				chromCanvas.maxMarkerCount());
			g.drawString(high, w-5-g.getFontMetrics().stringWidth(high), 12);
		}

		public Dimension getPreferredSize()
			{ return new Dimension(0, 16); }
	}
}