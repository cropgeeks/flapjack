// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.io.*;
import java.util.*;
import javax.swing.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.dialog.*;
import flapjack.gui.traits.*;
import flapjack.gui.visualization.*;
import flapjack.io.*;

import scri.commons.file.*;
import scri.commons.gui.*;

public class MenuFile
{
	private WinMain winMain;
	private WinMainMenuBar menubar;
	private NavPanel navPanel;
	private GenotypePanel gPanel;

	void setComponents(WinMain winMain, WinMainMenuBar menubar, NavPanel navPanel)
	{
		this.winMain = winMain;
		this.menubar = menubar;
		this.navPanel = navPanel;

		gPanel = navPanel.getGenotypePanel();
	}

	void fileNew()
	{
		Project project = winMain.getProject();

		if (ProjectSerializer.okToContinue(project, false) == false)
			return;

		project = new Project();
		winMain.setTitle(RB.getString("gui.WinMain.title") + " - " + Install4j.VERSION);

		gPanel.resetBufferedState(false);
		winMain.setProject(project);
		navPanel.setProject(project);
	}

	public void fileOpen(File file)
	{
		Project project = winMain.getProject();

		if (ProjectSerializer.okToContinue(project, false) == false)
			return;

		// If the file is invalid or the user cancels the dialog, just quit
		File toOpen = ProjectSerializer.queryOpen(file);
		if (toOpen == null)
			return;

		gPanel.resetBufferedState(false);

		// Attempt to open the project (as a trackable job)...
		SaveLoadHandler handler = new SaveLoadHandler(null, toOpen);
		ProgressDialog dialog = new ProgressDialog(handler,
			 RB.format("gui.MenuFile.loadTitle"),
			 RB.format("gui.MenuFile.loading"));

		if (dialog.getResult() == ProgressDialog.JOB_COMPLETED && handler.isOK)
		{
			Project openedProject = handler.project;

			winMain.setProject(openedProject);
			navPanel.setProject(openedProject);
			menubar.createRecentMenu(openedProject.filename);
			new DataOpenedAnimator(gPanel);

			winMain.setTitle(openedProject.filename.getName()
				+ " - " + RB.getString("gui.WinMain.title")
				+ " - " + Install4j.VERSION);
		}
		else
			gPanel.resetBufferedState(true);
	}

	public boolean fileSave(boolean saveAs)
	{
		Project project = winMain.getProject();

		// Check that it's ok to save (or save as)
		boolean gz = Prefs.guiSaveCompressed;
		if (ProjectSerializer.querySave(project, saveAs, gz) == false)
			return false;

		// If so, do so
		SaveLoadHandler handler = new SaveLoadHandler(project, null);
		ProgressDialog dialog = new ProgressDialog(handler,
			 RB.format("gui.MenuFile.saveTitle"),
			 RB.format("gui.MenuFile.saving"));

		if (dialog.getResult() == ProgressDialog.JOB_COMPLETED && handler.isOK)
		{
			Actions.projectSaved();
			menubar.createRecentMenu(project.filename);

			winMain.setTitle(project.filename.getName()
				+ " - " + RB.getString("gui.WinMain.title")
				+ " - " + Install4j.VERSION);

			return true;
		}

		return false;
	}

	public void fileImport()
	{
		boolean secondaryOptions = navPanel.getDataSetForSelection() != null;

		// Start by offering various import options
		ImportOptionsDialog optionsDialog = new ImportOptionsDialog(secondaryOptions);
		if (optionsDialog.isOK() == false)
			return;


		switch (Prefs.guiImportMethod)
		{
			// Import from file
			case 0: importGenotypeData();
				break;

			// Import from Germinate
			case 1:
				break;

			// Importing from a Flapjack-provided sample fileset
			case 2: importSampleData();
				break;

			// Import trait data
			case 20: importTraitData();
				break;

			// Import QTL data
			case 21: importQTLData();
				break;
		}
	}

	// Pops up the Import Data dialog, then uses the returned map and dat file
	// information to import data
	private void importGenotypeData()
	{
		DataImportDialog dialog = new DataImportDialog();
		if (dialog.isOK() == false)
			return;

		gPanel.resetBufferedState(false);

		importGenotypeData(dialog.getMapFile(), dialog.getGenotypeFile(), true);
	}

	// Extracts sample data from the jar file, writes it to a temp location,
	// then imports it
	private void importSampleData()
	{
		File dir = SystemUtils.getTempUserDirectory("scri-flapjack");
		File mapFile = new File(dir, "sample.map");
		File datFile = new File(dir, "sample.dat");

		try
		{
			FileUtils.writeFile(mapFile, getClass().getResourceAsStream("/res/samples/sample.map"));
			FileUtils.writeFile(datFile, getClass().getResourceAsStream("/res/samples/sample.dat"));
		}
		catch (Exception e)
		{
			System.out.println(e);
			TaskDialog.error(RB.format("gui.WinMain.readJarError", e.getMessage()),
				RB.getString("gui.text.close"));
			return;
		}

		importGenotypeData(mapFile, datFile, false);
	}

	// Given a map file and a genotype (dat) file, imports that data, showing a
	// progress bar while doing so
	private void importGenotypeData(File mapFile, File datFile, boolean usePrefs)
	{
		DataImportingDialog dialog = new DataImportingDialog(mapFile, datFile, usePrefs);

		if (dialog.isOK())
		{
			DataSet dataSet = dialog.getDataSet();

			winMain.getProject().addDataSet(dataSet);
			navPanel.addDataSetNode(dataSet);
			new DataOpenedAnimator(gPanel);

			Actions.projectModified();
		}
	}

	public void importTraitData()
	{
		DataSet dataSet = navPanel.getDataSetForSelection();

		// Find out what file to import
		BrowseDialog browseDialog = new BrowseDialog(Prefs.guiTraitHistory);
		if (browseDialog.isOK() == false)
			return;

		File file = browseDialog.getFile();

		Prefs.guiTraitHistory = browseDialog.getHistory();

		// Remove any existing traits first
		TabPanel ttp = navPanel.getTraitsPanel(dataSet);
		ttp.getTraitsPanel().removeAllTraits();

		// Import the data using the standard progress bar dialog...
		TraitImporter importer = new TraitImporter(file, dataSet);

		ProgressDialog dialog = new ProgressDialog(importer,
			 RB.format("gui.MenuFile.importTraits.dialogTitle"),
			 RB.format("gui.MenuFile.importTraits.dialogLabel"));

		// If the operation failed or was cancelled...
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				dialog.getException().printStackTrace();
				TaskDialog.error(
					RB.format("gui.MenuFile.importTraits.error",
					file, dialog.getException().getMessage()),
					RB.getString("gui.text.close"));
			}

			return;
		}

		// Check to see if any of the views need traits assigned to them
		for (GTViewSet viewSet: dataSet.getViewSets())
			viewSet.assignTraits();

		ttp.getTraitsPanel().updateModel();
		Actions.projectModified();
	}

	public void importQTLData()
	{
		DataSet dataSet = navPanel.getDataSetForSelection();

		// Find out what file to import
		BrowseDialog browseDialog = new BrowseDialog(Prefs.guiQTLHistory);
		if (browseDialog.isOK() == false)
			return;

		File file = browseDialog.getFile();
		Prefs.guiQTLHistory = browseDialog.getHistory();

		// Import the data using the standard progress bar dialog...
		QTLImporter importer = new QTLImporter(file, dataSet);

		ProgressDialog dialog = new ProgressDialog(importer,
			 RB.format("gui.MenuFile.importQTLs.dialogTitle"),
			 RB.format("gui.MenuFile.importQTLs.dialogLabel"));

		// If the operation failed or was cancelled...
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				dialog.getException().printStackTrace();
				TaskDialog.error(
					RB.format("gui.MenuFile.importQTLs.error",
					file, dialog.getException().getMessage()),
					RB.getString("gui.text.close"));
			}

			return;
		}

		TabPanel ttp = navPanel.getTraitsPanel(dataSet);
		ttp.getQTLPanel().updateModel();
		Actions.projectModified();
	}

	private static class SaveLoadHandler extends SimpleJob
	{
		Project project;
		File file;
		boolean isOK;

		SaveLoadHandler(Project project, File file)
		{
			this.project = project;
			this.file = file;
		}

		public void runJob(int index)
			throws Exception
		{
			// Loading...
			if (project == null)
			{
				if ((project = ProjectSerializer.open(file)) != null)
					isOK = true;
			}
			// Saving...
			else
				isOK = ProjectSerializer.save(project, Prefs.guiSaveCompressed);
		}
	}
}