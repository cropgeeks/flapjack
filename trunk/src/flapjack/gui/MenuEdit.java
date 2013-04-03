// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui;

import javax.swing.*;

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

		Actions.editModeNavigation.putValue(Action.SELECTED_KEY,
			Prefs.guiMouseMode == Constants.NAVIGATION);
		Actions.editModeMarker.putValue(Action.SELECTED_KEY,
			Prefs.guiMouseMode == Constants.MARKERMODE);
		Actions.editModeLine.putValue(Action.SELECTED_KEY,
			Prefs.guiMouseMode == Constants.LINEMODE);
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
		if (selectionType == Constants.SELECT_INVERT)
		{
			state.setMenuString(RB.getString("gui.visualization.SelectedMarkersState.selectedInvert"));

			for (int i = 0; i < view.markerCount(); i++)
				view.toggleMarkerState(i);
		}

		// And the redo state after the operation
		state.createRedoState();
		gPanel.addUndoState(state);

		editMode(Constants.MARKERMODE);
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
		if (selectionType == Constants.SELECT_INVERT)
		{
			state.setMenuString(RB.getString("gui.visualization.SelectedLinesState.selectedInvert"));

			for (int i = 0; i < view.lineCount(); i++)
				view.toggleLineState(i);
		}

		// And the redo state after the operation
		state.createRedoState();
		gPanel.addUndoState(state);

		editMode(Constants.LINEMODE);
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
		MissingMarkersDialog dialog = new MissingMarkersDialog();

		if (dialog.isOK())
		{
			// Set the undo state...
			HidMarkersState state = new HidMarkersState(gPanel.getView(),
				RB.getString("gui.visualization.HidMarkersState.hidMarkers"));
			state.createUndoState();

			// Hide the markers
			gPanel.getViewSet().filterMissingMarkers(
				Prefs.guiMissingMarkerAllChromsomes, Prefs.guiMissingMarkerPcnt);
			gPanel.refreshView();

			// Set the redo state...
			state.createRedoState();
			gPanel.addUndoState(state);
		}
	}
}