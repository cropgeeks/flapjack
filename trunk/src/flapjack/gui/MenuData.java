package flapjack.gui;

import java.net.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.dialog.*;
import flapjack.gui.dialog.analysis.*;
import flapjack.gui.visualization.*;

import scri.commons.gui.*;

class MenuData
{
	private WinMain winMain;
	private NavPanel navPanel;
	private GenotypePanel gPanel;

	void setComponents(WinMain winMain, NavPanel navPanel)
	{
		this.winMain = winMain;
		this.navPanel = navPanel;

		gPanel = navPanel.getGenotypePanel();
	}

	void dataSortLines()
	{
		SortLinesDialog dialog = new SortLinesDialog(gPanel);

		if (dialog.isOK())
		{
			boolean[] chromosomes = dialog.getSelectedChromosomes();

			GTViewSet viewSet = gPanel.getViewSet();
			int line = gPanel.getView().mouseOverLine;

			ILineSorter sort = new SortLinesBySimilarity(viewSet, line, chromosomes);
			new SortingLinesProgressDialog(gPanel, sort).runSort();
		}
	}

	void dataSortLinesByTrait()
	{
		SortLinesByTraitDialog dialog = new SortLinesByTraitDialog(gPanel);

		if (dialog.isOK())
		{
			GTViewSet viewSet = gPanel.getViewSet();

			int[] traits = dialog.getTraitIndices();
			boolean[] asc = dialog.getAscendingIndices();
			boolean assign = Prefs.guiAssignTraits;

			ILineSorter sort = new SortLinesByTrait(viewSet, traits, asc, assign);
			new SortingLinesProgressDialog(gPanel, sort).runSort();
		}
	}

	void dataFind()
	{
		winMain.getFindDialog().setVisible(true);
		Prefs.guiFindDialogShown = true;
	}

	void dataStatistics()
	{
		GTViewSet viewSet = gPanel.getViewSet();

		StatisticsProgressDialog dialog = new StatisticsProgressDialog(viewSet);

		if (dialog.isOK())
			new AlleleStatisticsDialog(viewSet, dialog.getResults());
	}

	// Fires off a URL request to a linked database for information on a line
	void dataDBLineName()
	{
		DBAssociation db = gPanel.getViewSet().getDataSet().getDbAssociation();
		GTView view = gPanel.getView();

		if (view.mouseOverLine >= 0 && view.mouseOverLine < view.getLineCount())
		{
			Line line = view.getLine(view.mouseOverLine);

			try
			{
				String lineURL = URLEncoder.encode(line.getName(), "UTF-8");
				String url = db.getLineSearch().replace("$LINE", lineURL);

				FlapjackUtils.visitURL(url.toString());
			}
			catch (Exception e) {}
		}
	}

	// Fires off a URL request to a linked database for information on a marker
	void dataDBMarkerName()
	{
		DBAssociation db = gPanel.getViewSet().getDataSet().getDbAssociation();
		GTView view = gPanel.getView();

		if (view.mouseOverMarker >= 0 && view.mouseOverMarker < view.getMarkerCount())
		{
			Marker marker = view.getMarker(view.mouseOverMarker);

			try
			{
				String markerURL = URLEncoder.encode(marker.getName(), "UTF-8");
				String url = db.getMarkerSearch().replace("$MARKER", markerURL);

				FlapjackUtils.visitURL(url);
			}
			catch (Exception e) {}
		}
	}

	void dataDBSettings()
	{
		DataSet dataSet = gPanel.getViewSet().getDataSet();

		DatabaseSettingsDialog dialog = new DatabaseSettingsDialog(dataSet);
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
			winMain.getProject().removeDataSet(dataSet);
			navPanel.removeDataSetNode(dataSet);

			Actions.projectModified();
		}
	}
}