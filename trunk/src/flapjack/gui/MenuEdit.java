package flapjack.gui;

import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.dialog.*;
import flapjack.gui.visualization.*;

import scri.commons.gui.*;

class MenuEdit
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

	void editMode(int newMode)
	{
		boolean wasInMarkerMode = (Prefs.guiMouseMode == Constants.MARKERMODE);
		boolean wasInLineMode = (Prefs.guiMouseMode == Constants.LINEMODE);
		Prefs.guiMouseMode = newMode;

		WinMainMenuBar.mEditModeNavigation.setSelected(newMode == Constants.NAVIGATION);
		WinMainToolBar.editModeNavigation.setSelected(newMode == Constants.NAVIGATION);

		WinMainMenuBar.mEditModeMarker.setSelected(newMode == Constants.MARKERMODE);
		WinMainToolBar.editModeMarker.setSelected(newMode == Constants.MARKERMODE);

		WinMainMenuBar.mEditModeLine.setSelected(newMode == Constants.LINEMODE);
		WinMainToolBar.editModeLine.setSelected(newMode == Constants.LINEMODE);

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
		// (only if required, and only if we have changed into marker mode)
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
}