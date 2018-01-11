// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui;

import java.io.*;
import java.net.*;
import javax.swing.filechooser.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.dialog.*;
import jhi.flapjack.gui.dialog.analysis.*;
import jhi.flapjack.gui.visualization.*;
import jhi.flapjack.io.*;

import scri.commons.gui.*;

public class MenuData
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

	public void dataFilterQTLs()
	{
		DataSet dataSet = navPanel.getDataSetForSelection();

		winMain.filterQTLDialog = new FilterQTLsDialog(gPanel, dataSet);
	}

	public void dataSelectGraph()
	{
		winMain.selectGraphDialog = new SelectGraphDialog(gPanel);
	}

	void dataFind()
	{
		winMain.getFindDialog().setVisible(true);
	}

	void dataStatistics()
	{
		GTViewSet viewSet = gPanel.getViewSet();

		DataSummary statistics = new DataSummary(viewSet);

		ProgressDialog pd = new ProgressDialog(statistics,
			 RB.format("gui.MenuData.statistics.title"),
			 RB.format("gui.MenuData.statistics.label"),
			 Flapjack.winMain);

		if (pd.getResult() == ProgressDialog.JOB_COMPLETED)
			new DataSummaryDialog(viewSet, statistics.getResults(),
				statistics.getAlleleCount());
	}

	// Fires off a URL request to a linked database for information on a line
	void dataDBLineName()
	{
		DBAssociation db = gPanel.getViewSet().getDataSet().getDbAssociation();
		GTView view = gPanel.getView();

		if (view.mouseOverLine >= 0 && view.mouseOverLine < view.lineCount())
		{
			Line line = view.getLine(view.mouseOverLine);

			try
			{
				String lineURL = URLEncoder.encode(line.getName(), "UTF-8");
				String url = db.getLineSearch().replace("$LINE", lineURL);
				if (url.indexOf("?") == -1)
					url += "?application=flapjack";
				else
					url += "&application=flapjack";

				System.out.println("URL String: " + url.toString());

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

		if (view.mouseOverMarker >= 0 && view.mouseOverMarker < view.markerCount())
		{
			Marker marker = view.getMarker(view.mouseOverMarker);

			try
			{
				String markerURL = URLEncoder.encode(marker.getName(), "UTF-8");
				String url = db.getMarkerSearch().replace("$MARKER", markerURL);
				if (url.indexOf("?") == -1)
					url += "?application=flapjack";
				else
					url += "&application=flapjack";

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

		if (TaskDialog.show(msg, TaskDialog.QST, 1, options) == 0)
		{
			// Determine the selected data set...
			DataSet dataSet = navPanel.getDataSetForSelection();

			// ...and remove it from both the project and the GUI
			winMain.getProject().removeDataSet(dataSet);
			navPanel.removeDataSetNode(dataSet);

			Actions.projectModified();
		}
	}

	public void dataSelectTraitsHeatmap()
	{
		SelectTraitsDialog dialog = new SelectTraitsDialog(gPanel.getViewSet(),
			SelectTraitsDialog.HEATMAP_TRAITS);

		gPanel.setViewSet(gPanel.getViewSet());
	}

	public void dataSelectTextTraits()
	{
		SelectTraitsDialog dialog = new SelectTraitsDialog(gPanel.getViewSet(),
			SelectTraitsDialog.TEXT_TRAITS);

		gPanel.setViewSet(gPanel.getViewSet());
	}

	public void dataExportQTLs()
	{
		DataSet dataSet = navPanel.getDataSetForSelection();

		String name = RB.format("gui.MenuData.exportQTLs.filename", dataSet.getName());
		File saveAs = new File(Prefs.guiCurrentDir, name);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters.txt"), "txt");

		// Ask the user for a filename to save the current view as
		String filename = FlapjackUtils.getSaveFilename(
			RB.getString("gui.MenuData.exportQTLs.saveDialog"), saveAs, filter);

		// Quit if the user cancelled the file selection
		if (filename == null)
			return;

		QTLExporter exporter = new QTLExporter(dataSet, new File(filename));
		ProgressDialog dialog = new ProgressDialog(exporter,
			RB.format("gui.dialog.ExportDataDialog.exportTitle"),
			 RB.format("gui.dialog.ExportDataDialog.exportLabel"), winMain);

		if (dialog.failed("gui.error"))
			return;

		TaskDialog.info(
			RB.format("gui.dialog.ExportDataDialog.exportSuccess", filename),
			RB.getString("gui.text.close"));
	}

	public void dataExportTraits()
	{
		DataSet dataSet = navPanel.getDataSetForSelection();

		String name = RB.format("gui.MenuData.exportTraits.filename", dataSet.getName());
		File saveAs = new File(Prefs.guiCurrentDir, name);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters.txt"), "txt");

		// Ask the user for a filename to save the current view as
		String filename = FlapjackUtils.getSaveFilename(
			RB.getString("gui.MenuData.exportTraits.saveDialog"), saveAs, filter);

		// Quit if the user cancelled the file selection
		if (filename == null)
			return;

		TraitExporter exporter = new TraitExporter(dataSet, new File(filename));
		ProgressDialog dialog = new ProgressDialog(exporter,
			RB.format("gui.dialog.ExportDataDialog.exportTitle"),
			 RB.format("gui.dialog.ExportDataDialog.exportLabel"), winMain);

		if (dialog.failed("gui.error"))
			return;

		TaskDialog.info(
			RB.format("gui.dialog.ExportDataDialog.exportSuccess", filename),
			RB.getString("gui.text.close"));
	}
}