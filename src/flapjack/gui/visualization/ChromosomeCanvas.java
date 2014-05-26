// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;

class ChromosomeCanvas extends JPanel
{
	private GTViewSet viewSet;

	// Holds the current dimensions of the canvas in an AWT friendly format
	private Dimension dimension = new Dimension();

	// This buffer holds the current viewport (visible) area
	BufferedImage imageViewPort;
	// TRUE if we really MUST redraw, rather than just copying from the buffer
	private boolean redraw = true;

	Point mousePos;

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
	}

	void setView(GTViewSet viewSet)
	{
		this.viewSet = viewSet;

		redraw = true;
		repaint();
	}

//	public Dimension getPreferredSize()
//		{ return dimension; }

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;

		g.setColor(Color.white);
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
		float longestMap = 0;
		for (GTView view: viewSet.getViews())
		{
			if (view.getChromosomeMap().isSpecialChromosome())
				continue;

			if (view.mapLength() > longestMap)
				longestMap = view.mapLength();
		}

		System.out.println("longestMap="+ longestMap);


		NumberFormat nf = NumberFormat.getInstance();
		g.setFont((Font)UIManager.get("Label.font"));

		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

		int y = 25;

		for (GTView view: viewSet.getViews())
		{
			if (view.getChromosomeMap().isSpecialChromosome())
				continue;

			// Pixel width for this map
			int longestMapW = getWidth()-50;
//			if (DONT_SCALE)
//				mapW = getWidth()-50;
			int mapW = (int) ((view.mapLength()/longestMap) * longestMapW);

			// Chromosome name
			String name = view.getChromosomeMap().getName() + ", "
				+ nf.format(view.markerCount()) + " markers";
			g.drawString(name, 25, y);
			y+= 5;

			// Map rectangle
			g.setPaint(new GradientPaint(0, y, Color.LIGHT_GRAY, 0, y+8, Color.WHITE, true));
			g.fillRect(25, y, mapW, 16);
			g.setColor(Color.black);
			g.drawRect(25, y, mapW, 16);


			// Markers
			int markerCount = view.markerCount();
			float xScale = (mapW) / view.mapLength();
			g.setColor(new Color(180, 180, 180));

			// Tracks the x pixel position of the last drawn marker, so any further
			// markers that map to the same position are skipped (for performance)
			int lastX = 0;

			for (int i = 0; i < markerCount; i++)
			{
				Marker m = view.getMarker(i);

				// if (dummy, blank, whatever, etc, skip...

				int pos = 25 + ((int) (m.getPosition() * xScale));
				if (pos != lastX)
				{
					g.drawLine(pos, y-2, pos, y+18);
					lastX = pos;
				}
			}

			y += 30;

			// Chromosome lengths
			g.setColor(Color.black);
			g.drawString(nf.format(0), 25, y);
			String strLength = nf.format(view.mapLength());
			int strWidth = g.getFontMetrics().stringWidth(strLength);
			g.drawString(strLength, 25 + mapW - strWidth, y);

			y+= 35;

//			g.drawString("" + y, 500, y);
		}
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
			repaint();
		}

		public void mouseExited(MouseEvent e)
		{
			mousePos = null;
		}
	}
}