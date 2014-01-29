// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.io.*;

import scri.commons.gui.*;

public class ExportDataDialog extends JDialog implements ActionListener
{
	private JButton bExport, bClose, bHelp;
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

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bExport);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bExport = SwingUtils.getButton(RB.getString("gui.dialog.ExportDataDialog.bExport"));
		bExport.addActionListener(this);
		bClose = SwingUtils.getButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "gui.dialog.ExportDataDialog");

		JPanel p1 = FlapjackUtils.getButtonPanel();
		p1.add(bExport);
		p1.add(bClose);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bClose)
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
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				dialog.getException().printStackTrace();
				TaskDialog.error(
					RB.format("gui.dialog.ExportDataDialog.exportException",
					dialog.getException().getMessage()),
					RB.getString("gui.text.close"));
			}

			return;
		}

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