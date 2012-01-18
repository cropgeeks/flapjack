// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;

import flapjack.data.*;

public class SplitterOverlay implements IOverlayRenderer
{
	private GenotypePanel gPanel;

	public SplitterOverlay(GenotypePanel gPanel)
	{
		this.gPanel = gPanel;
	}

	public void render(Graphics2D g)
	{
		GenotypeCanvas canvas = gPanel.canvas;
		GTView view = gPanel.getView();

		if (view == canvas.view && view.getSplitterIndex() != -1)
		{
			int y1 = view.getSplitterIndex() * canvas.boxH;

			g.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1f, new float[] { canvas.boxW/2 }, canvas.boxW/4));
			g.setPaint(new Color(100, 100, 100));

			g.drawLine(0, y1+(canvas.boxH/2), canvas.canvasW-1, y1+(canvas.boxH/2));
		}
	}
}