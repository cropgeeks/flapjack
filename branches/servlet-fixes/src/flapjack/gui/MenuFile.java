// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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

	private GraphImporter setupGraphImporter(File file, DataSet dataSet)
	{
		FlapjackFile f = new FlapjackFile(file.getAbsolutePath());

		if (f.canDetermineType() == false)
			return null;

		GraphImporter gi = null;

		if (f.getType() == FlapjackFile.GRAPH)
			gi = new GraphImporter(file, dataSet);
		else if (f.getType() == FlapjackFile.WIGGLE)
			gi = new GraphImporterWiggle(file, dataSet);

		return gi;
	}

	private void importGraphData(File file)
	{
		DataSet dataSet = navPanel.getDataSetForSelection();

		GraphImporter gi = setupGraphImporter(file, dataSet);

		if (gi == null)
			return;

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

		// Set (or reset) any existing selected graphs
		for (GTViewSet viewSet: dataSet.getViewSets())
			viewSet.setGraphs(new int[] { 0, -1, -1 });

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

	void handleDragDrop(String[] filenames)
	{
		System.out.println("Handle drag/drop");

		FlapjackFile[] files = new FlapjackFile[filenames.length];
		for (int i = 0; i < filenames.length; i++)
		{
			files[i] = new FlapjackFile(filenames[i]);
			System.out.println("Checking " + filenames[i]);
			files[i].canDetermineType();
		}

		// Process projects first
		for (FlapjackFile fjFile: files)
		{
			if (fjFile.isProjectFile())
			{
				fileOpen(fjFile);
				break;
			}
		}


		// Is there a MAP/GENPTYPE pair that can be imported?
		FlapjackFile mapFile = null, datFile = null;
		for (FlapjackFile fjFile: files)
		{
			if (fjFile.getType() == FlapjackFile.MAP && mapFile == null)
				mapFile = fjFile;
			if (fjFile.getType() == FlapjackFile.GENOTYPE && datFile == null)
				datFile = fjFile;
		}

		if (mapFile != null && datFile != null)
			importGenotypeData(mapFile.getFile(), datFile.getFile(), true);


		// Now check for other file types that can be imported into the dataset
		for (FlapjackFile fjFile: files)
		{
			if (fjFile.getType() == FlapjackFile.PHENOTYPE)
				importTraitData(fjFile.getFile());

			else if (fjFile.getType() == FlapjackFile.QTL)
				importQTLData(fjFile.getFile());

			else if (fjFile.getType() == FlapjackFile.GRAPH)
				importGraphData(fjFile.getFile());

			else if (fjFile.getType() == FlapjackFile.WIGGLE)
				importGraphData(fjFile.getFile());
		}
	}

	void fileExport()
	{
		QuickExportDialog qeDialog = new QuickExportDialog();
		String outputDir = qeDialog.getOutputDir();

		if (qeDialog.isOK() == false)
			return;


		// Run the project splitter...
		SplitProject splitter = new SplitProject(winMain.getProject(), outputDir);

		ProgressDialog dialog = new ProgressDialog(splitter,
			RB.format("gui.MenuFile.quickExport.dialogTitle"),
			RB.format("gui.MenuFile.quickExport.dialogLabel"),
			winMain);

		// If the operation failed or was cancelled...
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				dialog.getException().printStackTrace();
				TaskDialog.error(RB.format("gui.MenuFile.quickExport.error",
					dialog.getException().getMessage()),
					RB.getString("gui.text.close"));
			}
			return;
		}

		TaskDialog.info(RB.format("gui.MenuFile.quickExport.success", outputDir),
			RB.getString("gui.text.close"));
	}
}