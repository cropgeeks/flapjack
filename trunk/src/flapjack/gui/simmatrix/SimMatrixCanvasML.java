// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.simmatrix;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

class SimMatrixCanvasML extends MouseInputAdapter
{
	private SimMatrixPanel sPanel;
	private CanvasController controller;
	private SimMatrixCanvas sCanvas;

	private Point dragPoint;

	SimMatrixCanvasML(SimMatrixPanel sPanel, CanvasController controller)
	{
		this.sPanel = sPanel;
		this.controller = controller;

		sCanvas = sPanel.getSimMatrixCanvas();

		// Then add listeners and overlays to the canvas
		sCanvas.addMouseListener(this);
		sCanvas.addMouseMotionListener(this);
		sCanvas.addMouseWheelListener(this);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		dragPoint = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// Reset any dragging variables
		dragPoint = null;
		sCanvas.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		// Dragging the canvas...
		if (dragPoint != null)
		{
			sCanvas.setCursor(new Cursor(Cursor.HAND_CURSOR));

			int diffX = dragPoint.x - e.getPoint().x;
			int diffY = dragPoint.y - e.getPoint().y;

			controller.moveBy(diffX, diffY);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (e.getClickCount() == 1)
		{
			System.out.println("Over box X: " + (e.getX()/sCanvas.boxW) + " Y: " + (e.getY()/sCanvas.boxH));
		}
		// Click zooming
		if (e.getClickCount() == 2)
			sPanel.getController().clickZoom(e);
	}
}