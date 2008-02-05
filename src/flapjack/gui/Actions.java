package flapjack.gui;

import java.awt.event.*;
import javax.swing.*;

public class Actions
{
	private WinMain winMain;

	public static AbstractAction fileNew;
	public static AbstractAction fileOpen;
	public static AbstractAction fileSave;
	public static AbstractAction fileSaveAs;
	public static AbstractAction fileImport;
	public static AbstractAction fileExit;

	public static AbstractAction viewOverview;

	public static AbstractAction helpAbout;

	Actions(WinMain winMain)
	{
		this.winMain = winMain;

		createActions();

		// Set initial states for actions that shouldn't be enabled at the start
		resetActions();

		// Flapjack starts with a new (unmodified) project already created so
		// you can't save it as there's nothing (yet) to be saved
		fileSave.setEnabled(false);
	}

	public static void projectSaved() {
		fileSave.setEnabled(false);
	}

	public static void projectModified() {
		fileSave.setEnabled(true);
	}

	private void createActions()
	{
		fileNew = new AbstractAction(RB.getString("gui.Actions.fileNew"), Icons.FILENEW) {
			public void actionPerformed(ActionEvent e) {
				winMain.fileNew();
			}
		};

		fileOpen = new AbstractAction(RB.getString("gui.Actions.fileOpen"), Icons.FILEOPEN) {
			public void actionPerformed(ActionEvent e) {
				winMain.fileOpen(null);
			}
		};

		fileSave = new AbstractAction(RB.getString("gui.Actions.fileSave"), Icons.FILESAVE) {
			public void actionPerformed(ActionEvent e) {
				winMain.fileSave(false);
			}
		};

		fileSaveAs = new AbstractAction(RB.getString("gui.Actions.fileSaveAs"), Icons.FILESAVEAS) {
			public void actionPerformed(ActionEvent e) {
				winMain.fileSave(true);
			}
		};

		fileImport = new AbstractAction(RB.getString("gui.Actions.fileImport"), Icons.FILEIMPORT) {
			public void actionPerformed(ActionEvent e) {
				winMain.fileImport();
			}
		};

		fileExit = new AbstractAction(RB.getString("gui.Actions.fileExit")) {
			public void actionPerformed(ActionEvent e) {
				winMain.fileExit();
			}
		};


		viewOverview = new AbstractAction(RB.getString("gui.Actions.viewOverview")) {
			public void actionPerformed(ActionEvent e) {
				winMain.viewOverview();
			}
		};


		helpAbout = new AbstractAction(RB.getString("gui.Actions.helpAbout")) {
			public void actionPerformed(ActionEvent e) {
				winMain.helpAbout();
			}
		};
	}

	/** Called whenever the focus on the navigation tree changes. **/
	public static void resetActions()
	{
		viewOverview.setEnabled(false);
	}
}