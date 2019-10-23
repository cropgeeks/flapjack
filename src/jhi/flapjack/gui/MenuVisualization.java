// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui;

import javax.swing.*;
import static javax.swing.Action.*;
import javax.swing.event.*;

import jhi.flapjack.gui.dialog.*;
import jhi.flapjack.gui.visualization.*;
import jhi.flapjack.gui.visualization.colors.*;

public class MenuVisualization
{
	private GenotypePanel gPanel;

	// Highlighters for when lines/markers are selected
	private LMHighlighter lmHighlighter;

	void setComponents(NavPanel navPanel)
	{
		gPanel = navPanel.getGenotypePanel();
	}

	// Displays the dialog for exporting map/genotype data as an image
	void vizExportImage()
	{
		ExportImageDialog dialog = new ExportImageDialog(gPanel);
	}

	// Displays the dialog for exporting map/genotype data to disk
	void vizExportData()
	{
		ExportDataDialog dialog = new ExportDataDialog(gPanel.getViewSet());
	}

	void vizColorCustomize()
	{
		new ColorDialog(Flapjack.winMain, gPanel);
	}

	public void vizColor(int colorScheme)
	{
		// Set the initial index positions for similarity colouring (if needbe)
		if (colorScheme == ColorScheme.LINE_SIMILARITY ||
			colorScheme == ColorScheme.LINE_SIMILARITY_EXACT_MATCH ||
			colorScheme == ColorScheme.LINE_SIMILARITY_ANY_MATCH)
		{
			SelectLMDialog dialog = new SelectLMDialog(gPanel.getView(), true);
			if (dialog.isOK() == false)
				return;

			gPanel.getViewSet().setComparisonLineIndex(dialog.getSelectedIndex());
			gPanel.getView().initializeComparisons();
		}

		else if (colorScheme == ColorScheme.MARKER_SIMILARITY)
		{
			SelectLMDialog dialog = new SelectLMDialog(gPanel.getView(), false);
			if (dialog.isOK() == false)
				return;

			gPanel.getView().setComparisonMarkerIndex(dialog.getSelectedIndex());
			gPanel.getView().initializeComparisons();
		}

		else if (colorScheme == ColorScheme.SIMILARITY_TO_EACH_PARENT)
		{
			SelectParentsDialog dialog = new SelectParentsDialog(gPanel.getView());
			if (dialog.isOK() == false)
				return;

			gPanel.getViewSet().setComparisonLineIndex(dialog.getParent1());
			gPanel.getViewSet().setComparisonLineIndex2(dialog.getParent2());
			gPanel.getView().initializeComparisons();
		}

		else if (colorScheme == ColorScheme.PARENT_TOTAL)
		{
			SelectParentsDialog dialog = new SelectParentsDialog(gPanel.getView());
			if (dialog.isOK() == false)
				return;

			gPanel.getViewSet().setComparisonLineIndex(dialog.getParent1());
			gPanel.getViewSet().setComparisonLineIndex2(dialog.getParent2());
			gPanel.getView().initializeComparisons();
		}

		// Display the threshold dialog for allele frequency colouring
		else if (colorScheme == ColorScheme.ALLELE_FREQUENCY)
		{
			AlleleFrequencyDialog dialog = new AlleleFrequencyDialog(gPanel);
			System.out.println("dialog.isOK()=" + dialog.isOK());
			if (dialog.isOK() == false)
				return;
		}

		// Update the seed for random colour schemes
		else if (colorScheme == ColorScheme.RANDOM || colorScheme == ColorScheme.RANDOM_WSP)
			gPanel.getViewSet().setRandomColorSeed((int)(Math.random()*50000));


		gPanel.getViewSet().setColorScheme(colorScheme);
		gPanel.refreshView();

		Actions.projectModified();
	}

	void vizOverlayGenotypes()
	{
		Prefs.visShowGenotypes = !Prefs.visShowGenotypes;
		Actions.vizOverlayGenotypes.putValue(SELECTED_KEY, Prefs.visShowGenotypes);
		gPanel.refreshView();
	}

	void vizDisableGradients()
	{
		Prefs.visDisableGradients = !Prefs.visDisableGradients;
		Actions.vizDisableGradients.putValue(SELECTED_KEY, Prefs.visDisableGradients);
		gPanel.refreshView();
	}

	void vizHighlightHtZ()
	{
		Prefs.visHighlightHtZ = !Prefs.visHighlightHtZ;
		Actions.vizHighlightHtZ.putValue(SELECTED_KEY, Prefs.visHighlightHtZ);
		gPanel.refreshView();
	}

	void vizHighlightHoZ()
	{
		Prefs.visHighlightHoZ = !Prefs.visHighlightHoZ;
		Actions.vizHighlightHoZ.putValue(SELECTED_KEY, Prefs.visHighlightHoZ);
		gPanel.refreshView();
	}

	void vizHighlightGaps()
	{
		Prefs.visHighlightGaps = !Prefs.visHighlightGaps;
		Actions.vizHighlightGaps.putValue(SELECTED_KEY, Prefs.visHighlightGaps);
		gPanel.refreshView();
	}

	void vizCreatePedigree()
	{
	}

	void vizScaling(int method)
	{
		Prefs.visMapScaling = method;

		Actions.vizScalingLocal.putValue(SELECTED_KEY,
			Prefs.visMapScaling == Constants.LOCAL);
		Actions.vizScalingGlobal.putValue(SELECTED_KEY,
			Prefs.visMapScaling == Constants.GLOBAL);
		Actions.vizScalingClassic.putValue(SELECTED_KEY,
			Prefs.visMapScaling == Constants.CLASSIC);

		gPanel.refreshView();
	}


	// TODO: This is temporary proof of concept code that we probably want to remove
	// before making a release
	void vizHighlightParents()
	{
		/*
		GTView view = gPanel.getView();
		PedigreeManager manager = view.getViewSet().getDataSet().getPedigreeManager();

		int lineIndex = view.mouseOverLine;

		if (lineIndex != -1)
		{
			Line line = view.getLine(lineIndex);

			ArrayList<Line> parents = manager.getChildrenToParents().get(line);

			if (parents == null || parents.isEmpty())
			{
				lmHighlighter = new LMHighlighter(gPanel, new ArrayList<>(), lmHighlighter);
			}
			else
			{
				ArrayList<Integer> indices = parents.stream().map(l -> view.getViewSet().indexOf(l)).collect(Collectors.toCollection(ArrayList::new));
				indices.add(lineIndex);

				lmHighlighter = new LMHighlighter(gPanel, indices, lmHighlighter);
			}
		}
*/
	}

	// This method gets passed the JMenu "Color" menus for both the main manu
	// and the right-click menu. It then dynamically determines which of the
	// various colour scheme subitems should be ticked at runtime
	public void handleColorMenu(JMenu mVizColor)
	{
		mVizColor.addMenuListener(new MenuListener() {
			public void menuSelected(MenuEvent e)
			{
				int cScheme = 0;

				try { cScheme = gPanel.getViewSet().getColorScheme(); }
				catch (Exception ex) {}

				Actions.vizColorNucleotide.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.NUCLEOTIDE);
				Actions.vizColorNucleotide01.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.NUCLEOTIDE01);
				Actions.vizColorABHData.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.ABH_DATA);
				Actions.vizColorLineSim.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.LINE_SIMILARITY);
				Actions.vizColorLineSimExact.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.LINE_SIMILARITY_EXACT_MATCH);
				Actions.vizColorMarkerSim.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.MARKER_SIMILARITY);
				Actions.vizColorSimple2Color.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.SIMPLE_TWO_COLOR);
				Actions.vizColorAlleleFreq.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.ALLELE_FREQUENCY);
				Actions.vizColorBinned.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.BINNED_10);
				Actions.vizColorRandom.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.RANDOM);
				Actions.vizColorRandomWSP.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.RANDOM_WSP);
				Actions.vizColorMagic.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.MAGIC);
				Actions.vizColorParentDual.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.SIMILARITY_TO_EACH_PARENT);
				Actions.vizColorParentTotal.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.PARENT_TOTAL);
				Actions.vizColorLineSimAny.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.LINE_SIMILARITY_ANY_MATCH);
				Actions.vizColorFavAllele.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.FAV_ALLELE);
			}

			public void menuDeselected(MenuEvent e) {}

			public void menuCanceled(MenuEvent e) {}
		});
	}
}