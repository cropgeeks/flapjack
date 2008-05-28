package flapjack.gui;

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
}