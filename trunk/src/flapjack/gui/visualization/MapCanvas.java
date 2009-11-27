// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import static java.awt.RenderingHints.*;
import java.awt.image.*;
import java.text.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

class MapCanvas extends JPanel
{
	private NumberFormat nf = NumberFormat.getInstance();

	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	private int h = 55;

	private BufferedImage buffer;
	boolean updateBuffer = true;

	// Last known pX2 for the main canvas - if it's changed, we need to redraw
	private int pX2 = 0;

	// The index of the marker currently under the mouse on the main canvas
	private int mrkrIndex = -1;

	// Chromosome map values for the lowest (first) and highest) last markers
	// currently visible on screen
	private float mSPos, mEPos;

	// What is the current drawing width
	private int w;

	MapCanvas(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		add(new Canvas2D());
	}

	void setMarkerIndex(int mrkrIndex)
	{
		if (this.mrkrIndex != mrkrIndex  && canvas.locked == false)
		{
			this.mrkrIndex = mrkrIndex;
			repaint();
		}
	}

	private class Canvas2D extends JPanel
	{
		private Font FONT = new Font("Dialog", Font.PLAIN, 11);

		Canvas2D()
		{
			setPreferredSize(new Dimension(0, h));
		}

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics;

			if (pX2 != canvas.pX2)
			{
				updateBuffer = true;
				pX2 = canvas.pX2;
			}

			// Calculate the required offset and width
			int xOffset = gPanel.traitCanvas.getPanelWidth()
				+ gPanel.listPanel.getPanelWidth() + 1;
			g.translate(xOffset, 0);

			// Update the back buffer (if it needs redrawn)
			if (updateBuffer)
				paintBuffer();

			g.drawImage(buffer, 0, 0, null);

			highlightMarker(g);
			highlightFeatures(g);
		}
	}

	private void paintBuffer()
	{
		w = canvas.pX2 - canvas.pX1 + 1;

		// Only make a new buffer if we really really need to, as this has
		// a noticeable effect on performance because of the time it takes
		if (buffer == null || buffer.getWidth() != w || buffer.getHeight() != h)
			buffer = (BufferedImage) createImage(w>0 ? w:1, h>0 ? h:1);

		Graphics2D g = buffer.createGraphics();
		g.setColor(getBackground());
		g.fillRect(0, 0, w, h);

		int xS = canvas.pX1 / canvas.boxW;
		int xE = canvas.pX2 / canvas.boxW;
		render(g, xS, xE);

		g.dispose();
		updateBuffer = false;
	}

	private void render(Graphics2D g, int xS, int xE)
	{
		long s = System.nanoTime();

		// Draw the white rectangle representing the map
		g.setColor(Color.white);
		g.fillRect(0, 12, w-1, 10);
		g.setColor(Color.lightGray);
		g.drawRect(0, 12, w-1, 10);

		// Local scaling
		mSPos = canvas.view.getMarker(xS).getPosition();
		mEPos = canvas.view.getMarker(xE).getPosition();

		// If markers have been moved xS and xE won't actually be the markers
		// with the left- and right-most map positions
		if (canvas.view.getMarkersOrdered() == false)
		{
			for (int i = xS; i <= xE; i++)
			{
				float pos = canvas.view.getMarker(i).getPosition();
				if (pos < mSPos)
					mSPos = pos;
				else if (pos > mEPos)
					mEPos = pos;
			}
		}

		// Global scaling
		if (Prefs.visMapScaling == 1)
		{
			mSPos = canvas.view.getMarker(0).getPosition();
			mEPos = canvas.view.getMarker(canvas.view.getMarkerCount()-1).getPosition();
		}

		for (int i = xS; i <= xE; i++)
			renderMarker(g, i, xS, false);

		long e = System.nanoTime();
		System.out.println("Map render time: " + ((e-s)/1000000f) + "ms");
	}

	private void renderMarker(Graphics2D g, int i, int xS, boolean text)
	{
		float distance = mEPos - mSPos;

		// "Jiggle" adjustment (for the genotype lines)
		int jiggle = canvas.pX1 % canvas.boxW;

		Marker m = canvas.view.getMarker(i);

		// Local scaling
		int xMap = (int) ((m.getPosition()-mSPos) * ((w-1) / distance));
		// Global scaling
		if (Prefs.visMapScaling == 1)
			xMap = (int) ((m.getPosition()) * ((w-1) / mEPos));

		int xBox = (int) ((i-xS) * canvas.boxW + (canvas.boxW/2)) - jiggle;

		g.drawLine(xMap, 12, xMap, 22);
		g.drawLine(xMap, 22, xBox, h-5);

		if (text)
		{
			String str = m.getName() + "  (" + nf.format(m.getPosition()) + ")";
			int strWidth = g.getFontMetrics().stringWidth(str);

			g.drawString(str, getPosition(xMap, strWidth), 8);
		}
	}

	// Highlights the marker under the mouse
	private void highlightMarker(Graphics2D g)
	{
		// Change to red, and redraw the currently highlighted one
		if (mrkrIndex >= 0 && mrkrIndex < canvas.view.getMarkerCount())
		{
			g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
			g.setColor(Color.red);

			int xS = canvas.pX1 / canvas.boxW;
			renderMarker(g, mrkrIndex, xS, true);
		}
	}

	private void highlightFeatures(Graphics2D g)
	{
		// If no feature is selected, just quit now
		if (QTLCanvas.mouseOverFeature == null)
			return;

		g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		g.setColor(Color.red);

		Feature f = QTLCanvas.mouseOverFeature;
		float min = f.getMin();
		float max = f.getMax();

		// Where should it be drawn
		float distance = mEPos - mSPos;
		int xMap = (int) (((min + ((max-min)/2)-mSPos)) * ((w-1) / distance));


		// And what should be drawn
		String str = f.getName() + "  (";
		if (f instanceof QTL)
			str += nf.format(((QTL)f).getPosition()) + ": ";
		str += nf.format(min) + "-" +nf.format(max) + ")";
		int strWidth = g.getFontMetrics().stringWidth(str);

		g.drawString(str, getPosition(xMap, strWidth), 8);

		// Now see which markers are "under" this feature, and highlight them
		int mkrCount = canvas.view.getMarkerCount();
		int xS = canvas.pX1 / canvas.boxW;

		for (int i = 0; i < mkrCount; i++)
		{
			Marker m = canvas.view.getMarker(i);

			// Is this marker under the QTL?
			if (m.getPosition() >= min && m.getPosition() <= max)
				renderMarker(g, i, xS, false);
		}
	}

	// Computes the best position to draw a string onscreen, assuming an optimum
	// start position that *may* be adjusted if the text ends up partially drawn
	// offscreen on either the LHS or the RHS
	private int getPosition(int pos, int strWidth)
	{
		// Work out where the left and right hand edges of the text will be
		int leftPos = pos-(int)(strWidth/2f);
		int rghtPos = pos+(int)(strWidth/2f);

		// If we're offscreen to the left, adjust...
		if (leftPos < 0)
			leftPos = 0;
		// Similarly if we're offscreen to the right...
		else if (rghtPos > w)
			leftPos = w-strWidth-1;

		return leftPos;
	}

	BufferedImage createSavableImage(boolean full)
		throws Exception
	{
		// Render width if we're just saving the current view
		w = canvas.pX2 - canvas.pX1 + 1;
		int xS = canvas.pX1 / canvas.boxW;
		int xE = canvas.pX2 / canvas.boxW;

		// Or the entire map
		if (full)
		{
			w = canvas.canvasW;
			xS = 0;
			xE = canvas.view.getMarkerCount()-1;
		}

		BufferedImage image = (BufferedImage) createImage(w>0 ? w:1, h>0 ? h:1);

		Graphics2D g = image.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, w, h);
		render(g, xS, xE);
		g.dispose();

		return image;
	}
}