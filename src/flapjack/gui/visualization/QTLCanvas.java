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
	Vector<Vector<Feature>> featureList;
	// Another reference, but this time JUST to the features that are onscreen
	Vector<Vector<Feature>> onscreenList;

	// Scaling factor to convert between pixels and map positions
	private float xScale;
	private int xOffset;

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
		featureList = canvas.view.getChromosomeMap().getFeatures();

		qtlCanvas.setPreferredSize(new Dimension(0, h * featureList.size()));
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
			onscreenList = new Vector<Vector<Feature>>();
			int trackNum = 0;

			for (Vector<Feature> trackData: featureList)
			{
				// Move the graphics origin DOWN to the next track
				if (trackNum > 0)
					g.translate(0, h);

				BasicStroke s = new BasicStroke(1, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 10, new float[] { 5,2 }, 0);

				g.setColor(Color.lightGray);
				g.setStroke(s);
				g.drawLine(0, 10, canvas.canvasW, 10);
				g.setStroke(new BasicStroke(1));

				onscreenList.add(new Vector<Feature>());

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
//					g.setPaint(new GradientPaint(0, 5, Color.white, 0, 25, Color.lightGray));
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

				trackNum++;
			}
		}
	}

	private class QTLMouseListener extends MouseInputAdapter
	{
		private boolean isOSX = SystemUtils.isMacOS();
		private int oldTrack;

		public void mouseMoved(MouseEvent e)
		{
			detectFeature(e);
		}

		public void mouseExited(MouseEvent e)
		{
			mouseOverTrack = -1;
			mouseOverFeature = null;

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

			if (mouseOverTrack < 0 || mouseOverTrack >= featureList.size())
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
			Vector<Feature> onscreen = onscreenList.get(mouseOverTrack);
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
				featureList.get(oldTrack).remove(featureToMove);

				Vector<Feature> newTrack = featureList.get(mouseOverTrack);
				int preSize = newTrack.size();
				// Search for the best place to insert it into the new track
				for (int i = 0; i < newTrack.size(); i++)
					// Either before an existing element
					if (newTrack.get(i).getMin() >= featureToMove.getMin())
					{
						newTrack.add(i, featureToMove);
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