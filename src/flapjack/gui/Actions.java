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

	public static AbstractAction dataColorRandom;
	public static AbstractAction dataColorNucleotide;
	public static AbstractAction dataColorNucleotideSim;
	public static AbstractAction dataColorNucleotideSimGS;
	public static AbstractAction dataSortLinesBySimilarity;
	public static AbstractAction dataSortLinesByLocus;

	public static AbstractAction wndMinimize;
	public static AbstractAction wndZoom;
	public static AbstractAction wndFlapjack;

	public static AbstractAction helpAbout;

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

		dataColorRandom = new AbstractAction(RB.getString("gui.Actions.dataColorRandom")) {
			public void actionPerformed(ActionEvent e) {
				winMain.dataColor(ColorScheme.RANDOM);
			}
		};

		dataColorNucleotide = new AbstractAction(RB.getString("gui.Actions.dataColorNucleotide")) {
			public void actionPerformed(ActionEvent e) {
				winMain.dataColor(ColorScheme.NUCLEOTIDE);
			}
		};

		dataColorNucleotideSim = new AbstractAction(RB.getString("gui.Actions.dataColorNucleotideSim")) {
			public void actionPerformed(ActionEvent e) {
				winMain.dataColor(ColorScheme.NUCLEOTIDE_SIMILARITY);
			}
		};

		dataColorNucleotideSimGS = new AbstractAction(RB.getString("gui.Actions.dataColorNucleotideSimGS")) {
			public void actionPerformed(ActionEvent e) {
				winMain.dataColor(ColorScheme.NUCLEOTIDE_SIMILARITY_GS);
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
	}

	/** Called whenever the focus on the navigation tree changes. **/
	public static void resetActions()
	{
		vizOverview.setEnabled(false);
		vizExportImage.setEnabled(false);

		dataColorRandom.setEnabled(false);
		dataColorNucleotide.setEnabled(false);
		dataColorNucleotideSim.setEnabled(false);
		dataColorNucleotideSimGS.setEnabled(false);
		dataSortLinesBySimilarity.setEnabled(false);
		dataSortLinesByLocus.setEnabled(false);
	}
}