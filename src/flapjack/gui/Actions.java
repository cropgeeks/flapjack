package flapjack.gui;

import java.awt.event.*;
import javax.swing.*;

public class Actions
{
	private WinMain winMain;

	public static AbstractAction fileImport;
	public static AbstractAction fileExit;

	Actions(WinMain winMain)
	{
		this.winMain = winMain;

		createActions();
	}

	private void createActions()
	{
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