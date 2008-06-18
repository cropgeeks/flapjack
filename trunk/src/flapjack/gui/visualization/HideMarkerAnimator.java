package flapjack.gui.visualization;

import java.awt.*;
import javax.swing.*;

import flapjack.gui.*;

class HideMarkerAnimator extends Thread implements IOverlayRenderer
{
	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	private int alphaEffect = 0;
	private int markerIndex = 0;

	HideMarkerAnimator(GenotypePanel gPanel, int markerIndex)
	{
		this.gPanel = gPanel;
		this.markerIndex = markerIndex;

		canvas = gPanel.canvas;

		// Don't do this if there's only one marker left
		if (canvas.view.getMarkerCount() > 1)
			start();
	}

	public void run()
	{
		// Check another instance isn't already running
		for (IOverlayRenderer r: canvas.overlays)
			if (r instanceof HideMarkerAnimator)
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

		HidMarkersState state = new HidMarkersState(canvas.view,
			RB.getString("gui.visualization.HidMarkersState.hidMarkers"));
		state.createUndoState();

		canvas.view.hideMarker(markerIndex);
		canvas.overlays.remove(this);

		state.createRedoState();
		gPanel.addUndoState(state);


		// Updates from this non-AWT thread, must happen via SwingUtilities
		Runnable r = new Runnable() {
			public void run()
			{
				gPanel.refreshView();
			}
		};

		SwingUtilities.invokeLater(r);
	}

	public void render(Graphics2D g)
	{
		g.setPaint(new Color(255, 255, 255, alphaEffect));

		int mX = canvas.boxW * markerIndex;
		g.fillRect(mX, 0, canvas.boxW, canvas.canvasH);
	}
}