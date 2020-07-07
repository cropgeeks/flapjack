// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.simmatrix;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class CanvasController implements ChangeListener
{
	private SimMatrixPanel sPanel;
	private SimMatrixCanvas sCanvas;

	private JViewport viewport;
	private JScrollBar hBar, vBar;
	int zoomX = 2;
	int zoomY = 2;

	CanvasController(SimMatrixPanel sPanel, JScrollPane sp)
	{
		this.sPanel = sPanel;

		sCanvas = sPanel.getSimMatrixCanvas();

		viewport = sp.getViewport();
		viewport.addChangeListener(this);

		hBar = sp.getHorizontalScrollBar();
		vBar = sp.getVerticalScrollBar();

		computePanelSizes();
	}

	// When changing data or the zoom level...
	void computePanelSizes()
	{
		sCanvas.onResize(zoomX, zoomY);

		setScrollbarAdjustmentValues(sCanvas.boxW, sCanvas.boxH);
	}

	private void setScrollbarAdjustmentValues(int xIncrement, int yIncrement)
	{
		hBar.setUnitIncrement(xIncrement);
		hBar.setBlockIncrement(xIncrement);
		vBar.setUnitIncrement(yIncrement);
		vBar.setBlockIncrement(yIncrement);
	}

	// Moves the scroll bars by the given amount in the x and y directions
	void moveBy(int x, int y)
	{
		hBar.setValue(hBar.getValue() + x);
		vBar.setValue(vBar.getValue() + y);
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		// Each time the scollbars are moved, the canvas must be redrawn, with
		// the new dimensions of the canvas being passed to it (window size
		// changes will cause scrollbar movement events)
		sCanvas.onRedraw(viewport.getExtentSize(), viewport.getViewPosition());
	}

	void clickZoom(MouseEvent e)
	{
		int oldCanvasW = sCanvas.canvasW;
		int clickX = e.getX();
		int clickY = e.getY();
		System.out.println("OLD X: " + clickX + " Y: " + clickY);
		System.out.println("Over box X: " + (clickX/sCanvas.boxW) + " Y: " + (clickY/sCanvas.boxH));

		int centreX = sCanvas.pCenter.x;
		int centreY = sCanvas.pCenter.y;

		int newCanvasW = sCanvas.boxTotalX * (++zoomX*2);
		System.out.println("oldW: " + oldCanvasW + " newW: " + newCanvasW);
		float scale = newCanvasW / (float) oldCanvasW;

		int moveX = clickX-centreX;
		int moveY = clickY-centreY;
		int scaledMoveX = (int) (moveX*scale);
		int scaledMoveY = (int) (moveY*scale);
		System.out.println("MoveX: " + moveX + " scaled: " + scaledMoveX + " moveY: " + moveY + " scaled: " + scaledMoveY);

		moveBy(scaledMoveX, scaledMoveY);

		zoomX++;
		zoomY++;
		computePanelSizes();
		System.out.println("NEW X: " + sCanvas.pCenter.x + " Y: " + sCanvas.pCenter.y);
		System.out.println("Over box X: " + (sCanvas.pCenter.x/sCanvas.boxW) + " Y: " + (sCanvas.pCenter.y/sCanvas.boxH));
//
//		int newCanvasW = sCanvas.canvasW;
//
//		float scale = newCanvasW / (float) oldCanvasW;
//
//		int newX = sCanvas.pCenter.x;
//		int newY = sCanvas.pCenter.y;
//
//		System.out.println("Over box X: " + (sCanvas.pCenter.x/sCanvas.boxW) + " Y: " + (sCanvas.pCenter.y/sCanvas.boxH));
//
//		int moveX = clickX-newX;
//		int moveY = clickY-newY;
//		int scaledMoveX = (int) (moveX*scale);
//		int scaledMoveY = (int) (moveY*scale);
//		System.out.println("MoveX: " + moveX + " scaled: " + scaledMoveX + " moveY: " + moveY + " scaled: " + scaledMoveY);
//		moveBy(scaledMoveX, scaledMoveY);
//
//		System.out.println("NEW X: " + sCanvas.pCenter.x + " Y: " + sCanvas.pCenter.y);
//		System.out.println("Over box X: " + (sCanvas.pCenter.x/sCanvas.boxW) + " Y: " + (sCanvas.pCenter.y/sCanvas.boxH));
	}
}