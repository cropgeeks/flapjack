// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import javax.swing.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.undo.*;

import scri.commons.gui.*;

class HideLMAnimator extends Thread implements IOverlayRenderer
{
	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	private int alphaEffect = 0;
	private int index = 0;
	private boolean isMarkerIndex;

	HideLMAnimator(GenotypePanel gPanel, int index, boolean isMarkerIndex)
	{
		this.gPanel = gPanel;
		this.index = index;
		this.isMarkerIndex = isMarkerIndex;

		canvas = gPanel.canvas;

		// Don't do this if there's only one marker/line left
		if (isMarkerIndex && canvas.view.markerCount() > 1 ||
			!isMarkerIndex && canvas.view.lineCount() > 1)
			start();
	}

	public void run()
	{
		// Check another instance isn't already running
		for (IOverlayRenderer r: canvas.overlays)
			if (r instanceof HideLMAnimator)
				return;

		canvas.resetBufferedState(false);
		canvas.overlays.add(this);

		for (int i = 1; i <= 20; i++)
		{
			alphaEffect = (int) (i * 12.75);
			canvas.repaint();

			try { Thread.sleep(50); }
			catch (InterruptedException e) {}
		}

		if (isMarkerIndex)
		{
			HidMarkersState state = new HidMarkersState(canvas.view,
				RB.getString("gui.visualization.HidMarkersState.hidMarkers"));
			state.createUndoState();

			canvas.view.hideMarker(index);
			canvas.overlays.remove(this);

			state.createRedoState();
			gPanel.addUndoState(state);
		}
		else
		{
			HidLinesState state = new HidLinesState(canvas.viewSet,
				RB.getString("gui.visualization.HidLinesState.hidLines"));
			state.createUndoState();

			canvas.view.hideLine(index);
			canvas.overlays.remove(this);

			state.createRedoState();
			gPanel.addUndoState(state);
		}


		// Updates from this non-AWT thread, must happen via SwingUtilities
		Runnable r = () -> { gPanel.refreshView(); };
		SwingUtilities.invokeLater(r);
	}

	public void render(Graphics2D g)
	{
		g.setPaint(new Color(255, 255, 255, alphaEffect));

		if (isMarkerIndex)
		{
			int mX = canvas.boxW * index;
			g.fillRect(mX, 0, canvas.boxW, canvas.canvasH);
		}
		else
		{
			int mY = canvas.boxH * index;
			g.fillRect(0, mY, canvas.canvasW, canvas.boxH);
		}
	}
}