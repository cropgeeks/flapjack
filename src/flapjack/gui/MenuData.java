// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.io.*;
import java.net.*;
import javax.swing.filechooser.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.dialog.*;
import flapjack.gui.dialog.analysis.*;
import flapjack.gui.visualization.*;
import flapjack.gui.visualization.undo.*;
import flapjack.io.*;

import scri.commons.gui.*;

public class MenuData
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

	void dataSortLines()
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

	void dataSortLinesByTrait()
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

	void dataSortLinesByExternal()
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

	void dataSortLinesAlphabetically()
	{
		GTViewSet viewSet = gPanel.getViewSet();
		SortLinesAlphabetically sort = new SortLinesAlphabetically(viewSet);

		runSort(sort);
	}

	private void runSort(ITrackableJob sort)
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

	public void dataFilterQTLs()
	{
		DataSet dataSet = gPanel.getViewSet().getDataSet();

		winMain.filterQTLDialog = new FilterQTLsDialog(gPanel, dataSet);
	}

	public void dataSelectGraph()
	{
		winMain.selectGraphDialog = new SelectGraphDialog(gPanel);
	}

	void dataFind()
	{
		winMain.getFindDialog().setVisible(true);
	}

	void dataStatistics()
	{
		GTViewSet viewSet = gPanel.getViewSet();

		AlleleStatistics statistics = new AlleleStatistics(viewSet);

		ProgressDialog dialog = new ProgressDialog(statistics,
			 RB.format("gui.MenuData.statistics.title"),
			 RB.format("gui.MenuData.statistics.label"),
			 Flapjack.winMain);

		if (dialog.getResult() == ProgressDialog.JOB_COMPLETED)
			new AlleleStatisticsDialog(viewSet, statistics.getResults());
	}

	// Fires off a URL request to a linked database for information on a line
	void dataDBLineName()
	{
		DBAssociation db = gPanel.getViewSet().getDataSet().getDbAssociation();
		GTView view = gPanel.getView();

		if (view.mouseOverLine >= 0 && view.mouseOverLine < view.getLineCount())
		{
			Line line = view.getLine(view.mouseOverLine);

			try
			{
				String lineURL = URLEncoder.encode(line.getName(), "UTF-8");
				String url = db.getLineSearch().replace("$LINE", lineURL);
				if (url.indexOf("?") == -1)
					url += "?application=flapjack";
				else
					url += "&application=flapjack";

				FlapjackUtils.visitURL(url.toString());
			}
			catch (Exception e) {}
		}
	}

	// Fires off a URL request to a linked database for information on a marker
	void dataDBMarkerName()
	{
		DBAssociation db = gPanel.getViewSet().getDataSet().getDbAssociation();
		GTView view = gPanel.getView();

		if (view.mouseOverMarker >= 0 && view.mouseOverMarker < view.getMarkerCount())
		{
			Marker marker = view.getMarker(view.mouseOverMarker);

			try
			{
				String markerURL = URLEncoder.encode(marker.getName(), "UTF-8");
				String url = db.getMarkerSearch().replace("$MARKER", markerURL);
				if (url.indexOf("?") == -1)
					url += "?application=flapjack";
				else
					url += "&application=flapjack";

				FlapjackUtils.visitURL(url);
			}
			catch (Exception e) {}
		}
	}

	void dataDBSettings()
	{
		DataSet dataSet = gPanel.getViewSet().getDataSet();

		DatabaseSettingsDialog dialog = new DatabaseSettingsDialog(dataSet);
	}

	void dataRenameDataSet()
	{
		DataSet dataSet = navPanel.getDataSetForSelection();

		RenameDialog dialog = new RenameDialog(dataSet.getName());

		if (dialog.isOK())
		{
			dataSet.setName(dialog.getNewName());
			navPanel.updateNodeFor(dataSet);

			Actions.projectModified();
		}
	}

	void dataDeleteDataSet()
	{
		String msg = RB.getString("gui.WinMain.deleteDataSet");

		String[] options = new String[] {
			RB.getString("gui.WinMain.deleteDataSetButton"),
			RB.getString("gui.text.cancel") };

		if (TaskDialog.show(msg, TaskDialog.QST, 1, options) == 0)
		{
			// Determine the selected data set...
			DataSet dataSet = navPanel.getDataSetForSelection();

			// ...and remove it from both the project and the GUI
			winMain.getProject().removeDataSet(dataSet);
			navPanel.removeDataSetNode(dataSet);

			Actions.projectModified();
		}
	}

	public void dataSelectTraits()
	{
		SelectTraitsDialog dialog = new SelectTraitsDialog(gPanel.getViewSet());

		gPanel.setViewSet(gPanel.getViewSet());
	}

	public void dataExportQTLs()
	{
		DataSet dataSet = navPanel.getDataSetForSelection();

		String name = RB.format("gui.MenuData.exportQTLs.filename", dataSet.getName());
		File saveAs = new File(Prefs.guiCurrentDir, name);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters.txt"), "txt");

		// Ask the user for a filename to save the current view as
		String filename = FlapjackUtils.getSaveFilename(
			RB.getString("gui.MenuData.exportQTLs.saveDialog"), saveAs, filter);

		// Quit if the user cancelled the file selection
		if (filename == null)
			return;

		QTLExporter exporter = new QTLExporter(dataSet, new File(filename));
		ProgressDialog dialog = new ProgressDialog(exporter,
			RB.format("gui.dialog.ExportDataDialog.exportTitle"),
			 RB.format("gui.dialog.ExportDataDialog.exportLabel"), winMain);

		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				TaskDialog.error(
					RB.format("gui.dialog.ExportDataDialog.exportException",
					dialog.getException().getMessage()),
					RB.getString("gui.text.close"));
			}

			return;
		}

		TaskDialog.info(
			RB.format("gui.dialog.ExportDataDialog.exportSuccess", filename),
			RB.getString("gui.text.close"));
	}

	public void dataExportTraits()
	{
		DataSet dataSet = navPanel.getDataSetForSelection();

		String name = RB.format("gui.MenuData.exportTraits.filename", dataSet.getName());
		File saveAs = new File(Prefs.guiCurrentDir, name);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters.txt"), "txt");

		// Ask the user for a filename to save the current view as
		String filename = FlapjackUtils.getSaveFilename(
			RB.getString("gui.MenuData.exportTraits.saveDialog"), saveAs, filter);

		// Quit if the user cancelled the file selection
		if (filename == null)
			return;

		TraitExporter exporter = new TraitExporter(dataSet, new File(filename));
		ProgressDialog dialog = new ProgressDialog(exporter,
			RB.format("gui.dialog.ExportDataDialog.exportTitle"),
			 RB.format("gui.dialog.ExportDataDialog.exportLabel"), winMain);

		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				TaskDialog.error(
					RB.format("gui.dialog.ExportDataDialog.exportException",
					dialog.getException().getMessage()),
					RB.getString("gui.text.close"));
			}

			return;
		}

		TaskDialog.info(
			RB.format("gui.dialog.ExportDataDialog.exportSuccess", filename),
			RB.getString("gui.text.close"));
	}
}