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

/**
 * Canvas for rendering feature/QTL data. Note that an assumption is made here
 * that ALL data in the feature lists/vectors will be pre-sorted into ascending
 * left-to-right order, that is, a QTL whose minimum value is 5 will be found
 * in the list BEFORE a QTL with a minimum value of 6.
 */
class QTLCanvas extends JPanel implements PropertyChangeListener
{
	// The height of a SINGLE track
	private static final int H = 20;
	// Border height of the component itself
	private static final int BORDER = 5;

	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;
	private MapCanvas mapCanvas;
	private Canvas2D qtlCanvas;

	// Quick reference to the data (multiple tracks of features)
	ArrayList<ArrayList<FeatureGroup>> trackSet;

	// How many tracks are currently on screen?
	private int trackCount = 0;
	// What is the current drawing width
	private int w;
	// Over what chromosome distance
	private float distance;

	// Chromosome map values for the lowest (first) and highest (last) markers
	// currently visible on screen
	private float mSPos, mEPos;

	private BasicStroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT,
		BasicStroke.JOIN_MITER, 10, new float[] { 5,2 }, 0);
	private BasicStroke solid = new BasicStroke(1);

	// Mouse handling variables
	private int xOffset;
	private int mouseOverTrack = -1;
	static Feature mouseOverFeature = null;

	boolean full = false;

	QTLCanvas(GenotypePanel gPanel, GenotypeCanvas canvas, MapCanvas mapCanvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;
		this.mapCanvas = mapCanvas;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(BORDER, 0, 0, 0));
		add(qtlCanvas = new Canvas2D());

		QTLMouseListener ml = new QTLMouseListener();
		qtlCanvas.addMouseListener(ml);
		qtlCanvas.addMouseMotionListener(ml);
		qtlCanvas.setMinimumSize((new Dimension(0, H)));
	}

	public void propertyChange(PropertyChangeEvent e)
	{
		JSplitPane qtlSplitter = (JSplitPane) e.getSource();

		if (Prefs.visShowQTLCanvas && qtlSplitter.getDividerLocation() > BORDER)
		{
			Prefs.guiQTLSplitterLocation = qtlSplitter.getDividerLocation();
		 	updateCanvasSize(false);
		}
	}

	// Responds to either canvas size changes, or data (track) changes. If
	// required, the features will be re-organised over the available tracks.
	void updateCanvasSize(boolean forceUpdate)
	{
		if (canvas.view == null)
			return;

		int height = Prefs.guiQTLSplitterLocation - BORDER;
		int tracks = height / H;

		if (forceUpdate || tracks != trackCount && tracks > 0)
		{
			QTLTrackOptimiser optimiser = new QTLTrackOptimiser(canvas.viewSet.getDataSet());
			trackSet = optimiser.getTracks(tracks, canvas.view.getChromosomeMap());

			trackCount = tracks;
		}
	}

	private class Canvas2D extends JPanel
	{
		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics;

			w = canvas.pX2 - canvas.pX1 + 1;

			// Calculate the required offset and width
			xOffset = gPanel.traitCanvas.getPanelWidth()
				+ gPanel.listPanel.getPanelWidth() + 1;

			g.setClip(xOffset, 0, w, getHeight());
			g.translate(xOffset, 0);


			g.setColor(getBackground());
			g.fillRect(0, 0, w, getHeight());

			mSPos = mapCanvas.mSPos;
			mEPos = mapCanvas.mEPos;

			drawTracks(g);
		}
	}

	// Loops over the data, drawing each track
	private void drawTracks(Graphics2D g)
	{
		distance = mEPos - mSPos;

		int trackNum = 0;

		for (ArrayList<FeatureGroup> trackData: trackSet)
		{
			// Move the graphics origin to correct position for this track
			g.translate(0, H*trackNum);

			g.setColor(Color.lightGray);
			g.setStroke(dashed);
			g.drawLine(0, 10, w, 10);
			g.setStroke(solid);

			// Binary search to find *any* feature group currently on screen...
			int fgIndex = binarySearch(trackData, 0, (trackData.size()-1), mSPos, mEPos);

			if (fgIndex != -1)
			{
				// ...then linear search to find the left-most feature group
				while (trackData.get(fgIndex).getMax() > mSPos)
				{
					if (fgIndex > 0)
						fgIndex--;
					else
						break;
				}

				// Draw each feature group
				loop:
				for (FeatureGroup fg : trackData.subList(fgIndex, trackData.size()))
				{
					boolean grouped = fg.size() > 1;

					for (Feature feature: fg)
					{
						// Stop drawing features if we're offscreen on the rhs
						if (feature.getMin() > mEPos)
							break loop;

						drawFeature(g, feature, trackNum, grouped);
					}
				}
			}

			// Reset the origin
			g.translate(0, -H*trackNum);

			trackNum++;
		}

		// Redraw the feature under the mouse (so it always appears on top)
		if (mouseOverFeature != null)
		{
			g.translate(0, H*mouseOverTrack);

			drawFeature(g, mouseOverFeature, mouseOverTrack, false);
		}
	}

	// Maps between chromosome positions and pixel positions, given the value
	// to map, the distance currently visible on screen, and the s(tart) and
	// e(nd) chromosome positions for that distance. The method's return differs
	// based on the type of map scaling being used (local or global).
	private int getPixelPosition(float mapPos)
	{
		return (int) ((mapPos-mSPos) * ((w-1) / distance));
	}

	// Reverse mapping of the above (pixel on screen to chromosome)
	private float getMapPosition(int pixelPos)
	{
		return ((distance * pixelPos) / (float) (w-1)) + mSPos;
	}


	// Draws an individual feature
	private void drawFeature(Graphics2D g, Feature f, int trackNum, boolean grouped)
	{
		int minX = getPixelPosition(f.getMin());
		int maxX = getPixelPosition(f.getMax());

		Color c = f.getDisplayColor();
		Color c1 = c.brighter();
		Color c2 = c.darker();
		g.setPaint(new GradientPaint(0, 5, c1, 0, 10, c2, true));

		// Its interior...
//			g.setPaint(f.getDisplayColor());
//			g.setPaint(new GradientPaint(0, 5, Color.white, 0, 25, Color.lightGray));
		g.fillRect(minX, 5, (maxX-minX+1), 10);

		// Its outline...
		if (grouped)
			g.setColor(Color.black);
		else if (mouseOverTrack == trackNum && mouseOverFeature == f)
			g.setColor(Color.red);
		else
			g.setColor(Color.lightGray);
		g.drawRect(minX, 5, (maxX-minX+1), 10);

		if (f instanceof QTL)
		{
			int x = getPixelPosition(((QTL)f).getPosition());
			g.drawLine(x, 2, x, 18);
		}
	}

	private int binarySearch(ArrayList<FeatureGroup> trackData, int low, int high, float canvasLeft, float canvasRight)
	{
		if (high < low)
			return -1;

		int mid = low + ((high-low) /2);

		float max = trackData.get(mid).getMax();
		float min = trackData.get(mid).getMin();

		if (max < canvasLeft)
			return binarySearch(trackData, mid+1, high, canvasLeft, canvasRight);
		else if (min > canvasRight)
			return binarySearch(trackData, low, mid-1, canvasLeft, canvasRight);
		else
			return mid;
	}

	private class QTLMouseListener extends MouseInputAdapter
	{
		private boolean isOSX = SystemUtils.isMacOS();

		public void mouseEntered(MouseEvent e)
		{
			gPanel.statusPanel.setForFeatureUse();
		}

		public void mouseMoved(MouseEvent e)
		{
			detectFeature(e);
		}

		public void mouseExited(MouseEvent e)
		{
			mouseOverTrack = -1;
			mouseOverFeature = null;

			gPanel.statusPanel.setQTLDetails(null);
			gPanel.statusPanel.setForMainUse();

			mapCanvas.repaint();
			qtlCanvas.repaint();
		}

		private boolean detectTrack(MouseEvent e)
		{
			// Which track is the mouse over?
			mouseOverTrack = e.getY() / H;

			if (mouseOverTrack < 0 || mouseOverTrack >= trackSet.size())
				return false;
			else
				return true;
		}

		private void detectFeature(MouseEvent e)
		{
			if (detectTrack(e) == false)
				return;

			// Work out where (in map distances) the mouse is
			float mapPos = getMapPosition(e.getX() - xOffset);

			// NOTE: the search is backwards (right to left) as F2.pos > F1.pos
			// will mean F2 is drawn on TOP of F1
			Feature match = null;

			//grab track that the mouse is over
			ArrayList<FeatureGroup> onscreen = trackSet.get(mouseOverTrack);

			//binary search for visible features
			int feature = binarySearch(onscreen, 0, onscreen.size()-1, mSPos, mEPos);
			if(feature != -1)
			{
				//linear search to the right-most visible feature
				while(onscreen.get(feature).getMin() < mEPos)
				{
					if(feature < onscreen.size()-1)
					{
						feature++;
					}
					else
						break;
				}

				//iterate over this list until the left-most visible feature is found
				for( int i = onscreen.subList(0, feature).size(); i >= 0; i--)
				{
					// Features are stored right-most first (for drawing), so
					// search the list backwards
					FeatureGroup fg = onscreen.get(i);
					for (int fIndex = fg.size()-1; fIndex >= 0; fIndex--)
					{
						Feature f = fg.get(fIndex);

						if (f.getMin() <= mapPos && f.getMax() >= mapPos)
						{
							match = f;
							break;
						}
					}
				}
			}

			// Only do a repaint if the mouse has moved on/off something
			if (mouseOverFeature != match)
			{
				mouseOverFeature = match;

				if (match == null)
					gPanel.statusPanel.setQTLDetails(null);
				else if (match instanceof QTL)
					gPanel.statusPanel.setQTLDetails((QTL)match);

				mapCanvas.repaint();
				repaint();
			}
		}

		public void mousePressed(MouseEvent e)
		{
			if (mouseOverFeature == null)
				return;

			// Used to track the FIRST marker found and set the states of
			// all the other ones to the same value
			boolean undefined = true;
			// Which will be this state...
			boolean state = false;

			// Create the undo state
			SelectedMarkersState markerStates = new SelectedMarkersState(
				canvas.view, RB.getString("gui.visualization.SelectedMarkersState.selected"));
			markerStates.createUndoState();

			float min = mouseOverFeature.getMin();
			float max = mouseOverFeature.getMax();

			for (MarkerInfo mi: canvas.view.getMarkers())
			{
				// Is this marker under the QTL?
				if (mi.getMarker().getPosition() >= min && mi.getMarker().getPosition() <= max)
				{
					if (undefined)
					{
						state = !mi.getSelected();
						undefined = false;
					}

					mi.setSelected(state);
				}
			}

			// If markers had their states toggled, then set an undo/redo
			if (undefined == false)
			{
				markerStates.createRedoState();
				gPanel.addUndoState(markerStates);

				// And switch to marker mode
				Flapjack.winMain.mEdit.editMode(Constants.MARKERMODE);
			}
		}

		public void mouseReleased(MouseEvent e)
		{
			detectFeature(e);
		}
	}

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
		drawTracks(g);
		g.dispose();

		return image;
	}
}