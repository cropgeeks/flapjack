package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.gui.*;

import scri.commons.gui.*;

class CanvasMouseListener extends MouseInputAdapter
{
	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	// Deals with pop-up menus
	private CanvasMenu canvasMenu;

	// Deals with interative issues
	private InteractiveHandler iHandler = new InteractiveHandler();
	// Deals with navigation issues
	private NavigationHandler nHandler = new NavigationHandler();
	// Deals with selection issues
	private SelectionHandler sHandler = new SelectionHandler();

	private boolean isOSX = SystemUtils.isMacOS();


	CanvasMouseListener(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;

		canvasMenu = new CanvasMenu(canvas);

		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
	}

	private boolean isMetaClick(MouseEvent e)
	{
		return isOSX && e.isMetaDown() || !isOSX && e.isControlDown();
	}

	public void mouseClicked(MouseEvent e)
	{
		if (SwingUtilities.isRightMouseButton(e))
			return;

		if (e.isControlDown() && e.isAltDown() && e.getClickCount() == 2)
		{
			canvas.removeMouseListener(this);
			new MineSweeper(canvas, this);
		}

		else if (e.getClickCount() == 2 && Prefs.guiMouseMode == Constants.MARKERMODE)
		{
			int markerIndex = canvas.getMarker(e.getPoint());
			new HideMarkerAnimator(gPanel, markerIndex);
		}

//		else if (e.getClickCount() == 1)
//			canvas.locked = !canvas.locked;
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.isPopupTrigger())
			canvasMenu.handlePopup(e);

		if (SwingUtilities.isLeftMouseButton(e))
		{
			if (isMetaClick(e))
				iHandler.mousePressed(e);
			else if (Prefs.guiMouseMode == Constants.NAVIGATION)
				nHandler.mousePressed(e);
			else if (Prefs.guiMouseMode == Constants.MARKERMODE)
				sHandler.mousePressed(e);
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger())
			canvasMenu.handlePopup(e);

		nHandler.mouseReleased(e);
		iHandler.mouseReleased(e);
		sHandler.mouseReleased(e);
	}

	public void mouseDragged(MouseEvent e)
	{
		nHandler.mouseDragged(e);

		if (isMetaClick(e))
			iHandler.mouseDragged(e);
		else
			sHandler.mouseDragged(e);
	}

	public void mouseMoved(MouseEvent e)
	{
		int xIndex = canvas.getMarker(e.getPoint());
		int yIndex = canvas.getLine(e.getPoint());

		// Uncomment for experimental effect of dynamic line similarity highting
//		Actions.vizColorLineSim.actionPerformed(null);

		if (canvasMenu.isShowingMenu() == false)
			gPanel.overRow(xIndex, yIndex);
	}

	public void mouseExited(MouseEvent e)
	{
		// Remove highlighting if the mouse has left the canvas, but not if it's
		// over the canvas but "off" the canvas due to being in the popup menu
		if (canvasMenu.isShowingMenu() == false)
			gPanel.overRow(-1, -1);
	}

	/** Inner class to handle interactive mouse events (moving lines etc). */
	private class InteractiveHandler
	{
		private boolean isLineMoving = false;
		private int selectedLine = -1;
		private boolean isMarkerMoving = false;
		private int selectedMarker = -1;

		private MovedLinesState movedLinesState;
		private MovedMarkersState movedMarkersState;

		void mousePressed(MouseEvent e)
		{
			selectedLine = e.getY() / canvas.boxH;
			selectedMarker = e.getX() / canvas.boxW;

			// Create new states on initial mouse down
			movedLinesState = new MovedLinesState(canvas.viewSet,
				RB.getString("gui.visualization.MovedLinesState.movedLines"));
			movedLinesState.createUndoState();

			movedMarkersState = new MovedMarkersState(canvas.view,
				RB.getString("gui.visualization.MovedMarkersState.movedMarkers"));
			movedMarkersState.createUndoState();
		}

		void mouseReleased(MouseEvent e)
		{
			if (isLineMoving || isMarkerMoving)
			{
				if (isLineMoving)
				{
					// If lines were moved, track their new state
					movedLinesState.createRedoState();
					gPanel.addUndoState(movedLinesState);
				}

				if (isMarkerMoving)
				{
					// If markers were moved, track their new state
					movedMarkersState.createRedoState();
					gPanel.addUndoState(movedMarkersState);

					// The map only needs to be updated if markers moved
					gPanel.mapCanvas.createImage();
				}

				isLineMoving = isMarkerMoving = false;
				selectedLine = selectedMarker = -1;

				// The overview needs updated if lines *or* markers moved
				OverviewManager.createImage();
				canvas.resetBufferedState(true);
			}
		}

		void mouseDragged(MouseEvent e)
		{
			int x = e.getPoint().x;
			int y = e.getPoint().y;

			// Moving lines...
			if (selectedLine != -1 && isMarkerMoving == false)
			{
				// this.selectedLine is its old position...this will be its new one
				int newLine = y / canvas.boxH;

				// Force the new line position to be either at the top or the bottom
				// of the dataset, IF the cursor has gone beyond the limits
				if (newLine < 0)
					newLine = 0;
				else if (newLine >= canvas.view.getLineCount())
					newLine = canvas.view.getLineCount()-1;

				if (newLine != selectedLine)
				{
					// Moving up...
					if (newLine < selectedLine)
						for (int i = selectedLine; i > newLine; i--)
						{
							canvas.view.moveLine(i, i-1);
							gPanel.listPanel.moveLine(i, i-1);
						}
					// Moving down...
					else if (newLine > selectedLine)
						for (int i = selectedLine; i < newLine; i++)
						{
							canvas.view.moveLine(i, i+1);
							gPanel.listPanel.moveLine(i, i+1);
						}

					// Update the view
					selectedLine = newLine;
					canvas.resetBufferedState(false);

					// And ensure wherever the line now is, it's still visible
					canvas.scrollRectToVisible(new Rectangle(x-5, y-5, 10, 10));
					isLineMoving = true;
				}
			}

			// Moving markers...
			if (selectedMarker != -1 && isLineMoving == false)
			{
				int newMarker = x / canvas.boxW;

				if (newMarker < 0)
					newMarker = 0;
				else if (newMarker >= canvas.view.getMarkerCount())
					newMarker = canvas.view.getMarkerCount()-1;

				if (newMarker != selectedMarker)
				{
					// Moving left...
					if (newMarker < selectedMarker)
						for (int i = selectedMarker; i > newMarker; i--)
							canvas.view.moveMarker(i, i-1);
					// Moving right...
					else if (newMarker > selectedMarker)
						for (int i = selectedMarker; i < newMarker; i++)
							canvas.view.moveMarker(i, i+1);

					// Update the view
					selectedMarker = newMarker;
					canvas.resetBufferedState(false);

					// And ensure wherever the marker now is, it's still visible
					canvas.scrollRectToVisible(new Rectangle(x-5, y-5, 10, 10));
					isMarkerMoving = true;
				}
			}
		}
	}

	/** Inner class to handle navigation mouse events (dragging the canvas etc). */
	private class NavigationHandler
	{
		private Point dragPoint;

		void mousePressed(MouseEvent e)
		{
			dragPoint = e.getPoint();
		}

		void mouseReleased(MouseEvent e)
		{
			// Reset any dragging variables
			dragPoint = null;
			canvas.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}

		void mouseDragged(MouseEvent e)
		{
			// Dragging the canvas...
			if (dragPoint != null)
			{
				canvas.setCursor(new Cursor(Cursor.HAND_CURSOR));

				int diffX = dragPoint.x - e.getPoint().x;
				int diffY = dragPoint.y - e.getPoint().y;

				gPanel.moveBy(diffX, diffY);
			}
		}
	}

	private class SelectionHandler
	{
		private int selectedMarker = -1;
		private int firstSelected = -1;
		private boolean selectionState;

		private SelectedMarkersState markerStates;

		void mousePressed(MouseEvent e)
		{
			// What marker is at the location clicked on
			int index = canvas.getMarker(e.getPoint());

			// Check that the index is valid
			if (index < 0 || index > canvas.view.getMarkerCount())
				return;

			markerStates = new SelectedMarkersState(canvas.view,
				RB.getString("gui.visualization.SelectedMarkersState.selected"));
			markerStates.createUndoState();

			firstSelected = selectedMarker = index;
			selectionState = canvas.view.toggleMarkerState(selectedMarker);

			canvas.resetBufferedState(false);
		}

		void mouseReleased(MouseEvent e)
		{
			if (selectedMarker == -1)
				return;

			selectedMarker = -1;
			canvas.resetBufferedState(true);

			markerStates.createRedoState();
			gPanel.addUndoState(markerStates);
		}

		void mouseDragged(MouseEvent e)
		{
			int x = e.getPoint().x;
			int y = e.getPoint().y;

			if (selectedMarker != -1)
			{
				int newMarker = canvas.getMarker(e.getPoint());

				if (newMarker < 0)
					newMarker = 0;
				else if (newMarker >= canvas.view.getMarkerCount())
					newMarker = canvas.view.getMarkerCount()-1;

				if (newMarker != selectedMarker)
				{
					// Moving left...
					if (newMarker < selectedMarker)
						for (int i = selectedMarker; i >= newMarker; i--)
						{
							if (i <= firstSelected)
								canvas.view.setMarkerState(i, selectionState);
							else
								canvas.view.setMarkerState(i, !selectionState);
						}

					// Moving right...
					else if (newMarker > selectedMarker)
						for (int i = selectedMarker; i <= newMarker; i++)
						{
							if (i >= firstSelected)
								canvas.view.setMarkerState(i, selectionState);
							else
								canvas.view.setMarkerState(i, !selectionState);
						}

					// Update the view
					selectedMarker = newMarker;
					canvas.resetBufferedState(false);

					// And ensure wherever the marker now is, it's still visible
					canvas.scrollRectToVisible(new Rectangle(x-5, y-5, 10, 10));
				}
			}
		}
	}
}

/*
class CanvasToolTip extends JToolTip
{
	Color bgColor = (Color) UIManager.get("ToolTip.background");// new JLabel().createToolTip().getBackground();

	int w, h;

	CanvasToolTip()
	{
		w = 100;
		h = 25;
	}

//	public Dimension getPreferredSize()
//	{
//		return new Dimension(w, h);
//	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;

		g.setPaint(new GradientPaint(0, 0, Color.red.brighter(), w, h, Color.red.darker()));

		Rectangle2D.Float r = null;

//		RoundRectangle2D.Float r = new RoundRectangle2D.Float(1, 1, w-1, h-1, 7, 7);
//		Rectangle2D.Float r = new Rectangle2D.Float(0, 0, w, h);

		r = new Rectangle2D.Float(0, 0, w, h);

//		g.fill(r);
	}
}
*/