// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.gui.*;

import scri.commons.gui.*;

class MapCanvasML extends MouseInputAdapter implements ActionListener
{
	private JCheckBoxMenuItem mVizScalingLocal;
	private JCheckBoxMenuItem mVizScalingGlobal;
	private JCheckBoxMenuItem mVizScalingClassic;

	MapCanvasML(MapCanvas mapCanvas)
	{
		mapCanvas.addMouseListener(this);
		mapCanvas.addMouseMotionListener(this);

		mVizScalingLocal = WinMainMenuBar.getCheckedItem(
			Actions.vizScalingLocal, "gui.Actions.vizScalingLocal", 0, 0);
		RB.setText(mVizScalingLocal, "gui.Actions.vizScalingLocal.full");
		mVizScalingGlobal = WinMainMenuBar.getCheckedItem(
			Actions.vizScalingGlobal, "gui.Actions.vizScalingGlobal", 0, 0);
		RB.setText(mVizScalingGlobal, "gui.Actions.vizScalingGlobal.full");
		mVizScalingClassic = WinMainMenuBar.getCheckedItem(
			Actions.vizScalingClassic, "gui.Actions.vizScalingClassic", 0, 0);
		RB.setText(mVizScalingClassic, "gui.Actions.vizScalingClassic.full");

		ButtonGroup grp = new ButtonGroup();
		grp.add(mVizScalingLocal);
		grp.add(mVizScalingGlobal);
		grp.add(mVizScalingClassic);
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

	private void displayMenu(MouseEvent e)
	{
		JPopupMenu menu = new JPopupMenu();

		menu.add(mVizScalingLocal);
		menu.add(mVizScalingGlobal);
		menu.add(mVizScalingClassic);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	public void actionPerformed(ActionEvent e)
	{
	}
}