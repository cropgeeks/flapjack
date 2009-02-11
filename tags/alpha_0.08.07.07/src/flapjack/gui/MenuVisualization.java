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

	void vizSelectTraits()
	{
		SelectTraitsDialog dialog = new SelectTraitsDialog(gPanel.getViewSet());

		gPanel.repaint();
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
}