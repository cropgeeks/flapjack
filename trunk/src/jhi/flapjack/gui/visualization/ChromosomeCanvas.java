// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

class ChromosomeCanvas extends JPanel
{
	private GTViewSet viewSet;
	private ChromosomeCanvasKey key;
	private ChromosomeCanvasGraph graph;

	// A list of views (chromosomes) holding information to draw. These may be
	// references to real GTView objects, or to user-built custom maps
	ArrayList<GTView> views;

	// Holds the current dimensions of the canvas in an AWT friendly format
	private Dimension dimension = new Dimension();

	// This buffer holds the current viewport (visible) area
	BufferedImage imageViewPort;
	// TRUE if we really MUST redraw, rather than just copying from the buffer
	private boolean redraw = true;

	Point mousePos;

	private int maxMarkers;
	private int minMarkers;
	ArrayList<float[]> viewMarkersPerPixel;
	private ArrayList<Integer> viewMapWidths;
	double cmPerPixel;

	private NumberFormat nf = NumberFormat.getInstance();

	ChromosomeCanvas()
	{
		// This panel has to detect changes to its size, and recreate the image
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				redraw = true;
				repaint();
			}
		});

		new CanvasMouseListener(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
	}

	void setHelpers(ChromosomeCanvasKey key, ChromosomeCanvasGraph graph)
	{
		this.key = key;
		this.graph = graph;
	}

	void setView(GTViewSet viewSet)
	{
		this.viewSet = viewSet;

		viewSet.getCustomMaps().initForDisplay(viewSet);
		views = viewSet.getCustomMaps().getAllViews();

		dimension = new Dimension(this.getWidth(), views.size() * 70);

		redraw = true;
		repaint();
	}

	public Dimension getPreferredSize()
		{ return dimension; }

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;

		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (redraw)
		{
			long s = System.currentTimeMillis();
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
			System.out.println("Render time: " + (System.currentTimeMillis() - s));
		}

		g.drawImage(imageViewPort, 0, 0, null);
		redraw = false;
	}

	private void renderCanvas(Graphics2D g)
	{
		double longestMap = 0;
		for (GTView view: views)
			longestMap = Math.max(longestMap, view.mapLength());

		// Work out how many centimorgans each pixel represents
		int longestMapW = getWidth()-50;
		cmPerPixel = longestMap / longestMapW;

		NumberFormat nf = NumberFormat.getInstance();
		g.setFont((Font)UIManager.get("Label.font"));

		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

		int y = 25;

		calculateMarkersPerPixel(longestMap, longestMapW);
		// Once we know the new values, the key can be updated
		key.redraw();

		int viewNo = 0;
		for (GTView view: views)
		{
			// Pixel width for this map
//			if (DONT_SCALE)
//				mapW = getWidth()-50;
			int mapW = (int) ((view.mapLength()/longestMap) * longestMapW);

			g.setColor(Color.BLACK);
			// Chromosome name
			String name = view.getChromosomeMap().getName() + ": "
				+ RB.format("gui.visualization.ChromosomeCanvas.markers", nf.format(view.markerCount()));
			g.drawString(name, 25, y);
			y+= 5;

			float[] markersPerPixel = viewMarkersPerPixel.get(viewNo);

			for (int i=0; i < mapW; i++)
			{
				// Percentage value of highest value within the block
				float percent = markersPerPixel[i] / (float) maxMarkers;
				// Work out an intensity value for it (0-255 gives light shades too
				// close to white, so adjust the scale to 25-255)
				int alpha = 0 + (int) (((255-0) * (255*percent)) / 255f);

				// Then draw a line of colour x percentage
				g.setColor(new Color(70, 116, 162, alpha));
				g.drawLine(i+25, y, i+25, y+16);
			}
			g.setColor(Color.black);
			g.drawRect(25, y, mapW, 16);


			// Markers
//			int markerCount = view.markerCount();
//			float xScale = (mapW) / view.mapLength();
//			g.setColor(new Color(180, 180, 180));
//
//			// Tracks the x pixel position of the last drawn marker, so any further
//			// markers that map to the same position are skipped (for performance)
//			int lastX = 0;

//			for (int i = 0; i < markerCount; i++)
//			{
//				Marker m = view.getMarker(i);
//
//				// if (dummy, blank, whatever, etc, skip...
//
//				int pos = 25 + ((int) (m.getPosition() * xScale));
//				if (pos != lastX)
//				{
//					g.drawLine(pos, y-2, pos, y+18);
//					lastX = pos;
//				}
//			}

			y += 30;

			// Chromosome lengths:
			// Left (always 0) number
			g.setColor(Color.black);
			g.drawString(nf.format(0), 25, y);
			String strLength = nf.format(view.mapLength());

			// Right (cm length) number
			int strWidth = g.getFontMetrics().stringWidth(strLength);
			int strPos = 25 + mapW - strWidth;
			if (strPos < 25) strPos = 40;
			g.drawString(strLength, strPos, y);

			y+= 35;

			viewNo++;
//			g.drawString("" + y, 500, y);
		}
	}

	// This is called by the main rendering code when it needs to draw the
	// chromosome maps. This menthod calls out (one view at a time) to the
	// other calculate method that does the actual work per chromosome, and is
	// also used (in real-time) by the graph display code whenever a graph is
	// clicked on
	private void calculateMarkersPerPixel(double longestMap, int longestMapW)
	{
		maxMarkers = 0;
		minMarkers = Integer.MAX_VALUE;
		viewMarkersPerPixel = new ArrayList<float[]>();
		viewMapWidths = new ArrayList<Integer>();

		for (GTView view: views)
		{
			// Create an array of the appropriate length for this map
			// In this case the number of bins is equal to the number of pixels
			// that will be used to draw the chromosome
			int nBins = (int) ((view.mapLength()/longestMap) * longestMapW);
			viewMapWidths.add(nBins);

			float[] markersPerPixel = calculateMarkersPerPixel(view, cmPerPixel, nBins);
			viewMarkersPerPixel.add(markersPerPixel);
		}
	}

	// nBins will either be the number of pixels used to draw the chromosome in
	// question, or it will be a fixed size (eg 500) if this code is called by
	// ChromosomeGraphCanvas which needs greater resolution
	float[] calculateMarkersPerPixel(GTView view, double cmPerPixel, int nBins)
	{
		float[] markersPerPixel = new float[nBins];

		int startMarker = 0;
		// <= as we start at 1, not 0 and we need values for the full width
		for (int pixel = 1; pixel <= nBins; pixel++)
		{
			int markerCount = 0;
			double pixelPosition = pixel * cmPerPixel;
			for (int i = startMarker; i < view.markerCount(); i++)
			{
				Marker m = view.getMarker(i);

				// If the marker is located within the centimorgans which
				// make up the current pixel update the markerCount and start
				// marker
				if (m.getPosition() <= pixelPosition)
				{
					markerCount++;
					startMarker++;
				}

				else
					break;
			}

			// Save the value to the array and update maxMarkers if required
			markersPerPixel[pixel-1] = markerCount;
			maxMarkers = Math.max(maxMarkers, markerCount);
			minMarkers = Math.min(minMarkers, markerCount);
		}


		return markersPerPixel;
	}

	class CanvasMouseListener extends MouseInputAdapter
	{
		CanvasMouseListener(ChromosomeCanvas canvas)
		{
			canvas.addMouseListener(this);
			canvas.addMouseMotionListener(this);
		}

		public void mouseMoved(MouseEvent e)
		{
			mousePos = e.getPoint();

			setToolTipText(null);

			for (int i=0; i < viewMarkersPerPixel.size(); i++)
			{
				// Over chromosome in the y-axis
				if (mousePos.getY() > 30 + i*70 && mousePos.getY() < 30 + i*70 + 16)
				{
					int pixel = (int)mousePos.getX()-25;
					// Over chromosome in the x-axis
					if (pixel > 0 && pixel < viewMapWidths.get(i))
						setToolTipText("<html>" + RB.format("gui.visualization.ChromosomeCanvas.positonTooltip",  nf.format(pixel * cmPerPixel)) +
							"<br>" + RB.format("gui.visualization.ChromsomeCanvas.densityTooltip", viewMarkersPerPixel.get(i)[pixel]));
				}
			}
			repaint();
		}

		public void mouseExited(MouseEvent e)
		{
			mousePos = null;
		}

		public void mouseClicked(MouseEvent e)
		{
			for (int i=0; i < viewMarkersPerPixel.size(); i++)
			{
				// Over chromosome in the y-axis
				if (mousePos.getY() > 30 + i*70 && mousePos.getY() < 30 + i*70 + 16)
				{
					graph.display(i);
				}
			}
		}
	}

	int maxMarkerCount()
		{ return maxMarkers; }

	int minMarkerCount()
		{ return minMarkers; }
}