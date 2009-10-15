// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.*;
import flapjack.io.*;

import scri.commons.file.*;
import scri.commons.gui.*;

// TODO: This dialog allows itself to be closed during the loading operation
// which cancels the load in terms of data appearing in the interface, but the
// actual loading thread (in the background) will still run to completion.

/**
 * Dialog that appears during the importing of data. Shows a progress bar and
 * information/stats on the loading process and the data being read.
 */
public class DataImportingDialog extends JDialog implements Runnable
{
	private DataSet dataSet = new DataSet();

	// To load the map file...
	private File mapFile;
	private ChromosomeMapImporter mapImporter;

	// To load the genotype file...
	private File genoFile;
	private GenotypeDataImporter genoImporter;

	private JLabel mapsLabel, mrksLabel, lineLabel;
	private JProgressBar pBar;
	private boolean isIndeterminate = false;
	private boolean isOK = false;

	private long totalBytes;

	public DataImportingDialog(File mapFile, File genoFile, boolean usePrefs)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.DataImportingDialog.title"),
			true
		);

		this.mapFile = mapFile;
		this.genoFile = genoFile;
		totalBytes = mapFile.length() + genoFile.length();

		mapImporter  = new ChromosomeMapImporter(mapFile, dataSet);

		// Initializes the data importer, passing it the required options, either
		// from the preferences (if a user file is being opened) or with preset
		// options if we're loading the sample file (which has a set format)
		if (usePrefs)
			genoImporter = new GenotypeDataImporter(genoFile, dataSet, mapImporter.getMarkersHashMap(), Prefs.ioMissingData, Prefs.ioUseHetSep, Prefs.ioHeteroSeparator);
		else
			genoImporter = new GenotypeDataImporter(genoFile, dataSet, mapImporter.getMarkersHashMap(), "-", true, "/");

		add(createControls());

		addWindowListener(new WindowAdapter()
		{
			public void windowOpened(WindowEvent e) {
				loadData();
			}

			public void windowClosing(WindowEvent e) {
				mapImporter.cancelImport();
				genoImporter.cancelImport();
			}
		});

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createControls()
	{
		pBar = new JProgressBar(0, 50000);
		pBar.setPreferredSize(new Dimension(300, pBar.getPreferredSize().height));

		mapsLabel = new JLabel();
		mrksLabel = new JLabel();
		lineLabel = new JLabel();

		JPanel labelPanel = new JPanel(new GridLayout(3, 2));
		labelPanel.add(
			new JLabel(RB.getString("gui.dialog.DataImportingDialog.maps")));
		labelPanel.add(mapsLabel);
		labelPanel.add(
			new JLabel(RB.getString("gui.dialog.DataImportingDialog.line")));
		labelPanel.add(lineLabel);
		labelPanel.add(
			new JLabel(RB.getString("gui.dialog.DataImportingDialog.mrks")));
		labelPanel.add(mrksLabel);

		JPanel panel = new JPanel(new BorderLayout(5, 10));
		panel.add(labelPanel, BorderLayout.WEST);
		panel.add(pBar, BorderLayout.SOUTH);

		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		return panel;
	}

	public boolean isOK() {
		return isOK;
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	private void loadData()
	{
		new Thread(new MonitorThread()).start();
		new Thread(this).start();
	}

	public void run()
	{
		try
		{
			// Read the map
			mapImporter.importMap();

			// Read the genotype data
			genoImporter.importGenotypeData();

			// Post-import stuff...
			PostImportOperations pio = new PostImportOperations(dataSet);
			pBar.setIndeterminate(true);
			pio.setName(genoFile);

			// Collapse heterozyous states
			if (Prefs.ioHeteroCollapse)
				pio.collapseHeterozygotes();

			pio.calculateMarkerFrequencies();
			pio.createDefaultView();

			if (Prefs.warnDuplicateMarkers)
				displayDuplicates();

			isOK = true;
		}
		catch (IOException e)
		{
			TaskDialog.error(
				RB.format("gui.dialog.DataImportingDialog.ioException", e.getMessage()),
				RB.getString("gui.text.close"));

			e.printStackTrace();
		}
		catch (Exception e)
		{
			TaskDialog.error(
				RB.format("gui.dialog.DataImportingDialog.exception", e.getMessage()),
				RB.getString("gui.text.close"));

			e.printStackTrace();
		}

		setVisible(false);
	}

	private void displayDuplicates()
	{
		if (mapImporter.getDuplicates().size() == 0)
			return;

		Runnable r = new Runnable() {
			public void run() {
				new DuplicateMarkersDialog(mapImporter.getDuplicates());
			}
		};

		try { SwingUtilities.invokeAndWait(r); }
		catch (Exception e) {}
	}

	private class MonitorThread implements Runnable
	{
		public void run()
		{
			final NumberFormat nf = NumberFormat.getInstance();

			Runnable r = new Runnable()
			{
				public void run()
				{
					long mapBytes = mapImporter.getBytesRead();
					long genoBytes = genoImporter.getBytesRead();
					long bytesRead = mapBytes + genoBytes;

					int lineCount = genoImporter.getLineCount();
					mapsLabel.setText("  " + dataSet.countChromosomeMaps());
					lineLabel.setText("  " + nf.format(genoImporter.getLineCount()));
					mrksLabel.setText("  " + nf.format(genoImporter.getMarkerCount()));

					pBar.setValue((int)(bytesRead/(float)totalBytes * 50000));
				}
			};

			while (isVisible())
			{
				SwingUtilities.invokeLater(r);

				try { Thread.sleep(100); }
				catch (InterruptedException e) {}
			}
		}
	}
}