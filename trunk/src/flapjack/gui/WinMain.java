package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.dialog.*;
import flapjack.gui.dialog.analysis.*;
import flapjack.gui.visualization.*;
import flapjack.io.*;

import scri.commons.gui.*;

public class WinMain extends JFrame
{
	private WinMainMenuBar menubar;
	private WinMainToolBar toolbar;
	private WinMainStatusBar statusbar;

	private NavPanel navPanel;
	private GenotypePanel gPanel;

	// The user's project
	private Project project = new Project();


	WinMain()
	{
		setTitle(RB.getString("gui.WinMain.title"));
		setIconImage(Icons.FLAPJACK.getImage());

		menubar = new WinMainMenuBar(this);
		toolbar = new WinMainToolBar();
		statusbar = new WinMainStatusBar();
		setJMenuBar(menubar);

		navPanel = new NavPanel(this);
		gPanel = navPanel.getGenotypePanel();


		add(toolbar, BorderLayout.NORTH);
		add(navPanel);
		add(statusbar, BorderLayout.SOUTH);

		setSize(Prefs.guiWinMainWidth, Prefs.guiWinMainHeight);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);


		// Work out the current screen's width and height
		int scrnW = SwingUtils.getVirtualScreenDimension().width;
		int scrnH = SwingUtils.getVirtualScreenDimension().height;

		// Determine where on screen to display
		if (Prefs.isFirstRun || Prefs.guiWinMainX > (scrnW-50) || Prefs.guiWinMainY > (scrnH-50))
			setLocationRelativeTo(null);
		else
			setLocation(Prefs.guiWinMainX, Prefs.guiWinMainY);

		// Maximize the frame if neccassary
		if (Prefs.guiWinMainMaximized)
			setExtendedState(Frame.MAXIMIZED_BOTH);

		// Window listeners are added last so they don't interfere with the
		// maximization from above
		addListeners();
	}

	private void addListeners()
	{
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e)
			{
				if (getExtendedState() != Frame.MAXIMIZED_BOTH)
				{
					Prefs.guiWinMainWidth  = getSize().width;
					Prefs.guiWinMainHeight = getSize().height;
					Prefs.guiWinMainX = getLocation().x;
					Prefs.guiWinMainY = getLocation().y;

					Prefs.guiWinMainMaximized = false;
				}
				else
					Prefs.guiWinMainMaximized = true;
			}

			public void componentMoved(ComponentEvent e)
			{
				if (getExtendedState() != Frame.MAXIMIZED_BOTH)
				{
					Prefs.guiWinMainX = getLocation().x;
					Prefs.guiWinMainY = getLocation().y;
				}
			}
		});
	}

	boolean okToExit()
	{
		return ProjectSerializer.okToContinue(project, true);
	}

	void fileNew()
	{
		if (ProjectSerializer.okToContinue(project, false) == false)
			return;

		project = new Project();
		navPanel.setProject(project);
	}

	void fileOpen(File file)
	{
		if (ProjectSerializer.okToContinue(project, false) == false)
			return;

		gPanel.resetBufferedState(false);

		SaveLoadDialog dialog = new SaveLoadDialog(false);
		Project openedProject = dialog.open(file);

		if (openedProject != null)
		{
			project = openedProject;
			navPanel.setProject(project);
			menubar.createRecentMenu(project.filename);
		}
		else
			gPanel.resetBufferedState(true);
	}

	public boolean fileSave(boolean saveAs)
	{
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
		DataImportDialog dialog = new DataImportDialog();

		if (dialog.isOK())
		{
			gPanel.resetBufferedState(false);

			File mapFile  = dialog.getMapFile();
			File genoFile = dialog.getGenotypeFile();

			DataImportingDialog dialog2 = new DataImportingDialog(mapFile, genoFile);

			if (dialog2.isOK())
			{
				DataSet dataSet = dialog2.getDataSet();

				project.addDataSet(dataSet);
				navPanel.addDataSetNode(dataSet);

				Actions.projectModified();
			}
		}
	}

	void fileExit()
	{
		WindowEvent evt = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		processWindowEvent(evt);
	}

	void viewOverview()
	{
		OverviewManager.toggleOverviewDialog();
	}

	void dataSortLines()
	{
		SortLinesDialog dialog = new SortLinesDialog(gPanel);

		if (dialog.isOK())
			new SortingLinesProgressDialog().runSort(gPanel);
	}

	void dataColor(int colorScheme)
	{
		gPanel.getViewSet().setColorScheme(colorScheme);
		gPanel.refreshView();
	}

	void helpAbout()
	{
		scri.commons.gui.TaskDialog.info("Flapjack - Genotype Visualization Tool 2.0"
			+ "\n\nCopyright (C) 2008"
			+ "\nPlant Bioinformatics Group"
			+ "\nScottish Crop Research Institute"
			+ "\n\nIain Milne, Micha Bayer, Paul Shaw, Linda Cardle, David Marshall"
			+ "\n\nEnglish language files: Iain Milne"
			+ "\nDeutsche Übersetzungen: Micha Bayer, Dominik Lindner",
			RB.getString("gui.text.close"));
	}
}