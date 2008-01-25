package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.dialog.*;
import flapjack.io.*;

public class WinMain extends JFrame
{
	private WinMainMenuBar menubar;

	private NavPanel navPanel;

	// The user's project
	private Project project = new Project();


	WinMain()
	{
		setTitle(RB.getString("gui.WinMain.title"));
		setIconImage(Icons.FLAPJACK.getImage());

		menubar = new WinMainMenuBar(this);
		setJMenuBar(menubar);

		navPanel = new NavPanel();



		add(navPanel);

		setSize(Prefs.guiWinMainWidth, Prefs.guiWinMainHeight);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);


		// Work out the current screen's width and height
		int scrnW = Toolkit.getDefaultToolkit().getScreenSize().width;
		int scrnH = Toolkit.getDefaultToolkit().getScreenSize().height;

		// Determine where on screen (TODO: on which monitor?) to display
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

	void fileNew()
	{
		if (ProjectSerializer.okToContinue(project) == false)
			return;
	}

	void fileOpen()
	{
		if (ProjectSerializer.okToContinue(project) == false)
			return;

		Project openedProject = ProjectSerializer.open(null);
		if (openedProject != null)
		{
			project = openedProject;

			// TODO: Pass to navPanel
		}
	}

	void fileSave(boolean saveAs)
	{
		setCursor(new Cursor(Cursor.WAIT_CURSOR));

		if (ProjectSerializer.save(project, saveAs))
		{
		}

		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	void fileImport()
	{
		DataImportDialog dialog = new DataImportDialog();

		if (dialog.isOK())
		{
			File mapFile  = dialog.getMapFile();
			File genoFile = dialog.getGenotypeFile();

			DataSet dataSet = new DataLoadingDialog(mapFile, genoFile).getDataSet();

			if (dataSet != null)
			{
				project.addDataSet(dataSet);
				navPanel.addDataSetNode(dataSet);
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
		navPanel.getGenotypePanel().getOverviewDialog().setVisible(true);
	}

	void helpAbout()
	{
		scri.commons.gui.TaskDialog.info("Flapjack - Genotype Visualization Tool 2.0"
			+ "\n\nCopyright (C) 2008 Plant Bioinformatics Group"
			+ "\nScottish Crop Research Institute"
			+ "\n\nIain Milne, Micha Bayer, Paul Shaw, Linda Cardle, David Marshall"
			+ "\n\nEnglish language files: Iain Milne"
			+ "\nDeutsche Übersetzungen: Micha Bayer, Dominik Lindner",
			RB.getString("gui.text.close"));
	}
}