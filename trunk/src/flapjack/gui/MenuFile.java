package flapjack.gui;

import java.io.*;
import javax.swing.*;

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

	void fileOpen(File file)
	{
		Project project = winMain.getProject();

		if (ProjectSerializer.okToContinue(project, false) == false)
			return;

		gPanel.resetBufferedState(false);

		SaveLoadDialog dialog = new SaveLoadDialog(false);
		Project openedProject = dialog.open(file);

		if (openedProject != null)
		{
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

		SaveLoadDialog dialog = new SaveLoadDialog(true);

		if (dialog.save(project, saveAs))
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

	void fileImport()
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

		importGenotypeData(dialog.getMapFile(), dialog.getGenotypeFile());
	}

	// Extracts sample data from the jar file, writes it to a temp location,
	// then imports it
	private void importSampleData()
	{
		File dir = SystemUtils.getTempUserDirectory("flapjack");
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

		importGenotypeData(mapFile, datFile);
	}

	// Given a map file and a genotype (dat) file, imports that data, showing a
	// progress bar while doing so
	private void importGenotypeData(File mapFile, File datFile)
	{
		DataImportingDialog dialog = new DataImportingDialog(mapFile, datFile);

		if (dialog.isOK())
		{
			DataSet dataSet = dialog.getDataSet();

			winMain.getProject().addDataSet(dataSet);
			navPanel.addDataSetNode(dataSet);
			new DataOpenedAnimator(gPanel);

			Actions.projectModified();
		}
	}

	private void importTraitData()
	{
		DataSet dataSet = navPanel.getDataSetForSelection();

		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Browse for Trait File");
		fc.setCurrentDirectory(new File(Prefs.guiCurrentDir));

		if (fc.showOpenDialog(winMain) != JFileChooser.APPROVE_OPTION)
			return;

		File file = fc.getSelectedFile();
		Prefs.guiCurrentDir = fc.getCurrentDirectory().toString();


		TraitsImportingProgressDialog dialog =
			new TraitsImportingProgressDialog(file, dataSet);

		if (dialog.isOK())
		{
			navPanel.getTraitsPanel(dataSet).updateModel();
			Actions.projectModified();
		}
	}
}