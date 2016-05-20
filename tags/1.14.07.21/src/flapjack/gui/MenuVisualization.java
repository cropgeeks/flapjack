// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui;

import javax.swing.*;
import javax.swing.event.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.dialog.*;
import flapjack.gui.dialog.pedigrees.*;
import flapjack.gui.visualization.*;
import flapjack.gui.visualization.colors.*;

import scri.commons.gui.*;

public class MenuVisualization
{
	private GenotypePanel gPanel;

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
		if (colorScheme == ColorScheme.LINE_SIMILARITY || colorScheme == ColorScheme.LINE_SIMILARITY_GS)
		{
			SelectLMDialog dialog = new SelectLMDialog(gPanel.getView(), true);
			if (dialog.isOK() == false)
				return;

			gPanel.getView().mouseOverLine = dialog.getSelectedIndex();
			gPanel.getView().initializeComparisons();
		}

		else if (colorScheme == ColorScheme.MARKER_SIMILARITY || colorScheme == ColorScheme.MARKER_SIMILARITY_GS)
		{
			SelectLMDialog dialog = new SelectLMDialog(gPanel.getView(), false);
			if (dialog.isOK() == false)
				return;

			gPanel.getView().mouseOverMarker = dialog.getSelectedIndex();
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
		gPanel.refreshView();
	}

	void vizDisableGradients()
	{
		Prefs.visDisableGradients = !Prefs.visDisableGradients;
		gPanel.refreshView();
	}

	void vizHighlightHtZ()
	{
		Prefs.visHighlightHtZ = !Prefs.visHighlightHtZ;
		gPanel.refreshView();
	}

	void vizHighlightHoZ()
	{
		Prefs.visHighlightHoZ = !Prefs.visHighlightHoZ;
		gPanel.refreshView();
	}

	void vizHighlightGaps()
	{
		Prefs.visHighlightGaps = !Prefs.visHighlightGaps;
		gPanel.refreshView();
	}

	void vizCreatePedigree()
	{
		PedigreeSettingsDialog dialog = new PedigreeSettingsDialog(
			Prefs.guiPedigreeList);

		if (dialog.isOK() == false)
			return;

		Prefs.guiPedigreeList = dialog.getHistory();

		GTViewSet viewSet = gPanel.getViewSet();
		PedigreeGenerator pg = new PedigreeGenerator(viewSet, dialog.getFile(), dialog.getSelectedButton());

		ProgressDialog pDialog = new ProgressDialog(pg,
			"Communicating with Server",
			"Communicating with server - please be patient...",
			Flapjack.winMain);

		if (pDialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (pDialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				pDialog.getException().printStackTrace();
				TaskDialog.error(pDialog.getException().toString(), "Close");
			}

			return;
		}

		new PedigreeDialog(pg.getImage());
	}

	void vizScaling(int method)
	{
		Prefs.visMapScaling = method;
		gPanel.refreshView();
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
				Actions.vizColorLineSimGS.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.LINE_SIMILARITY_GS);
				Actions.vizColorMarkerSim.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.MARKER_SIMILARITY);
				Actions.vizColorMarkerSimGS.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.MARKER_SIMILARITY_GS);
				Actions.vizColorSimple2Color.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.SIMPLE_TWO_COLOR);
				Actions.vizColorAlleleFreq.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.ALLELE_FREQUENCY);
				Actions.vizColorBinned.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.BINNED_10);
				Actions.vizColorRandom.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.RANDOM);
				Actions.vizColorRandomWSP.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.RANDOM_WSP);
				Actions.vizColorMagic.putValue(Action.SELECTED_KEY, cScheme == ColorScheme.MAGIC);
			}

			public void menuDeselected(MenuEvent e) {}

			public void menuCanceled(MenuEvent e) {}
		});
	}
}