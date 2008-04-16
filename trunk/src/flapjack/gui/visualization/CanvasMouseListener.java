package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.gui.Actions;

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

	private boolean isOSX = SystemUtils.isMacOS();


	CanvasMouseListener(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;

		canvasMenu = new CanvasMenu(gPanel, canvas);

		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
	}

	public void mouseClicked(MouseEvent e)
	{
		if (SwingUtilities.isRightMouseButton(e))
			return;

		if (e.isControlDown() && e.isAltDown() && e.getClickCount() == 2)
		{
			canvas.removeMouseListener(this);
			new MineSweeper(canvas);
		}

		else if (e.getClickCount() == 2)
		{
			int markerIndex = canvas.getMarker(e.getPoint());
			new CanvasAnimator(gPanel, canvas, markerIndex);
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
			if (isOSX && e.isMetaDown() || !isOSX && e.isControlDown())
				iHandler.mousePressed(e);
			else
				nHandler.mousePressed(e);
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger())
			canvasMenu.handlePopup(e);

		iHandler.mouseReleased(e);
		nHandler.mouseReleased(e);
	}

	public void mouseDragged(MouseEvent e)
	{
		iHandler.mouseDragged(e);
		nHandler.mouseDragged(e);
	}

	public void mouseMoved(MouseEvent e)
	{
		int xIndex = canvas.getMarker(e.getPoint());
		int yIndex = canvas.getLine(e.getPoint());

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
		private boolean isLineSelected = false;
		private int selectedLine = -1;
		private boolean isMarkerSelected = false;
		private int selectedMarker = -1;

		void mousePressed(MouseEvent e)
		{
			selectedLine = e.getY() / canvas.boxH;
			selectedMarker = e.getX() / canvas.boxW;
		}

		void mouseReleased(MouseEvent e)
		{
			if (selectedMarker != -1)
				gPanel.mapCanvas.createImage();

			if (selectedLine != -1 || selectedMarker != -1)
			{
				isLineSelected = isMarkerSelected = false;
				selectedLine = selectedMarker = -1;

				OverviewManager.createImage();
				canvas.resetBufferedState(true);

				Actions.projectModified();
			}
		}

		void mouseDragged(MouseEvent e)
		{
			int x = e.getPoint().x;
			int y = e.getPoint().y;

			// Moving lines...
			if (selectedLine != -1 && !isMarkerSelected)
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
					isLineSelected = true;
				}
			}

			// Moving markers...
			if (selectedMarker != -1 && !isLineSelected)
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
					isMarkerSelected = true;
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