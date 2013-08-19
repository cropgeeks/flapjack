// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;

import java.awt.image.*;
import scri.commons.gui.*;

class TraitCanvas extends JPanel
{
	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;
	private Canvas2D traitCanvas;

	private int boxW = 10;
	private int w = 0;

	TraitCanvas(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;

		setBackground(Color.RED);

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(1, 5, 0, 5));
		add(traitCanvas = new Canvas2D());
	}

	int getPanelWidth()
	{
		return isVisible() ? getWidth() : 0;
	}

	// Decides whether to show this panel or not, based on a) are there traits
	// that *can* be shown, and b) does the user want to see the panel
	void determineVisibility()
	{
		int traitCount = 0;
		if (canvas.viewSet != null)
			traitCount = canvas.viewSet.getTraits().length;

		w = boxW * traitCount;

		setSize(w, 0);
		setVisible(traitCount > 0 && Prefs.visShowTraitCanvas);
	}

	private class Canvas2D extends JPanel
	{
		Canvas2D()
		{
			TraitCanvasML mt = new TraitCanvasML(gPanel, canvas, boxW);

			addMouseListener(mt);
			addMouseMotionListener(mt);
		}

		public Dimension getPreferredSize()
			{ return new Dimension(w, 0); }

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics;

			// This translation "jiggles" the start of the top row
			int jiggle = canvas.pY1 % canvas.boxH;
			g.translate(0, -jiggle);
			g.setClip(0, jiggle, w, canvas.pY2-canvas.pY1+1);

			int yS = canvas.pY1 / canvas.boxH;
			int yE = canvas.pY2 / canvas.boxH;

			render(g, yS, yE);
		}
	}

	private void render(Graphics2D g, int yS, int yE)
	{
		int[] tIndex = canvas.viewSet.getTraits();
		int boxH = canvas.boxH;

		Color col1 = Prefs.visColorHeatmapLow;
		int[] c1 = new int[] { col1.getRed(), col1.getGreen(), col1.getBlue() };
		Color col2 = Prefs.visColorHeatmapHigh;
		int[] c2 = new int[] { col2.getRed(), col2.getGreen(), col2.getBlue() };

		for (int i = 0; i < tIndex.length; i++)
		{
			// If there's no index for this location, skip it
			if (tIndex[i] == -1)
				continue;

			for (int yIndex = yS, y = 0; yIndex <= yE; yIndex++, y += boxH)
			{
				Line line = canvas.view.getLine(yIndex);
				// Skip dummy lines (they don't have trait values)
				if (canvas.view.isDummyLine(yIndex) || canvas.view.isSplitter(yIndex))
					continue;

				TraitValue tv = line.getTraitValues().get(tIndex[i]);

				// Or if the trait is undefined, just skip it
				if (tv.isDefined() == false)
					continue;

				float f1 = (float) (1.0 - tv.getNormal());
				float f2 = (float) tv.getNormal();

				g.setColor(new Color(
          			(int) (f1 * c1[0] + f2 * c2[0]),
      				(int) (f1 * c1[1] + f2 * c2[1]),
      				(int) (f1 * c1[2] + f2 * c2[2])));

				g.fillRect(i*boxW, y, boxW, boxH);
			}
		}
	}

	// Generates an image (of the correct size) to save either the current view
	// or the entire view, and then renders the data onto it
	BufferedImage createSavableImage(boolean createFull)
	{
		try
		{
			int yS = canvas.pY1 / canvas.boxH;
			int yE = canvas.pY2 / canvas.boxH;
			int h = canvas.pY2-canvas.pY1+1;

			if (createFull)
			{
				yS = 0;
				yE = canvas.view.lineCount() - 1;
				h = canvas.canvasH;
			}

			BufferedImage buffer = (BufferedImage) createImage(w, h);

			Graphics2D g = buffer.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, w, h);

			if (createFull == false)
				g.translate(0, -canvas.pY1 % canvas.boxH);

			render(g, yS, yE);
			g.dispose();

			return buffer;
		}
		catch (Throwable t) { return null; }
	}
}