package flapjack.gui.visualization;

import java.awt.*;
import javax.swing.*;

import flapjack.gui.*;
import flapjack.gui.visualization.colors.*;

/**
 * Heterozygote (HZ) highligher class.
 */
public class HZHighlighter extends Thread implements IOverlayRenderer
{
	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	public HZHighlighter(GenotypePanel gPanel)
	{
		this.gPanel = gPanel;

		canvas = gPanel.canvas;

		start();
	}

	public void run()
	{
/*		canvas.resetBufferedState(false);

		for (int i = 1; i <= 30 && !isInterrupted(); i++)
		{
			if (Prefs.visHighlightHZ)
				ColorState.setAlpha((int)(i * (200f/30f)));
			else
				ColorState.setAlpha((int)(200 - (i * 200f/30f)));

			canvas.updateColorScheme();
			canvas.repaint();

			try { Thread.sleep(500/60); }
			catch (InterruptedException e) {}
		}


		ColorState.setAlpha(200);
*/

		canvas.updateColorScheme();
		canvas.resetBufferedState(true);
	}

	public void render(Graphics2D g)
	{

	}
}