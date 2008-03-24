package flapjack.gui;

import java.awt.*;
import javax.swing.*;

import scri.commons.gui.*;

class WinMainToolBar extends JToolBar
{
	private JButton fileNew;
	private JButton fileOpen;
	private JButton fileSave;
	private JButton fileImport;

	WinMainToolBar()
	{
		setFloatable(false);
		setBorderPainted(false);
//		setVisible(Prefs.gui_toolbar_visible);

		// New Project
		fileNew = (JButton) getButton(false,
			RB.getString("gui.WinMainToolBar.fileNew"),
			RB.getString("gui.WinMainToolBar.fileNewTT"),
			Icons.FILENEW, Actions.fileNew);

		// Open Project
		fileOpen = (JButton) getButton(false,
			RB.getString("gui.WinMainToolBar.fileOpen"),
			RB.getString("gui.WinMainToolBar.fileOpenTT"),
			Icons.FILEOPEN, Actions.fileOpen);

		// Save
		fileSave = (JButton) getButton(false, null,
			RB.getString("gui.WinMainToolBar.fileSaveTT"),
			Icons.FILESAVE, Actions.fileSave);

		// Import
		fileImport = (JButton) getButton(false,
			RB.getString("gui.WinMainToolBar.fileImport"),
			RB.getString("gui.WinMainToolBar.fileImportTT"),
			Icons.FILEIMPORT, Actions.fileImport);

		add(new JLabel(" "));

		add(fileNew);
		addSeparator(false);
		add(fileOpen);
		addSeparator(true);
		add(fileSave);
		addSeparator(true);
		add(fileImport);

		add(new JLabel(" "));
	}

	private void addSeparator(boolean separator)
	{
		if (SystemUtils.isMacOS())
		{
			add(new JLabel(" "));
			if (separator)
				add(new JLabel(" "));
		}
		else if (separator)
			addSeparator();
	}

	// Utility method to help create the buttons. Sets their text, tooltip, and
	// icon, as well as adding actionListener, defining margings, etc.
	public static AbstractButton getButton(boolean toggle, String title,
			String tt, ImageIcon icon, Action a)
	{
		AbstractButton button = null;

		if (toggle)
			button = new JToggleButton(a);
		else
			button = new JButton(a);

		button.setText(title != null ? title : "");
		button.setToolTipText(tt);
		button.setIcon(icon);
		button.setFocusPainted(false);

		if (SystemUtils.isMacOS() == false)
			button.setMargin(new Insets(1, 1, 1, 1));

		return button;
	}
}