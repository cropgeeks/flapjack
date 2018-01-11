// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dendrogram;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class DendrogramCanvas extends JPanel
{
	private DendrogramPanel dPanel;

	// This buffer holds the current viewport (visible) area
	BufferedImage image;

	// These are the x and y pixel positions on the canvas that currently appear
	// in the top left corner of the current view
	int pX1, pY1;

	public DendrogramCanvas(DendrogramPanel dPanel)
	{
		this.dPanel = dPanel;

		setBackground(Color.WHITE);
	}

	void setImage(BufferedImage image)
	{
		this.image = image;
	}

	// Compute real-time variables, that change as the viewpoint is moved across
	// the canvas
	void onRedraw(Dimension viewSize, Point viewPosition)
	{
		pX1 = viewPosition.x;
		pY1 = viewPosition.y;

		int width = image == null ? 0 : image.getWidth();
		int height = image == null ? 0 : image.getHeight();

		setPreferredSize(new Dimension(width, height));

		repaint();
	}

	@Override
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;

		g.translate(-pX1, -pY1);
		g.drawImage(image, null, pX1, pY1);
	}
}