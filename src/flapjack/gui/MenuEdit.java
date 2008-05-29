package flapjack.gui;

import flapjack.data.*;
import flapjack.gui.visualization.*;

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
		Prefs.guiMouseMode = newMode;

		WinMainMenuBar.mEditModeNavigation.setSelected(newMode == Constants.NAVIGATION);
		WinMainToolBar.editModeNavigation.setSelected(newMode == Constants.NAVIGATION);

		WinMainMenuBar.mEditModeMarker.setSelected(newMode == Constants.MARKERMODE);
		WinMainToolBar.editModeMarker.setSelected(newMode == Constants.MARKERMODE);

		gPanel.resetBufferedState(true);
	}

	void editSelectMarkers(int selectionType)
	{
		GTView view = gPanel.getView();

		// Select All
		if (selectionType == Constants.SELECT_ALL)
		{
			for (int i = 0; i < view.getMarkerCount(); i++)
				view.setMarkerState(i, true);
		}
		// Select None
		else if (selectionType == Constants.SELECT_NONE)
		{
			for (int i = 0; i < view.getMarkerCount(); i++)
				view.setMarkerState(i, false);
		}
		// Invert
		if (selectionType == Constants.SELECT_INVERT)
		{
			for (int i = 0; i < view.getMarkerCount(); i++)
				view.toggleMarkerState(i);
		}

		editMode(Constants.MARKERMODE);
	}
}