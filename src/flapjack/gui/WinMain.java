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
import flapjack.gui.visualization.colors.*;
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
		setTitle(RB.getString("gui.WinMain.title") + " - " + Install4j.VERSION);
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

		gPanel.resetBufferedState(false);
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
			new DataOpenedAnimator(gPanel);
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

				project.addDataSet(dataSet);
				navPanel.addDataSetNode(dataSet);
				new DataOpenedAnimator(gPanel);

				Actions.projectModified();
			}
		}
	}

	void fileExit()
	{
		WindowEvent evt = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		processWindowEvent(evt);
	}

	void editUndoRedo(boolean undo)
	{
		gPanel.processUndoRedo(undo);
	}

	void editMode(int newMode)
	{
		Prefs.guiMouseMode = newMode;

		WinMainMenuBar.mEditModeNavigation.setSelected(newMode == Constants.NAVIGATION);
		WinMainToolBar.editModeNavigation.setSelected(newMode == Constants.NAVIGATION);

		WinMainMenuBar.mEditModeMarker.setSelected(newMode == Constants.MARKERMODE);
		WinMainToolBar.editModeMarker.setSelected(newMode == Constants.MARKERMODE);

		gPanel.resetBufferedState(true);
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

	void vizColorCustomize()
	{
		new ColorDialog(this, gPanel);
	}

	public void vizColor(int colorScheme)
	{
		// Set the initial index positions for similarity colouring (if needbe)
		if (colorScheme == ColorScheme.LINE_SIMILARITY || colorScheme == ColorScheme.LINE_SIMILARITY_GS)
		{
			SelectLMDialog dialog = new SelectLMDialog(gPanel.getView(), true);
			if (dialog.isOK() == false)
				return;

			gPanel.getView().mouseOverLine = dialog.getSelectedIndex();
			gPanel.getView().initializeComparisons();
		}

		else if (colorScheme == ColorScheme.MARKER_SIMILARITY || colorScheme == ColorScheme.MARKER_SIMILARITY_GS)
		{
			SelectLMDialog dialog = new SelectLMDialog(gPanel.getView(), false);
			if (dialog.isOK() == false)
				return;

			gPanel.getView().mouseOverMarker = dialog.getSelectedIndex();
			gPanel.getView().initializeComparisons();
		}

		// Update the seed for random colour schemes
		else if (colorScheme == ColorScheme.RANDOM)
			gPanel.getViewSet().setRandomColorSeed((int)(Math.random()*50000));


		gPanel.getViewSet().setColorScheme(colorScheme);
		gPanel.refreshView();

		Actions.projectModified();
	}

	void vizOverlayGenotypes()
	{
		Prefs.visShowGenotypes = !Prefs.visShowGenotypes;
		WinMainMenuBar.mVizOverlayGenotypes.setSelected(Prefs.visShowGenotypes);
		CanvasMenu.mShowGenotypes.setSelected(Prefs.visShowGenotypes);

		gPanel.refreshView();
	}

	void vizHighlightHZ()
	{
		Prefs.visHighlightHZ = !Prefs.visHighlightHZ;
		WinMainMenuBar.mVizHighlightHZ.setSelected(Prefs.visHighlightHZ);
		CanvasMenu.mHighlightHZ.setSelected(Prefs.visHighlightHZ);

		new HZHighlighter(gPanel);
	}

	void vizNewView()
	{
		DataSet dataSet = navPanel.getDataSetForSelection();
		GTViewSet viewSet = navPanel.getViewSetForSelection();

		NewViewDialog dialog = new NewViewDialog(dataSet, viewSet);

		if (dialog.isOK())
		{
			GTViewSet newViewSet = dialog.getNewViewSet();

			dataSet.getViewSets().add(newViewSet);
			navPanel.addedNewVisualizationNode(dataSet);

			Actions.projectModified();
		}
	}

	void vizRenameView()
	{
		GTViewSet viewSet = navPanel.getViewSetForSelection();

		RenameDialog dialog = new RenameDialog(viewSet.getName());

		if (dialog.isOK())
		{
			viewSet.setName(dialog.getNewName());
			navPanel.updateNodeFor(viewSet);

			Actions.projectModified();
		}
	}

	void vizDeleteView()
	{
		GTViewSet viewSet = navPanel.getViewSetForSelection();

		String msg = RB.format("gui.WinMain.deleteViewSet",
			viewSet.getName(), viewSet.getDataSet().getName());

		String[] options = new String[] {
			RB.getString("gui.WinMain.deleteViewSetButton"),
			RB.getString("gui.text.cancel") };

		if (TaskDialog.show(msg, MsgBox.QST, 1, options) == 0)
		{
			// Remove it from both the project and the GUI
			viewSet.getDataSet().getViewSets().remove(viewSet);
			navPanel.removeVisualizationNode(viewSet);

			Actions.projectModified();
		}
	}

	void vizToggleCanvas()
	{
		new ToggleCanvasDialog(gPanel);
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

	void dataStatistics()
	{
		GTViewSet viewSet = gPanel.getViewSet();

		StatisticsProgressDialog dialog = new StatisticsProgressDialog(viewSet);

		if (dialog.isOK())
			new AlleleStatisticsDialog(viewSet, dialog.getResults());
	}

	void dataRenameDataSet()
	{
		DataSet dataSet = navPanel.getDataSetForSelection();

		RenameDialog dialog = new RenameDialog(dataSet.getName());

		if (dialog.isOK())
		{
			dataSet.setName(dialog.getNewName());
			navPanel.updateNodeFor(dataSet);

			Actions.projectModified();
		}
	}

	void dataDeleteDataSet()
	{
		String msg = RB.getString("gui.WinMain.deleteDataSet");

		String[] options = new String[] {
			RB.getString("gui.WinMain.deleteDataSetButton"),
			RB.getString("gui.text.cancel") };

		if (TaskDialog.show(msg, MsgBox.QST, 1, options) == 0)
		{
			// Determine the selected data set...
			DataSet dataSet = navPanel.getDataSetForSelection();

			// ...and remove it from both the project and the GUI
			project.removeDataSet(dataSet);
			navPanel.removeDataSetNode(dataSet);

			Actions.projectModified();
		}
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

		scri.commons.gui.TaskDialog.info("Flapjack - Version " + Install4j.VERSION
			+ "\n\nCopyright \u00A9 2007-2008"
			+ "\nPlant Bioinformatics Group"
			+ "\nScottish Crop Research Institute"
			+ "\n\nIain Milne, Micha Bayer, Paul Shaw, Linda Cardle, David Marshall"
			+ "\n\n\nJava version: " + javaVer
			+ "\nMemory available to JVM: " + nf.format((long)(freeMem/1024f/1024f)) + "MB"
			+ "\nCurrent Locale: " + java.util.Locale.getDefault()
			+ "\nFlapjack ID: " + Prefs.flapjackID,
			RB.getString("gui.text.close"));
	}
}