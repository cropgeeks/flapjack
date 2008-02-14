package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

class CanvasMouseListener extends MouseInputAdapter
{
	private GenotypeCanvas canvas;
	private GenotypePanel gPanel;

	// Deals with interative issues
	private InteractiveHandler iHandler = new InteractiveHandler();
	// Deals with navigation issues
	private NavigationHandler nHandler = new NavigationHandler();


	CanvasMouseListener(GenotypeCanvas canvas, GenotypePanel gPanel)
	{
		this.canvas = canvas;
		this.gPanel = gPanel;

		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
	}

	public void mouseClicked(MouseEvent e)
	{
		if (SwingUtilities.isRightMouseButton(e))
			return;

		if (e.isControlDown() && e.getClickCount() == 2)// && canvas.boxH == 16)
		{
			canvas.removeMouseListener(this);
			new MineSweeper(canvas);
		}

		else if (e.getClickCount() == 2)
		{
			if (canvas.renderMode == 0)
				canvas.renderMode = 1;
			else
				canvas.renderMode = 0;

			System.out.println("Drawing to buffer = " + (canvas.renderMode == 1));

//				if (canvas.renderLive)
//					canvas.setOpaque(true);
//				else
//					canvas.setOpaque(false);

			// setOpaque false is the default in JComponent

			canvas.repaint();
		}

		else if (e.getClickCount() == 1)
		{
			canvas.locked = !canvas.locked;
		}
	}

	public void mousePressed(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			if (e.isControlDown())
				iHandler.mousePressed(e);
			else
				nHandler.mousePressed(e);
		}
	}

	public void mouseReleased(MouseEvent e)
	{
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
		int xIndex = e.getPoint().x / canvas.boxW;
		int yIndex = e.getPoint().y / canvas.boxH;

		gPanel.overRow(xIndex, yIndex);
	}

	public void mouseExited(MouseEvent e)
	{
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
			selectedLine = e.getPoint().y / canvas.boxH;
			selectedMarker = e.getPoint().x / canvas.boxW;
		}

		void mouseReleased(MouseEvent e)
		{
			// If a line was moved during the mouse movement, reset it and update
			// the overview images
			if (selectedLine != -1)
			{
				isLineSelected = false;
				selectedLine = -1;
				OverviewManager.createImage();
			}

			if (selectedMarker != -1)
			{
				isMarkerSelected = false;
				selectedMarker = -1;
				OverviewManager.createImage();
				gPanel.mapCanvas.createImage();
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
				int newLine = e.getPoint().y / canvas.boxH;

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
					canvas.repaint();

					// And ensure wherever the line now is, it's still visible
					canvas.scrollRectToVisible(new Rectangle(x-5, y-5, 10, 10));
					isLineSelected = true;
				}
			}

			// Moving markers...
			if (selectedMarker != -1 && !isLineSelected)
			{
				int newMarker = e.getPoint().x / canvas.boxW;

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
					canvas.repaint();

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