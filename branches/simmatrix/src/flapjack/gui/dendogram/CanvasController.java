// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dendogram;

import javax.swing.*;
import javax.swing.event.*;

public class CanvasController implements ChangeListener
{
	private DendogramCanvas dCanvas;

	private JViewport viewport;

	CanvasController(DendogramPanel dPanel, JScrollPane sp)
	{
		dCanvas = dPanel.getDendogramCanvas();

		viewport = sp.getViewport();
		viewport.addChangeListener(this);
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		// Each time the scollbars are moved, the canvas must be redrawn, with
		// the new dimensions of the canvas being passed to it (window size
		// changes will cause scrollbar movement events)
		dCanvas.onRedraw(viewport.getExtentSize(), viewport.getViewPosition());
	}
}