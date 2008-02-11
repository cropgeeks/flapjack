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

	private Point dragPoint;

	private int selectedLine = -1;

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
				selectedLine = e.getPoint().y / canvas.boxH;
			else
				dragPoint = e.getPoint();
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		dragPoint = null;
		selectedLine = -1;

		gPanel.viewUpdated(true);

		canvas.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public void mouseDragged(MouseEvent e)
	{
		int x = e.getPoint().x;
		int y = e.getPoint().y;

		// Moving lines...
		if (selectedLine != -1)
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
				// Move the line
				canvas.view.moveLine(selectedLine, newLine);
				selectedLine = newLine;

				// Update the view
				gPanel.viewUpdated(false);

				// And ensure wherever the line now is, it's still visible
				canvas.scrollRectToVisible(new Rectangle(x-5, y-5, 10, 10));
			}
		}

		// Dragging the canvas...
		if (dragPoint != null)
		{
			canvas.setCursor(new Cursor(Cursor.HAND_CURSOR));

			int diffX = dragPoint.x - x;
			int diffY = dragPoint.y - y;

			gPanel.moveBy(diffX, diffY);
		}
	}

	public void mouseMoved(MouseEvent e)
	{
		int x = e.getPoint().x;
		int y = e.getPoint().y;

		int xIndex = x / canvas.boxW;
		int yIndex = y / canvas.boxH;

		gPanel.overRow(xIndex, yIndex);
	}

	public void mouseExited(MouseEvent e)
	{
		gPanel.overRow(-1, -1);
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