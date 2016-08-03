// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui;

import java.io.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.dialog.*;
import jhi.flapjack.gui.dialog.analysis.*;
import jhi.flapjack.gui.simmatrix.*;
import jhi.flapjack.gui.visualization.*;
import jhi.flapjack.gui.visualization.colors.*;
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
		if (dialog.failed("gui.error"))
			return;

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
		int id = viewSet.getDataSet().getMatrixCount() + 1;
		viewSet.getDataSet().setMatrixCount(id);
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
		int id = viewSet.getDataSet().getDendrogramCount() + 1;
		viewSet.getDataSet().setDendrogramCount(id);
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
		DataSet dataSet = navPanel.getDataSetForSelection();
		GTViewSet viewSet = gPanel.getViewSet();

		// Prompt the user for input variables
		MABCStatsDialog dialog = new MABCStatsDialog(viewSet);
		if (dialog.isOK() == false)
			return;

		// Retrieve information required for analysis from dialog
		boolean[] selectedChromosomes = dialog.getSelectedChromosomes();
		int rpIndex = dialog.getRecurrentParent();
		int dpIndex = dialog.getDonorParent();

		GTViewSet finalViewSet = viewSet.createClone("", true);

		// Run the stats calculations
		MABCStats stats = new MABCStats(
			finalViewSet, selectedChromosomes, Prefs.mabcMaxMrkrCoverage, rpIndex, dpIndex);
		ProgressDialog pDialog = new ProgressDialog(stats,
			RB.getString("gui.MenuAnalysis.mabc.title"),
			RB.getString("gui.MenuAnalysis.pcoa.label"), Flapjack.winMain);

		// Create titles for the new view and its results table
		int id = dataSet.getMabcCount() + 1;
		dataSet.setMabcCount(id);
		finalViewSet.setName(RB.format("gui.MenuAnalysis.mabc.view", id));
		// mabc thingy. RB.format("gui.MenuAnalysis.mabc.panel", id);
		// set?

		// TODO: temporary workaround to get all chromosomes view back into MABC view
//		FlapjackUtils.addAllChromosomesViewToClonedViewSet(viewSet, finalViewSet);

		// Create new NavPanel components to hold the results
		dataSet.getViewSets().add(finalViewSet);
		navPanel.addVisualizationNode(dataSet, finalViewSet);
		navPanel.addMabcNode(finalViewSet);
	}

	public void gobiiPedVer()
	{
		// TODO: Checks for data type? ABH, etc?
		DataSet dataSet = navPanel.getDataSetForSelection();
		GTViewSet viewSet = gPanel.getViewSet();

		// Clone the view (as the clone will ultimately contain a reordered
		// list of lines that match the order in the dendrogram)
		GTViewSet newViewSet = viewSet.createClone("", true);

		PedVerF1StatsDialog dialog = new PedVerF1StatsDialog(viewSet);
		if (dialog.isOK() == false)
			return;

		// Retrieve information required for analysis from dialog
		boolean[] selectedChromosomes = dialog.getSelectedChromosomes();
		int p1Index = dialog.getParent1();
		int p2Index = dialog.getParent2();
		int f1Index = dialog.getF1();

		if (dialog.simulateF1())
		{
			SimulateF1 f1Sim = new SimulateF1(newViewSet, p1Index, p2Index);

			ProgressDialog pDialog = new ProgressDialog(f1Sim,
				"Running F1 Simulation",
				"Running F1 simulation - please be patient...",
				Flapjack.winMain);

			f1Index = f1Sim.getF1Index();
		}

		PedVerF1Stats stats = new PedVerF1Stats(newViewSet, selectedChromosomes, p1Index, p2Index, f1Index);
		ProgressDialog pDialog = new ProgressDialog(stats,
			"Running PedVer Stats",
			"Running PedVer stats - please be patient...",
			Flapjack.winMain);

		newViewSet.setName("PedVerF1s View");

		// TODO: temporary workaround to get all chromosomes view back into MABC view
//		FlapjackUtils.addAllChromosomesViewToClonedViewSet(viewSet, newViewSet);

		// Create new NavPanel components to hold the results
		dataSet.getViewSets().add(newViewSet);
		navPanel.addVisualizationNode(dataSet, newViewSet);
		navPanel.addPedVerNode(newViewSet);
	}

	public void gobiiPedVerLines()
	{
		// TODO: Checks for data type? ABH, etc?
		DataSet dataSet = navPanel.getDataSetForSelection();
		GTViewSet viewSet = gPanel.getViewSet();

		// Clone the view (as the clone will ultimately contain a reordered
		// list of lines that match the order in the dendrogram)
		GTViewSet newViewSet = viewSet.createClone("", true);

		AnalysisSet as = new AnalysisSet(newViewSet)
			.withViews(null)
			.withSelectedLines()
			.withSelectedMarkers();

		PedVerLinesStatsDialog dialog = new PedVerLinesStatsDialog(as);
		if (dialog.isOK() == false)
			return;

		LineInfo ref = dialog.getReferenceLine();
		LineInfo test = dialog.getTestLine();

		int refIndex = newViewSet.getLines().indexOf(ref);
		int testIndex = newViewSet.getLines().indexOf(test);

		// Move the parent lines to the top of the display
		GTView view = newViewSet.getView(0);
		view.moveLine(refIndex, 0);
		view.moveLine(testIndex, 1);

		AnalysisSet linesSet = new AnalysisSet(newViewSet)
			.withViews(null)
			.withSelectedLines()
			.withSelectedMarkers();

		PedVerLinesStats stats = new PedVerLinesStats(linesSet, newViewSet, newViewSet.getDataSet().getStateTable(), refIndex, testIndex);
		ProgressDialog pDialog = new ProgressDialog(stats,
			"Running PedVer Stats",
			"Running PedVer stats - please be patient...",
			Flapjack.winMain);

		newViewSet.setName("PedVerLines View");
		// Set the colour scheme to the similarity to line exact match scheme and set the comparison line equal to the
		// F1
		newViewSet.setColorScheme(ColorScheme.LINE_SIMILARITY);
		newViewSet.setComparisonLineIndex(newViewSet.getLines().indexOf(test));
		newViewSet.setComparisonLine(test.getLine());

		// Create new NavPanel components to hold the results
		dataSet.getViewSets().add(newViewSet);
		navPanel.addVisualizationNode(dataSet, newViewSet);
		navPanel.addPedVerLinesNode(newViewSet);
	}
}