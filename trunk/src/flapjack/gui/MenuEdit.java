// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui;

import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.dialog.*;
import flapjack.gui.visualization.*;

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

			for (int i = 0; i < view.getMarkerCount(); i++)
				view.setMarkerState(i, true);
		}
		// Select None
		else if (selectionType == Constants.SELECT_NONE)
		{
			state.setMenuString(RB.getString("gui.visualization.SelectedMarkersState.selectedNone"));

			for (int i = 0; i < view.getMarkerCount(); i++)
				view.setMarkerState(i, false);
		}
		// Invert
		if (selectionType == Constants.SELECT_INVERT)
		{
			state.setMenuString(RB.getString("gui.visualization.SelectedMarkersState.selectedInvert"));

			for (int i = 0; i < view.getMarkerCount(); i++)
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

			for (int i = 0; i < view.getLineCount(); i++)
				view.setLineState(i, true);
		}
		// Select None
		else if (selectionType == Constants.SELECT_NONE)
		{
			state.setMenuString(RB.getString("gui.visualization.SelectedLinesState.selectedNone"));

			for (int i = 0; i < view.getLineCount(); i++)
				view.setLineState(i, false);
		}
		// Invert
		if (selectionType == Constants.SELECT_INVERT)
		{
			state.setMenuString(RB.getString("gui.visualization.SelectedLinesState.selectedInvert"));

			for (int i = 0; i < view.getLineCount(); i++)
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

		if (view.mouseOverLine >= 0 && view.mouseOverLine < view.getLineCount())
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
		if (index >= 0 && index < view.getLineCount())
		{
			Line line = view.getLine(index);
			if (view.isDummyLine(line))
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
}