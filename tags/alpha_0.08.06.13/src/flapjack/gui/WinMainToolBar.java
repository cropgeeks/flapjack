package flapjack.gui;

import java.awt.*;
import javax.swing.*;

import scri.commons.gui.*;

public class WinMainToolBar extends JToolBar
{
	private JButton fileNew;
	private JButton fileOpen;
	private JButton fileSave;
	private JButton fileImport;

	public static JButton editUndo;
	public static JButton editRedo;
	static JToggleButton editModeNavigation;
	static JToggleButton editModeMarker;

	private JButton dataFind;

	private JButton helpContents;

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


		// Edit, undo
		editUndo = (JButton) getButton(false, null,
			RB.getString("gui.WinMainToolBar.editUndo"),
			Icons.UNDO, Actions.editUndo);

		// Edit, redo
		editRedo = (JButton) getButton(false, null,
			RB.getString("gui.WinMainToolBar.editRedo"),
			Icons.REDO, Actions.editRedo);

		// Edit, navigation mode
		editModeNavigation = (JToggleButton) getButton(true, null,
			RB.getString("gui.WinMainToolBar.editModeNavigation"),
			Icons.NAVIGATIONMODE, Actions.editModeNavigation);
		editModeNavigation.setSelected(Prefs.guiMouseMode == Constants.NAVIGATION);

		// Edit, marker selection mode
		editModeMarker = (JToggleButton) getButton(true, null,
			RB.getString("gui.WinMainToolBar.editModeMarker"),
			Icons.MARKERMODE, Actions.editModeMarker);
		editModeMarker.setSelected(Prefs.guiMouseMode == Constants.MARKERMODE);


		// Data find
		dataFind = (JButton) getButton(false, null,
			RB.getString("gui.WinMainToolBar.dataFind"),
			Icons.FIND, Actions.dataFind);


		helpContents = (JButton) getButton(false, null,
			RB.getString("gui.WinMainToolBar.helpContents"),
			Icons.HELP, Actions.helpContents);


		add(new JLabel(" "));

		add(fileNew);
		addSeparator(false);
		add(fileOpen);
		addSeparator(true);
		add(fileSave);
		addSeparator(true);
		add(fileImport);

		addSeparator(true);
		add(editUndo);
		add(editRedo);

		addSeparator(true);
		add(dataFind);

		addSeparator(true);
		add(editModeNavigation);
		add(editModeMarker);

		addSeparator(true);
		add(helpContents);

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
		button.setFocusable(false);

		if (SystemUtils.isMacOS() == false)
			button.setMargin(new Insets(2, 1, 2, 1));

		return button;
	}
}