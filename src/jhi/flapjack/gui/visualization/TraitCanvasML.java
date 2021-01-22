// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

class TraitCanvasML extends MouseInputAdapter implements ActionListener
{
	private NumberFormat nf = NumberFormat.getInstance();

	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	private int mouseOverIndex;
	private int boxW;

	// Menu items (and selected trait at menu click time)
	private int menuTraitIndex;
	private JMenuItem sortAsc, sortDec, sortAdv;
	private JMenuItem selectTraits;
	private JCheckBoxMenuItem showCatBoundaries;

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

		String trait = tv.getTrait().getName();
		// Do we need to display its experiment too?
		if (tv.getTrait().experimentDefined())
			trait += " (" + tv.getTrait().getExperiment() + ")";
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
		// Track the index of the trait under the mouse at popup time
		int[] traits = canvas.viewSet.getTraits();
		menuTraitIndex = traits[mouseOverIndex];

		JPopupMenu menu = new JPopupMenu();

		// Sort menus
		sortAsc = new JMenuItem();
		sortAsc.addActionListener(this);
		sortDec = new JMenuItem();
		sortDec.addActionListener(this);

		// Toggle the cat/numerical text on the buttons depending on the type
		if (getSelectedTrait().traitIsNumerical())
		{
			RB.setText(sortAsc, "gui.visualization.TraitCanvasML.sortAscNum");
			RB.setText(sortDec, "gui.visualization.TraitCanvasML.sortDecNum");
		}
		else
		{
			RB.setText(sortAsc, "gui.visualization.TraitCanvasML.sortAscCat");
			RB.setText(sortDec, "gui.visualization.TraitCanvasML.sortDecCat");
		}

		// Advanced sort
		sortAdv = new JMenuItem();
		RB.setText(sortAdv, "gui.visualization.TraitCanvasML.sortAdv");
		sortAdv.addActionListener(this);

		// Select traits
		selectTraits = new JMenuItem();
		RB.setText(selectTraits, "gui.visualization.TraitCanvasML.selectTraits");
		selectTraits.addActionListener(this);

		// Show category boundaries
		showCatBoundaries = new JCheckBoxMenuItem();
		RB.setText(showCatBoundaries, "gui.visualization.TraitCanvasML.showCatBoundaries");
		showCatBoundaries.setSelected(Prefs.visShowCatBoundaries);
		showCatBoundaries.addActionListener(this);

		menu.add(sortAsc);
		menu.add(sortDec);
		menu.add(sortAdv);
		menu.addSeparator();
		menu.add(selectTraits);
		menu.addSeparator();
		menu.add(showCatBoundaries);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == selectTraits)
			Flapjack.winMain.mData.dataSelectTraitsHeatmap();

		else if (e.getSource() == sortAsc)
			runSort(true);

		else if (e.getSource() == sortDec)
			runSort(false);

		else if (e.getSource() == sortAdv)
			Flapjack.winMain.mAnalysis.sortLinesByTrait();

		else if (e.getSource() == showCatBoundaries)
			toggleCatBoundaries();
	}

	// Runs the selected sort menu item by creating a new SortLines object and
	// handing it back to MenuAnalysis to actually run (it deals with all the
	// tracking dialog guff).
	private void runSort(boolean ascending)
	{
		int[] traits = new int[] { menuTraitIndex };

		SortLinesByTrait sort = new SortLinesByTrait(
			gPanel.getViewSet(), menuTraitIndex, ascending);
		Flapjack.winMain.mAnalysis.runSort(sort, gPanel.getViewSet());
	}

	private Trait getSelectedTrait()
	{
		DataSet dataSet = gPanel.getViewSet().getDataSet();
		return dataSet.getTraits().get(menuTraitIndex);
	}

	private void toggleCatBoundaries()
	{
		Prefs.visShowCatBoundaries = !Prefs.visShowCatBoundaries;
		Flapjack.winMain.repaint();
	}
}