// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.filechooser.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.dialog.*;
import flapjack.gui.dialog.analysis.*;
import flapjack.gui.simmatrix.*;
import flapjack.gui.visualization.*;
import flapjack.gui.visualization.undo.*;
import flapjack.io.*;

import scri.commons.gui.*;

public class MenuAnalysis
{
	private WinMain winMain;
	private NavPanel navPanel;
	private GenotypePanel gPanel;

	void setComponents(WinMain winMain, NavPanel navPanel)
	{
		this.winMain = winMain;
		this.navPanel = navPanel;

		gPanel = navPanel.getGenotypePanel();
	}

	void sortLines()
	{
		SortLinesDialog dialog = new SortLinesDialog(gPanel);

		if (dialog.isOK())
		{
			boolean[] chromosomes = dialog.getSelectedChromosomes();

			GTViewSet viewSet = gPanel.getViewSet();
			Line line = dialog.getSelectedLine();

			SortLinesBySimilarity sort = new SortLinesBySimilarity(viewSet, line, chromosomes);
			runSort(sort);
		}
	}

	public void sortLinesByTrait()
	{
		SortLinesByTraitDialog dialog = new SortLinesByTraitDialog(gPanel);

		if (dialog.isOK())
		{
			GTViewSet viewSet = gPanel.getViewSet();

			int[] traits = dialog.getTraitIndices();
			boolean[] asc = dialog.getAscendingIndices();
			boolean assign = Prefs.guiAssignTraits;

			SortLinesByTrait sort = new SortLinesByTrait(viewSet, traits, asc, assign);
			runSort(sort);
		}
	}

	void sortLinesByExternal()
	{
		String rbTitle = "gui.MenuData.sortExternal.title";
		String rbLabel = "gui.MenuData.sortExternal.label";
		String rbButton = "gui.MenuData.sortExternal.button";
		String help = "gui.dialog.SortLinesExternal";

		// Find out what file to import
		BrowseDialog browseDialog = new BrowseDialog(Prefs.guiExternalSortHistory,
			rbTitle, rbLabel, rbButton, help);

		if (browseDialog.isOK())
		{
			File file = browseDialog.getFile();
			Prefs.guiExternalSortHistory = browseDialog.getHistory();

			GTViewSet viewSet = gPanel.getViewSet();
			SortLinesExternally sort = new SortLinesExternally(viewSet, file);

			runSort(sort);
		}
	}

	void sortLinesAlphabetically()
	{
		GTViewSet viewSet = gPanel.getViewSet();
		SortLinesAlphabetically sort = new SortLinesAlphabetically(viewSet);

		runSort(sort);
	}

	public void runSort(ITrackableJob sort)
	{
		MovedLinesState state = setupSort();

		ProgressDialog dialog = new ProgressDialog(sort,
			RB.getString("gui.MenuData.sorting.title"),
			RB.getString("gui.MenuData.sorting.label"),
			Flapjack.winMain);

		// If the operation failed or was cancelled...
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
				TaskDialog.error(RB.format("gui.MenuData.sorting.error",
					dialog.getException()), RB.getString("gui.text.close"));

			return;
		}

		completeSort(state);
	}

	/**
	 * Prepares the lines to be sorted. This includes removing any dummy lines
	 * and also setting an undo state.
	 */
	private MovedLinesState setupSort()
	{
		MovedLinesState state = new MovedLinesState(gPanel.getViewSet(),
			RB.getString("gui.visualization.MovedLinesState.sortedLines"));

		gPanel.getViewSet().setDisplayLineScores(false);

		state.createUndoState();

		return state;
	}

	/**
	 * Finish the sort action by updating the display, creating a redo state
	 * and marking the project as having been modified (for the purposes of
	 * prompting the user to save changes on exit).
	 */
	private void completeSort(MovedLinesState state)
	{
		state.createRedoState();
		gPanel.addUndoState(state);

		gPanel.refreshView();
		gPanel.moveToPosition(0, -1, false);

		Actions.projectModified();
	}

	public void simMatrix()
	{
		GTViewSet viewSet = gPanel.getViewSet();
		GTView view = gPanel.getView();


		// Set up the calculator
		CalculateSimilarityMatrix calculator = new CalculateSimilarityMatrix(viewSet, view);

		ProgressDialog dialog = new ProgressDialog(calculator,
			RB.format("gui.MenuData.simMatrix.title"),
			RB.format("gui.MenuData.simMatrix.label"), Flapjack.winMain);

		// If the operation failed or was cancelled...
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
				TaskDialog.error(RB.format("gui.MenuData.simMatrix.error",
					dialog.getException().getMessage()),
					RB.getString("gui.text.close"));

			return;
		}

		// Add the result to the navigation panel
		SimMatrix matrix = calculator.getMatrix();
		navPanel.addedNewSimMatrixNode(viewSet, matrix);
	}

	public void dendrogram()
	{
		// This menu option should *only* be enabled if the user is currently
		// viewing a simmatrix, so the following code only works in that case
		SimMatrixPanel panel = navPanel.getActiveSimMatrixPanel();
		GTViewSet viewSet = panel.getViewSet();
		SimMatrix matrix = panel.getSimMatrix();


		String newName = viewSet.getName() + " Dendrogram";
		System.out.println(newName);
		GTViewSet newViewSet = viewSet.createClone(newName, false, true);

		DendrogramGenerator dg = new DendrogramGenerator(matrix, newViewSet);

		ProgressDialog dialog = new ProgressDialog(dg,
			"Generating Dendrogram",
			"Generating dendrogram - please be patient", Flapjack.winMain);


		// If the operation failed or was cancelled...
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
				TaskDialog.error(RB.format("TODO: Error: {0}",
					dialog.getException().getMessage()),
					RB.getString("gui.text.close"));

			return;
		}

		Dendrogram dendrogram = dg.getDendrogram();
		ArrayList<Integer> rIntOrder = dg.rIntOrder();

		DataSet dataSet = navPanel.getDataSetForSelection();
		dataSet.getViewSets().add(newViewSet);
		navPanel.addedNewVisualizationNode(dataSet);


		SimMatrix orderedMatrix = matrix.cloneAndReorder(rIntOrder, dendrogram.viewLineOrder());
		navPanel.addedNewSimMatrixNode(newViewSet, orderedMatrix);
		navPanel.addedNewDendogramNode(newViewSet, newViewSet.getDataSet(), matrix, dendrogram);
	}
}