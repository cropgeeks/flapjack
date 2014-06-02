package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

public class ChromosomeCanvasKey extends JPanel
{
	private boolean redraw;

	// This buffer holds the current viewport (visible) area
	private BufferedImage imageViewPort;

	private Dimension dimension = new Dimension();

	private ChromosomeCanvas chromCanvas;

	ChromosomeCanvasKey(ChromosomeCanvas chromCanvas)
	{
		this.chromCanvas = chromCanvas;
		// This panel has to detect changes to its size, and recreate the image
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				redraw = true;
				repaint();
			}
		});

		dimension = new Dimension(getWidth(), 16);
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

			renderCanvas(gImage);
			gImage.dispose();
		}

		g.drawImage(imageViewPort, 0, 0, null);
		redraw = false;
	}

	private void renderCanvas(Graphics2D g)
	{
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		for (int i=0; i < getWidth(); i++)
		{
			int alpha = 0 + (int) (((255-0) * (255*(i/ (float)getWidth()))) / 255f);
			g.setColor(new Color(70, 116, 162, alpha));
			g.drawLine(i, 0, i, 16);
		}

		g.setColor(Color.BLACK);
		g.drawString("LOW (" + chromCanvas.minMarkerCount() + ")", 5, 12);

		g.setColor(Color.WHITE);
		String high = "(" + chromCanvas.maxMarkerCount() + ") HIGH";
		g.drawString(high, getWidth()-5- g.getFontMetrics().stringWidth(high), 12);
	}

	public Dimension getPreferredSize()
		{ return dimension; }
}
