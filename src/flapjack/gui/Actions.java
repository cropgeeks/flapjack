// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.awt.event.*;
import javax.swing.*;

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
	public static AbstractAction editSelectLinesAll;
	public static AbstractAction editSelectLinesNone;
	public static AbstractAction editSelectLinesInvert;
	public static AbstractAction editHideLines;
	public static AbstractAction editInsertLine;
	public static AbstractAction editDeleteLine;

	public static AbstractAction vizExportImage;
	public static AbstractAction vizExportData;
	public static AbstractAction vizCreatePedigree;
	public static AbstractAction vizColorCustomize;
	public static AbstractAction vizColorRandom;
	public static AbstractAction vizColorRandomWSP;
	public static AbstractAction vizColorNucleotide;
	public static AbstractAction vizColorLineSim;
	public static AbstractAction vizColorLineSimGS;
	public static AbstractAction vizColorMarkerSim;
	public static AbstractAction vizColorMarkerSimGS;
	public static AbstractAction vizColorSimple2Color;
	public static AbstractAction vizColorAlleleFreq;
	public static AbstractAction vizOverlayGenotypes;
	public static AbstractAction vizHighlightHZ;
	public static AbstractAction vizHighlightGaps;
	public static AbstractAction vizSelectTraits;
	public static AbstractAction vizNewView;
	public static AbstractAction vizRenameView;
	public static AbstractAction vizDeleteView;
	public static AbstractAction vizToggleCanvas;
	public static AbstractAction vizOverview;
	public static AbstractAction vizBookmark;
	public static AbstractAction vizDeleteBookmark;

	public static AbstractAction dataSortLinesBySimilarity;
	public static AbstractAction dataSortLinesByTrait;
	public static AbstractAction dataSortLinesAlphabetically;
	public static AbstractAction dataFilterQTLs;
	public static AbstractAction dataFind;
	public static AbstractAction dataStatistics;
	public static AbstractAction dataDBLineName;
	public static AbstractAction dataDBMarkerName;
	public static AbstractAction dataDBSettings;
	public static AbstractAction dataRenameDataSet;
	public static AbstractAction dataDeleteDataSet;

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
				winMain.mFile.fileImport();
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

		vizSelectTraits = new AbstractAction(RB.getString("gui.Actions.vizSelectTraits")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizSelectTraits();
			}
		};

		vizNewView = new AbstractAction(RB.getString("gui.Actions.vizNewView")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizNewView();
			}
		};

		vizRenameView = new AbstractAction(RB.getString("gui.Actions.vizRenameView"), getIcon("RENAME")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizRenameView();
			}
		};

		vizDeleteView = new AbstractAction(RB.getString("gui.Actions.vizDeleteView"), getIcon("DELETE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizDeleteView();
			}
		};

		vizToggleCanvas = new AbstractAction(RB.getString("gui.Actions.vizToggleCanvas")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizToggleCanvas();
			}
		};

		vizBookmark = new AbstractAction(RB.getString("gui.Actions.vizBookmark"), getIcon("BOOKMARKADD")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizBookmark();
			}
		};

		vizDeleteBookmark = new AbstractAction(RB.getString("gui.Actions.vizDeleteBookmark"), getIcon("DELETE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizDeleteBookmark();
			}
		};

		vizOverview = new AbstractAction(RB.getString("gui.Actions.vizOverview")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mViz.vizOverview();
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

		dataSortLinesAlphabetically = new AbstractAction(RB.getString("gui.Actions.dataSortLinesAlphabetically")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataSortLinesAlphabetically();
			}
		};

		dataFilterQTLs = new AbstractAction(RB.getString("gui.Actions.dataFilterQTLs")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mData.dataFilterQTLs();
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
		editSelectLinesAll.setEnabled(false);
		editSelectLinesNone.setEnabled(false);
		editSelectLinesInvert.setEnabled(false);
		editHideLines.setEnabled(false);
		editInsertLine.setEnabled(false);
		editDeleteLine.setEnabled(false);

		vizExportImage.setEnabled(false);
		vizExportData.setEnabled(false);
		vizCreatePedigree.setEnabled(false);
		vizColorCustomize.setEnabled(false);
		vizColorRandom.setEnabled(false);
		vizColorRandomWSP.setEnabled(false);
		vizColorNucleotide.setEnabled(false);
		vizColorLineSim.setEnabled(false);
		vizColorLineSimGS.setEnabled(false);
		vizColorMarkerSim.setEnabled(false);
		vizColorMarkerSimGS.setEnabled(false);
		vizColorSimple2Color.setEnabled(false);
		vizColorAlleleFreq.setEnabled(false);
		vizOverlayGenotypes.setEnabled(false);
		vizHighlightHZ.setEnabled(false);
		vizHighlightGaps.setEnabled(false);
		vizSelectTraits.setEnabled(false);
		vizNewView.setEnabled(false);
		vizRenameView.setEnabled(false);
		vizDeleteView.setEnabled(false);
		vizToggleCanvas.setEnabled(false);
		vizOverview.setEnabled(false);
		vizBookmark.setEnabled(false);
		vizDeleteBookmark.setEnabled(false);

		dataSortLinesBySimilarity.setEnabled(false);
		dataSortLinesByTrait.setEnabled(false);
		dataSortLinesAlphabetically.setEnabled(false);
		dataFilterQTLs.setEnabled(false);
		dataFind.setEnabled(false);
		dataStatistics.setEnabled(false);
		dataDBLineName.setEnabled(false);
		dataDBMarkerName.setEnabled(false);
		dataDBSettings.setEnabled(false);
		dataRenameDataSet.setEnabled(false);
		dataDeleteDataSet.setEnabled(false);

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