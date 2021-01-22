// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import static java.awt.RenderingHints.*;
import java.awt.image.*;
import java.text.*;
import java.util.stream.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

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
	double mSPos, mEPos;

	// What is the current drawing width
	private int w;

	private MapCanvasML mapCanvasML;

	MapCanvas(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		add(new Canvas2D());

		mapCanvasML = new MapCanvasML(this, gPanel);
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
		private Font font = null;

		Canvas2D()
		{
			setPreferredSize(new Dimension(0, h));

			if (SystemUtils.isMacOS())
				font = new Font("Dialog", Font.PLAIN, 10);
			else
				font = new Font("Dialog", Font.PLAIN, 11);
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
			// This clipping is only needed for the "live" mouse-over paints
			g.setClip(0, 0, canvas.pX2Max-canvas.pX1+1, getHeight());

			// Update the back buffer (if it needs redrawn)
			if (updateBuffer)
				paintBuffer();

			if (MapCanvasAAThread.bufferAvailable)
				g.drawImage(aaBuffer, 0, 0, null);
			else
				g.drawImage(buffer, 0, 0, null);

			g.setFont(font);

			highlightMarker(g);
			highlightQTL(g);
			drawChromosomePosition(g);
		}

		private void drawChromosomePosition(Graphics2D g)
		{
			Integer mousePos = mapCanvasML.mousePos;
			if (mousePos == null || canvas.view.markerCount() == 0)
				return;

			// What is the width of the actual drawing canvas?
			float canvasWidth = canvas.pX2-canvas.pX1+1;
			// How far along this width (as a ratio) is the mouse position?
			float posAsRatio = mousePos / canvasWidth;
			// Now work out the length (in cM) of what's on screen
			double chromosomeLength = mEPos - mSPos;


			// The actual position of the mouse is now just an offset + the
			// ratio of this length
			double chromosomePos = mSPos + (posAsRatio * chromosomeLength);

			// Don't draw if the mouse is beyond the (visible) canvas extents
			if (chromosomePos < mSPos || chromosomePos > mEPos)
				return;

			// If we're on a super chromosome we need to fudge the chromosomePos
			// back into the real coordinates for each individual chromosome
			double realChromosomePos = getChromosomePosOnSuperChromosome(chromosomePos);

			g.setColor(Color.red);
			g.drawLine(mousePos, 12, mousePos, 22);

			String str = "";
			if (canvas.view.getChromosomeMap().isSpecialChromosome())
				str = nf.format(realChromosomePos) + " (" + getChromosomeNameOnSuperChromosome(chromosomePos) + ")";
			else
				nf.format(chromosomePos);

			int strWidth = g.getFontMetrics().stringWidth(str);
			g.drawString(str, getPosition(mousePos, strWidth), 8);
		}
	}

	private int getChromosomeForPosition(double chromosomePos)
	{
		int acc = 0;
		for (int chr = 0; chr < canvas.viewSet.getViews().size(); chr++)
		{
			double mapLength = canvas.viewSet.getView(chr).mapLength();
			if (chromosomePos > acc + mapLength)
				acc += mapLength;
			else
				return chr;
		}

		return 0;
	}

	private double getChromosomePosOnSuperChromosome(double chromosomePos)
	{
		int chromosome = getChromosomeForPosition(chromosomePos);
		double offset = IntStream.range(0, chromosome)
			.mapToDouble(i -> canvas.viewSet.getView(i).mapLength())
			.sum();

		return chromosomePos - offset;
	}

	private String getChromosomeNameOnSuperChromosome(double chromosomePos)
	{
		int chromosome = getChromosomeForPosition(chromosomePos);

		return canvas.viewSet.getView(chromosome).getChromosomeMap().getName();
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
		if (canvas.view.markerCount() == 0)
			return;

		int y = 12;
		int yH = 10;

		setScaling(xS, xE);

		// Draw the maps of the special chromosome with alternating background
		// colors.
		if (canvas.view.getChromosomeMap().isSpecialChromosome())
		{
			double start = 0;
			double dS = 0;
			for (int i=0; i < canvas.viewSet.getViews().size(); i++)
			{
				double mapLength = canvas.viewSet.getView(i).mapLength();

				// Set background color (alternate on map index)
				Color bg = i % 2 == 0 ? Color.WHITE : new Color(206,221,235);
				g.setColor(bg);

				double distance = mEPos - mSPos;
				int dEnd = (int) ((start + mapLength - mSPos) * ((w-1) / distance));

				g.fillRect((int)dS, y, dEnd, yH);

				start += mapLength;
				dS = dEnd > 0 ? dEnd : 0;
			}
		}
		else
		{
			// Draw the white rectangle representing the map
			g.setColor(Color.white);
			g.fillRect(0, y, w - 1, yH);
		}

		g.setColor(Color.lightGray);

		// Local/global maps always fit the screen
		if (Prefs.visMapScaling != Constants.CLASSIC)
			g.drawRect(0, y, w - 1, yH);
			// Classic maps fit the total width (and have "edges")
		else
			g.drawRect(-canvas.pX1, y, canvas.canvasW - 1, yH);

		for (int i = xS; i <= xE; i++)
			renderMarker(g, i, xS, false);

		// TODO 05/09/16: This only draws the first and last notches of the
		// super chromosome and all other notches at map boundaries are cheated
		// in by rendering where there is a dummy marker, instead of doing
		// anything clever.
		// Start/End notches
		int y1 = y-5;
		int y2 = y+yH+5;
		if (Prefs.visMapScaling == Constants.GLOBAL)
		{
			g.drawLine(0, y1, 0, y2);
			g.drawLine(w-1, y1, w-1, y2);
		}
		else if (Prefs.visMapScaling == Constants.LOCAL)
		{
			// Similar to global, but only draw the notches if the start/end
			// markers are at the very start/end of the map
			if (mSPos == 0)
				g.drawLine(0, y1, 0, y2);
			if (mEPos == canvas.view.mapLength())
				g.drawLine(w-1, y1, w-1, y2);
		}
		else if (Prefs.visMapScaling == Constants.CLASSIC)
		{
			int x1 = -canvas.pX1;
			int x2 = canvas.canvasW-canvas.pX1-1;

			g.drawLine(x1, y1, x1, y2);
			g.drawLine(x2, y1, x2, y2);
		}
	}

	private void renderMarker(Graphics2D g, int i, int xS, boolean text)
	{
		Marker m = canvas.view.getMarker(i);
		int markerIndex = canvas.view.getMarkerInfo(i).getIndex();

		double distance = mEPos - mSPos;
		int xMap = (int) ((m.getPosition()-mSPos) * ((w-1) / distance));

		// "Jiggle" adjustment (for the genotype lines)
		int jiggle = canvas.pX1 % canvas.boxW;
		int xBox = (int) ((i-xS) * canvas.boxW + (canvas.boxW/2)) - jiggle;

		if (m.dummyMarker() == false)
		{
			Color current = g.getColor();

			if (g.getColor() != Color.red && canvas.qtlHash.contains(markerIndex))
				g.setColor(new Color(128, 128, 128));

			g.drawLine(xMap, 12, xMap, 22);
			g.drawLine(xMap, 22, xBox, h-9);

			if (canvas.qtlHash.contains(markerIndex))
			{
				int xBoxL = (int) ((i-xS) * canvas.boxW) - jiggle;

				g.setColor(Color.yellow);
				g.fillRect(xBoxL, h-7, canvas.boxW-1, 5);
				g.setColor(Color.lightGray);
				g.drawRect(xBoxL, h-7, canvas.boxW-1, 5);
			}
			else
				g.drawLine(xBox, h-9, xBox, h-5);

			g.setColor(current);
		}
		// Draw map tick to denote boundary between maps on the super chromosome
		// view
		else
		{
			g.drawLine(xMap, 6, xMap, 22);
		}

		if (text && m.dummyMarker() == false)
		{
			String str = m.getName() + "  (" + nf.format(m.getRealPosition()) + ")";
			int strWidth = g.getFontMetrics().stringWidth(str);

			g.drawString(str, getPosition(xMap, strWidth), 8);
		}
	}

	// Highlights the marker under the mouse
	private void highlightMarker(Graphics2D g)
	{
		// Change to red, and redraw the currently highlighted one
		if (mrkrIndex >= 0 && mrkrIndex < canvas.view.markerCount())
		{
			g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
			g.setColor(Color.red);

			int xS = canvas.pX1 / canvas.boxW;
			renderMarker(g, mrkrIndex, xS, true);
		}
	}

	private void highlightQTL(Graphics2D g)
	{
		// If no feature is selected, just quit now
		if (QTLCanvas.mouseOverQTL == null)
			return;

		g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		g.setColor(Color.red);

		QTLInfo qtlInfo = QTLCanvas.mouseOverQTL;
		QTL qtl = qtlInfo.getQTL();
		double min = qtlInfo.min();
		double max = qtlInfo.max();

		// Where should it be drawn
		double distance = mEPos - mSPos;
		int xMap = (int) (((min + ((max-min)/2)-mSPos)) * ((w-1) / distance));


		// And what should be drawn
		String str = qtl.getName() + "  (";
		str += nf.format(qtl.getPosition()) + ": ";
		str += nf.format(qtl.getMin()) + "-" +nf.format(qtl.getMax()) + ")";
		int strWidth = g.getFontMetrics().stringWidth(str);

		g.drawString(str, getPosition(xMap, strWidth), 8);

		// Now see which markers are "under" this feature, and highlight them
		int mkrCount = canvas.view.markerCount();
		int xS = canvas.pX1 / canvas.boxW;

		for (int i = 0; i < mkrCount; i++)
		{
			Marker m = canvas.view.getMarker(i);

			// Is this marker under the QTL?
			// This requires two checks: 1st: is the marker's actual position
			// within the QTL's actual region, 2nd: in the case of a super-chromosome
			// markers from multiple original chromosomes might meet case 1, so
			// we need to also check against the virtual positions.
			// We can't *just* do check 2 because chromosomes "touch" in the super
			// chromosome so a QTL starting a zero can cover the last marker of
			// the previous chromosome too
			if (m.getRealPosition() >= qtl.getMin() && m.getRealPosition() <= qtl.getMax())
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
		if (Prefs.visMapScaling == Constants.LOCAL)
		{
			mSPos = canvas.view.getMarker(xS).getPosition();
			mEPos = canvas.view.getMarker(xE).getPosition();

			// If markers have been moved xS and xE won't actually be the markers
			// with the left- and right-most map positions
			if (canvas.view.getMarkersOrdered() == false)
			{
				for (int i = xS; i <= xE; i++)
				{
					double pos = canvas.view.getMarker(i).getPosition();
					if (pos < mSPos)
						mSPos = pos;
					else if (pos > mEPos)
						mEPos = pos;
				}
			}
		}

		// Global scaling
		else if (Prefs.visMapScaling == Constants.GLOBAL)
		{
			mSPos = 0;//canvas.view.getMarker(0).getPosition();
//			mEPos = canvas.view.getMarker(canvas.view.markerCount()-1).getPosition();
			mEPos = canvas.view.mapLength();
		}

		// "Classic" Flapjack scaling
		else
		{
			double xScale = (canvas.canvasW-1) / canvas.view.mapLength();

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
			xE = canvas.view.markerCount()-1;
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