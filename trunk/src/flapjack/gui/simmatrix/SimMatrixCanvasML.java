// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.simmatrix;

import java.awt.event.*;
import javax.swing.event.*;

class SimMatrixCanvasML extends MouseInputAdapter
{
	private SimMatrixPanel sPanel;
	private SimMatrixCanvas sCanvas;

	SimMatrixCanvasML(SimMatrixPanel sPanel)
	{
		this.sPanel = sPanel;
		sCanvas = sPanel.getSimMatrixCanvas();

		// Then add listeners and overlays to the canvas
		sCanvas.addMouseListener(this);
		sCanvas.addMouseMotionListener(this);
		sCanvas.addMouseWheelListener(this);
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