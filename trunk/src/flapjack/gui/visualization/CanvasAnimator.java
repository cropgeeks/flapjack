package flapjack.gui.visualization;

import java.awt.*;
import javax.swing.*;

class CanvasAnimator extends Thread implements IOverlayRenderer
{
	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	private int alphaEffect = 0;
	private int markerIndex = 0;

	CanvasAnimator(GenotypePanel gPanel, GenotypeCanvas canvas, int markerIndex)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;
		this.markerIndex = markerIndex;

		this.start();
	}

	public void run()
	{
		canvas.resetBufferedState(false);
		canvas.overlays.add(this);

		for (int i = 1; i <= 20; i++)
		{
			alphaEffect = (int) (i * 12.75);
			canvas.repaint();

			try { Thread.sleep(50); }
			catch (InterruptedException e) {}
		}

		canvas.view.hideMarker(markerIndex);
		canvas.overlays.remove(this);


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