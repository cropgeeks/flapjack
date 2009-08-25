package flapjack.gui;

import flapjack.data.*;
import flapjack.gui.dialog.*;
import flapjack.gui.visualization.*;
import flapjack.gui.visualization.colors.*;

import scri.commons.gui.*;

public class MenuVisualization
{
	private NavPanel navPanel;
	private GenotypePanel gPanel;

	void setComponents(NavPanel navPanel)
	{
		this.navPanel = navPanel;

		gPanel = navPanel.getGenotypePanel();
	}

	// Displays the dialog for exporting map/genotype data as an image
	void vizExportImage()
	{
		ExportImageDialog dialog = new ExportImageDialog(gPanel);
	}

	// Displays the dialog for exporting map/genotype data to disk
	void vizExportData()
	{
		ExportDataDialog dialog = new ExportDataDialog(gPanel.getViewSet());
	}

	void vizOverview()
	{
		OverviewManager.toggleOverviewDialog();
	}

	void vizColorCustomize()
	{
		new ColorDialog(Flapjack.winMain, gPanel);
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

		// Display the threshold dialog for allele frequency colouring
		else if (colorScheme == ColorScheme.ALLELE_FREQUENCY)
			new AlleleFrequencyDialog(gPanel);

		// Update the seed for random colour schemes
		else if (colorScheme == ColorScheme.RANDOM || colorScheme == ColorScheme.RANDOM_WSP)
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

	public void vizSelectTraits()
	{
		SelectTraitsDialog dialog = new SelectTraitsDialog(gPanel.getViewSet());

		gPanel.setViewSet(gPanel.getViewSet());
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

		if (TaskDialog.show(msg, TaskDialog.QST, 1, options) == 0)
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

	void vizBookmark()
	{
		GTView view = gPanel.getView();

		// First ensure the mouse was actually clicked while over an allele
		if (view.mouseOverLine < 0 || view.mouseOverLine >= view.getLineCount())
			return;
		if (view.mouseOverMarker < 0 || view.mouseOverMarker >= view.getMarkerCount())
			return;

		ChromosomeMap chromosome = view.getChromosomeMap();
		Line line = view.getLine(view.mouseOverLine);
		Marker marker = view.getMarker(view.mouseOverMarker);
		Bookmark bookmark = new Bookmark(chromosome, line, marker);

		// Stop the highlighter from running before it gets the chance
		// (as the navpanel adding the node would normally enable it)
		BookmarkHighlighter.enable = false;

		view.getViewSet().getBookmarks().add(bookmark);
		navPanel.addedNewBookmarkNode(view.getViewSet(), bookmark);

		Actions.projectModified();
	}

	void vizDeleteBookmark()
	{
		GTViewSet viewSet = gPanel.getViewSet();

		// Remove the bookmark from the navigation panel
		Bookmark bookmark = navPanel.removeSelectedBookmarkNode();
		// And then remove it from the project
		viewSet.getBookmarks().remove(bookmark);

		Actions.projectModified();
	}
}