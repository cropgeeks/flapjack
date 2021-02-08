// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dendrogram;

import javax.swing.*;
import javax.swing.event.*;

public class CanvasController implements ChangeListener
{
	private DendrogramCanvas dCanvas;

	private JViewport viewport;

	CanvasController(DendrogramPanel dPanel, JScrollPane sp)
	{
		dCanvas = dPanel.getDendrogramCanvas();

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