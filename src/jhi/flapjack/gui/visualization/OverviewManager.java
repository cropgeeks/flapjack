// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.event.*;
import java.awt.image.*;

import jhi.flapjack.gui.*;
import java.awt.Dimension;

public class OverviewManager
{
	// This is the actual canvas that displays the overview
	private static OverviewCanvas dCanvas;
	private static OverviewCanvas pCanvas;

	// Which will be "owned" by either the dialog or the panel, but not both
	private static OverviewDialog dialog;
	private static OverviewPanel  panel;

	static void initialize(WinMain winMain, GenotypePanel gPanel, GenotypeCanvas genotypeCanvas)
	{
		dCanvas = new OverviewCanvas(gPanel, genotypeCanvas);
		pCanvas = new OverviewCanvas(gPanel, genotypeCanvas);

		dialog = new OverviewDialog(winMain, dCanvas);
		panel  = new OverviewPanel();
		panel.setMinimumSize(new Dimension(0, 100));

//		dialog.addCanvas(dCanvas);
//		panel.addCanvas(pCanvas);

		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				// Will still be set to true if the user closed the dialog
				// rather than it being programatically closed
				if (Prefs.guiOverviewDialog)
					Prefs.guiOverviewDialog = false;

//				dialog.removeCanvas();
//				panel.addCanvas(canvas);
			}

			public void windowOpened(WindowEvent e)
			{
//				panel.removeCanvas();
//				dialog.addCanvas(canvas);
			}
		});
	}

	public static void setVisible(boolean visible)
	{
		if (visible)
		{
			dialog.setVisible(Prefs.guiOverviewDialog);
			panel.addCanvas(pCanvas);
		}
		else
		{
			dialog.setVisible(false);
			panel.removeCanvas();
		}
	}

	public static void toggleOverviewDialog()
	{
		Prefs.guiOverviewDialog = !Prefs.guiOverviewDialog;

		dialog.setVisible(Prefs.guiOverviewDialog);
	}

	public static OverviewPanel getPanel() {
		return panel;
	}

	static void createImage()
	{
		dCanvas.createImage();
		pCanvas.createImage();
	}

	static void updateOverviewSelectionBox(int xIndex, int xW, int yIndex, int yH)
	{
		dCanvas.updateOverviewSelectionBox(xIndex, xW, yIndex, yH);
		pCanvas.updateOverviewSelectionBox(xIndex, xW, yIndex, yH);
	}

	public static BufferedImage getExportableImage(int w, int h)
	{
		return pCanvas.exportImage(w, h);
	}
}