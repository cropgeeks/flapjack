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

	public static AbstractAction vizOverview;
	public static AbstractAction vizExportImage;
	public static AbstractAction vizColorRandom;
	public static AbstractAction vizColorNucleotide;
	public static AbstractAction vizColorNucleotideSim;
	public static AbstractAction vizColorNucleotideSimGS;
	public static AbstractAction vizOverlayGenotypes;

	public static AbstractAction dataSortLinesBySimilarity;
	public static AbstractAction dataSortLinesByLocus;

	public static AbstractAction wndMinimize;
	public static AbstractAction wndZoom;
	public static AbstractAction wndFlapjack;

	public static AbstractAction helpAbout;
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

		vizColorNucleotideSim = new AbstractAction(RB.getString("gui.Actions.vizColorNucleotideSim")) {
			public void actionPerformed(ActionEvent e) {
				winMain.vizColor(ColorScheme.NUCLEOTIDE_SIMILARITY);
			}
		};

		vizColorNucleotideSimGS = new AbstractAction(RB.getString("gui.Actions.vizColorNucleotideSimGS")) {
			public void actionPerformed(ActionEvent e) {
				winMain.vizColor(ColorScheme.NUCLEOTIDE_SIMILARITY_GS);
			}
		};

		vizOverlayGenotypes = new AbstractAction(RB.getString("gui.Actions.vizOverlayGenotypes")) {
			public void actionPerformed(ActionEvent e) {
				winMain.vizOverlayGenotypes();
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

		helpPrefs = new AbstractAction(RB.getString("gui.Actions.helpPrefs")) {
			public void actionPerformed(ActionEvent e) {
				winMain.helpPrefs();
			}
		};
	}

	/** Called whenever the focus on the navigation tree changes. **/
	public static void resetActions()
	{
		vizOverview.setEnabled(false);
		vizExportImage.setEnabled(false);
		vizColorRandom.setEnabled(false);
		vizColorNucleotide.setEnabled(false);
		vizColorNucleotideSim.setEnabled(false);
		vizColorNucleotideSimGS.setEnabled(false);

		dataSortLinesBySimilarity.setEnabled(false);
		dataSortLinesByLocus.setEnabled(false);
	}
}