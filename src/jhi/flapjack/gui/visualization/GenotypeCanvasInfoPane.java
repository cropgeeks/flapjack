// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import jhi.flapjack.data.*;

class GenotypeCanvasInfoPane implements IOverlayRenderer
{
	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	private Color bgColor;
	private Font titleFont, labelFont;
	private FontMetrics fmTitle;

	// Where is the mouse
	private Point mouse;
	// Where are we going to start drawing
	private int x, y, w;

	// Distance between each line of text
	private int lineSpacing = 15;
	private int yPos;

	// **********
	// TODO: THINK ABOUT 4K !!!
	// **********

	GenotypeCanvasInfoPane(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;

		Color c = (Color) UIManager.get("info");
		bgColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 190);

		titleFont = new Font("Dialog", Font.BOLD, 12);
		labelFont = new Font("Dialog", Font.PLAIN, 11);

		// Pre-build some font metrics for calculating string widths
		Image image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		fmTitle = g.getFontMetrics(titleFont);
		g.dispose();
	}

	public void setMousePosition(Point mouse)
	{
		this.mouse = mouse;
	}

	@Override
	public void render(Graphics2D g)
	{
		GTView view = gPanel.getView();
		int line = view.mouseOverLine;
		int mrkr = view.mouseOverMarker;

		// TODO: Don't think these are range checked on visible data; it's just row/col data for the whole canvas...
		if (mouse == null || line == -1 || mrkr == -1)
			return;

		w = 300;
		yPos = 0;

		calculatePosition(75);
		g.translate(x, y);

		drawBoxBackground(g, 75);

		g.setColor(Color.black);
		g.setFont(labelFont);
		g.drawString(mrkr + "," + line, 20, 20);

		// Reset the translation so the next overlay isn't broken
		g.translate(-x, -y);
	}

	// Draws the outline and background
	private void drawBoxBackground(Graphics2D g, int h)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// The background rectangle
		g.setColor(bgColor);
		g.fillRoundRect(0, yPos, w - 1, h - 1, 10, 10);
		g.setColor(Color.black);
		g.drawRoundRect(0, yPos, w - 1, h - 1, 10, 10);

		yPos += 15;
	}

	private void calculatePosition(int h)
	{
		// Decide where to draw (roughly)
		x = mouse.x + 15;
		y = mouse.y + 20;

		int pX2Max = canvas.pX2Max;
		int pY2 = canvas.pY2Max;

		// Then adjust if the box would be offscreen to the right or bottom
		if (x + w >=pX2Max)
			x = pX2Max - w - 1;
		if (y + h >= pY2)
			y = pY2 - h - 1;
	}
}