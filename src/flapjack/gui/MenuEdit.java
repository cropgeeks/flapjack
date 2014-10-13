// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.io.*;
import java.util.*;
import javax.swing.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.dialog.*;
import flapjack.gui.visualization.*;
import flapjack.gui.visualization.undo.*;

import scri.commons.gui.*;

public class MenuEdit
{
	private GenotypePanel gPanel;

	void setComponents(GenotypePanel gPanel)
	{
		this.gPanel = gPanel;
	}

	void editUndoRedo(boolean undo)
	{
		gPanel.processUndoRedo(undo);
	}

	public void editMode(int newMode)
	{
		boolean wasInMarkerMode = (Prefs.guiMouseMode == Constants.MARKERMODE);
		boolean wasInLineMode = (Prefs.guiMouseMode == Constants.LINEMODE);
		Prefs.guiMouseMode = newMode;

		gPanel.resetBufferedState(true);

		// Popup the warning dialog with markermode info...
		// (only if required, and only if we have changed into marker mode)
		if (Prefs.warnEditMarkerMode &&
			wasInMarkerMode == false && newMode == Constants.MARKERMODE)
		{
			JCheckBox checkbox = new JCheckBox();
			RB.setText(checkbox, "gui.MenuEdit.warnEditMarkerMode");

			TaskDialog.info(
				RB.getString("gui.MenuEdit.editMarkerMode"),
				RB.getString("gui.text.close"),
				checkbox);

			Prefs.warnEditMarkerMode = !checkbox.isSelected();
		}

		// Popup the warning dialog with markermode info...
		// (only if required, and only if we have changed into line mode)
		if (Prefs.warnEditLineMode &&
			wasInLineMode == false && newMode == Constants.LINEMODE)
		{
			JCheckBox checkbox = new JCheckBox();
			RB.setText(checkbox, "gui.MenuEdit.warnEditLineMode");

			TaskDialog.info(
				RB.getString("gui.MenuEdit.editLineMode"),
				RB.getString("gui.text.close"),
				checkbox);

			Prefs.warnEditLineMode = !checkbox.isSelected();
		}
	}

	void editSelectMarkers(int selectionType)
	{
		GTView view = gPanel.getView();

		// Track the undo state before doing anything
		SelectedMarkersState state = new SelectedMarkersState(view);
		state.createUndoState();

		// Select All
		if (selectionType == Constants.SELECT_ALL)
		{
			state.setMenuString(RB.getString("gui.visualization.SelectedMarkersState.selectedAll"));

			for (int i = 0; i < view.markerCount(); i++)
				view.setMarkerState(i, true);
		}
		// Select None
		else if (selectionType == Constants.SELECT_NONE)
		{
			state.setMenuString(RB.getString("gui.visualization.SelectedMarkersState.selectedNone"));

			for (int i = 0; i < view.markerCount(); i++)
				view.setMarkerState(i, false);
		}
		// Invert
		else if (selectionType == Constants.SELECT_INVERT)
		{
			state.setMenuString(RB.getString("gui.visualization.SelectedMarkersState.selectedInvert"));

			for (int i = 0; i < view.markerCount(); i++)
				view.toggleMarkerState(i);
		}
		// Import
		else if (selectionType == Constants.SELECT_IMPORT)
		{
			state.setMenuString(RB.getString("gui.visualization.SelectedMarkersState.selectedImport"));
			if (loadMarkerSelectionFromFile(view) == false)
				return;
		}

		// And the redo state after the operation
		state.createRedoState();
		gPanel.addUndoState(state);

		editMode(Constants.MARKERMODE);
	}

	private boolean loadMarkerSelectionFromFile(GTView view)
	{
		String rbTitle = "gui.MenuEdit.externalMarkerSelection.title";
		String rbLabel = "gui.MenuEdit.externalMarkerSelection.label";
		String rbButton = "gui.MenuEdit.externalMarkerSelection.button";
		String help = "_-_Selecting_Lines/Markers";

		// Find out which file to import
		BrowseDialog browseDialog = new BrowseDialog(Prefs.guiExternalMarkerSelectionHistory,
			rbTitle, rbLabel, rbButton, help);

		if (browseDialog.isOK())
		{
			File file = browseDialog.getFile();
			Prefs.guiExternalMarkerSelectionHistory = browseDialog.getHistory();

			ExternalSelection selectMarkers = new ExternalSelection(file);
			ProgressDialog dialog = new ProgressDialog(selectMarkers,
				RB.getString("gui.MenuEdit.markerSelection.title"),
				RB.getString("gui.MenuEdit.markerSelection.label"),
				Flapjack.winMain);

			if (dialog.getResult() == ProgressDialog.JOB_COMPLETED)
			{
				// Take our list of strings and convert it to a set of line infos
				HashSet<MarkerInfo> selectedMarkers = new HashSet<>();
				for (MarkerInfo info : view.getMarkers())
					for (String name : selectMarkers.selectionStrings())
						if (info.getMarker().getName().equals(name))
							selectedMarkers.add(info);

				// Loop over the markers and select or deselect as necesarry
				for (int i = 0; i < view.markerCount(); i++)
					view.setMarkerState(i, selectedMarkers.contains(view.getMarkerInfo(i)));

				// Display summary of selected markers
				TaskDialog.info(RB.format("gui.MenuEdit.markerSelectionSummary", selectedMarkers.size(), selectMarkers.selectionStrings().size()), RB.getString("gui.text.ok"));

				return true;
			}
			else
			{
				if (dialog.getResult() == ProgressDialog.JOB_FAILED)
					TaskDialog.error(RB.format("gui.MenuEdit.externalMarkerSelection.error",
						dialog.getException()), RB.getString("gui.text.close"));

				return false;
			}
		}
		return false;
	}

	void editHideMarkers()
	{
		HideLMDialog dialog = new HideLMDialog(gPanel, true);

		if (dialog.isOK())
		{
			// Set the undo state...
			HidMarkersState state = new HidMarkersState(gPanel.getView(),
				RB.getString("gui.visualization.HidMarkersState.hidMarkers"));
			state.createUndoState();

			// Hide the markers
			gPanel.getView().hideMarkers(Prefs.guiHideSelectedMarkers);
			gPanel.refreshView();

			// Set the redo state...
			state.createRedoState();
			gPanel.addUndoState(state);
		}
	}

	void editSelectLines(int selectionType)
	{
		GTView view = gPanel.getView();

		// Track the undo state before doing anything
		SelectedLinesState state = new SelectedLinesState(view);
		state.createUndoState();

		// Select All
		if (selectionType == Constants.SELECT_ALL)
		{
			state.setMenuString(RB.getString("gui.visualization.SelectedLinesState.selectedAll"));

			for (int i = 0; i < view.lineCount(); i++)
				view.setLineState(i, true);
		}
		// Select None
		else if (selectionType == Constants.SELECT_NONE)
		{
			state.setMenuString(RB.getString("gui.visualization.SelectedLinesState.selectedNone"));

			for (int i = 0; i < view.lineCount(); i++)
				view.setLineState(i, false);
		}
		// Invert
		else if (selectionType == Constants.SELECT_INVERT)
		{
			state.setMenuString(RB.getString("gui.visualization.SelectedLinesState.selectedInvert"));

			for (int i = 0; i < view.lineCount(); i++)
				view.toggleLineState(i);
		}

		else if (selectionType == Constants.SELECT_IMPORT)
		{
			state.setMenuString(RB.getString("gui.visualization.SelectedLinesState.selectedImport"));
			if (loadLineSelectionFromFile(state, view) == false)
				return;
		}

		// And the redo state after the operation
		state.createRedoState();
		gPanel.addUndoState(state);

		editMode(Constants.LINEMODE);
	}

	private boolean loadLineSelectionFromFile(SelectedLinesState state, GTView view)
	{
		String rbTitle = "gui.MenuEdit.externalLineSelection.title";
		String rbLabel = "gui.MenuEdit.externalLineSelection.label";
		String rbButton = "gui.MenuEdit.externalLineSelection.button";
		String help = "_-_Selecting_Lines/Markers";

		// Find out which file to import
		BrowseDialog browseDialog = new BrowseDialog(Prefs.guiExternalLineSelectionHistory,
				rbTitle, rbLabel, rbButton, help);

		if (browseDialog.isOK())
		{
			File file = browseDialog.getFile();
			Prefs.guiExternalLineSelectionHistory = browseDialog.getHistory();
			ExternalSelection selectLines = new ExternalSelection(file);
			ProgressDialog dialog = new ProgressDialog(selectLines,
					RB.getString("gui.MenuEdit.lineSelection.title"),
					RB.getString("gui.MenuEdit.lineSelection.label"),
					Flapjack.winMain);

			if (dialog.getResult() == ProgressDialog.JOB_COMPLETED)
			{
				// Take our list of strings and convert it to a set of line infos
				HashSet<LineInfo> selectedLines = new HashSet<>();
				for (LineInfo info : view.getViewSet().getLines())
					for (String name : selectLines.selectionStrings())
						if (info.getLine().getName().equals(name))
							selectedLines.add(info);

				// Loop over the lines and select or deselect as necesarry
				for (int i = 0; i < view.lineCount(); i++)
					view.setLineState(i, selectedLines.contains(view.getLineInfo(i)));

				// Display summary of selected lines
				TaskDialog.info(RB.format("gui.MenuEdit.lineSelectionSummary", selectedLines.size(), selectLines.selectionStrings().size()), RB.getString("gui.text.ok"));

				return true;
			}
			else
			{
				if (dialog.getResult() == ProgressDialog.JOB_FAILED)
					TaskDialog.error(RB.format("gui.MenuEdit.externalLineSelection.error",
						dialog.getException()), RB.getString("gui.text.close"));

				return false;
			}
		}
		return false;
	}

	void editHideLines()
	{
		HideLMDialog dialog = new HideLMDialog(gPanel, false);

		if (dialog.isOK())
		{
			// Set the undo state...
			HidLinesState state = new HidLinesState(gPanel.getViewSet(),
				RB.getString("gui.visualization.HidLinesState.hidLines"));
			state.createUndoState();

			// Hide the markers
			gPanel.getView().hideLines(Prefs.guiHideSelectedLines);
			gPanel.refreshView();

			// Set the redo state...
			state.createRedoState();
			gPanel.addUndoState(state);
		}
	}

	void editInsertLine()
	{
		GTViewSet viewSet = gPanel.getViewSet();
		GTView view = gPanel.getView();

		if (view.mouseOverLine >= 0 && view.mouseOverLine < view.lineCount())
		{
			// Set the undo state
			InsertedLineState state = new InsertedLineState(viewSet,
				RB.getString("gui.visualization.InsertedLineState.insert"));
			state.createUndoState();

			// Insert the line
			viewSet.insertDummyLine(view.mouseOverLine);

			// Set the redo state
			state.createRedoState();
			gPanel.addUndoState(state);

			view.cacheLines();
			gPanel.refreshView();
		}
	}

	// Displays a dialog box asking the user if they want to delete the dummy
	// line under the mouse, or all dummy lines
	void editDeleteLine()
	{
		GTViewSet viewSet = gPanel.getViewSet();
		GTView view = gPanel.getView();

		// Get the index of the line clicked on
		int index = view.mouseOverLine;
		boolean allowSingleDelete = false;

		// Determine if it *is* actually a dummy line
		if (index >= 0 && index < view.lineCount())
		{
			if (view.isDummyLine(index))
				allowSingleDelete = true;
		}

		// Display the dialog prompt
		boolean[] states = new boolean[] { allowSingleDelete, true, true };

		String msg = RB.getString("gui.MenuEdit.deleteLineMsg");

		String[] options = new String[] {
			RB.getString("gui.MenuEdit.deleteLine"),
			RB.getString("gui.MenuEdit.deleteAll"),
			RB.getString("gui.text.cancel") };

		int response = TaskDialog.show(msg, TaskDialog.QST, 0, options, states);
		if (response == -1 || response == 2)
			return;

		// Set the undo state
		InsertedLineState state = new InsertedLineState(viewSet,
			RB.getString("gui.visualization.InsertedLineState.remove"));
		state.createUndoState();

		// Remove a single line
		if (response == 0)
			viewSet.getLines().remove(index);
		// Or remove all dummy lines
		else
			viewSet.removeAllDummyLines();

		// Set the redo state
		state.createRedoState();
		gPanel.addUndoState(state);

		view.cacheLines();
		gPanel.refreshView();
	}

	void editDuplicateLine()
	{
		GTViewSet viewSet = gPanel.getViewSet();
		GTView view = gPanel.getView();

		if (view.mouseOverLine >= 0 && view.mouseOverLine < view.lineCount())
		{
			// Set the undo state
			InsertedLineState state = new InsertedLineState(viewSet,
				RB.getString("gui.visualization.InsertedLineState.duplicate"));
			state.createUndoState();

			// Insert the line
			viewSet.duplicateLine(view.mouseOverLine);

			// Set the redo state
			state.createRedoState();
			gPanel.addUndoState(state);

			view.cacheLines();
			gPanel.refreshView();
		}
	}

	void editDuplicateLineRemove()
	{
		GTViewSet viewSet = gPanel.getViewSet();
		GTView view = gPanel.getView();

		// Get the index of the line clicked on
		int index = view.mouseOverLine;
		boolean allowSingleDelete = false;

		// Determine if it *is* actually a duplicate line
		if (index >= 0 && index < view.lineCount())
		{
			if (view.isDuplicate(index))
				allowSingleDelete = true;
		}

		// Display the dialog prompt
		boolean[] states = new boolean[] { allowSingleDelete, true, true };

		String msg = RB.getString("gui.MenuEdit.deleteDuplicateMsg");

		String[] options = new String[] {
			RB.getString("gui.MenuEdit.deleteLine"),
			RB.getString("gui.MenuEdit.deleteAllDuplicates"),
			RB.getString("gui.text.cancel") };

		int response = TaskDialog.show(msg, TaskDialog.QST, 0, options, states);
		if (response == -1 || response == 2)
			return;

		// Set the undo state
		InsertedLineState state = new InsertedLineState(viewSet,
			RB.getString("gui.visualization.InsertedLineState.removeDuplicate"));
		state.createUndoState();

		// Remove a single line
		if (response == 0)
			viewSet.getLines().remove(index);
		// Or remove all duplicate lines
		else
			viewSet.removeAllDuplicates();

		// Set the redo state
		state.createRedoState();
		gPanel.addUndoState(state);

		view.cacheLines();
		gPanel.refreshView();
	}

	void editInsertSplitter()
	{
		GTViewSet viewSet = gPanel.getViewSet();
		GTView view = gPanel.getView();

		// If a splitter already exists, we can't add another
		if (view.getSplitterIndex() != -1)
			return;

		if (view.mouseOverLine >= 0 && view.mouseOverLine < view.lineCount())
		{
			// Set the undo state
			InsertedLineState state = new InsertedLineState(viewSet,
				RB.getString("gui.visualization.InsertedSplitterState.insert"));
			state.createUndoState();

			// Insert the line
			viewSet.insertSplitterLine(view.mouseOverLine);

			// Set the redo state
			state.createRedoState();
			gPanel.addUndoState(state);

			view.cacheLines();
			gPanel.refreshView();
		}
	}

	// Displays a dialog box asking the user if they want to delete the dummy
	// line under the mouse, or all dummy lines
	void editDeleteSplitter()
	{
		GTViewSet viewSet = gPanel.getViewSet();
		GTView view = gPanel.getView();

		int response = TaskDialog.show(RB.getString("gui.MenuEdit.removeSplitter"),
			TaskDialog.INF, 0, new String[] { RB.getString("gui.text.ok"), RB.getString("gui.text.cancel") } );

		// Set the undo state
		InsertedLineState state = new InsertedLineState(viewSet,
			RB.getString("gui.visualization.InsertedSplitterState.remove"));
		state.createUndoState();

		if (response == 0)
		{
			viewSet.getLines().remove(view.getSplitterIndex());
			viewSet.getDataSet().setSplitter(null);
		}

		// Set the redo state
		state.createRedoState();
		gPanel.addUndoState(state);

		view.cacheLines();
		gPanel.refreshView();
	}

	void editFilterMissingMarkers()
	{
		MissingMarkersDialog mmDialog = new MissingMarkersDialog();

		if (mmDialog.isOK())
		{
			// Set the undo state...
			HidMarkersState state = new HidMarkersState(gPanel.getView(),
				RB.getString("gui.visualization.HidMarkersState.hidMarkers"));
			state.createUndoState();


			FilterMissingMarkers fmm = new FilterMissingMarkers(
				gPanel.getViewSet(), Prefs.guiMissingMarkerAllChromsomes,
				Prefs.guiMissingMarkerPcnt);

			ProgressDialog dialog = new ProgressDialog(fmm,
				RB.getString("gui.MenuEdit.fmm.title"),
				RB.getString("gui.MenuEdit.fmm.label"),
				Flapjack.winMain);

			// If the operation failed or was cancelled...
			if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
			{
				// As we'll now be left with some markers removed and some not,
				// put the view back into its previous state
				editUndoRedo(true);
				gPanel.refreshView();

				return;
			}


			gPanel.refreshView();

			// Set the redo state...
			state.createRedoState();
			gPanel.addUndoState(state);
		}
	}

	void editCustomMap()
	{
		GTView view = gPanel.getView();

		// All-Chromocomes aren't going to work with this
		if (view.getChromosomeMap().isSpecialChromosome())
		{
			TaskDialog.info(RB.getString("gui.MenuEdit.editCustomMapSpecial"),
				RB.getString("gui.text.close"));
			return;
		}

		if (Prefs.warnEditCustomMap)
		{
			JCheckBox checkbox = new JCheckBox();
			RB.setText(checkbox, "gui.MenuEdit.warnEditCustomMap");

			TaskDialog.info(
				RB.getString("gui.MenuEdit.editCustomMap"),
				RB.getString("gui.text.close"),
				checkbox);

			Prefs.warnEditCustomMap = !checkbox.isSelected();
		}

		ViewSetAnalyses.createCustomMap(view);

		// Force the display to show the Chromosomes panel
		gPanel.refreshView();
		Flapjack.winMain.mView.viewGenotypesOrChromosomes(true);

		Actions.projectModified();
	}
}