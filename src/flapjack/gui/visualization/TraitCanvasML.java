// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

class TraitCanvasML extends MouseInputAdapter
{
	private NumberFormat nf = NumberFormat.getInstance();

	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	private int mouseOverIndex;
	private int boxW;

	TraitCanvasML(GenotypePanel gPanel, GenotypeCanvas canvas, int boxW)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;
		this.boxW = boxW;
	}

	public void mouseEntered(MouseEvent e)
	{
		gPanel.statusPanel.setForHeatmapUse();
		mouseOverIndex = e.getPoint().x / boxW;
	}

	public void mouseExited(MouseEvent e)
	{
		gPanel.statusPanel.setForMainUse();
		gPanel.statusPanel.setHeatmapValues(" ", " ", " ");
		mouseOverIndex = -1;
	}

	// Works out which line/trait/value is under the mouse and displays this
	// on the main status panel
	public void mouseMoved(MouseEvent e)
	{
		int y = e.getPoint().y + canvas.pY1;

		int yIndex = y / canvas.boxH;
		mouseOverIndex = e.getPoint().x / boxW;

		int[] traits = canvas.viewSet.getTraits();

		if (mouseOverIndex < 0 || mouseOverIndex >= traits.length)
			return;

		int tIndex = traits[mouseOverIndex];

		// Don't attempt to set a tooltip if there's no trait displayed or
		// if the mouse isn't over an actual line
		if (tIndex == -1 || yIndex > canvas.view.lineCount()-1)
		{
			gPanel.statusPanel.setHeatmapValues(" ", " ", " ");
			return;
		}

		Line line = canvas.view.getLine(yIndex);
		// Don't attempt to display information for dummy lines
		if (canvas.view.isDummyLine(yIndex) || canvas.view.isSplitter(yIndex))
		{
			gPanel.statusPanel.setHeatmapValues(" ", " ", " ");
			return;
		}

		TraitValue tv = line.getTraitValues().get(tIndex);

		String trait = tv.getTrait().getName() + " ("
			+ tv.getTrait().getExperiment() + ")";
		String value = " ";

		if (tv.isDefined() && tv.getTrait().traitIsNumerical())
			value = nf.format(tv.getValue());
		else if (tv.isDefined())
			value = tv.getTrait().format(tv);

		String name = canvas.view.getLineInfo(yIndex).name();
		gPanel.statusPanel.setHeatmapValues(name, trait, value);
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.isPopupTrigger())
			handlePopup(e);
	}

	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger())
			handlePopup(e);
	}

	// Pops up a menu with all the current traits, allowing the user to
	// quickly select a new trait (for the column under the mouse)
	private void handlePopup(MouseEvent e)
	{
		JPopupMenu menu = new JPopupMenu();

		JMenuItem item = new JMenuItem();
		RB.setText(item, "gui.visualization.TraitCanvas.popup");

		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)	{
				Flapjack.winMain.mData.dataSelectTraits();
			}
		});

		menu.add(item);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}