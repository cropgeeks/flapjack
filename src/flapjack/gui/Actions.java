package flapjack.gui;

import java.awt.event.*;
import javax.swing.*;

public class Actions
{
	private WinMain winMain;

	public static AbstractAction importFile;

	Actions(WinMain winMain)
	{
		this.winMain = winMain;

		createActions();
	}

	private void createActions()
	{
		importFile = new AbstractAction(RB.getString("gui.Actions.importFile")) {
			public void actionPerformed(ActionEvent e) {
				winMain.importFile();
			}
		};
	}
}