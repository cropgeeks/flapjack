// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

public class CrosshairOverlay implements IOverlayRenderer
{
	private GenotypePanel gPanel;

	CrosshairOverlay(GenotypePanel gPanel)
	{
		this.gPanel = gPanel;
	}

	public void render(Graphics2D g)
	{
		GTView view = gPanel.getView();
		GenotypeCanvas canvas = gPanel.canvas;
		
		if (Prefs.visCrosshair && view != null &&
			(view.mouseOverMarker != -1 || view.mouseOverLine != -1))
		{
			g.setPaint(new Color(255, 255, 255, 75));

			g.fillRect(canvas.boxW*view.mouseOverMarker, 0, canvas.boxW, canvas.canvasH);
			g.fillRect(0, canvas.boxH*view.mouseOverLine, canvas.canvasW, canvas.boxH);
		}
	}
}