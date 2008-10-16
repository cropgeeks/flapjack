package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;

class QTLCanvas extends JPanel
{
	// The height of a SINGLE track
	private static final int h = 20;

	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;
	private MapCanvas mapCanvas;
	private Canvas2D qtlCanvas;

	// Quick reference to the data (multiple tracks of features)
	Vector<Vector<Feature>> featureList;
	// Another reference, but this time JUST to the features that are onscreen
	Vector<LinkedList<Feature>> onscreenList;

	// Scaling factor to convert between pixels and map positions
	private float xScale;
	private int xOffset;

	// Mouse handling variables
	private int mouseOverTrack = -1;
	static Feature mouseOverFeature = null;

	QTLCanvas(GenotypePanel gPanel, GenotypeCanvas canvas, MapCanvas mapCanvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;
		this.mapCanvas = mapCanvas;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		add(qtlCanvas = new Canvas2D());

		QTLMouseListener ml = new QTLMouseListener();
		qtlCanvas.addMouseListener(ml);
		qtlCanvas.addMouseMotionListener(ml);
	}

	void updateCanvasSize()
	{
		featureList = canvas.view.getChromosomeMap().getFeatures();

		qtlCanvas.setPreferredSize(new Dimension(0, h * featureList.size()));
		qtlCanvas.revalidate();
	}

	private class Canvas2D extends JPanel
	{
		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics;

			// Calculate the required offset and width to draw from
			xOffset = gPanel.traitCanvas.getPanelWidth()
				+ gPanel.listPanel.getPanelWidth() + 1;
			int width = (canvas.pX2-canvas.pX1);

			g.setClip(xOffset, 0, width, getHeight());
			g.translate(xOffset - canvas.pX1, 0);

			xScale = canvas.canvasW / canvas.view.mapLength();

			drawTracks(g);
		}

		// Loops over the data, drawing each track
		private void drawTracks(Graphics2D g)
		{
			onscreenList = new Vector<LinkedList<Feature>>();

			int trackNum = 0;
			for (Vector<Feature> trackData: featureList)
			{
				// Move the graphics origin DOWN to the next track
				g.translate(0, trackNum * (getHeight() / featureList.size()));

				BasicStroke s = new BasicStroke(1, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 10, new float[] { 5,2 }, 0);

				g.setColor(Color.lightGray);
				g.setStroke(s);
				g.drawLine(0, 10, canvas.canvasW, 10);
				g.setStroke(new BasicStroke(1));

				onscreenList.add(new LinkedList<Feature>());

				// Draw each feature
				for (Feature f: trackData)
				{
					int minX = Math.round(xScale * f.getMin());
					int maxX = Math.round(xScale * f.getMax());

					// If this feature even on screen?
					if (maxX < canvas.pX1)	// Keep skipping lft->rht until we find some
						continue;
					if (minX > canvas.pX2)	// Once QTLs are offscreen-rht, might as well quit
						break;

					onscreenList.get(trackNum).add(f);

					// Its interior...
					g.setPaint(new Color(255, 255, 255));
					g.fillRect(minX, 5, (maxX-minX+1), 10);

					// Its outline...
					if (mouseOverTrack == trackNum && mouseOverFeature == f)
						g.setColor(Color.red);
					else
						g.setColor(Color.lightGray);
					g.drawRect(minX, 5, (maxX-minX+1), 10);

					if (f instanceof QTL)
					{
						int x = Math.round(xScale * ((QTL)f).getPosition());
						g.drawLine(x, 2, x, 18);
					}
				}

				trackNum++;
			}
		}
	}

	private class QTLMouseListener extends MouseInputAdapter
	{
		public void mouseMoved(MouseEvent e)
		{
			// Actual X position is X minus the non-drawnable offset, plus the
			// distance the canvas is scrolled to the right
			int x = e.getX() - xOffset + canvas.pX1;
			// Luckily Y is nice and easy
			int y = e.getY();

			// Which track is the mouse over?
			mouseOverTrack = y / h;

			// Work out where (in map distances) the mouse is
			float mapPos = x / xScale;

			// Then search the ONSCREEN features to see if it's over any of them
			Feature match = null;
			for (Feature f: onscreenList.get(mouseOverTrack))
			{
				if (f.getMin() <= mapPos && f.getMax() >= mapPos)
				{
					match = f;
					break;
				}
			}

			// Only do a repaint if the mouse has moved on/off something
			if (mouseOverFeature != match)
			{
				mouseOverFeature = match;

				mapCanvas.repaint();
				repaint();
			}
		}

		public void mouseExited(MouseEvent e)
		{
			mouseOverTrack = -1;
			mouseOverFeature = null;

			mapCanvas.repaint();
			repaint();
		}
	}
}