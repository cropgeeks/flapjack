// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.dialog.*;
import jhi.flapjack.gui.dialog.analysis.*;
import jhi.flapjack.gui.simmatrix.*;
import jhi.flapjack.gui.visualization.*;
import jhi.flapjack.gui.visualization.undo.*;

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
			runSort(sort, viewSet);
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
			runSort(sort, viewSet);
		}
	}

	void sortLinesByExternal()
	{
		String rbTitle = "gui.MenuData.sortExternal.title";
		String rbLabel = "gui.MenuData.sortExternal.label";
		String rbButton = "gui.MenuData.sortExternal.button";
		String help = "_-_Sort_Lines";

		// Find out what file to import
		BrowseDialog browseDialog = new BrowseDialog(Prefs.guiExternalSortHistory,
			rbTitle, rbLabel, rbButton, help);

		if (browseDialog.isOK())
		{
			File file = browseDialog.getFile();
			Prefs.guiExternalSortHistory = browseDialog.getHistory();

			GTViewSet viewSet = gPanel.getViewSet();
			SortLinesExternally sort = new SortLinesExternally(viewSet, file);

			runSort(sort, viewSet);
		}
	}

	void sortLinesAlphabetically()
	{
		GTViewSet viewSet = gPanel.getViewSet();
		SortLinesAlphabetically sort = new SortLinesAlphabetically(viewSet);

		runSort(sort, viewSet);
	}

	public void runSort(ITrackableJob sort, GTViewSet viewSet)
	{
		MovedLinesState state = setupSort(viewSet);

		ProgressDialog dialog = new ProgressDialog(sort,
			RB.getString("gui.MenuData.sorting.title"),
			RB.getString("gui.MenuData.sorting.label"),
			Flapjack.winMain);

		// If the operation failed or was cancelled...
		if (dialog.failed("gui.error"))
			return;

		completeSort(state);
	}

	/**
	 * Prepares the lines to be sorted. This includes removing any dummy lines
	 * and also setting an undo state.
	 */
	private MovedLinesState setupSort(GTViewSet viewSet)
	{
		MovedLinesState state = new MovedLinesState(viewSet,
			RB.getString("gui.visualization.MovedLinesState.sortedLines"));

		viewSet.setDisplayLineScores(false);

		Flapjack.winMain.getNavPanel().getGenotypePanel();

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

		// If too few lines are selected we want to warn the user and not open
		// the calculate similarity matrix dialog.
		if (view.countSelectedLines() < 2)
		{
			TaskDialog.warning(RB.getString("gui.MenuAnalysis.simMatrix.lineWarning"), RB.getString("gui.text.close"));
			return;
		}

		// Prompt for settings
		CalculateSimMatrixDialog matrixDialog = new CalculateSimMatrixDialog(viewSet);
		if (matrixDialog.isOK() == false)
			return;

		// Set up the calculator
		CalculateSimilarityMatrix calculator = new CalculateSimilarityMatrix(
			viewSet, view, matrixDialog.getSelectedChromosomes(), true);

		ProgressDialog dialog = new ProgressDialog(calculator,
			RB.getString("gui.MenuAnalysis.simMatrix.title"),
			RB.getString("gui.MenuAnalysis.simMatrix.label"), Flapjack.winMain);

		// If the operation failed or was cancelled...
		if (dialog.failed("gui.error"))
			return;

		SimMatrix matrix = calculator.getMatrix();

		// Create a title for this new matrix
		int id = viewSet.getDataSet().getNavPanelCounts().getOrDefault("matrixCount", 0) + 1;
		viewSet.getDataSet().getNavPanelCounts().put("matrixCount", id);
		String title = RB.format("gui.MenuAnalysis.simMatrix.name", id,
			matrix.getLineInfos().size(), matrix.getLineInfos().size());
		matrix.setTitle(title);

		// Add the result to the navigation panel
		navPanel.addSimMatrixNode(viewSet, matrix);

		Actions.projectModified();
	}

	public void dendrogram()
	{
		DendrogramSettingsDialog dsd = new DendrogramSettingsDialog();
		if (dsd.isOK() == false)
			return;

		// This menu option should *only* be enabled if the user is currently
		// viewing a simmatrix, so the following code only works in that case
		SimMatrixPanel panel = navPanel.getActiveSimMatrixPanel();
		GTViewSet viewSet = panel.getViewSet();
		SimMatrix matrix = panel.getSimMatrix();

		// Clone the view (as the clone will ultimately contain a reordered
		// list of lines that match the order in the dendrogram)
		GTViewSet newViewSet = viewSet.createClone("", true);

		DendrogramGenerator dg = new DendrogramGenerator(matrix, newViewSet);

		ProgressDialog dialog = new ProgressDialog(dg,
			RB.getString("gui.MenuAnalysis.dendrogram.title"),
			RB.getString("gui.MenuAnalysis.dendrogram.label"), Flapjack.winMain);


		// If the operation failed or was cancelled...
		if (dialog.failed("gui.error"))
			return;

		// Create a title for this new dendrogram
		int id = viewSet.getDataSet().getNavPanelCounts().getOrDefault("dendrogramCount", 0) + 1;
		viewSet.getDataSet().getNavPanelCounts().put("dendrogramCount", id);
		String title = RB.format("gui.MenuAnalysis.dendrogram.name", id,
			matrix.getLineInfos().size());
		dg.getDendrogram().setTitle(title);

		// And use the ID for the new view's name too
		title = RB.format("gui.MenuAnalysis.dendrogram.view", id);
		newViewSet.setName(title);

		DataSet dataSet = navPanel.getDataSetForSelection();
		dataSet.getViewSets().add(newViewSet);
		navPanel.addVisualizationNode(dataSet, newViewSet);

		Actions.projectModified();
	}

	public void principalCordAnalysis()
	{
		int noLines = navPanel.getActiveSimMatrixPanel().getViewSet().getLines().size();

		PCoASettingsDialog psd = new PCoASettingsDialog(noLines);
		if (psd.isOK() == false)
			return;

		// This menu option should *only* be enabled if the user is currently
		// viewing a simmatrix, so the following code only works in that case
		SimMatrixPanel panel = navPanel.getActiveSimMatrixPanel();
		GTViewSet viewSet = panel.getViewSet();
		SimMatrix matrix = panel.getSimMatrix();

		String noDimensions = psd.getNoDimensions();

		PCoAGenerator pco = new PCoAGenerator(viewSet, matrix, noDimensions);

		ProgressDialog dialog = new ProgressDialog(pco,
			RB.getString("gui.MenuAnalysis.pcoa.title"),
			RB.getString("gui.MenuAnalysis.pcoa.label"), Flapjack.winMain);

		// If the operation failed or was cancelled...
		if (dialog.failed("gui.error"))
			return;
	}

	public void gobiiMABC()
	{
		// Single run parameters
		GTViewSet viewSet = gPanel.getViewSet();

		// Batch run parameters
		ArrayList<GTViewSet> viewSets = winMain.getProject().retrieveAllViews();

		// Prompt the user for input variables
		MABCStatsDialog dialog = new MABCStatsDialog(viewSet, viewSets);
		if (dialog.isOK() == false)
			return;

		if (dialog.isSingle())
			mabcSingleRun(viewSet, dialog);
		else
			mabcBatchRun(viewSets, dialog);
	}

	private void mabcSingleRun(GTViewSet viewSet, MABCStatsDialog dialog)
	{
		DataSet dataSet = navPanel.getDataSetForSelection();

		// Retrieve information required for analysis from dialog
		MABCStatsSinglePanelNB ui = dialog.getSingleUI();
		boolean[] selectedChromosomes = ui.getSelectedChromosomes();
		int rpIndex = ui.getRecurrentParent();
		int dpIndex = ui.getDonorParent();

		// Run the stats calculations
		MabcAnalysis stats = new MabcAnalysis(
			viewSet, selectedChromosomes, Prefs.mabcMaxMrkrCoverage, rpIndex,
			dpIndex, Prefs.guiMabcExcludeParents, Prefs.guiUseSimpleMabcStats,
			RB.getString("gui.navpanel.MabcNode.node"));

		ProgressDialog pDialog = new ProgressDialog(stats,
			RB.getString("gui.MenuAnalysis.mabc.title"),
			RB.getString("gui.MenuAnalysis.mabc.label"), Flapjack.winMain);

		// Create new NavPanel components to hold the results
		navPanel.addVisualizationNode(dataSet, stats.getViewSet());

		Actions.projectModified();
	}

	private void mabcBatchRun(ArrayList<GTViewSet> viewSets, MABCStatsDialog dialog)
	{
		// Retrieve information required for analysis from dialog
		MABCStatsBatchPanelNB ui = dialog.getBatchUI();
		boolean simpleStats = Prefs.guiUseSimpleMabcStats;

		// Run the stats calculations
		MabcBatchAnalysis stats = new MabcBatchAnalysis(
			viewSets, Prefs.mabcMaxMrkrCoverage, Prefs.guiUseSimpleMabcStats,
			RB.getString("gui.navpanel.MabcNode.node"));

		ProgressDialog pDialog = new ProgressDialog(stats,
			RB.getString("gui.MenuAnalysis.mabc.title"),
			RB.getString("gui.MenuAnalysis.mabc.label"), Flapjack.winMain);

		// Create new NavPanel components to hold the results
		for (GTViewSet viewSet: stats.getResultViewSets())
			navPanel.addVisualizationNode(viewSet.getDataSet(), viewSet);

		Actions.projectModified();
	}

	public void gobiiPedVer()
	{
		DataSet dataSet = navPanel.getDataSetForSelection();
		GTViewSet viewSet = gPanel.getViewSet();

		PedVerF1StatsDialog dialog = new PedVerF1StatsDialog(viewSet);
		if (dialog.isOK() == false)
			return;

		// Retrieve information required for analysis from dialog
		boolean[] selectedChromosomes = dialog.getSelectedChromosomes();
		int p1Index = dialog.getParent1();
		int p2Index = dialog.getParent2();
		int f1Index = dialog.getF1();
		boolean simulateF1 = dialog.simulateF1();

		// Setup and run the stats
		PedVerF1sAnalysis stats = new PedVerF1sAnalysis(viewSet,
			selectedChromosomes, p1Index, p2Index, simulateF1, f1Index,
			Prefs.guiPedVerF1sExcludeParents, RB.getString("gui.navpanel.PedVerF1s.node"));

		new ProgressDialog(stats,
			RB.getString("gui.MenuAnalysis.pedVerF1s.title"),
			RB.getString("gui.MenuAnalysis.pedVerF1s.label"),
			Flapjack.winMain);

		// Retrieve the newly created viewSet from the analysis class and
		// add it to the navPanel so that it appears in the display
		navPanel.addVisualizationNode(dataSet, stats.getViewSet());

		Actions.projectModified();
	}

	public void gobiiPedVerLines()
	{
		// TODO: Checks for data type? ABH, etc?
		DataSet dataSet = navPanel.getDataSetForSelection();
		GTViewSet viewSet = gPanel.getViewSet();

		PedVerLinesStatsDialog dialog = new PedVerLinesStatsDialog(viewSet);
		if (dialog.isOK() == false)
			return;

		// Retrieve information required for analysis from dialog
		boolean[] selectedChromosomes = dialog.getSelectedChromosomes();

		// TODO: I've currently hacked out the dialog parental selection
		int refIndex = dialog.getReferenceLine();
		int testIndex = dialog.getTestLine();

		PedVerLinesAnalysis stats = new PedVerLinesAnalysis(viewSet, selectedChromosomes, refIndex, testIndex, "PedVerLines Results");
		ProgressDialog pDialog = new ProgressDialog(stats,
			"Running PedVer Stats",
			"Running PedVer stats - please be patient...",
			Flapjack.winMain);

		navPanel.addVisualizationNode(dataSet, stats.getViewSet());

		Actions.projectModified();
	}

	public void gobiiForwardBreeding()
	{
		DataSet dataSet = navPanel.getDataSetForSelection();
		GTViewSet viewSet = gPanel.getViewSet();

		ForwardBreedingStatsDialog dialog = new ForwardBreedingStatsDialog(viewSet);
		if (dialog.isOK() == false)
			return;

		// Retrieve information required for analysis from dialog
		boolean[] selectedChromosomes = dialog.getSelectedChromosomes();

		ForwardBreedingAnalysis stats = new ForwardBreedingAnalysis(viewSet, selectedChromosomes, "Forward Breeding Results");
		ProgressDialog pDialog = new ProgressDialog(stats,
			"Running Forward Breeding Stats",
			"Running Forward Breeding stats - please be patient...",
			Flapjack.winMain);

		navPanel.addVisualizationNode(dataSet, stats.getViewSet());

		Actions.projectModified();
	}
}