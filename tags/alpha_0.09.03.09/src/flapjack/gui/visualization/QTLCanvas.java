package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

/**
 * Canvas for rendering feature/QTL data. Note that an assumption is made here
 * that ALL data in the feature lists/vectors will be pre-sorted into ascending
 * left-to-right order, that is, a QTL whose minimum value is 5 will be found
 * in the list BEFORE a QTL with a minimum value of 6.
 */
class QTLCanvas extends JPanel
{
	// The height of a SINGLE track
	private static final int h = 20;

	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;
	private MapCanvas mapCanvas;
	private Canvas2D qtlCanvas;

	// Quick reference to the data (multiple tracks of features)
	Vector<Vector<Feature>> trackSet;
	// Another reference, but this time JUST to the features that are onscreen
	Vector<Vector<Feature>> screenSet;

	// Scaling factor to convert between pixels and map positions
	private float xScale;
	private int xOffset;

	private BasicStroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT,
		BasicStroke.JOIN_MITER, 10, new float[] { 5,2 }, 0);
	private BasicStroke solid = new BasicStroke(1);

	// Mouse handling variables
	private int mouseOverTrack = -1;
	static Feature mouseOverFeature = null;
	private Feature featureToMove = null;

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
		trackSet = canvas.view.getChromosomeMap().getTrackSet();

		qtlCanvas.setPreferredSize(new Dimension(0, h * trackSet.size()));
		qtlCanvas.revalidate();
		qtlCanvas.repaint();
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
			screenSet = new Vector<Vector<Feature>>();
			int trackNum = 0;

			for (Vector<Feature> trackData: trackSet)
			{
				// Move the graphics origin to correct position for this track
				g.translate(0, h*trackNum);

				g.setColor(Color.lightGray);
				g.setStroke(dashed);
				// Workaround due to JAVA BUG ID 6574155
				// (we should be able to do just drawLine(0, 10, width, 10)
				if (canvas.canvasW < canvas.pX2)
					g.drawLine(canvas.pX1, 10, canvas.canvasW, 10);
				else
					g.drawLine(canvas.pX1, 10, canvas.pX2, 10);
				g.setStroke(solid);

				screenSet.add(new Vector<Feature>());

				// Draw each feature
				for (Feature f: trackData)
				{
					// Should this feature be drawn?
					if (f.isVisible() == false || f.isAllowed() == false)
						continue;

					int minX = Math.round(xScale * f.getMin());
					int maxX = Math.round(xScale * f.getMax());

					// Is this feature even on screen?
					if (maxX < canvas.pX1)	// Keep skipping lft->rht until we find some
						continue;
					if (minX > canvas.pX2)	// Once QTLs are offscreen-rht, might as well quit
						break;

					screenSet.get(trackNum).add(f);

					drawFeature(g, f, minX, maxX, trackNum);
				}

				// Reset the origin
				g.translate(0, -h*trackNum);

				trackNum++;
			}

			// Redraw the feature under the mouse (so it always appears on top)
			if (mouseOverFeature != null)
			{
				g.translate(0, h*mouseOverTrack);

				int minX = Math.round(xScale * mouseOverFeature.getMin());
				int maxX = Math.round(xScale * mouseOverFeature.getMax());

				drawFeature(g, mouseOverFeature, minX, maxX, mouseOverTrack);
			}
		}

		// Draws an individual feature
		private void drawFeature(Graphics2D g, Feature f, int minX, int maxX, int trackNum)
		{
			Color c = f.getDisplayColor();
			Color c1 = c.brighter();
			Color c2 = c.darker();
			g.setPaint(new GradientPaint(0, 5, c1, 0, 10, c2, true));

			// Its interior...
//			g.setPaint(f.getDisplayColor());
//			g.setPaint(new GradientPaint(0, 5, Color.white, 0, 25, Color.lightGray));
			g.fillRect(minX, 5, (maxX-minX+1), 10);

			// Its outline...
			if (featureToMove == f)
				g.setColor(Color.blue);
			else if (mouseOverTrack == trackNum && mouseOverFeature == f)
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
	}

	private class QTLMouseListener extends MouseInputAdapter
	{
		private boolean isOSX = SystemUtils.isMacOS();
		private int oldTrack;

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
			// Actual X position is X minus the non-drawnable offset, plus the
			// distance the canvas is scrolled to the right
			int x = e.getX() - xOffset + canvas.pX1;
			// Luckily Y is nice and easy
			int y = e.getY();

			// Which track is the mouse over?
			mouseOverTrack = y / h;

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
			float mapPos = (e.getX()-xOffset+canvas.pX1) / xScale;

			// Then search the ONSCREEN features to see if it's over any of them
			// NOTE: the search is backwards (right to left) as F2.pos > F1.pos
			// will mean F2 is drawn on TOP of F1
			Feature match = null;
			Vector<Feature> onscreen = screenSet.get(mouseOverTrack);
			for (int i = onscreen.size()-1; i >= 0; i--)
			{
				Feature f = onscreen.get(i);

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

				if (match == null)
					gPanel.statusPanel.setQTLDetails(null);
				else if (match instanceof QTL)
					gPanel.statusPanel.setQTLDetails((QTL)match);

				mapCanvas.repaint();
				repaint();
			}
		}

		public void mouseDragged(MouseEvent e)
		{
			if (detectTrack(e) == false)
				return;

			if (featureToMove != null && mouseOverTrack != oldTrack)
			{
				// Remove the feature from the old track
				trackSet.get(oldTrack).remove(featureToMove);

				Vector<Feature> newTrack = trackSet.get(mouseOverTrack);
				int preSize = newTrack.size();
				// Search for the best place to insert it into the new track
				for (int i = 0; i < newTrack.size(); i++)
					// Either before an existing element
					if (newTrack.get(i).getMin() >= featureToMove.getMin())
					{
						newTrack.add(i, featureToMove);
						Actions.projectModified();

						break;
					}
				// Or on the end...
				if (newTrack.size() != (preSize+1))
					newTrack.add(featureToMove);

				oldTrack = mouseOverTrack;

				qtlCanvas.repaint();
			}
		}

		public void mousePressed(MouseEvent e)
		{
			if (mouseOverFeature == null)
				return;

			// Meta click operation - we're MOVING a feature
			if (isMetaClick(e))
			{
				oldTrack = mouseOverTrack;
				featureToMove = mouseOverFeature;
				mouseOverFeature = null;

				qtlCanvas.repaint();
				mapCanvas.repaint();
			}

			// Non meta click - we're SELECTING a feature
			else
			{
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
		}

		public void mouseReleased(MouseEvent e)
		{
			featureToMove = null;
			detectFeature(e);
		}

		private boolean isMetaClick(MouseEvent e)
		{
			return isOSX && e.isMetaDown() || !isOSX && e.isControlDown();
		}
	}
}