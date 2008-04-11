package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.management.*;
import java.text.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.dialog.*;
import flapjack.gui.dialog.analysis.*;
import flapjack.gui.dialog.prefs.*;
import flapjack.gui.visualization.*;
import flapjack.io.*;

import scri.commons.file.*;
import scri.commons.gui.*;

public class WinMain extends JFrame
{
	private WinMainMenuBar menubar;
	private WinMainToolBar toolbar;
	private WinMainStatusBar statusbar;

	private NavPanel navPanel;
	private GenotypePanel gPanel;

	private FindDialog findDialog;

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
		// Start by offering various import options
		ImportOptionsDialog optionsDialog = new ImportOptionsDialog();
		if (optionsDialog.isOK() == false)
			return;

		File mapFile = null, datFile = null;

		// Importing from file...
		if (Prefs.guiImportMethod == 0)
		{
			DataImportDialog dialog = new DataImportDialog();
			if (dialog.isOK())
			{
				gPanel.resetBufferedState(false);

				mapFile  = dialog.getMapFile();
				datFile = dialog.getGenotypeFile();
			}
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
				TaskDialog.error(RB.format("gui.WinMain.readJarError", e),
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

	void vizExportImage()
	{
		ExportImageDialog dialog = new ExportImageDialog(gPanel);

		if (dialog.isOK())
			new ExportingImageDialog(gPanel, dialog.getFile());
	}

	void vizOverview()
	{
		OverviewManager.toggleOverviewDialog();
	}

	void vizColor(int colorScheme)
	{
		gPanel.getViewSet().setColorScheme(colorScheme);
		gPanel.refreshView();
	}

	void vizOverlayGenotypes()
	{
		Prefs.visShowGenotypes = !Prefs.visShowGenotypes;
		WinMainMenuBar.mVizOverlayGenotypes.setSelected(Prefs.visShowGenotypes);
		CanvasMenu.mShowGenotypes.setSelected(Prefs.visShowGenotypes);

		gPanel.refreshView();
	}

	void dataSortLines(int sortMethod)
	{
		SortLinesDialog dialog = new SortLinesDialog(gPanel);

		if (dialog.isOK())
			new SortingLinesProgressDialog().runSort(gPanel, sortMethod);
	}

	void dataFind()
	{
		if (findDialog == null)
			findDialog = new FindDialog(this, gPanel);

		findDialog.setVisible(true);
		Prefs.guiFindDialogShown = true;
	}

	void helpPrefs()
	{
		if (new PreferencesDialog().isOK())
			gPanel.refreshView();
	}

	void helpUpdate()
	{
		Install4j.checkForUpdate(false);
	}

	void helpAbout()
	{
		String javaVer = System.getProperty("java.version");
		long freeMem = (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()
				- ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());
	
		NumberFormat nf = NumberFormat.getInstance();
		
		scri.commons.gui.TaskDialog.info("Flapjack - Version 0.08.04.10"
			+ "\n\nCopyright \u00A9 2007-2008"
			+ "\nPlant Bioinformatics Group"
			+ "\nScottish Crop Research Institute"
			+ "\n\nIain Milne, Micha Bayer, Paul Shaw, Linda Cardle, David Marshall"
			+ "\n\nJava version: " + javaVer
			+ "\nMemory available to JVM: " + nf.format((long)(freeMem/1024f/1024f)) + "MB",
			RB.getString("gui.text.close"));
	}
}