// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.gui.*;
import flapjack.gui.visualization.undo.*;

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
	private MarkerSelectionHandler mrkrSelHandler = new MarkerSelectionHandler();
	private LineSelectionHandler lineSelHandler = new LineSelectionHandler();

	private boolean isOSX = SystemUtils.isMacOS();

	CanvasMouseListener(GenotypePanel gPanel, GenotypeCanvas canvas, WinMain winMain)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;

		canvasMenu = new CanvasMenu(canvas, winMain);

		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addMouseWheelListener(this);
	}

	private boolean isMetaClick(MouseEvent e)
	{
		return isOSX && e.isMetaDown() || !isOSX && e.isControlDown();
	}

	public void mouseClicked(MouseEvent e)
	{
		if (SwingUtilities.isRightMouseButton(e))
			return;

		if (isMetaClick(e) && e.isAltDown() && e.getClickCount() == 2)
		{
			canvas.removeMouseListener(this);
			new MineSweeper(canvas, this);
		}

		// CTRL+dbl-click marker hiding
		else if (isMetaClick(e) && e.getClickCount() == 2 && Prefs.guiMouseMode == Constants.MARKERMODE)
		{
			int markerIndex = canvas.getMarker(e.getPoint());
			new HideLMAnimator(gPanel, markerIndex, true);
		}
		// CTRL+dbl-click line hiding
		else if (isMetaClick(e) && e.getClickCount() == 2 && Prefs.guiMouseMode == Constants.LINEMODE)
		{
			int lineIndex = canvas.getLine(e.getPoint());
			new HideLMAnimator(gPanel, lineIndex, false);
		}

		// Click zooming
		else if (e.getClickCount() == 2)
			gPanel.getController().clickZoom(e);
	}

	public void mousePressed(MouseEvent e)
	{
		canvas.overlays.remove(canvas.crosshair);
		canvas.repaint();

		if (e.isPopupTrigger())
			canvasMenu.handlePopup(e);

		if (SwingUtilities.isLeftMouseButton(e))
		{
			if (isMetaClick(e))
				iHandler.mousePressed(e);
			else if (Prefs.guiMouseMode == Constants.NAVIGATION)
				nHandler.mousePressed(e);
			else if (Prefs.guiMouseMode == Constants.MARKERMODE)
				mrkrSelHandler.mousePressed(e);
			else if (Prefs.guiMouseMode == Constants.LINEMODE)
				lineSelHandler.mousePressed(e);
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if (!canvas.overlays.contains(canvas.crosshair))
		{
			canvas.overlays.add(canvas.crosshair);
			canvas.repaint();
		}

		if (e.isPopupTrigger())
			canvasMenu.handlePopup(e);

		nHandler.mouseReleased(e);
		iHandler.mouseReleased(e);
		mrkrSelHandler.mouseReleased(e);
		lineSelHandler.mouseReleased(e);
	}

	public void mouseDragged(MouseEvent e)
	{
		nHandler.mouseDragged(e);

		if (isMetaClick(e))
			iHandler.mouseDragged(e);
		else if (Prefs.guiMouseMode == Constants.MARKERMODE)
			mrkrSelHandler.mouseDragged(e);
		else if (Prefs.guiMouseMode == Constants.LINEMODE)
			lineSelHandler.mouseDragged(e);

		mouseMoved(e);
	}

	public void mouseMoved(MouseEvent e)
	{
		int xIndex = canvas.getMarker(e.getPoint());
		int yIndex = canvas.getLine(e.getPoint());

		if (canvasMenu.isShowingMenu() == false)
			gPanel.overRow(xIndex, yIndex);
	}

	public void mouseEntered(MouseEvent e)
	{
		gPanel.statusPanel.setForMainUse();
	}

	public void mouseExited(MouseEvent e)
	{
		// Remove highlighting if the mouse has left the canvas, but not if it's
		// over the canvas but "off" the canvas due to being in the popup menu
		if (canvasMenu.isShowingMenu() == false)
			gPanel.overRow(-1, -1);
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		int shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		// CTRL/CMD down: do canvas zooming
		if (e.getModifiers() == shortcut && Prefs.visLinkSliders)
		{
			int currentValue = gPanel.statusPanel.getZoomY();
			gPanel.statusPanel.setZoomY(currentValue - e.getWheelRotation());
		}

		// Otherwise, do canvas scrolling
		else
		{
			CanvasController controller = gPanel.getController();

			JScrollBar sBar = null;
			if (controller.getVBar().isVisible())
				sBar = controller.getVBar();
			else if (controller.getHBar().isVisible())
				sBar = controller.getHBar();

			if (sBar != null)
			{
				int notches = e.getWheelRotation();
				int value = sBar.getValue();
				int units = 5 * sBar.getUnitIncrement();

				sBar.setValue(value + (notches * units));
			}
		}
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

			// Don't allow selection of dummy markers
			if (canvas.view.getMarker(selectedMarker).dummyMarker())
				selectedMarker = -1;
			else
			{
				movedMarkersState = new MovedMarkersState(canvas.view,
				RB.getString("gui.visualization.MovedMarkersState.movedMarkers"));
				movedMarkersState.createUndoState();
			}

			// Create new states on initial mouse down
			movedLinesState = new MovedLinesState(canvas.viewSet,
				RB.getString("gui.visualization.MovedLinesState.movedLines"));
			movedLinesState.createUndoState();
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

					gPanel.mapCanvas.updateBuffer = true;
					for (GraphCanvas gc : gPanel.graphCanvas)
						gc.updateBuffer = true;
					canvas.view.setMarkersOrdered(false);
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
				else if (newLine >= canvas.view.lineCount())
					newLine = canvas.view.lineCount()-1;

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
				else if (newMarker >= canvas.view.markerCount())
					newMarker = canvas.view.markerCount()-1;

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

					for (GraphCanvas gc : gPanel.graphCanvas)
					{
						gc.updateBuffer = true;
						gc.repaint();
					}

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

				gPanel.getController().moveBy(diffX, diffY);
			}
		}
	}

	private class MarkerSelectionHandler
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
			if (index < 0 || index > canvas.view.markerCount())
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
				else if (newMarker >= canvas.view.markerCount())
					newMarker = canvas.view.markerCount()-1;

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

					// And ensure wherever the marker is now, it's still visible
					canvas.scrollRectToVisible(new Rectangle(x-5, y-5, 10, 10));
				}
			}
		}
	}

	private class LineSelectionHandler
	{
		private int selectedLine = -1;
		private int firstSelected = -1;
		private boolean selectionState;

		private SelectedLinesState lineStates;

		void mousePressed(MouseEvent e)
		{
			// What line is at the location clicked on
			int index = canvas.getLine(e.getPoint());

			// Check that the index is valid
			if (index < 0 || index > canvas.view.lineCount())
				return;

			lineStates = new SelectedLinesState(canvas.view,
				RB.getString("gui.visualization.SelectedLinesState.selected"));
			lineStates.createUndoState();

			firstSelected = selectedLine = index;
			selectionState = canvas.view.toggleLineState(selectedLine);

			canvas.resetBufferedState(false);
		}

		void mouseReleased(MouseEvent e)
		{
			if (selectedLine == -1)
				return;

			selectedLine = -1;
			canvas.resetBufferedState(true);

			lineStates.createRedoState();
			gPanel.addUndoState(lineStates);
		}

		void mouseDragged(MouseEvent e)
		{
			int x = e.getPoint().x;
			int y = e.getPoint().y;

			if (selectedLine != -1)
			{
				int newLine = canvas.getLine(e.getPoint());

				if (newLine < 0)
					newLine = 0;
				else if (newLine >= canvas.view.lineCount())
					newLine = canvas.view.lineCount()-1;

				if (newLine != selectedLine)
				{
					// Moving up...
					if (newLine < selectedLine)
						for (int i = selectedLine; i >= newLine; i--)
						{
							if (i <= firstSelected)
								canvas.view.setLineState(i, selectionState);
							else
								canvas.view.setLineState(i, !selectionState);
						}

					// Moving down...
					else if (newLine > selectedLine)
						for (int i = selectedLine; i <= newLine; i++)
						{
							if (i >= firstSelected)
								canvas.view.setLineState(i, selectionState);
							else
								canvas.view.setLineState(i, !selectionState);
						}

					// Update the view
					selectedLine = newLine;
					canvas.resetBufferedState(false);

					// And ensure wherever the line is now, it's still visible
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