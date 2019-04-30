// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.gui.*;

import scri.commons.gui.*;

class MapCanvasML extends MouseInputAdapter
{
	private JCheckBoxMenuItem mVizScalingLocal;
	private JCheckBoxMenuItem mVizScalingGlobal;
	private JCheckBoxMenuItem mVizScalingClassic;

	private MapCanvas mapCanvas;
	private GenotypePanel gPanel;

	Integer mousePos = null;

	MapCanvasML(MapCanvas mapCanvas, GenotypePanel gPanel)
	{
		mapCanvas.addMouseListener(this);
		mapCanvas.addMouseMotionListener(this);

		this.mapCanvas = mapCanvas;
		this.gPanel = gPanel;

		mVizScalingLocal = WinMainMenuBar.getCheckedItem(
			Actions.vizScalingLocal, "gui.Actions.vizScalingLocal", 0, 0);
		RB.setText(mVizScalingLocal, "gui.Actions.vizScalingLocal.full");
		mVizScalingGlobal = WinMainMenuBar.getCheckedItem(
			Actions.vizScalingGlobal, "gui.Actions.vizScalingGlobal", 0, 0);
		RB.setText(mVizScalingGlobal, "gui.Actions.vizScalingGlobal.full");
		mVizScalingClassic = WinMainMenuBar.getCheckedItem(
			Actions.vizScalingClassic, "gui.Actions.vizScalingClassic", 0, 0);
		RB.setText(mVizScalingClassic, "gui.Actions.vizScalingClassic.full");
	}

	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger())
			displayMenu(e);
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.isPopupTrigger())
			displayMenu(e);
	}

	public void mouseMoved(MouseEvent e)
	{
		int xOffset = gPanel.traitCanvas.getPanelWidth()
					+ gPanel.listPanel.getPanelWidth() + 1;
		mousePos = e.getX() - xOffset;
		mapCanvas.repaint();
	}

	public void mouseExited(MouseEvent e)
	{
		mousePos = null;
	}

	private void displayMenu(MouseEvent e)
	{
		JPopupMenu menu = new JPopupMenu();

		menu.add(mVizScalingGlobal);
		menu.add(mVizScalingLocal);
		menu.add(mVizScalingClassic);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}