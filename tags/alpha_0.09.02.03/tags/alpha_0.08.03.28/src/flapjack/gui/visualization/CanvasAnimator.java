package flapjack.gui.visualization;

import javax.swing.*;

class CanvasAnimator extends Thread
{
	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	CanvasAnimator(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;
		this.start();
	}

	public void run()
	{
		canvas.resetBufferedState(false);

		for (int i = 1; i <= 20; i++)
		{
			canvas.alphaEffect = (int) (i * 12.75);
			canvas.repaint();

			try { Thread.sleep(50); }
			catch (InterruptedException e) {}
		}

		canvas.view.hideMarker(canvas.view.hideMarker);
		canvas.view.hideMarker = -1;
		canvas.alphaEffect = 0;


		// Updates from this non-AWT thread, must happen via SwingUtilities
		Runnable r = new Runnable() {
			public void run()
			{
				gPanel.refreshView();
			}
		};

		SwingUtilities.invokeLater(r);
	}
}