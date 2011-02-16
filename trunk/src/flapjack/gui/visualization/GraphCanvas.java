// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
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
	private MapCanvas mapCanvas;
	private Canvas2D graphCanvas;

	private GraphData graphData;

	private BufferedImage buffer, aaBuffer;
	boolean updateBuffer = true;

	// Last known pX2 for the main canvas - if it's changed, we need to redraw
	private int pX2 = 0;

	// What is the current drawing width
	private int w;

	boolean full = false;

	GraphCanvas(GenotypePanel gPanel, GenotypeCanvas canvas, MapCanvas mapCanvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;
		this.mapCanvas = mapCanvas;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(BORDER, 0, 0, 0));
		add(graphCanvas = new Canvas2D());
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
			int xOffset = gPanel.traitCanvas.getPanelWidth()
				+ gPanel.listPanel.getPanelWidth() + 1;
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
		if (xS > canvas.view.getMarkerCount() || xE > canvas.view.getMarkerCount())
			return;

		g.translate(-canvas.pX1, 0);

		float[] data = graphData.getGraphs().get(10);
		float max = graphData.getMaxes().get(10);
		float scaleBy = h / max;

		for (int i = xS; i <= xE; i++)
		{
			// We need to know which *real* marker this "virtual" markerIndex
			// refers back to
			MarkerInfo mi = canvas.view.getMarkerInfo(i);
			// Then we can get the value for it from the graph data
			float value = data[mi.getIndex()];


			// Work out an intensity value for it (0-255 gives light shades too
			// close to white, so adjust the scale to 25-255)
			float ratio = value / max;
			int alpha = 25 + (int) (((255-25) * (255*ratio)) / 255f);
			g.setColor(new Color(70, 116, 162, alpha));

			int height = (int) (scaleBy * value);
			int x = i*canvas.boxW;
			g.fillRect(x, 0, canvas.boxW, height);
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