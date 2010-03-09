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

	private BufferedImage buffer, aaBuffer;
	boolean updateBuffer = true;

	// Last known pX2 for the main canvas - if it's changed, we need to redraw
	private int pX2 = 0;

	// The index of the marker currently under the mouse on the main canvas
	private int mrkrIndex = -1;

	// Chromosome map values for the lowest (first) and highest (last) markers
	// currently visible on screen
	float mSPos, mEPos;

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

			if (MapCanvasAAThread.bufferAvailable)
				g.drawImage(aaBuffer, 0, 0, null);
			else
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
		{
			buffer = (BufferedImage) createImage(w>0 ? w:1, h>0 ? h:1);
			aaBuffer = (BufferedImage) createImage(w>0 ? w:1, h>0 ? h:1);
		}

		Graphics2D g = buffer.createGraphics();
		g.setColor(getBackground());
		g.fillRect(0, 0, w, h);

		int xS = canvas.pX1 / canvas.boxW;
		int xE = canvas.pX2 / canvas.boxW;

		render(g, xS, xE);
		new MapCanvasAAThread(this, xS, xE);

		g.dispose();
		updateBuffer = false;
	}

	void render(Graphics2D g, int xS, int xE)
	{
		// Draw the white rectangle representing the map
		g.setColor(Color.white);
		g.fillRect(0, 12, w-1, 10);
		g.setColor(Color.lightGray);
		g.drawRect(0, 12, w-1, 10);

		setScaling(xS, xE);

		for (int i = xS; i <= xE; i++)
			renderMarker(g, i, xS, false);
	}

	private void renderMarker(Graphics2D g, int i, int xS, boolean text)
	{
		Marker m = canvas.view.getMarker(i);

		float distance = mEPos - mSPos;
		int xMap = (int) ((m.getPosition()-mSPos) * ((w-1) / distance));

		// "Jiggle" adjustment (for the genotype lines)
		int jiggle = canvas.pX1 % canvas.boxW;
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

		// Clip so that markers under the QTL, but not on screen, aren't drawn
		g.setClip(0, 0, w, getHeight());

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

	// Sets the bounds of the chromosome map (its left and right edges) based on
	// the type of scaling being used to fit it on screen. We have three options
	// 0 - local scaling: mSPos is set to the value of the leftmost marker on
	//     screen, and mEPos is set to the value of the rightmost marker
	// 1 - global scaling: the entire map is shown, from 0 to length
	// 2 - classic scaling: the map's pixel width is set to be as wide as the
	//     width required to show all the markers on the main canvas
	private void setScaling(int xS, int xE)
	{
		// Local scaling
		if (Prefs.visMapScaling == 0)
		{
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
		}

		// Global scaling
		else if (Prefs.visMapScaling == 1)
		{
			mSPos = 0;//canvas.view.getMarker(0).getPosition();
			mEPos = canvas.view.getMarker(canvas.view.getMarkerCount()-1).getPosition();
		}

		// "Classic" Flapjack scaling
		else
		{
			float xScale = (canvas.canvasW-1) / canvas.view.mapLength();

			mSPos = canvas.pX1 / xScale;
			mEPos = canvas.pX2 / xScale;
		}
	}

	Graphics2D getAntiAliasedBufferGraphics()
		{ return aaBuffer.createGraphics(); }

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
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.white);
		g.fillRect(0, 0, w, h);
		render(g, xS, xE);
		g.dispose();

		return image;
	}
}