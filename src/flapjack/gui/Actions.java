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

	public static AbstractAction vizOverview;
	public static AbstractAction vizExportImage;
	public static AbstractAction vizColorRandom;
	public static AbstractAction vizColorNucleotide;
	public static AbstractAction vizColorLineSim;
	public static AbstractAction vizColorLineSimGS;
	public static AbstractAction vizColorMarkerSim;
	public static AbstractAction vizColorMarkerSimGS;
	public static AbstractAction vizColorSimple2Color;
	public static AbstractAction vizOverlayGenotypes;
	public static AbstractAction vizNewView;
	public static AbstractAction vizRenameView;
	public static AbstractAction vizDeleteView;

	public static AbstractAction dataSortLinesBySimilarity;
	public static AbstractAction dataSortLinesByLocus;
	public static AbstractAction dataFind;
	public static AbstractAction dataRenameDataSet;
	public static AbstractAction dataDeleteDataSet;

	public static AbstractAction wndMinimize;
	public static AbstractAction wndZoom;
	public static AbstractAction wndFlapjack;

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

	private ImageIcon getIcon(ImageIcon icon)
	{
		if (SystemUtils.isMacOS())
			return null;
		else
			return icon;
	}

	private void createActions()
	{
		fileNew = new AbstractAction(RB.getString("gui.Actions.fileNew"), getIcon(Icons.FILENEW)) {
			public void actionPerformed(ActionEvent e) {
				winMain.fileNew();
			}
		};

		fileOpen = new AbstractAction(RB.getString("gui.Actions.fileOpen"), getIcon(Icons.FILEOPEN)) {
			public void actionPerformed(ActionEvent e) {
				winMain.fileOpen(null);
			}
		};

		fileSave = new AbstractAction(RB.getString("gui.Actions.fileSave"), getIcon(Icons.FILESAVE)) {
			public void actionPerformed(ActionEvent e) {
				winMain.fileSave(false);
			}
		};

		fileSaveAs = new AbstractAction(RB.getString("gui.Actions.fileSaveAs"), getIcon(Icons.FILESAVEAS)) {
			public void actionPerformed(ActionEvent e) {
				winMain.fileSave(true);
			}
		};

		fileImport = new AbstractAction(RB.getString("gui.Actions.fileImport"), getIcon(Icons.FILEIMPORT)) {
			public void actionPerformed(ActionEvent e) {
				winMain.fileImport();
			}
		};

		fileExit = new AbstractAction(RB.getString("gui.Actions.fileExit")) {
			public void actionPerformed(ActionEvent e) {
				winMain.fileExit();
			}
		};


		editUndo = new AbstractAction(RB.format("gui.Actions.editUndo", ""), getIcon(Icons.UNDO)) {
			public void actionPerformed(ActionEvent e) {
				winMain.editUndoRedo(true);
			}
		};

		editRedo = new AbstractAction(RB.format("gui.Actions.editRedo", ""), getIcon(Icons.REDO)) {
			public void actionPerformed(ActionEvent e) {
				winMain.editUndoRedo(false);
			}
		};


		vizOverview = new AbstractAction(RB.getString("gui.Actions.vizOverview")) {
			public void actionPerformed(ActionEvent e) {
				winMain.vizOverview();
			}
		};

		vizExportImage = new AbstractAction(RB.getString("gui.Actions.vizExportImage")) {
			public void actionPerformed(ActionEvent e) {
				winMain.vizExportImage();
			}
		};

		vizColorRandom = new AbstractAction(RB.getString("gui.Actions.vizColorRandom")) {
			public void actionPerformed(ActionEvent e) {
				winMain.vizColor(ColorScheme.RANDOM);
			}
		};

		vizColorNucleotide = new AbstractAction(RB.getString("gui.Actions.vizColorNucleotide")) {
			public void actionPerformed(ActionEvent e) {
				winMain.vizColor(ColorScheme.NUCLEOTIDE);
			}
		};

		vizColorLineSim = new AbstractAction(RB.getString("gui.Actions.vizColorLineSim")) {
			public void actionPerformed(ActionEvent e) {
				winMain.vizColor(ColorScheme.LINE_SIMILARITY);
			}
		};

		vizColorLineSimGS = new AbstractAction(RB.getString("gui.Actions.vizColorLineSimGS")) {
			public void actionPerformed(ActionEvent e) {
				winMain.vizColor(ColorScheme.LINE_SIMILARITY_GS);
			}
		};

		vizColorMarkerSim = new AbstractAction(RB.getString("gui.Actions.vizColorMarkerSim")) {
			public void actionPerformed(ActionEvent e) {
				winMain.vizColor(ColorScheme.MARKER_SIMILARITY);
			}
		};

		vizColorMarkerSimGS = new AbstractAction(RB.getString("gui.Actions.vizColorMarkerSimGS")) {
			public void actionPerformed(ActionEvent e) {
				winMain.vizColor(ColorScheme.MARKER_SIMILARITY_GS);
			}
		};

		vizColorSimple2Color = new AbstractAction(RB.getString("gui.Actions.vizColorSimple2Color")) {
			public void actionPerformed(ActionEvent e) {
				winMain.vizColor(ColorScheme.SIMPLE_TWO_COLOR);
			}
		};

		vizOverlayGenotypes = new AbstractAction(RB.getString("gui.Actions.vizOverlayGenotypes")) {
			public void actionPerformed(ActionEvent e) {
				winMain.vizOverlayGenotypes();
			}
		};

		vizNewView = new AbstractAction(RB.getString("gui.Actions.vizNewView")) {
			public void actionPerformed(ActionEvent e) {
				winMain.vizNewView();
			}
		};

		vizRenameView = new AbstractAction(RB.getString("gui.Actions.vizRenameView"), getIcon(Icons.RENAME)) {
			public void actionPerformed(ActionEvent e) {
				winMain.vizRenameView();
			}
		};

		vizDeleteView = new AbstractAction(RB.getString("gui.Actions.vizDeleteView"), getIcon(Icons.DELETE)) {
			public void actionPerformed(ActionEvent e) {
				winMain.vizDeleteView();
			}
		};


		dataSortLinesBySimilarity = new AbstractAction(RB.getString("gui.Actions.dataSortLinesBySimilarity")) {
			public void actionPerformed(ActionEvent e) {
				winMain.dataSortLines(0);
			}
		};

		dataSortLinesByLocus = new AbstractAction(RB.getString("gui.Actions.dataSortLinesByLocus")) {
			public void actionPerformed(ActionEvent e) {
				winMain.dataSortLines(1);
			}
		};

		dataFind = new AbstractAction(RB.getString("gui.Actions.dataFind"), getIcon(Icons.FIND)) {
			public void actionPerformed(ActionEvent e) {
				winMain.dataFind();
			}
		};

		dataRenameDataSet = new AbstractAction(RB.getString("gui.Actions.dataRenameDataSet"), getIcon(Icons.RENAME)) {
			public void actionPerformed(ActionEvent e) {
				winMain.dataRenameDataSet();
			}
		};

		dataDeleteDataSet = new AbstractAction(RB.getString("gui.Actions.dataDeleteDataSet"), getIcon(Icons.DELETE)) {
			public void actionPerformed(ActionEvent e) {
				winMain.dataDeleteDataSet();
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


		helpAbout = new AbstractAction(RB.getString("gui.Actions.helpAbout")) {
			public void actionPerformed(ActionEvent e) {
				winMain.helpAbout();
			}
		};

		helpUpdate = new AbstractAction(RB.getString("gui.Actions.helpUpdate"),  getIcon(Icons.CHECKUPDATE)) {
			public void actionPerformed(ActionEvent e) {
				winMain.helpUpdate();
			}
		};

		helpPrefs = new AbstractAction(RB.getString("gui.Actions.helpPrefs"), getIcon(Icons.PREFERENCES)) {
			public void actionPerformed(ActionEvent e) {
				winMain.helpPrefs();
			}
		};
	}

	/** Called whenever the focus on the navigation tree changes. **/
	public static void resetActions()
	{
		editUndo.setEnabled(false);
		editRedo.setEnabled(false);

		vizOverview.setEnabled(false);
		vizExportImage.setEnabled(false);
		vizColorRandom.setEnabled(false);
		vizColorNucleotide.setEnabled(false);
		vizColorLineSim.setEnabled(false);
		vizColorLineSimGS.setEnabled(false);
		vizColorMarkerSim.setEnabled(false);
		vizColorMarkerSimGS.setEnabled(false);
		vizColorSimple2Color.setEnabled(false);
		vizOverlayGenotypes.setEnabled(false);
		vizNewView.setEnabled(false);
		vizRenameView.setEnabled(false);
		vizDeleteView.setEnabled(false);

		dataSortLinesBySimilarity.setEnabled(false);
		dataSortLinesByLocus.setEnabled(false);
		dataFind.setEnabled(false);
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