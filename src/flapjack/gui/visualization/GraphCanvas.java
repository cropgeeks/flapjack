// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.visualization.undo.*;

import scri.commons.gui.*;

class GraphCanvas extends JPanel
{
	private static final int h = 45;
	// Border height of the component itself
	private static final int BORDER = 5;

	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;
	private Canvas2D graphCanvas;

	GraphData graphData;
	int graphIndex = 10;

	private BufferedImage buffer, aaBuffer;
	boolean updateBuffer = true;

	// Last known pX2 for the main canvas - if it's changed, we need to redraw
	private int pX2 = 0;

	// What is the current drawing width
	private int w;
	// And canvas offset amount
	int xOffset;

	boolean full = false;

	GraphCanvas(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;

		setLayout(new BorderLayout());
//		setBorder(BorderFactory.createEmptyBorder(BORDER, 0, 0, 0));
		add(graphCanvas = new Canvas2D());

		new GraphCanvasML(gPanel, this);
	}

	private class Canvas2D extends JPanel
	{
		Canvas2D()
		{
			setPreferredSize(new Dimension(0, h));
		}

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics;

			graphData = canvas.view.getChromosomeMap().getGraphData();
			if (graphData == null)
				return;

			// If the user has scrolled, then we need to redraw the buffer(s)
			if (pX2 != canvas.pX2)
			{
				updateBuffer = true;
				pX2 = canvas.pX2;
			}

			// Calculate the required offset and width
			xOffset = gPanel.traitCanvas.getPanelWidth() + gPanel.listPanel.getPanelWidth() + 1;
			g.translate(xOffset, 0);

			// Update the back buffer (if it needs redrawn)
			if (updateBuffer)
				paintBuffer();

			if (GraphCanvasAAThread.bufferAvailable)
				g.drawImage(aaBuffer, 0, 0, null);
			else
				g.drawImage(buffer, 0, 0, null);
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


		int xS = canvas.xS;
		int xE = canvas.xE;
		render(g, xS, xE);

		g.dispose();
		updateBuffer = false;

		// Start the AA-thread making its "nicer" image for display (once ready)
		new GraphCanvasAAThread(this, xS, xE);
	}

	void render(Graphics2D g, int xS, int xE)
	{
		g.translate(-canvas.pX1, 0);

		if (xS > canvas.view.getMarkerCount() || xE > canvas.view.getMarkerCount())
			return;

		// If we're drawing a line graph, then translate by half a position
		if (Prefs.guiGraphStyle == 1)
			g.translate(canvas.boxW/2, 0);

		// See if we can render one extra marker to each side
		if (xS > 0)	xS--;
		if (xE < canvas.view.getMarkerCount()-1) xE++;

		// Retrieve the data
		float[] data = graphData.getGraphs().get(graphIndex);
		Float prev = null;

		for (int i = xS; i <= xE; i++)
		{
			// We need to know which *real* marker this "virtual" markerIndex
			// refers back to
			MarkerInfo mi = canvas.view.getMarkerInfo(i);
			// Then we can get the value for it from the graph data
			float value = data[mi.getIndex()];

			// Draw co-ordindates:
			int y = (int) (value * h);
			int x = i*canvas.boxW;


			// Draw a BAR at each position
			if (Prefs.guiGraphStyle == 0)
			{
				// Work out an intensity value for it (0-255 gives light shades too
			// close to white, so adjust the scale to 25-255)
				int alpha = 25 + (int) (((255-25) * (255*value)) / 255f);
				g.setColor(new Color(70, 116, 162, alpha));

				g.fillRect(x, 0, canvas.boxW, y);
			}

			// Draw a LINE at each position (from prevPos to current)
			else
			{
				g.setColor(new Color(70, 116, 162));

				if (prev != null)
					g.drawLine(x-canvas.boxW, (int)(prev * h), x, y);
				else
					g.drawLine(x, y, x, y);

				prev = value;
			}
		}
	}

	Graphics2D getAntiAliasedBufferGraphics()
		{ return aaBuffer.createGraphics(); }

	BufferedImage createSavableImage(boolean full)
		throws Exception
	{
		// Render width if we're just saving the current view
		w = canvas.pX2 - canvas.pX1 + 1;
		int h = getHeight();

		// Or the entire map
		if (full)
			w = canvas.canvasW;

		BufferedImage image = (BufferedImage) createImage(w>0 ? w:1, h>0 ? h:1);

		Graphics2D g = image.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, w, h);
//		drawGraph(g);
		g.dispose();

		return image;
	}
}