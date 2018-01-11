// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class CanvasController extends JPanel implements ChangeListener
{
	private GenotypePanel gPanel;

	GenotypeCanvas canvas;
	MapCanvas mapCanvas;
	private MiniMapCanvas miniMapCanvas;
	private RowCanvas rowCanvas;
	private ColCanvas colCanvas;
	private QTLCanvas qtlCanvas;
	GraphCanvas[] graphCanvas;
	TraitCanvas traitCanvas;
	ListPanel listPanel;
	StatusPanelNB statusPanel;

	// Secondary components needed by the panel
	private JScrollPane sp;
	private JScrollBar hBar, vBar;
	private JViewport viewport;

	// Normal or click zooming (affects which base to zoom in on)
	private boolean isClickZooming = false;
	// Tracks the genotype to zoom in on
	private float gtCenterX, gtCenterY;

	CanvasController(GenotypePanel gPanel, JScrollPane sp)
	{
		this.gPanel = gPanel;
		this.sp = sp;

		canvas = gPanel.canvas;
		listPanel = gPanel.listPanel;
		mapCanvas = gPanel.mapCanvas;
		miniMapCanvas = gPanel.miniMapCanvas;
		qtlCanvas = gPanel.qtlCanvas;
		graphCanvas = gPanel.graphCanvas;
		statusPanel = gPanel.statusPanel;

		viewport = sp.getViewport();
		viewport.addChangeListener(this);
		hBar = sp.getHorizontalScrollBar();
		vBar = sp.getVerticalScrollBar();
	}

	JScrollBar getHBar()
		{ return hBar; }

	JScrollBar getVBar()
		{ return vBar; }

	public void stateChanged(ChangeEvent e)
	{
		// Each time the scollbars are moved, the canvas must be redrawn, with
		// the new dimensions of the canvas being passed to it (window size
		// changes will cause scrollbar movement events)
		canvas.computeForRedraw(viewport.getExtentSize(), viewport.getViewPosition());
	}

	private void setScrollbarAdjustmentValues(int xIncrement, int yIncrement)
	{
		hBar.setUnitIncrement(xIncrement);
		hBar.setBlockIncrement(xIncrement);
		vBar.setUnitIncrement(yIncrement);
		vBar.setBlockIncrement(yIncrement);
	}

	void doZoom()
	{
		// Track the center of the screen (before the zoom)
		if (isClickZooming == false)
		{
			gtCenterX = canvas.gtCenterX;
			gtCenterY = canvas.gtCenterY;
		}

		// This is needed because for some crazy reason the moveToPosition call
		// further down will not work correctly until after Swing has stopped
		// generating endless resize events that affect the scrollbars
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				moveTo(Math.round(gtCenterY), Math.round(gtCenterX), true);

			}
		});

		computePanelSizes();
	}

	void clickZoom(MouseEvent e)
	{
		isClickZooming = true;

		gtCenterX = (e.getX() / canvas.boxW);
		gtCenterY = (e.getY() / canvas.boxH);

		int currentValue = gPanel.statusPanel.getZoomY();
		gPanel.statusPanel.setZoomY(currentValue + 6);

		isClickZooming = false;
	}

	// When changing data or the zoom level...
	void computePanelSizes()
	{
		int zoomX = statusPanel.getZoomX();
		int zoomY = statusPanel.getZoomY();

		// Fails on first run, but we want it to happen instantly ever other time
		try
		{
			listPanel.computeDimensions(zoomY);
		}
		catch (Exception e)
		{
			// So when it fails, invoke it later and we still get a correct display
			SwingUtilities.invokeLater(() -> listPanel.computeDimensions(zoomY));
		}
		canvas.setDimensions(zoomX, zoomY);

		setScrollbarAdjustmentValues(canvas.boxW, canvas.boxH);

		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				mapCanvas.updateBuffer = true;
				for (int i = 0; i < graphCanvas.length; i++)
					graphCanvas[i].updateBuffer = true;
				qtlCanvas.updateCanvasSize(true);
				miniMapCanvas.createImage();
			}
		});
	}

	void moveToLater(final int rowIndex, final int colIndex, final boolean centre)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				moveTo(rowIndex, colIndex, centre);
			}
		});
	}

	// Jumps to a position relative to a x/y index within the dataset array
	void moveTo(int lineIndex, int markerIndex, boolean centre)
	{
		// If 'centre' is true, offset by half the screen
		int offset = 0;

		if (lineIndex != -1)
		{
			if (centre)
				offset = ((canvas.boxCountY * canvas.boxH) / 2) - canvas.boxH;
			int y = lineIndex * canvas.boxH - offset;

			vBar.setValue(y);
		}

		if (markerIndex != -1)
		{
			if (centre)
				offset = ((canvas.boxCountX * canvas.boxW) / 2) - canvas.boxW;
			int x = markerIndex * canvas.boxW - offset;

			hBar.setValue(x);
		}
	}

	// Moves the scroll bars by the given amount in the x and y directions
	void moveBy(int x, int y)
	{
		hBar.setValue(hBar.getValue() + x);
		vBar.setValue(vBar.getValue() + y);
	}

	public void pageLeft()
	{
		int jumpTo = (canvas.pX1/canvas.boxW) - (canvas.boxCountX);
		moveToLater(-1, jumpTo, false);
	}

	public void pageRight()
	{
		int jumpTo = (canvas.pX2Max/canvas.boxW) + 1;
		moveToLater(-1, jumpTo, false);
	}

	public long computeCanvasViewPortBufferInBytes()
	{
		return (long)viewport.getWidth() * (long)viewport.getHeight() * 3;
	}
}