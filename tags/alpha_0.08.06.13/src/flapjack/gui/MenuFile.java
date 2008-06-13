package flapjack.gui;

import java.io.*;

import flapjack.data.*;
import flapjack.gui.dialog.*;
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

			return true;
		}

		return false;
	}

	void fileImport()
	{
		// Start by offering various import options
		ImportOptionsDialog optionsDialog = new ImportOptionsDialog();
		if (optionsDialog.isOK() == false)
			return;

		File mapFile = null, datFile = null;

		// Importing from file...
		if (Prefs.guiImportMethod == 0)
		{
			DataImportDialog dialog = new DataImportDialog();
			if (dialog.isOK() == false)
				return;

			gPanel.resetBufferedState(false);

			mapFile  = dialog.getMapFile();
			datFile = dialog.getGenotypeFile();
		}

		// Importing from a Flapjack-provided sample fileset
		else if (Prefs.guiImportMethod == 2)
		{
			File dir = SystemUtils.getTempUserDirectory("flapjack");
			mapFile = new File(dir, "sample.map");
			datFile = new File(dir, "sample.dat");

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
		}

		// Regardless of option, open these files...
		if (Prefs.guiImportMethod == 0 || Prefs.guiImportMethod == 2)
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
	}
}