package flapjack.gui.visualization;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

/**
 * This class deals with highlighting (and animating) a line/marker intersection
 * on the canvas, and also with ensuring that the canvas is displaying the
 * correct location for that intersection. It is called by code that cannot be
 * sure that the GenotypePanel is even showing the view that contains these
 * markers, so we have to ensure this thread does nothing until the view matches.
 */
public class BookmarkHighlighter extends Thread implements IOverlayRenderer
{
	public static boolean enable = true;

	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;
	private GTView view;

	private int lineIndex = -1, markerIndex = -1;
	private int alphaEffect = 0;

	public BookmarkHighlighter(GenotypePanel gPanel, GTView view, int lineIndex, int markerIndex)
	{
		this.gPanel = gPanel;
		this.view = view;
		this.lineIndex = lineIndex;
		this.markerIndex = markerIndex;

		canvas = gPanel.canvas;

		// Only run if enable is set to true. If it's not, then re-enable it for
		// next time (this stops highlighting happening when adding bookmarks)
		if (enable == false)
		{
			enable = true;
			return;
		}

		start();
	}

	public void run()
	{
		// Check another instance isn't already running
		for (IOverlayRenderer r: canvas.overlays)
			if (r instanceof BookmarkHighlighter)
			{
				((BookmarkHighlighter)r).interrupt();
				canvas.overlays.remove(r);
				break;
			}

		// Don't do ANYTHING until we know the correct view is in use
		while (canvas.view != view)
		{
			try { Thread.sleep(20); }
			catch (InterruptedException e) {}
		}

		canvas.resetBufferedState(false);
		canvas.overlays.add(this);
		moveTo();

		// Darken the regions around the line/marker
		alphaEffect = 200;
		canvas.repaint();

		// Then wait for a few seconds
		try { Thread.sleep(3000); }
		catch (InterruptedException e) {}

		// Before fading everything back to normality
		for (int i = 1; i <= 40 && !isInterrupted(); i++)
		{
			// 40 * 5 = 200 (the starting alpha)
			alphaEffect = (int) (200 - (i * 5));
			canvas.repaint();

			// 25 * 40 = 1000 (1 second)
			try { Thread.sleep(25); }
			catch (InterruptedException e) {}
		}

		canvas.overlays.remove(this);
	}

	private void moveTo()
	{
		// Updates from this non-AWT thread, must happen via SwingUtilities
		Runnable r = new Runnable() {
			public void run()
			{
				gPanel.jumpToPosition(lineIndex, markerIndex, true);
			}
		};

		SwingUtilities.invokeLater(r);
	}

	public void render(Graphics2D g)
	{
		g.setPaint(new Color(20, 20, 20, alphaEffect));

		int y1 = lineIndex * canvas.boxH;
		int y2 = y1 + canvas.boxH;
		int x1 = markerIndex * canvas.boxW;
		int x2 = x1 + canvas.boxW;

		g.fillRect(0, 0, x1, y1);
		g.fillRect(x2, 0, canvas.canvasW-x2, y1);
		g.fillRect(0, y2, x1, canvas.canvasH-y2);
		g.fillRect(x2, y2, canvas.canvasW-x2, canvas.canvasH-y2);
	}
}