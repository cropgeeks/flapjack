// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import flapjack.io.*;

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
	private JMenuItem mFileExport;
	private JMenuItem mFileExit;

	private JMenu mEdit;
	public static JMenuItem mEditUndo;
	public static JMenuItem mEditRedo;
	private JRadioButtonMenuItem mEditModeNavigation;
	private JRadioButtonMenuItem mEditModeMarker;
	private JRadioButtonMenuItem mEditModeLine;
	private JMenu mEditSelectMarkers;
	private JMenuItem mEditSelectMarkersAll;
	private JMenuItem mEditSelectMarkersNone;
	private JMenuItem mEditSelectMarkersInvert;
	private JMenuItem mEditSelectMarkersImport;
	private JMenuItem mEditHideMarkers;
	private JMenuItem mEditFilterMissingMarkers;
	private JMenu mEditSelectLines;
	private JMenuItem mEditSelectLinesAll;
	private JMenuItem mEditSelectLinesNone;
	private JMenuItem mEditSelectLinesInvert;
	private JMenuItem mEditSelectLinesImport;
	private JMenuItem mEditHideLines;

	private JMenu mView;
	private JMenuItem mViewNewView;
	private JMenuItem mViewRenameView;
	private JMenuItem mViewDeleteView;
	private JMenuItem mViewToggleCanvas;
	public static JCheckBoxMenuItem mViewOverview;
	private JMenuItem mViewPageLeft;
	private JMenuItem mViewPageRight;
	private JRadioButtonMenuItem mViewGenotypes;
	private JRadioButtonMenuItem mViewChromosomes;

	private JMenu mViz;
	private JMenuItem mVizExportImage;
	private JMenuItem mVizExportData;
	private JMenuItem mVizCreatePedigree;
	private JMenu mVizColor;
	private JMenuItem mVizColorCustomize;
	private JRadioButtonMenuItem mVizColorRandom;
	private JRadioButtonMenuItem mVizColorRandomWSP;
	private JRadioButtonMenuItem mVizColorNucleotide;
	private JRadioButtonMenuItem mVizColorNucleotide01;
	private JRadioButtonMenuItem mVizColorABHData;
	private JRadioButtonMenuItem mVizColorLineSim;
	private JRadioButtonMenuItem mVizColorLineSimGS;
	private JRadioButtonMenuItem mVizColorMarkerSim;
	private JRadioButtonMenuItem mVizColorMarkerSimGS;
	private JRadioButtonMenuItem mVizColorSimple2Color;
	private JRadioButtonMenuItem mVizColorAlleleFreq;
	private JRadioButtonMenuItem mVizColorBinned;
	private JMenu mVizScaling;
	private JCheckBoxMenuItem mVizScalingLocal;
	private JCheckBoxMenuItem mVizScalingGlobal;
	private JCheckBoxMenuItem mVizScalingClassic;
	private JCheckBoxMenuItem mVizOverlayGenotypes;
	private JCheckBoxMenuItem mVizDisableGradients;
	private JMenu mVizHighlight;
	private JCheckBoxMenuItem mVizHighlightHtZ;
	private JCheckBoxMenuItem mVizHighlightHoZ;
	private JCheckBoxMenuItem mVizHighlightGaps;
	private JMenuItem mDataSelectTraits;

	private JMenu mAnalysis;
	private JMenu mAlysSortLines;
	private JMenuItem mAlysSortLinesBySimilarity;
	private JMenuItem mAlysSortLinesByTrait;
	private JMenuItem mAlysSortLinesByExternal;
	private JMenuItem mAlysSortLinesAlphabetically;
	private JMenuItem mAlysSimMatrix;
	private JMenuItem mAlysDendrogram;
	private JMenuItem mAlysPCoA;

	private JMenu mData;
	private JMenuItem malysFilterQTLs;
	private JMenuItem mDataFind;
	private JMenuItem mDataStatistics;
	private JMenu mDataDB;
	private JMenuItem mDataDBLineName;
	private JMenuItem mDataDBMarkerName;
	private JMenuItem mDataDBSettings;
	private JMenuItem mDataRenameDataSet;
	private JMenuItem mDataDeleteDataSet;
	private JMenuItem mDataSelectGraph;

	private JMenu mWnd;
	private JMenuItem mWndMinimize;
	private JMenuItem mWndZoom;
	static  JCheckBoxMenuItem mWndFlapjack;

	private JMenu mHelp;
	private JMenuItem mHelpContents;
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
		createViewMenu();
		createVizMenu();
		createAnalysisMenu();
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
		mFileExport = getItem(Actions.fileExport, "gui.Actions.fileExport", 0, 0);
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
		mFile.add(mFileExport);
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
		mEditModeNavigation = getRadioItem(Actions.editModeNavigation, "gui.Actions.editModeNavigation",
			KeyEvent.VK_1, InputEvent.ALT_MASK);
		mEditModeMarker = getRadioItem(Actions.editModeMarker, "gui.Actions.editModeMarker",
			KeyEvent.VK_2, InputEvent.ALT_MASK);
		mEditModeLine = getRadioItem(Actions.editModeLine, "gui.Actions.editModeLine",
			KeyEvent.VK_3, InputEvent.ALT_MASK);
		mEditSelectMarkersAll = getItem(Actions.editSelectMarkersAll, "gui.Actions.editSelectMarkersAll", 0, 0);
		mEditSelectMarkersNone = getItem(Actions.editSelectMarkersNone, "gui.Actions.editSelectMarkersNone", 0, 0);
		mEditSelectMarkersInvert = getItem(Actions.editSelectMarkersInvert, "gui.Actions.editSelectMarkersInvert", 0, 0);
		mEditSelectMarkersImport = getItem(Actions.editSelectMarkersImport, "gui.Actions.editSelectMarkersImport", 0, 0);
		mEditHideMarkers = getItem(Actions.editHideMarkers, "gui.Actions.editHideMarkers", 0, 0);
		mEditFilterMissingMarkers = getItem(Actions.editFilterMissingMarkers, "gui.Actions.editFilterMissingMarkers", 0, 0);
		mEditSelectLinesAll = getItem(Actions.editSelectLinesAll, "gui.Actions.editSelectLinesAll", 0, 0);
		mEditSelectLinesNone = getItem(Actions.editSelectLinesNone, "gui.Actions.editSelectLinesNone", 0, 0);
		mEditSelectLinesInvert = getItem(Actions.editSelectLinesInvert, "gui.Actions.editSelectLinesInvert", 0, 0);
		mEditSelectLinesImport = getItem(Actions.editSelectLinesImport, "gui.Actions.editSelectLinesImport", 0, 0);
		mEditHideLines = getItem(Actions.editHideLines, "gui.Actions.editHideLines", 0, 0);

		ButtonGroup grp = new ButtonGroup();
		grp.add(mEditModeNavigation);
		grp.add(mEditModeMarker);
		grp.add(mEditModeLine);

		mEditSelectMarkers.add(mEditSelectMarkersAll);
		mEditSelectMarkers.add(mEditSelectMarkersNone);
		mEditSelectMarkers.add(mEditSelectMarkersInvert);
		mEditSelectMarkers.addSeparator();
		mEditSelectMarkers.add(mEditSelectMarkersImport);
		mEditSelectLines.add(mEditSelectLinesAll);
		mEditSelectLines.add(mEditSelectLinesNone);
		mEditSelectLines.add(mEditSelectLinesInvert);
		mEditSelectLines.addSeparator();
		mEditSelectLines.add(mEditSelectLinesImport);

		mEdit.add(mEditUndo);
		mEdit.add(mEditRedo);
		mEdit.addSeparator();
		mEdit.add(mEditModeNavigation);
		mEdit.add(mEditModeMarker);
		mEdit.add(mEditModeLine);
		mEdit.addSeparator();
		mEdit.add(mEditSelectMarkers);
		mEdit.add(mEditHideMarkers);
		mEdit.add(mEditFilterMissingMarkers);
		mEdit.addSeparator();
		mEdit.add(mEditSelectLines);
		mEdit.add(mEditHideLines);

		add(mEdit);
	}

	private void createViewMenu()
	{
		mView = new JMenu(RB.getString("gui.WinMainMenuBar.mView"));
		RB.setMnemonic(mView, "gui.WinMainMenuBar.mView");

		mViewNewView = getItem(Actions.viewNewView, "gui.Actions.viewNewView", 0, 0);
		mViewRenameView = getItem(Actions.viewRenameView, "gui.Actions.viewRenameView", 0, 0);
		mViewDeleteView = getItem(Actions.viewDeleteView, "gui.Actions.viewDeleteView", 0, 0);
		mViewToggleCanvas = getItem(Actions.viewToggleCanvas, "gui.Actions.viewToggleCanvas", 0, 0);
		mViewOverview = getCheckedItem(Actions.viewOverview, "gui.Actions.viewOverview", KeyEvent.VK_F7, 0);
		mViewPageLeft = getItem(Actions.viewPageLeft, "gui.Actions.viewPageLeft", 0, 0);
		mViewPageRight = getItem(Actions.viewPageRight, "gui.Actions.viewPageRight", 0, 0);
		mViewGenotypes = getRadioItem(Actions.viewGenotypes, "gui.Actions.viewGenotypes",
			KeyEvent.VK_5, InputEvent.ALT_MASK);
		mViewChromosomes = getRadioItem(Actions.viewChromosomes, "gui.Actions.viewChromosomes",
			KeyEvent.VK_6, InputEvent.ALT_MASK);

		mView.add(mViewNewView);
		mView.add(mViewRenameView);
		mView.add(mViewDeleteView);
		mView.addSeparator();
		mView.add(mViewGenotypes);
		mView.add(mViewChromosomes);
		mView.addSeparator();
		mView.add(mViewPageLeft);
		mView.add(mViewPageRight);
		mView.addSeparator();
		mView.add(mViewToggleCanvas);
		mView.add(mViewOverview);

		ButtonGroup grp = new ButtonGroup();
		grp.add(mViewGenotypes);
		grp.add(mViewChromosomes);


		add(mView);
	}

	private void createVizMenu()
	{
		mViz = new JMenu(RB.getString("gui.WinMainMenuBar.mViz"));
		RB.setMnemonic(mViz, "gui.WinMainMenuBar.mViz");

		mVizColor = new JMenu(RB.getString("gui.WinMainMenuBar.mVizColor"));
		mVizColor.setIcon(Actions.getIcon("COLORS"));
		RB.setMnemonic(mVizColor, "gui.WinMainMenuBar.mVizColor");
		winMain.mViz.handleColorMenu(mVizColor);

		mVizScaling = new JMenu(RB.getString("gui.WinMainMenuBar.mVizScaling"));
		RB.setMnemonic(mVizScaling, "gui.WinMainMenuBar.mVizScaling");

		mVizHighlight = new JMenu(RB.getString("gui.WinMainMenuBar.mVizHighlight"));
		RB.setMnemonic(mVizHighlight, "gui.WinMainMenuBar.mVizHighlight");

		mVizExportImage = getItem(Actions.vizExportImage, "gui.Actions.vizExportImage", 0, 0);
		mVizExportData = getItem(Actions.vizExportData, "gui.Actions.vizExportData", 0, 0);
		mVizCreatePedigree = getItem(Actions.vizCreatePedigree, "gui.Actions.vizCreatePedigree", 0, 0);
		mVizColorCustomize = getItem(Actions.vizColorCustomize, "gui.Actions.vizColorCustomize", 0, 0);
		mVizColorRandom = getRadioItem(Actions.vizColorRandom, "gui.Actions.vizColorRandom", 0, 0);
		mVizColorRandomWSP = getRadioItem(Actions.vizColorRandomWSP, "gui.Actions.vizColorRandomWSP", 0, 0);
		mVizColorNucleotide = getRadioItem(Actions.vizColorNucleotide, "gui.Actions.vizColorNucleotide", 0, 0);
		mVizColorNucleotide01 = getRadioItem(Actions.vizColorNucleotide01, "gui.Actions.vizColorNucleotide01", 0, 0);
		mVizColorABHData = getRadioItem(Actions.vizColorABHData, "gui.Actions.vizColorABHData", 0, 0);
		mVizColorLineSim = getRadioItem(Actions.vizColorLineSim, "gui.Actions.vizColorLineSim", 0, 0);
//		mVizColorLineSimGS = getRadioItem(Actions.vizColorLineSimGS, KeyEvent.VK_G, 0, 0);
		mVizColorMarkerSim = getRadioItem(Actions.vizColorMarkerSim, "gui.Actions.vizColorMarkerSim", 0, 0);
//		mVizColorMarkerSimGS = getRadioItem(Actions.vizColorMarkerSimGS, KeyEvent.VK_G, 0, 0);
		mVizColorSimple2Color = getRadioItem(Actions.vizColorSimple2Color, "gui.Actions.vizColorSimple2Color", 0, 0);
		mVizColorAlleleFreq = getRadioItem(Actions.vizColorAlleleFreq, "gui.Actions.vizColorAlleleFreq", 0, 0);
		mVizColorBinned = getRadioItem(Actions.vizColorBinned, "gui.Actions.vizColorBinned", 0, 0);
		mVizScalingLocal = getCheckedItem(Actions.vizScalingLocal, "gui.Actions.vizScalingLocal", 0, 0);
		mVizScalingGlobal = getCheckedItem(Actions.vizScalingGlobal, "gui.Actions.vizScalingGlobal", 0, 0);
		mVizScalingClassic = getCheckedItem(Actions.vizScalingClassic, "gui.Actions.vizScalingClassic", 0, 0);
		mVizOverlayGenotypes = getCheckedItem(Actions.vizOverlayGenotypes, "gui.Actions.vizOverlayGenotypes",
			KeyEvent.VK_G, menuShortcut);
		mVizDisableGradients = getCheckedItem(Actions.vizDisableGradients, "gui.Actions.vizDisableGradients", 0, 0);
		mVizHighlightHtZ = getCheckedItem(Actions.vizHighlightHtZ, "gui.Actions.vizHighlightHtZ", 0, 0);
		mVizHighlightHoZ = getCheckedItem(Actions.vizHighlightHoZ, "gui.Actions.vizHighlightHoZ", 0, 0);
		mVizHighlightGaps = getCheckedItem(Actions.vizHighlightGaps, "gui.Actions.vizHighlightGaps", 0, 0);


		ButtonGroup grp = new ButtonGroup();
		grp.add(mVizScalingLocal);
		grp.add(mVizScalingGlobal);
		grp.add(mVizScalingClassic);

		mVizColor.add(mVizColorNucleotide);
		mVizColor.add(mVizColorNucleotide01);
		mVizColor.add(mVizColorABHData);
		mVizColor.add(mVizColorSimple2Color);
		mVizColor.add(mVizColorLineSim);
//		mVizColor.add(mVizColorLineSimGS);
		mVizColor.add(mVizColorMarkerSim);
//		mVizColor.add(mVizColorMarkerSimGS);
		mVizColor.add(mVizColorAlleleFreq);
		mVizColor.add(mVizColorBinned);
		mVizColor.addSeparator();
		mVizColor.add(mVizColorRandom);
		mVizColor.add(mVizColorRandomWSP);
		mVizColor.addSeparator();
		mVizColor.add(mVizColorCustomize);

		mVizHighlight.add(mVizHighlightHtZ);
		mVizHighlight.add(mVizHighlightHoZ);
		mVizHighlight.add(mVizHighlightGaps);

		mVizScaling.add(mVizScalingLocal);
		mVizScaling.add(mVizScalingGlobal);
		mVizScaling.add(mVizScalingClassic);

		mViz.add(mVizExportImage);
		mViz.add(mVizExportData);
		mViz.addSeparator();
//		mViz.add(mVizCreatePedigree);
//		mViz.addSeparator();
		mViz.add(mVizScaling);
		mViz.addSeparator();
		mViz.add(mVizColor);
		mViz.add(mVizOverlayGenotypes);
		mViz.add(mVizDisableGradients);
		mViz.add(mVizHighlight);

		add(mViz);
	}

	private void createAnalysisMenu()
	{
		mAnalysis = new JMenu(RB.getString("gui.WinMainMenuBar.mAnalysis"));
		RB.setMnemonic(mAnalysis, "gui.WinMainMenuBar.mAnalysis");

		mAlysSortLines = new JMenu(RB.getString("gui.WinMainMenuBar.mAlysSortLines"));
		RB.setMnemonic(mAlysSortLines, "gui.WinMainMenuBar.mAlysSortLines");

		mAlysSortLinesBySimilarity = getItem(Actions.alysSortLinesBySimilarity, "gui.Actions.alysSortLinesBySimilarity", 0, 0);
		mAlysSortLinesByTrait = getItem(Actions.alysSortLinesByTrait, "gui.Actions.alysSortLinesByTrait", 0, 0);
		mAlysSortLinesByExternal = getItem(Actions.alysSortLinesByExternal, "gui.Actions.alysSortLinesByExternal", 0, 0);
		mAlysSortLinesAlphabetically = getItem(Actions.alysSortLinesAlphabetically, "gui.Actions.alysSortLinesAlphabetically", 0, 0);

		mAlysSimMatrix = getItem(Actions.alysSimMatrix, "gui.Actions.alysSimMatrix", 0, 0);
		mAlysDendrogram = getItem(Actions.alysDendrogram, "gui.Actions.alysDendrogram", 0, 0);
		mAlysPCoA = getItem(Actions.alysPCoA, "gui.Actions.alysPCoA", 0, 0);

		mAlysSortLines.add(mAlysSortLinesAlphabetically);
		mAlysSortLines.addSeparator();
		mAlysSortLines.add(mAlysSortLinesBySimilarity);
		mAlysSortLines.add(mAlysSortLinesByTrait);
		mAlysSortLines.add(mAlysSortLinesByExternal);

		mAnalysis.add(mAlysSortLines);
		mAnalysis.addSeparator();
		mAnalysis.add(mAlysSimMatrix);
		mAnalysis.add(mAlysDendrogram);
		mAnalysis.add(mAlysPCoA);

		add(mAnalysis);
	}

	private void createDataMenu()
	{
		mData = new JMenu(RB.getString("gui.WinMainMenuBar.mData"));
		RB.setMnemonic(mData, "gui.WinMainMenuBar.mData");

		mDataDB = new JMenu(RB.getString("gui.WinMainMenuBar.mDataDB"));
		mDataDB.setIcon(Actions.getIcon("DATABASE"));
		RB.setMnemonic(mDataDB, "gui.WinMainMenuBar.mDataDB");

		malysFilterQTLs = getItem(Actions.dataFilterQTLs, "gui.Actions.dataFilterQTLs", 0, 0);
		mDataSelectGraph = getItem(Actions.dataSelectGraph, "gui.Actions.dataSelectGraph", 0, 0);
		mDataFind = getItem(Actions.dataFind, "gui.Actions.dataFind", KeyEvent.VK_F, menuShortcut);

		mDataStatistics = getItem(Actions.dataStatistics, "gui.Actions.dataStatistics", 0, 0);
		mDataDBLineName = getItem(Actions.dataDBLineName, "gui.Actions.dataDBLineName", 0, 0);
		mDataDBMarkerName = getItem(Actions.dataDBMarkerName, "gui.Actions.dataDBMarkerName", 0, 0);
		mDataDBSettings = getItem(Actions.dataDBSettings, "gui.Actions.dataDBSettings", 0, 0);
		mDataRenameDataSet = getItem(Actions.dataRenameDataSet, "gui.Actions.dataRenameDataSet", 0, 0);
		mDataDeleteDataSet = getItem(Actions.dataDeleteDataSet, "gui.Actions.dataDeleteDataSet", 0, 0);
		mDataSelectTraits = getItem(Actions.dataSelectTraits, "gui.Actions.dataSelectTraits", 0, 0);

		mDataDB.add(mDataDBLineName);
		mDataDB.add(mDataDBMarkerName);
		mDataDB.addSeparator();
		mDataDB.add(mDataDBSettings);

		mData.add(malysFilterQTLs);
		mData.add(mDataSelectTraits);
		mData.add(mDataSelectGraph);
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
		mWndFlapjack = getCheckedItem(Actions.wndFlapjack, "gui.Actions.wndFlapjack", 0, 0);

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
		mHelpPrefs = getItem(Actions.helpPrefs, "gui.Actions.helpPrefs", 0, 0);
		mHelpUpdate = getItem(Actions.helpUpdate, "gui.Actions.helpUpdate", 0, 0);
		mHelpAbout = getItem(Actions.helpAbout, "gui.Actions.helpAbout", 0, 0);

		mHelp.add(mHelpContents);
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

	public static JCheckBoxMenuItem getCheckedItem(Action action, String key, int keymask, int modifiers)
	{
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
		RB.setMnemonic(item, key);

		if (keymask != 0)
			item.setAccelerator(KeyStroke.getKeyStroke(keymask, modifiers));

		return item;
	}

	public static JRadioButtonMenuItem getRadioItem(Action action, String key, int keymask, int modifiers)
	{
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(action);
		RB.setMnemonic(item, key);

		if (keymask != 0)
			item.setAccelerator(KeyStroke.getKeyStroke(keymask, modifiers));

		return item;
	}

	// Maintains and creates the Recent Projects file menu, adding new entries
	// as previously unseen projects are opened or saved, and ensuring that:
	//   a) the most recently accessed file is always at the start of the list
	//   b) the list never grows bigger than four entries
	void createRecentMenu(FlapjackFile file)
	{
		// Begin by making a list of the recent file locations
		LinkedList<String> entries = new LinkedList<>();
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
						winMain.mFile.fileOpen(new FlapjackFile(entry));
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