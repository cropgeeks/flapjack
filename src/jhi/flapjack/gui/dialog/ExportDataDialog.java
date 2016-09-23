// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.io.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class ExportDataDialog extends JDialog implements ActionListener
{
	private JButton bExport, bCancel, bHelp;
	private ExportDataPanelNB nbPanel;

	private GTViewSet viewSet;
	private String baseName;

	public ExportDataDialog(GTViewSet viewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.ExportDataDialog.title"),
			true
		);

		this.viewSet = viewSet;
		baseName = viewSet.getDataSet().getName();
		baseName = baseName.substring(0, baseName.lastIndexOf(" "));

		nbPanel = new ExportDataPanelNB(viewSet);

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		FlapjackUtils.initDialog(this, bExport, bCancel, true, getContentPane());
	}

	private JPanel createButtons()
	{
		bExport = new JButton(RB.getString("gui.dialog.ExportDataDialog.bExport"));
		bExport.addActionListener(this);
		bCancel = new JButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = new JButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "_-_Export_Data");

		JPanel p1 = new DialogPanel();
		p1.add(bExport);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bCancel)
			setVisible(false);

		else if (e.getSource() == bExport)
		{
			if (nbPanel.combo.getSelectedIndex() == 0)
				exportMap();
			else
				exportDat();
		}
	}

	// Counts how many markers will be exported
	private int getMarkerCount(boolean[] chrm)
	{
		boolean allMarkers = nbPanel.rAll.isSelected();

		int count = 0;
		for (int i = 0; i < viewSet.chromosomeCount(); i++)
		{
			if (chrm[i] == false)
				continue;

			GTView view = viewSet.getView(i);

			if (allMarkers)
				count += view.markerCount();
			else
				count += view.countSelectedMarkers();
		}

		return count;
	}

	// Counts how many lines will be exported
	private int getLineCount()
	{
		boolean allLines = nbPanel.rAll.isSelected();

		if (allLines)
			return viewSet.getView(0).lineCount();
		else
			return viewSet.getView(0).countSelectedLines();
	}

	// Export the map to disk
	private void exportMap()
	{
		boolean useAll = nbPanel.rAll.isSelected();
		boolean[] chrm = nbPanel.getSelectedChromosomes();

		int count = getMarkerCount(chrm);

		String name = baseName + "_" + count + ".map";
		File filename = promptForFilename(new File(name), "map");

		if (filename != null)
		{
			ChromosomeMapExporter exporter
				= new ChromosomeMapExporter(filename, viewSet, useAll, chrm, count);

			displayDialog(exporter, filename);
		}
	}

	// Export the genotype data to disk
	private void exportDat()
	{
		boolean useAll = nbPanel.rAll.isSelected();
		boolean[] chrm = nbPanel.getSelectedChromosomes();

		int mrkrCount = getMarkerCount(chrm);
		int lineCount = getLineCount();

		String name = baseName + "_" + viewSet.getName() + "_"
			+ lineCount + "x" + mrkrCount + ".dat";
		File filename = promptForFilename(new File(name), "dat");

		if (filename != null)
		{
			GenotypeDataExporter exporter
				= new GenotypeDataExporter(filename, viewSet, useAll, chrm, lineCount);

			displayDialog(exporter, filename);
		}
	}

	private void displayDialog(ITrackableJob exporter, File filename)
	{
		ProgressDialog dialog = new ProgressDialog(exporter,
			 RB.format("gui.dialog.ExportDataDialog.exportTitle"),
			 RB.format("gui.dialog.ExportDataDialog.exportLabel"),
			 Flapjack.winMain);

		// If the operation failed or was cancelled...
		if (dialog.failed("gui.error"))
			return;

		TaskDialog.info(
			RB.format("gui.dialog.ExportDataDialog.exportSuccess", filename),
			RB.getString("gui.text.close"));
	}

	private File promptForFilename(File name, String extension)
	{
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters." + extension), extension);

		String filename = FlapjackUtils.getSaveFilename(
			RB.getString("gui.dialog.ExportDataDialog.saveDialog"), name, filter);

		if (filename != null)
			return new File(filename);
		else
			return null;
	}
}