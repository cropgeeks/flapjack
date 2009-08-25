// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import scri.commons.gui.*;

public class WinMainMenuBar extends JMenuBar
{
	private WinMain winMain;
	private int menuShortcut;

	private JMenu mFile;
	private JMenu mFileRecent;
	private JMenuItem mFileNew;
	private JMenuItem mFileOpen;
	private JMenuItem mFileSave;
	private JMenuItem mFileSaveAs;
	private JMenuItem mFileImport;
	private JMenuItem mFileExit;

	private JMenu mEdit;
	public static JMenuItem mEditUndo;
	public static JMenuItem mEditRedo;
	static  JCheckBoxMenuItem mEditModeNavigation;
	static  JCheckBoxMenuItem mEditModeMarker;
	static  JCheckBoxMenuItem mEditModeLine;
	private JMenu mEditSelectMarkers;
	private JMenuItem mEditSelectMarkersAll;
	private JMenuItem mEditSelectMarkersNone;
	private JMenuItem mEditSelectMarkersInvert;
	private JMenuItem mEditHideMarkers;
	private JMenu mEditSelectLines;
	private JMenuItem mEditSelectLinesAll;
	private JMenuItem mEditSelectLinesNone;
	private JMenuItem mEditSelectLinesInvert;
	private JMenuItem mEditHideLines;

	private JMenu mViz;
	private JMenuItem mVizExportImage;
	private JMenuItem mVizExportData;
	private JMenu mVizColor;
	private JMenuItem mVizColorCustomize;
	private JMenuItem mVizColorRandom;
	private JMenuItem mVizColorRandomWSP;
	private JMenuItem mVizColorNucleotide;
	private JMenuItem mVizColorLineSim;
	private JMenuItem mVizColorLineSimGS;
	private JMenuItem mVizColorMarkerSim;
	private JMenuItem mVizColorMarkerSimGS;
	private JMenuItem mVizColorSimple2Color;
	private JMenuItem mVizColorAlleleFreq;
	static  JCheckBoxMenuItem mVizOverlayGenotypes;
	static  JCheckBoxMenuItem mVizHighlightHZ;
	private JMenuItem mVizSelectTraits;
	private JMenuItem mVizNewView;
	private JMenuItem mVizRenameView;
	private JMenuItem mVizDeleteView;
	private JMenuItem mVizToggleCanvas;
	public static JCheckBoxMenuItem mVizOverview;

	private JMenu mData;
	private JMenu mDataSortLines;
	private JMenuItem mDataSortLinesBySimilarity;
	private JMenuItem mDataSortLinesByTrait;
	private JMenuItem mDataSortLinesAlphabetically;
	private JMenuItem mDataFilterQTLs;
	private JMenuItem mDataFind;
	private JMenuItem mDataStatistics;
	private JMenu mDataDB;
	private JMenuItem mDataDBLineName;
	private JMenuItem mDataDBMarkerName;
	private JMenuItem mDataDBSettings;
	private JMenuItem mDataRenameDataSet;
	private JMenuItem mDataDeleteDataSet;

	private JMenu mWnd;
	private JMenuItem mWndMinimize;
	private JMenuItem mWndZoom;
	static  JCheckBoxMenuItem mWndFlapjack;

	private JMenu mHelp;
	private JMenuItem mHelpContents;
	private JMenuItem mHelpLicence;
	private JMenuItem mHelpPrefs;
	private JMenuItem mHelpUpdate;
	private JMenuItem mHelpAbout;

	WinMainMenuBar(WinMain winMain)
	{
		this.winMain = winMain;

		// Returns value for "CTRL" under most OSs, and the "apple" key for OS X
		menuShortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		new Actions(winMain);

		setBorderPainted(false);

		createFileMenu();
		createEditMenu();
		createVizMenu();
		createDataMenu();
		createWndMenu();
		createHelpMenu();
	}

	private void createFileMenu()
	{
		mFile = new JMenu(RB.getString("gui.WinMainMenuBar.mFile"));
		RB.setMnemonic(mFile, "gui.WinMainMenuBar.mFile");

		mFileNew = getItem(Actions.fileNew, "gui.Actions.fileNew", KeyEvent.VK_N, menuShortcut);
		mFileOpen = getItem(Actions.fileOpen, "gui.Actions.fileOpen", KeyEvent.VK_O, menuShortcut);
		mFileSave = getItem(Actions.fileSave, "gui.Actions.fileSave", KeyEvent.VK_S, menuShortcut);
		mFileSaveAs = getItem(Actions.fileSaveAs, "gui.Actions.fileSaveAs", 0, 0);
		mFileImport = getItem(Actions.fileImport, "gui.Actions.fileImport", 0, 0);
		mFileExit = getItem(Actions.fileExit, "gui.Actions.fileExit", 0, 0);

		mFileRecent = new JMenu(RB.getString("gui.WinMainMenuBar.mFileRecent"));
		RB.setMnemonic(mFileRecent, "gui.WinMainMenuBar.mFileRecent");
		createRecentMenu(null);

		mFile.add(mFileNew);
		mFile.add(mFileOpen);
		mFile.addSeparator();
		mFile.add(mFileSave);
		mFile.add(mFileSaveAs);
		mFile.addSeparator();
		mFile.add(mFileImport);
		mFile.addSeparator();
		mFile.add(mFileRecent);
		// We don't add these options to OS X as they are auto-added by Apple
		if (SystemUtils.isMacOS() == false)
		{
			mFile.addSeparator();
			mFile.add(mFileExit);
		}

		add(mFile);
	}

	private void createEditMenu()
	{
		mEdit = new JMenu(RB.getString("gui.WinMainMenuBar.mEdit"));
		RB.setMnemonic(mEdit, "gui.WinMainMenuBar.mEdit");

		mEditSelectMarkers = new JMenu(RB.getString("gui.WinMainMenuBar.mEditSelectMarkers"));
		RB.setMnemonic(mEditSelectMarkers, "gui.WinMainMenuBar.mEditSelectMarkers");
		mEditSelectLines = new JMenu(RB.getString("gui.WinMainMenuBar.mEditSelectLines"));
		RB.setMnemonic(mEditSelectLines, "gui.WinMainMenuBar.mEditSelectLines");

		mEditUndo = getItem(Actions.editUndo, "gui.Actions.editUndo", KeyEvent.VK_Z, menuShortcut);
		mEditRedo = getItem(Actions.editRedo, "gui.Actions.editRedo", KeyEvent.VK_Y, menuShortcut);
		mEditModeNavigation = getCheckedItem(Actions.editModeNavigation, "gui.Actions.editModeNavigation",
			KeyEvent.VK_1, InputEvent.ALT_MASK, Prefs.guiMouseMode == Constants.NAVIGATION);
		mEditModeMarker = getCheckedItem(Actions.editModeMarker, "gui.Actions.editModeMarker",
			KeyEvent.VK_2, InputEvent.ALT_MASK, Prefs.guiMouseMode == Constants.MARKERMODE);
		mEditModeLine = getCheckedItem(Actions.editModeLine, "gui.Actions.editModeLine",
			KeyEvent.VK_3, InputEvent.ALT_MASK, Prefs.guiMouseMode == Constants.LINEMODE);
		mEditSelectMarkersAll = getItem(Actions.editSelectMarkersAll, "gui.Actions.editSelectMarkersAll", 0, 0);
		mEditSelectMarkersNone = getItem(Actions.editSelectMarkersNone, "gui.Actions.editSelectMarkersNone", 0, 0);
		mEditSelectMarkersInvert = getItem(Actions.editSelectMarkersInvert, "gui.Actions.editSelectMarkersInvert", 0, 0);
		mEditHideMarkers = getItem(Actions.editHideMarkers, "gui.Actions.editHideMarkers", 0, 0);
		mEditSelectLinesAll = getItem(Actions.editSelectLinesAll, "gui.Actions.editSelectLinesAll", 0, 0);
		mEditSelectLinesNone = getItem(Actions.editSelectLinesNone, "gui.Actions.editSelectLinesNone", 0, 0);
		mEditSelectLinesInvert = getItem(Actions.editSelectLinesInvert, "gui.Actions.editSelectLinesInvert", 0, 0);
		mEditHideLines = getItem(Actions.editHideLines, "gui.Actions.editHideLines", 0, 0);

		mEditSelectMarkers.add(mEditSelectMarkersAll);
		mEditSelectMarkers.add(mEditSelectMarkersNone);
		mEditSelectMarkers.add(mEditSelectMarkersInvert);
		mEditSelectLines.add(mEditSelectLinesAll);
		mEditSelectLines.add(mEditSelectLinesNone);
		mEditSelectLines.add(mEditSelectLinesInvert);

		mEdit.add(mEditUndo);
		mEdit.add(mEditRedo);
		mEdit.addSeparator();
		mEdit.add(mEditModeNavigation);
		mEdit.add(mEditModeMarker);
		mEdit.add(mEditModeLine);
		mEdit.addSeparator();
		mEdit.add(mEditSelectMarkers);
		mEdit.add(mEditHideMarkers);
		mEdit.addSeparator();
		mEdit.add(mEditSelectLines);
		mEdit.add(mEditHideLines);

		add(mEdit);
	}

	private void createVizMenu()
	{
		mViz = new JMenu(RB.getString("gui.WinMainMenuBar.mViz"));
		RB.setMnemonic(mViz, "gui.WinMainMenuBar.mViz");

		mVizColor = new JMenu(RB.getString("gui.WinMainMenuBar.mVizColor"));
		mVizColor.setIcon(Actions.getIcon("COLORS"));
		RB.setMnemonic(mVizColor, "gui.WinMainMenuBar.mVizColor");

		mVizExportImage = getItem(Actions.vizExportImage, "gui.Actions.vizExportImage", 0, 0);
		mVizExportData = getItem(Actions.vizExportData, "gui.Actions.vizExportData", 0, 0);
		mVizColorCustomize = getItem(Actions.vizColorCustomize, "gui.Actions.vizColorCustomize", 0, 0);
		mVizColorRandom = getItem(Actions.vizColorRandom, "gui.Actions.vizColorRandom", 0, 0);
		mVizColorRandomWSP = getItem(Actions.vizColorRandomWSP, "gui.Actions.vizColorRandomWSP", 0, 0);
		mVizColorNucleotide = getItem(Actions.vizColorNucleotide, "gui.Actions.vizColorNucleotide", 0, 0);
		mVizColorLineSim = getItem(Actions.vizColorLineSim, "gui.Actions.vizColorLineSim", 0, 0);
//		mVizColorLineSimGS = getItem(Actions.vizColorLineSimGS, KeyEvent.VK_G, 0, 0);
		mVizColorMarkerSim = getItem(Actions.vizColorMarkerSim, "gui.Actions.vizColorMarkerSim", 0, 0);
//		mVizColorMarkerSimGS = getItem(Actions.vizColorMarkerSimGS, KeyEvent.VK_G, 0, 0);
		mVizColorSimple2Color = getItem(Actions.vizColorSimple2Color, "gui.Actions.vizColorSimple2Color", 0, 0);
		mVizColorAlleleFreq = getItem(Actions.vizColorAlleleFreq, "gui.Actions.vizColorAlleleFreq", 0, 0);
		mVizOverlayGenotypes = getCheckedItem(Actions.vizOverlayGenotypes, "gui.Actions.vizOverlayGenotypes",
			KeyEvent.VK_G, menuShortcut, Prefs.visShowGenotypes);
		mVizHighlightHZ = getCheckedItem(Actions.vizHighlightHZ, "gui.Actions.vizHighlightHZ",
			KeyEvent.VK_H, menuShortcut, Prefs.visHighlightHZ);
		mVizSelectTraits = getItem(Actions.vizSelectTraits, "gui.Actions.vizSelectTraits", 0, 0);
		mVizNewView = getItem(Actions.vizNewView, "gui.Actions.vizNewView", 0, 0);
		mVizRenameView = getItem(Actions.vizRenameView, "gui.Actions.vizRenameView", 0, 0);
		mVizDeleteView = getItem(Actions.vizDeleteView, "gui.Actions.vizDeleteView", 0, 0);
		mVizToggleCanvas = getItem(Actions.vizToggleCanvas, "gui.Actions.vizToggleCanvas", 0, 0);
		mVizOverview = getCheckedItem(Actions.vizOverview, "gui.Actions.vizOverview",
			KeyEvent.VK_F7, 0, Prefs.guiOverviewDialog);

		mVizColor.add(mVizColorNucleotide);
		mVizColor.add(mVizColorSimple2Color);
		mVizColor.add(mVizColorLineSim);
//		mVizColor.add(mVizColorLineSimGS);
		mVizColor.add(mVizColorMarkerSim);
//		mVizColor.add(mVizColorMarkerSimGS);
		mVizColor.add(mVizColorAlleleFreq);
		mVizColor.addSeparator();
		mVizColor.add(mVizColorRandom);
		mVizColor.add(mVizColorRandomWSP);
		mVizColor.addSeparator();
		mVizColor.add(mVizColorCustomize);

		mViz.add(mVizExportImage);
		mViz.add(mVizExportData);
		mViz.addSeparator();
		mViz.add(mVizColor);
		mViz.add(mVizOverlayGenotypes);
		mViz.add(mVizHighlightHZ);
		mViz.add(mVizSelectTraits);
		mViz.addSeparator();
		mViz.add(mVizNewView);
		mViz.add(mVizRenameView);
		mViz.add(mVizDeleteView);
		mViz.addSeparator();
		mViz.add(mVizToggleCanvas);
		mViz.add(mVizOverview);

		add(mViz);
	}

	private void createDataMenu()
	{
		mData = new JMenu(RB.getString("gui.WinMainMenuBar.mData"));
		RB.setMnemonic(mData, "gui.WinMainMenuBar.mData");

		mDataSortLines = new JMenu(RB.getString("gui.WinMainMenuBar.mDataSortLines"));
		RB.setMnemonic(mDataSortLines, "gui.WinMainMenuBar.mDataSortLines");

		mDataDB = new JMenu(RB.getString("gui.WinMainMenuBar.mDataDB"));
		mDataDB.setIcon(Actions.getIcon("DATABASE"));
		RB.setMnemonic(mDataDB, "gui.WinMainMenuBar.mDataDB");

		mDataSortLinesBySimilarity = getItem(Actions.dataSortLinesBySimilarity, "gui.Actions.dataSortLinesBySimilarity", 0, 0);
		mDataSortLinesByTrait = getItem(Actions.dataSortLinesByTrait, "gui.Actions.dataSortLinesByTrait", 0, 0);
		mDataSortLinesAlphabetically = getItem(Actions.dataSortLinesAlphabetically, "gui.Actions.dataSortLinesAlphabetically", 0, 0);
		mDataFilterQTLs = getItem(Actions.dataFilterQTLs, "gui.Actions.dataFilterQTLs", 0, 0);
		mDataFind = getItem(Actions.dataFind, "gui.Actions.dataFind", KeyEvent.VK_F, menuShortcut);
		mDataStatistics = getItem(Actions.dataStatistics, "gui.Actions.dataStatistics", 0, 0);
		mDataDBLineName = getItem(Actions.dataDBLineName, "gui.Actions.dataDBLineName", 0, 0);
		mDataDBMarkerName = getItem(Actions.dataDBMarkerName, "gui.Actions.dataDBMarkerName", 0, 0);
		mDataDBSettings = getItem(Actions.dataDBSettings, "gui.Actions.dataDBSettings", 0, 0);
		mDataRenameDataSet = getItem(Actions.dataRenameDataSet, "gui.Actions.dataRenameDataSet", 0, 0);
		mDataDeleteDataSet = getItem(Actions.dataDeleteDataSet, "gui.Actions.dataDeleteDataSet", 0, 0);

		mDataSortLines.add(mDataSortLinesAlphabetically);
		mDataSortLines.addSeparator();
		mDataSortLines.add(mDataSortLinesBySimilarity);
		mDataSortLines.add(mDataSortLinesByTrait);

		mDataDB.add(mDataDBLineName);
		mDataDB.add(mDataDBMarkerName);
		mDataDB.addSeparator();
		mDataDB.add(mDataDBSettings);

		mData.add(mDataSortLines);
		mData.add(mDataFilterQTLs);
		mData.addSeparator();
		mData.add(mDataFind);
		mData.add(mDataStatistics);
		mData.add(mDataDB);
		mData.addSeparator();
		mData.add(mDataRenameDataSet);
		mData.add(mDataDeleteDataSet);

		add(mData);
	}

	private void createWndMenu()
	{
		mWnd = new JMenu(RB.getString("gui.WinMainMenuBar.mWnd"));
		RB.setMnemonic(mWnd, "gui.WinMainMenuBar.mWnd");

		mWndMinimize = getItem(Actions.wndMinimize, "gui.Actions.wndMinimize", KeyEvent.VK_M, menuShortcut);
		mWndZoom = getItem(Actions.wndZoom, "gui.Actions.wndZoom", 0, 0);
		mWndFlapjack = getCheckedItem(Actions.wndFlapjack, "gui.Actions.wndFlapjack", 0, 0, true);

		mWnd.add(mWndMinimize);
		mWnd.add(mWndZoom);
		mWnd.addSeparator();
		mWnd.add(mWndFlapjack);

		if (SystemUtils.isMacOS())
			add(mWnd);
	}

	private void createHelpMenu()
	{
		mHelp = new JMenu(RB.getString("gui.WinMainMenuBar.mHelp"));
		RB.setMnemonic(mHelp, "gui.WinMainMenuBar.mHelp");

		mHelpContents = getItem(Actions.helpContents, "gui.Actions.helpContents", KeyEvent.VK_F1, 0);
		mHelpLicence = getItem(Actions.helpLicence, "gui.Actions.helpLicence", 0, 0);
		mHelpPrefs = getItem(Actions.helpPrefs, "gui.Actions.helpPrefs", 0, 0);
		mHelpUpdate = getItem(Actions.helpUpdate, "gui.Actions.helpUpdate", 0, 0);
		mHelpAbout = getItem(Actions.helpAbout, "gui.Actions.helpAbout", 0, 0);

		mHelp.add(mHelpContents);
		mHelp.add(mHelpLicence);
		mHelp.addSeparator();

		// We don't add this option to OS X as it is auto-added by Apple
		if (SystemUtils.isMacOS() == false)
		{
			mHelp.add(mHelpPrefs);
			mHelp.addSeparator();
		}

		mHelp.add(mHelpUpdate);

		// We don't add this option to OS X as it is auto-added by Apple
		if (SystemUtils.isMacOS() == false)
		{
			mHelp.addSeparator();
			mHelp.add(mHelpAbout);
		}

		add(mHelp);
	}

	public static JMenuItem getItem(Action action, String key, int keymask, int modifiers)
	{
		JMenuItem item = new JMenuItem(action);
		RB.setMnemonic(item, key);

		if (keymask != 0)
			item.setAccelerator(KeyStroke.getKeyStroke(keymask, modifiers));

		return item;
	}

	public static JCheckBoxMenuItem getCheckedItem(Action action, String key, int keymask, int modifiers, boolean state)
	{
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
		item.setState(state);
		RB.setMnemonic(item, key);

		if (keymask != 0)
			item.setAccelerator(KeyStroke.getKeyStroke(keymask, modifiers));

		return item;
	}

	// Maintains and creates the Recent Projects file menu, adding new entries
	// as previously unseen projects are opened or saved, and ensuring that:
	//   a) the most recently accessed file is always at the start of the list
	//   b) the list never grows bigger than four entries
	void createRecentMenu(File file)
	{
		// Begin by making a list of the recent file locations
		LinkedList<String> entries = new LinkedList<String>();
		entries.add(Prefs.guiRecentProject1);
		entries.add(Prefs.guiRecentProject2);
		entries.add(Prefs.guiRecentProject3);
		entries.add(Prefs.guiRecentProject4);

		// See if any of the items on that list match the file being accessed,
		// moving (or adding) the entry to the first location
		if (file != null)
		{
			int location = -1;
			for (int i = 0; i < entries.size(); i++)
				if (entries.get(i) != null)
					if (entries.get(i).equals(file.getPath()))
						location = i;

			if (location != -1)
				entries.remove(location);

			entries.addFirst(file.getPath());

			if (entries.size() > 4)
				entries.removeLast();
		}

		// The menu can then be built up, one item per entry
		mFileRecent.removeAll();

		int vk = 0;
		for (final String entry: entries)
		{
			if (entry != null)
			{
				JMenuItem item = new JMenuItem((++vk) + " " + entry);
				item.setMnemonic(KeyEvent.VK_0 + vk);
				item.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						winMain.mFile.fileOpen(new File(entry));
					}
				});

				mFileRecent.add(item);
			}
		}

		mFileRecent.setEnabled(mFileRecent.getItemCount() > 0);

		// Finally, update the preference strings with the new ordering
		Prefs.guiRecentProject1 = entries.get(0);
		Prefs.guiRecentProject2 = entries.get(1);
		Prefs.guiRecentProject3 = entries.get(2);
		Prefs.guiRecentProject4 = entries.get(3);
	}
}