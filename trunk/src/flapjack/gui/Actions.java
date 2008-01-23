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

	Actions(WinMain winMain)
	{
		this.winMain = winMain;

		createActions();
	}

	private void createActions()
	{
		fileNew = new AbstractAction(RB.getString("gui.Actions.fileNew"), Icons.FILENEW) {
			public void actionPerformed(ActionEvent e) {
				winMain.fileNew();
			}
		};

		fileNew.setEnabled(false);

		fileOpen = new AbstractAction(RB.getString("gui.Actions.fileOpen"), Icons.FILEOPEN) {
			public void actionPerformed(ActionEvent e) {
				winMain.fileOpen();
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
	}
}