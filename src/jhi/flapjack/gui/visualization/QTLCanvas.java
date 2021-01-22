// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.undo.*;

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
	private double distance;

	// Chromosome map values for the lowest (first) and highest (last) markers
	// currently visible on screen
	private double mSPos, mEPos;

	private BasicStroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT,
		BasicStroke.JOIN_MITER, 10, new float[] { 5,2 }, 0);
	private BasicStroke solid = new BasicStroke(1);

	// Mouse handling variables
	private int xOffset;
	private int mouseOverTrack = -1;
	static QTLInfo mouseOverQTL = null;

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
			QTLTrackOptimiser optimiser = new QTLTrackOptimiser();
			trackSet = optimiser.getTracks(tracks, canvas.view);

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
				while (fgIndex > 0 && trackData.get(fgIndex-1).getMax() >= mSPos)
					fgIndex--;

				// Draw each feature group
				for (FeatureGroup fg : trackData.subList(fgIndex, trackData.size()))
				{
					if (fg.getMin() > mEPos)
						break;

					boolean grouped = fg.size() > 1;

					for (QTLInfo qtlInfo: fg)
						// Don't draw features it they are offscreeen
						if (qtlInfo.max() >= mSPos && qtlInfo.min() <= mEPos)
							drawQTL(g, qtlInfo, trackNum, grouped);
				}
			}

			// Reset the origin
			g.translate(0, -H*trackNum);

			trackNum++;
		}

		// Redraw the feature under the mouse (so it always appears on top)
		if (mouseOverQTL != null)
		{
			g.translate(0, H*mouseOverTrack);

			drawQTL(g, mouseOverQTL, mouseOverTrack, false);
		}
	}

	// Maps between chromosome positions and pixel positions, given the value
	// to map, the distance currently visible on screen, and the s(tart) and
	// e(nd) chromosome positions for that distance. The method's return differs
	// based on the type of map scaling being used (local or global).
	private int getPixelPosition(double mapPos)
	{
		return (int) ((mapPos-mSPos) * ((w-1) / distance));
	}

	// Reverse mapping of the above (pixel on screen to chromosome)
	private double getMapPosition(int pixelPos)
	{
		return ((distance * pixelPos) / (float) (w-1)) + mSPos;
	}


	// Draws an individual feature
	private void drawQTL(Graphics2D g, QTLInfo qtlInfo, int trackNum, boolean grouped)
	{
		int minX = getPixelPosition(qtlInfo.min()) -2;
		int maxX = getPixelPosition(qtlInfo.max()) +2;

		Color c = qtlInfo.getQTL().getDisplayColor();
		Color c1 = c.brighter();
		Color c2 = c.darker();
		g.setPaint(new GradientPaint(0, 5, c1, 0, 10, c2, true));

		// Its interior...
//			g.setPaint(f.getDisplayColor());
//			g.setPaint(new GradientPaint(0, 5, Color.white, 0, 25, Color.lightGray));
		g.fillRect(minX, 5, (maxX-minX), 10);

		// Its outline...
		if (grouped)
			g.setColor(Color.black);
		else if (mouseOverTrack == trackNum && mouseOverQTL == qtlInfo)
			g.setColor(Color.red);
		else
			g.setColor(Color.lightGray);
		g.drawRect(minX, 5, (maxX-minX), 10);

		int x = getPixelPosition(qtlInfo.displayPosition());
		g.drawLine(x, 2, x, 18);
	}

	private int binarySearch(ArrayList<FeatureGroup> trackData, int low, int high, double canvasLeft, double canvasRight)
	{
		if (high < low)
			return -1;

		int mid = low + ((high-low) /2);

		double max = trackData.get(mid).getMax();
		double min = trackData.get(mid).getMin();

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
			detectQTL(e);
		}

		public void mouseExited(MouseEvent e)
		{
			mouseOverTrack = -1;
			mouseOverQTL = null;

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

		private void detectQTL(MouseEvent e)
		{
			if (detectTrack(e) == false)
				return;

			// Work out where (in map distances) the mouse is
//			float mapPos = getMapPosition(e.getX() - xOffset);
//			System.out.println("mapPos=" + mapPos);

			// NOTE: the search is backwards (right to left) as F2.pos > F1.pos
			// will mean F2 is drawn on TOP of F1
			QTLInfo match = null;

			//grab track that the mouse is over
			ArrayList<FeatureGroup> onscreen = trackSet.get(mouseOverTrack);

			//binary search for visible features
			int qtlIndex = binarySearch(onscreen, 0, onscreen.size()-1, mSPos, mEPos);
			if(qtlIndex != -1)
			{
				//linear search to the right-most visible feature
				while(onscreen.get(qtlIndex).getMin() < mEPos)
				{
					if(qtlIndex < onscreen.size()-1)
						qtlIndex++;
					else
						break;
				}

				//iterate over this list until the left-most visible feature is found
				for( int i = onscreen.subList(0, qtlIndex).size(); i >= 0; i--)
				{
					// Features are stored right-most first (for drawing), so
					// search the list backwards
					FeatureGroup fg = onscreen.get(i);
					for (int fIndex = fg.size()-1; fIndex >= 0; fIndex--)
					{
						QTLInfo qtl = fg.get(fIndex);

						int mouseX = e.getX() - xOffset;
						if ((getPixelPosition(qtl.min())-2) <= mouseX && (getPixelPosition(qtl.max())+2) >= mouseX)
//						if (qtl.min() <= mapPos && qtl.max() >= mapPos)
						{
							match = qtl;
							break;
						}
					}
				}
			}

			// Only do a repaint if the mouse has moved on/off something
			if (mouseOverQTL != match)
			{
				mouseOverQTL = match;

				if (match == null)
					gPanel.statusPanel.setQTLDetails(null);
				else
					gPanel.statusPanel.setQTLDetails(match.getQTL());

				mapCanvas.repaint();
				repaint();
			}
		}

		public void mousePressed(MouseEvent e)
		{
			if (mouseOverQTL == null)
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

			double min = mouseOverQTL.min();
			double max = mouseOverQTL.max();
			QTL qtl = mouseOverQTL.getQTL();

			for (MarkerInfo mi: canvas.view.getMarkers())
			{
				Marker m = mi.getMarker();

				// Is this marker under the QTL?
				// *** See IMPORTANT comments in MapCanvas.highlightQTL() ***
				if (m.getRealPosition() >= qtl.getMin() && m.getRealPosition() <= qtl.getMax())
				{
					if (m.getPosition() >= min && m.getPosition() <= max)
					{
						if (undefined)
						{
							state = !mi.getSelected();
							undefined = false;
						}

						mi.selectMarkerAndLinkedMarker(state);
					}
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
			detectQTL(e);
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