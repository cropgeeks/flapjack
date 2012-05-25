// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.awt.event.*;
import javax.swing.*;
import static javax.swing.Action.*;

import flapjack.gui.visualization.colors.*;

import scri.commons.gui.*;

public class Actions
{
	private WinMain winMain;

	public static AbstractAction fileNew;
	public static AbstractAction fileOpen;
	public static AbstractAction fileSave;
	public static AbstractAction fileSaveAs;
	public static AbstractAction fileImport;
	public static AbstractAction fileExport;
	public static AbstractAction fileExit;

	public static AbstractAction editUndo;
	public static AbstractAction editRedo;
	public static AbstractAction editModeNavigation;
	public static AbstractAction editModeMarker;
	public static AbstractAction editModeLine;
	public static AbstractAction editSelectMarkersAll;
	public static AbstractAction editSelectMarkersNone;
	public static AbstractAction editSelectMarkersInvert;
	public static AbstractAction editHideMarkers;
	public static AbstractAction editFilterMissingMarkers;
	public static AbstractAction editSelectLinesAll;
	public static AbstractAction editSelectLinesNone;
	public static AbstractAction editSelectLinesInvert;
	public static AbstractAction editHideLines;
	public static AbstractAction editInsertLine;
	public static AbstractAction editDeleteLine;
	public static AbstractAction editInsertSplitter;
	public static AbstractAction editDeleteSplitter;

	public static AbstractAction viewNewView;
	public static AbstractAction viewRenameView;
	public static AbstractAction viewDeleteView;
	public static AbstractAction viewToggleCanvas;
	public static AbstractAction viewOverview;
	public static AbstractAction viewBookmark;
	public static AbstractAction viewDeleteBookmark;
	public static AbstractAction viewPageLeft;
	public static AbstractAction viewPageRight;

	public static AbstractAction vizExportImage;
	public static AbstractAction vizExportData;
	public static AbstractAction vizCreatePedigree;
	public static AbstractAction vizColorCustomize;
	public static AbstractAction vizColorRandom;
	public static AbstractAction vizColorRandomWSP;
	public static AbstractAction vizColorNucleotide;
	public static AbstractAction vizColorABHData;
	public static AbstractAction vizColorLineSim;
	public static AbstractAction vizColorLineSimGS;
	public static AbstractAction vizColorMarkerSim;
	public static AbstractAction vizColorMarkerSimGS;
	public static AbstractAction vizColorSimple2Color;
	public static AbstractAction vizColorAlleleFreq;
	public static AbstractAction vizScalingLocal;
	public static AbstractAction vizScalingGlobal;
	public static AbstractAction vizScalingClassic;
	public static AbstractAction vizOverlayGenotypes;
	public static AbstractAction vizHighlightHZ;
	public static AbstractAction vizHighlightGaps;

	public static AbstractAction dataSortLinesBySimilarity;
	public static AbstractAction dataSortLinesByTrait;
	public static AbstractAction dataSortLinesByExternal;
	public static AbstractAction dataSortLinesAlphabetically;
	public static AbstractAction dataFilterQTLs;
	public static AbstractAction dataSelectGraph;
	public static AbstractAction dataFind;
	public static AbstractAction dataSimMatrix;
	public static AbstractAction dataStatistics;
	public static AbstractAction dataDBLineName;
	public static AbstractAction dataDBMarkerName;
	public static AbstractAction dataDBSettings;
	public static AbstractAction dataRenameDataSet;
	public static AbstractAction dataDeleteDataSet;
	public static AbstractAction dataSelectTraits;

	public static AbstractAction wndMinimize;
	public static AbstractAction wndZoom;
	public static AbstractAction wndFlapjack;

	public static AbstractAction helpContents;
	public static AbstractAction helpLicence;
	public static AbstractAction helpAbout;
	public static AbstractAction helpUpdate;
	public static AbstractAction helpPrefs;

	Actions(WinMain winMain)
	{
		this.winMain = winMain;

		createActions();
		setInitialStates();

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

	public static ImageIcon getIcon(String name)
	{
		ImageIcon icon = Icons.getIcon(name);

		if (SystemUtils.isMacOS())
			return null;
		else
			return icon;
	}

	private void createActions()
	{
		fileNew = new AbstractAction(RB.getString("gui.Actions.fileNew"), getIcon("FILENEW")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mFile.fileNew();
			}
		};

		fileOpen = new AbstractAction(RB.getString("gui.Actions.fileOpen"), getIcon("FILEOPEN")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mFile.fileOpen(null);
			}
		};

		fileSave = new AbstractAction(RB.getString("gui.Actions.fileSave"), getIcon("FILESAVE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mFile.fileSave(false);
			}
		};

		fileSaveAs = new AbstractAction(RB.getString("gui.Actions.fileSaveAs"), getIcon("FILESAVEAS")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mFile.fileSave(true);
			}
		};

		fileImport = new AbstractAction(RB.getString("gui.Actions.fileImport"), getIcon("FILEIMPORT")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mFile.fileImport(0);
			}
		};

		fileExport = new AbstractAction(RB.getString("gui.Actions.fileExport")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mFile.fileExport();
			}
		};

		fileExit = new AbstractAction(RB.getString("gui.Actions.fileExit")) {
			public void actionPerformed(ActionEvent e) {
				winMain.fileExit();
			}
		};


		editUndo = new AbstractAction(RB.format("gui.Actions.editUndo", ""), getIcon("UNDO")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editUndoRedo(true);
			}
		};

		editRedo = new AbstractAction(RB.format("gui.Actions.editRedo", ""), getIcon("REDO")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editUndoRedo(false);
			}
		};

		editModeNavigation = new AbstractAction(RB.format("gui.Actions.editModeNavigation", "")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editMode(Constants.NAVIGATION);
			}
		};

		editModeMarker = new AbstractAction(RB.format("gui.Actions.editModeMarker", "")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editMode(Constants.MARKERMODE);
			}
		};

		editModeLine = new AbstractAction(RB.format("gui.Actions.editModeLine", "")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editMode(Constants.LINEMODE);
			}
		};

		editSelectMarkersAll = new AbstractAction(RB.format("gui.Actions.editSelectMarkersAll", ""), getIcon("SELECTALL")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editSelectMarkers(Constants.SELECT_ALL);
			}
		};

		editSelectMarkersNone = new AbstractAction(RB.format("gui.Actions.editSelectMarkersNone", ""), getIcon("SELECTNONE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editSelectMarkers(Constants.SELECT_NONE);
			}
		};

		editSelectMarkersInvert = new AbstractAction(RB.format("gui.Actions.editSelectMarkersInvert", ""), getIcon("INVERT")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editSelectMarkers(Constants.SELECT_INVERT);
			}
		};

		editHideMarkers = new AbstractAction(RB.format("gui.Actions.editHideMarkers", "")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editHideMarkers();
			}
		};

		editFilterMissingMarkers = new AbstractAction(RB.getString("gui.Actions.editFilterMissingMarkers")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editFilterMissingMarkers();
			}
		};

		editSelectLinesAll = new AbstractAction(RB.format("gui.Actions.editSelectLinesAll", ""), getIcon("SELECTALL")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editSelectLines(Constants.SELECT_ALL);
			}
		};

		editSelectLinesNone = new AbstractAction(RB.format("gui.Actions.editSelectLinesNone", ""), getIcon("SELECTNONE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editSelectLines(Constants.SELECT_NONE);
			}
		};

		editSelectLinesInvert = new AbstractAction(RB.format("gui.Actions.editSelectLinesInvert", ""), getIcon("INVERT")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editSelectLines(Constants.SELECT_INVERT);
			}
		};

		editHideLines = new AbstractAction(RB.format("gui.Actions.editHideLines", "")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editHideLines();
			}
		};

		editInsertLine = new AbstractAction(RB.format("gui.Actions.editInsertLine", "")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editInsertLine();
			}
		};

		editDeleteLine = new AbstractAction(RB.format("gui.Actions.editDeleteLine", "")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editDeleteLine();
			}
		};

		editInsertSplitter = new AbstractAction(RB.format("gui.Actions.editInsertSplitter", "")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editInsertSplitter();
			}
		};

		editDeleteSplitter = new AbstractAction(RB.format("gui.Actions.editDeleteSplitter", "")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mEdit.editDeleteSplitter();
			}
		};

		viewNewView = new AbstractAction(RB.getString("gui.Actions.viewNewView")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.viewNewView();
			}
		};

		viewRenameView = new AbstractAction(RB.getString("gui.Actions.viewRenameView"), getIcon("RENAME")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.viewRenameView();
			}
		};

		viewDeleteView = new AbstractAction(RB.getString("gui.Actions.viewDeleteView"), getIcon("DELETE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.viewDeleteView();
			}
		};

		viewToggleCanvas = new AbstractAction(RB.getString("gui.Actions.viewToggleCanvas")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.viewToggleCanvas();
			}
		};

		viewBookmark = new AbstractAction(RB.getString("gui.Actions.viewBookmark"), getIcon("BOOKMARKADD")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.viewBookmark();
			}
		};

		viewDeleteBookmark = new AbstractAction(RB.getString("gui.Actions.viewDeleteBookmark"), getIcon("DELETE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.viewDeleteBookmark();
			}
		};

		viewOverview = new AbstractAction(RB.getString("gui.Actions.viewOverview")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.viewOverview();
			}
		};

		viewPageLeft = new AbstractAction(RB.getString("gui.Actions.viewPageLeft")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.viewPageLeft();
			}
		};

		viewPageRight = new AbstractAction(RB.getString("gui.Actions.viewPageRight")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.viewPageRight();
			}
		};


		vizExportImage = new AbstractAction(RB.getString("gui.Actions.vizExportImage")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizExportImage();
			}
		};

		vizExportData = new AbstractAction(RB.getString("gui.Actions.vizExportData")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizExportData();
			}
		};

		vizCreatePedigree = new AbstractAction(RB.getString("gui.Actions.vizCreatePedigree")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizCreatePedigree();
			}
		};

		vizColorCustomize = new AbstractAction(RB.getString("gui.Actions.vizColorCustomize")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizColorCustomize();
			}
		};

		vizColorRandom = new AbstractAction(RB.getString("gui.Actions.vizColorRandom")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizColor(ColorScheme.RANDOM);
			}
		};

		vizColorRandomWSP = new AbstractAction(RB.getString("gui.Actions.vizColorRandomWSP")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizColor(ColorScheme.RANDOM_WSP);
			}
		};

		vizColorNucleotide = new AbstractAction(RB.getString("gui.Actions.vizColorNucleotide")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizColor(ColorScheme.NUCLEOTIDE);
			}
		};

		vizColorABHData = new AbstractAction(RB.getString("gui.Actions.vizColorABHData")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizColor(ColorScheme.ABH_DATA);
			}
		};

		vizColorLineSim = new AbstractAction(RB.getString("gui.Actions.vizColorLineSim")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizColor(ColorScheme.LINE_SIMILARITY);
			}
		};

		vizColorLineSimGS = new AbstractAction(RB.getString("gui.Actions.vizColorLineSimGS")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizColor(ColorScheme.LINE_SIMILARITY_GS);
			}
		};

		vizColorMarkerSim = new AbstractAction(RB.getString("gui.Actions.vizColorMarkerSim")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizColor(ColorScheme.MARKER_SIMILARITY);
			}
		};

		vizColorMarkerSimGS = new AbstractAction(RB.getString("gui.Actions.vizColorMarkerSimGS")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizColor(ColorScheme.MARKER_SIMILARITY_GS);
			}
		};

		vizColorSimple2Color = new AbstractAction(RB.getString("gui.Actions.vizColorSimple2Color")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizColor(ColorScheme.SIMPLE_TWO_COLOR);
			}
		};

		vizColorAlleleFreq = new AbstractAction(RB.getString("gui.Actions.vizColorAlleleFreq")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizColor(ColorScheme.ALLELE_FREQUENCY);
			}
		};

		vizScalingLocal = new AbstractAction(RB.getString("gui.Actions.vizScalingLocal")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizScaling(Constants.LOCAL);
			}
		};

		vizScalingGlobal = new AbstractAction(RB.getString("gui.Actions.vizScalingGlobal")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizScaling(Constants.GLOBAL);
			}
		};

		vizScalingClassic = new AbstractAction(RB.getString("gui.Actions.vizScalingClassic")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizScaling(Constants.CLASSIC);
			}
		};

		vizOverlayGenotypes = new AbstractAction(RB.getString("gui.Actions.vizOverlayGenotypes")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizOverlayGenotypes();
			}
		};

		vizHighlightHZ = new AbstractAction(RB.getString("gui.Actions.vizHighlightHZ")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizHighlightHZ();
			}
		};

		vizHighlightGaps = new AbstractAction(RB.getString("gui.Actions.vizHighlightGaps")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizHighlightGaps();
			}
		};


		dataSortLinesBySimilarity = new AbstractAction(RB.getString("gui.Actions.dataSortLinesBySimilarity")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataSortLines();
			}
		};

		dataSortLinesByTrait = new AbstractAction(RB.getString("gui.Actions.dataSortLinesByTrait")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataSortLinesByTrait();
			}
		};

		dataSortLinesByExternal = new AbstractAction(RB.getString("gui.Actions.dataSortLinesByExternal")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataSortLinesByExternal();
			}
		};

		dataSortLinesAlphabetically = new AbstractAction(RB.getString("gui.Actions.dataSortLinesAlphabetically")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataSortLinesAlphabetically();
			}
		};

		dataFilterQTLs = new AbstractAction(RB.getString("gui.Actions.dataFilterQTLs"), getIcon("TRAITS")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataFilterQTLs();
			}
		};

		dataSelectGraph = new AbstractAction(RB.getString("gui.Actions.dataSelectGraph")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataSelectGraph();
			}
		};

		dataSimMatrix = new AbstractAction(RB.getString("gui.Actions.dataSimMatrix")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataSimMatrix();
			}
		};

		dataFind = new AbstractAction(RB.getString("gui.Actions.dataFind"), getIcon("FIND")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataFind();
			}
		};

		dataStatistics = new AbstractAction(RB.getString("gui.Actions.dataStatistics")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataStatistics();
			}
		};

		dataDBLineName = new AbstractAction(RB.getString("gui.Actions.dataDBLineName")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataDBLineName();
			}
		};

		dataDBMarkerName = new AbstractAction(RB.getString("gui.Actions.dataDBMarkerName")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataDBMarkerName();
			}
		};

		dataDBSettings = new AbstractAction(RB.getString("gui.Actions.dataDBSettings"), getIcon("PREFERENCES")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataDBSettings();
			}
		};

		dataRenameDataSet = new AbstractAction(RB.getString("gui.Actions.dataRenameDataSet"), getIcon("RENAME")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataRenameDataSet();
			}
		};

		dataDeleteDataSet = new AbstractAction(RB.getString("gui.Actions.dataDeleteDataSet"), getIcon("DELETE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataDeleteDataSet();
			}
		};

		dataSelectTraits = new AbstractAction(RB.getString("gui.Actions.dataSelectTraits")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataSelectTraits();
			}
		};


		wndMinimize = new AbstractAction(RB.getString("gui.Actions.wndMinimize")) {
			public void actionPerformed(ActionEvent e) {
				Flapjack.osxMinimize();
			}
		};

		wndZoom = new AbstractAction(RB.getString("gui.Actions.wndZoom")) {
			public void actionPerformed(ActionEvent e) {
				Flapjack.osxZoom();
			}
		};

		wndFlapjack = new AbstractAction(RB.getString("gui.Actions.wndFlapjack")) {
			public void actionPerformed(ActionEvent e) {
				Flapjack.osxFlapjack();
			}
		};


		helpContents = new AbstractAction(RB.getString("gui.Actions.helpContents"), getIcon("HELP")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mHelp.helpContents();
			}
		};

		helpLicence = new AbstractAction(RB.getString("gui.Actions.helpLicence")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mHelp.helpLicence();
			}
		};

		helpAbout = new AbstractAction(RB.getString("gui.Actions.helpAbout")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mHelp.helpAbout();
			}
		};

		helpUpdate = new AbstractAction(RB.getString("gui.Actions.helpUpdate"),  getIcon("CHECKUPDATE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mHelp.helpUpdate();
			}
		};

		helpPrefs = new AbstractAction(RB.getString("gui.Actions.helpPrefs"), getIcon("PREFERENCES")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mHelp.helpPrefs();
			}
		};
	}

	private static void setInitialStates()
	{
		wndFlapjack.putValue(SELECTED_KEY, true);

		editModeNavigation.putValue(SELECTED_KEY,
			Prefs.guiMouseMode == Constants.NAVIGATION);
		editModeMarker.putValue(SELECTED_KEY,
			Prefs.guiMouseMode == Constants.MARKERMODE);
		editModeLine.putValue(SELECTED_KEY,
			Prefs.guiMouseMode == Constants.LINEMODE);

		viewOverview.putValue(SELECTED_KEY,	Prefs.guiOverviewDialog);

		vizScalingLocal.putValue(SELECTED_KEY,
			Prefs.visMapScaling == Constants.LOCAL);
		vizScalingGlobal.putValue(SELECTED_KEY,
			Prefs.visMapScaling == Constants.GLOBAL);
		vizScalingClassic.putValue(SELECTED_KEY,
			Prefs.visMapScaling == Constants.CLASSIC);

		vizOverlayGenotypes.putValue(SELECTED_KEY, Prefs.visShowGenotypes);
		vizHighlightHZ.putValue(SELECTED_KEY, Prefs.visHighlightHZ);
		vizHighlightGaps.putValue(SELECTED_KEY, Prefs.visHighlightGaps);
	}

	/** Called whenever the focus on the navigation tree changes. **/
	public static void resetActions()
	{
		editUndo.setEnabled(false);
		editRedo.setEnabled(false);
		editModeNavigation.setEnabled(false);
		editModeMarker.setEnabled(false);
		editModeLine.setEnabled(false);
		editSelectMarkersAll.setEnabled(false);
		editSelectMarkersNone.setEnabled(false);
		editSelectMarkersInvert.setEnabled(false);
		editHideMarkers.setEnabled(false);
		editFilterMissingMarkers.setEnabled(false);
		editSelectLinesAll.setEnabled(false);
		editSelectLinesNone.setEnabled(false);
		editSelectLinesInvert.setEnabled(false);
		editHideLines.setEnabled(false);
		editInsertLine.setEnabled(false);
		editDeleteLine.setEnabled(false);
		editInsertSplitter.setEnabled(false);
		editDeleteSplitter.setEnabled(false);

		viewNewView.setEnabled(false);
		viewRenameView.setEnabled(false);
		viewDeleteView.setEnabled(false);
		viewToggleCanvas.setEnabled(false);
		viewOverview.setEnabled(false);
		viewBookmark.setEnabled(false);
		viewDeleteBookmark.setEnabled(false);
		viewPageLeft.setEnabled(false);
		viewPageRight.setEnabled(false);

		vizExportImage.setEnabled(false);
		vizExportData.setEnabled(false);
		vizCreatePedigree.setEnabled(false);
		vizColorCustomize.setEnabled(false);
		vizColorRandom.setEnabled(false);
		vizColorRandomWSP.setEnabled(false);
		vizColorNucleotide.setEnabled(false);
		vizColorABHData.setEnabled(false);
		vizColorLineSim.setEnabled(false);
		vizColorLineSimGS.setEnabled(false);
		vizColorMarkerSim.setEnabled(false);
		vizColorMarkerSimGS.setEnabled(false);
		vizColorSimple2Color.setEnabled(false);
		vizColorAlleleFreq.setEnabled(false);
		vizScalingLocal.setEnabled(false);
		vizScalingGlobal.setEnabled(false);
		vizScalingClassic.setEnabled(false);
		vizOverlayGenotypes.setEnabled(false);
		vizHighlightHZ.setEnabled(false);
		vizHighlightGaps.setEnabled(false);

		dataSortLinesBySimilarity.setEnabled(false);
		dataSortLinesByTrait.setEnabled(false);
		dataSortLinesByExternal.setEnabled(false);
		dataSortLinesAlphabetically.setEnabled(false);
		dataFilterQTLs.setEnabled(false);
		dataSelectGraph.setEnabled(false);
		dataFind.setEnabled(false);
		dataSimMatrix.setEnabled(false);
		dataStatistics.setEnabled(false);
		dataDBLineName.setEnabled(false);
		dataDBMarkerName.setEnabled(false);
		dataDBSettings.setEnabled(false);
		dataRenameDataSet.setEnabled(false);
		dataDeleteDataSet.setEnabled(false);
		dataSelectTraits.setEnabled(false);

		// Special case for the Edit->Undo/Redo options who have their text
		// dynamically set - this resets the text to its default
		// Note: it's an explicit setText() rather than putValue("Name", "X")
		// on the Action, as the latter would also apply text to the toolbar
		// buttons which isn't what we want to happen
		if (WinMainMenuBar.mEditUndo != null)
		{
			WinMainMenuBar.mEditUndo.setText(
				RB.getString("gui.Actions.editUndo"));
			WinMainMenuBar.mEditRedo.setText(
				RB.getString("gui.Actions.editRedo"));

			WinMainToolBar.editUndo.setToolTipText(
				RB.getString("gui.WinMainToolBar.editUndo"));
			WinMainToolBar.editRedo.setToolTipText(
				RB.getString("gui.WinMainToolBar.editRedo"));
		}
	}
}