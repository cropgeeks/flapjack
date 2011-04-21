// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.io.*;

import flapjack.data.*;
import flapjack.gui.dialog.*;
import flapjack.gui.traits.*;
import flapjack.gui.visualization.*;
import flapjack.io.*;

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

		XMLRoot.reset();
		project = new Project();
		winMain.setTitle(RB.getString("gui.WinMain.title") + " - " + Install4j.VERSION);

		gPanel.resetBufferedState(false);
		winMain.setProject(project);
		navPanel.setProject(project);
	}

	public void fileOpen(FlapjackFile file)
	{
		Project project = winMain.getProject();

		if (ProjectSerializer.okToContinue(project, false) == false)
			return;

		// If the file is invalid or the user cancels the dialog, just quit
		FlapjackFile toOpen = ProjectSerializer.queryOpen(file);
		if (toOpen == null)
			return;

		gPanel.resetBufferedState(false);

		// Attempt to open the project (as a trackable job)...
		SaveLoadHandler handler = new SaveLoadHandler(null, toOpen);
		ProgressDialog dialog = new ProgressDialog(handler,
			 RB.format("gui.MenuFile.loadTitle"),
			 RB.format("gui.MenuFile.loading"),
			 Flapjack.winMain);

		if (dialog.getResult() == ProgressDialog.JOB_COMPLETED && handler.isOK)
		{
			Project openedProject = handler.project;

			winMain.setProject(openedProject);
			navPanel.setProject(openedProject);
			menubar.createRecentMenu(openedProject.fjFile);
			new DataOpenedAnimator(gPanel);

			winMain.setTitle(openedProject.fjFile.getName()
				+ " - " + RB.getString("gui.WinMain.title")
				+ " - " + Install4j.VERSION);
		}
		else
			gPanel.resetBufferedState(true);
	}

	public boolean fileSave(boolean saveAs)
	{
		Project project = winMain.getProject();


		int format = Prefs.ioProjectFormat;
		if (project.format == -1)
			project.format = format;

		// Find out if we're still writing to the same format and if that's ok
		if (project.format != format)
		{
			String msg = RB.format("gui.MenuFile.saveFormat",
				RB.getString("gui.MenuFile.saveFormat." + project.format),
				RB.getString("gui.MenuFile.saveFormat." + format));

			String[] options = new String[] {
				RB.getString("gui.MenuFile.saveFormat.continue"),
				RB.getString("gui.MenuFile.saveFormat.switch"),
				RB.getString("gui.text.cancel") };

			int response = TaskDialog.show(msg, TaskDialog.QST, 1, options);
			// Switch...
			if (response == 1)
				project.format = format;
			// Cancel
			else if (response == -1 || response == 2)
				return false;
		}

		// Check that it's ok to save (or save as)
		if (ProjectSerializer.querySave(project, saveAs) == false)
			return false;

		// If so, do so
		SaveLoadHandler handler = new SaveLoadHandler(project, null);
		ProgressDialog dialog = new ProgressDialog(handler,
			 RB.format("gui.MenuFile.saveTitle"),
			 RB.format("gui.MenuFile.saving"),
			 Flapjack.winMain);

		if (dialog.getResult() == ProgressDialog.JOB_COMPLETED && handler.isOK)
		{
			Actions.projectSaved();
			menubar.createRecentMenu(project.fjFile);

			winMain.setTitle(project.fjFile.getName()
				+ " - " + RB.getString("gui.WinMain.title")
				+ " - " + Install4j.VERSION);

			return true;
		}

		return false;
	}

	public void fileImport(int tabIndex)
	{
		boolean secondaryOptions = navPanel.getDataSetForSelection() != null;

		ImportDialog dialog = new ImportDialog(tabIndex, secondaryOptions);

		if (dialog.isOK() == false)
			return;

		switch (dialog.getSelectedAction())
		{
			// Import from file
			case 0:
			{
				File mapFile = dialog.getMapFile();
				File genoFile = dialog.getGenotypeFile();
				importGenotypeData(mapFile, genoFile, true);
			}
			break;

			// Import trait data
			case 1: importTraitData(dialog.getTraitsFile());
				break;

			// Import QTL data
			case 2: importQTLData(dialog.getFeaturesFile());
				break;

			case 3: importGraphData(dialog.getGraphsFile());
				break;

			// Importing from a Flapjack-provided sample fileset
			case 4: fileOpen(dialog.getSampleProject());
				break;
		}
	}

	// Given a map file and a genotype (dat) file, imports that data, showing a
	// progress bar while doing so
	private void importGenotypeData(File mapFile, File datFile, boolean usePrefs)
	{
		gPanel.resetBufferedState(false);

		DataImporter importer = new DataImporter(mapFile, datFile, usePrefs);

		ProgressDialog dialog = new ProgressDialog(importer,
			 RB.format("gui.MenuFile.import.title"),
			 RB.format("gui.MenuFile.import.message"),
			 Flapjack.winMain);

		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				TaskDialog.error(
					RB.format("gui.MenuFile.import.error",
					dialog.getException()),
					RB.getString("gui.text.close"));
			}

			return;
		}

		// If everything was ok...
		DataSet dataSet = importer.getDataSet();

		winMain.getProject().addDataSet(dataSet);
		navPanel.addDataSetNode(dataSet);
		new DataOpenedAnimator(gPanel);

		Actions.projectModified();
	}

	private void importTraitData(File file)
	{
		DataSet dataSet = navPanel.getDataSetForSelection();

		// Remove any existing traits first
		TabPanel ttp = navPanel.getTraitsPanel(dataSet);
		ttp.getTraitsPanel().removeAllTraits();

		// Import the data using the standard progress bar dialog...
		TraitImporter importer = new TraitImporter(file, dataSet);
		ProgressDialog dialog = new ProgressDialog(importer,
			RB.format("gui.MenuFile.importTraits.dialogTitle"),
			RB.format("gui.MenuFile.importTraits.dialogLabel"),
			Flapjack.winMain);

		// If the operation failed or was cancelled...
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				dialog.getException().printStackTrace();
				TaskDialog.error(RB.format("gui.MenuFile.importTraits.error",
					file, dialog.getException().getMessage()),
					RB.getString("gui.text.close"));
			}
			return;
		}

		for (GTViewSet viewSet : dataSet.getViewSets())
			viewSet.assignTraits();

		ttp.getTraitsPanel().updateModel();
		Actions.projectModified();

		TaskDialog.info(RB.format("gui.MenuFile.importTraits.success",
			importer.getTraitsCount(), importer.getTraitsRead()),
			RB.getString("gui.text.close"));
	}

	private void importQTLData(File file)
	{
		DataSet dataSet = navPanel.getDataSetForSelection();

		// Import the data using the standard progress bar dialog...
		QTLImporter importer = new QTLImporter(file, dataSet);
		ProgressDialog dialog = new ProgressDialog(importer,
			RB.format("gui.MenuFile.importQTLs.dialogTitle"),
			RB.format("gui.MenuFile.importQTLs.dialogLabel"),
			Flapjack.winMain);

		// If the operation failed or was cancelled...
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				dialog.getException().printStackTrace();
				TaskDialog.error(RB.format("gui.MenuFile.importQTLs.error",
					file, dialog.getException().getMessage()),
					RB.getString("gui.text.close"));
			}
			return;
		}

		TabPanel ttp = navPanel.getTraitsPanel(dataSet);
		ttp.getQTLPanel().updateModel();
		Actions.projectModified();

		TaskDialog.info(RB.format("gui.MenuFile.importQTLs.success",
			importer.getFeaturesRead(), importer.getFeaturesAdded()),
			RB.getString("gui.text.close"));
	}

	private void importGraphData(File file)
	{
		DataSet dataSet = navPanel.getDataSetForSelection();

		GraphImporter gi = new GraphImporter(file, dataSet);
		ProgressDialog dialog = new ProgressDialog(gi,
			RB.format("gui.MenuFile.importGraphs.dialogTitle"),
			RB.format("gui.MenuFile.importGraphs.dialogLabel"),
			Flapjack.winMain);

		// If the operation failed or was cancelled...
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				dialog.getException().printStackTrace();
				TaskDialog.error(RB.format("gui.MenuFile.importGraphs.error",
					file, dialog.getException().getMessage()),
					RB.getString("gui.text.close"));
			}
			return;
		}

		// Set (or reset) any existing graph indexes back to 0
		for (GTViewSet viewSet: dataSet.getViewSets())
			viewSet.setGraphIndex(0);

		Actions.projectModified();
		gPanel.refreshView();

		int count = dataSet.getChromosomeMaps().get(0).getGraphs().size();
		TaskDialog.info(RB.format("gui.MenuFile.importGraphs.success", count),
			RB.getString("gui.text.close"));
	}

	private static class SaveLoadHandler extends SimpleJob
	{
		Project project;
		FlapjackFile file;
		boolean isOK;

		SaveLoadHandler(Project project, FlapjackFile file)
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
			{
				isOK = ProjectSerializer.save(project);
			}
		}
	}
}