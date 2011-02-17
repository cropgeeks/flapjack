// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.io;

import java.text.*;
import java.io.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.dialog.*;

import scri.commons.gui.*;

// TODO: This dialog allows itself to be closed during the loading operation
// which cancels the load in terms of data appearing in the interface, but the
// actual loading thread (in the background) will still run to completion.

/**
 * Dialog that appears during the importing of data. Shows a progress bar and
 * information/stats on the loading process and the data being read.
 */
public class DataImporter extends SimpleJob
{
	private DataSet dataSet = new DataSet();

	// To load the map file...
	private ChromosomeMapImporter mapImporter;

	// To load the genotype file...
	private File genoFile;
	private GenotypeDataImporter genoImporter;


	private long totalBytes;

	public DataImporter(File mapFile, File genoFile, boolean usePrefs)
	{
		this.genoFile = genoFile;
		totalBytes = mapFile.length() + genoFile.length();

		mapImporter  = new ChromosomeMapImporter(mapFile, dataSet);

		maximum = 5555;

		// Initializes the data importer, passing it the required options, either
		// from the preferences (if a user file is being opened) or with preset
		// options if we're loading the sample file (which has a set format)
		if (usePrefs)
			genoImporter = new GenotypeDataImporter(genoFile, dataSet, mapImporter.getMarkersHashMap(), Prefs.ioMissingData, Prefs.ioUseHetSep, Prefs.ioHeteroSeparator);
		else
			genoImporter = new GenotypeDataImporter(genoFile, dataSet, mapImporter.getMarkersHashMap(), "-", true, "/");
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public void runJob(int jobIndex) throws Exception
	{
		// Read the map
		mapImporter.importMap();

		// Read the genotype data
		genoImporter.importGenotypeData(Prefs.ioTransposed);
		genoImporter.cleanUp();

		if (Prefs.ioMakeAllChromosome)
			dataSet.createSuperChromosome(RB.getString("io.DataImporter.allChromosomes"));

		if(okToRun)
		{
			// Post-import stuff...
			PostImportOperations pio = new PostImportOperations(dataSet);
			pio.setName(genoFile);

			// Collapse heterozyous states
			if (Prefs.ioHeteroCollapse)
				pio.collapseHeterozygotes();

			pio.createDefaultView();
		}

		System.gc();

		if (Prefs.warnDuplicateMarkers && okToRun)
			displayDuplicates();
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

	public int getMaximum()
		{ return 5555; }


	public int getValue()
	{
		long mapBytes = mapImporter.getBytesRead();
		long genoBytes = genoImporter.getBytesRead();
		long bytesRead = mapBytes + genoBytes;

		return Math.round((bytesRead / (float) totalBytes) * 5555);
	}

	@Override
	public String getMessage()
	{
		final NumberFormat nf = NumberFormat.getInstance();

		return RB.format("io.DataImporter.message",
			nf.format(dataSet.countChromosomeMaps()),
			nf.format(mapImporter.getMarkerCount()),
			nf.format(genoImporter.getLineCount()),
			nf.format(genoImporter.getMarkerCount()));
	}

	public void cancelJob()
	{
		super.cancelJob();

		mapImporter.cancelImport();
		genoImporter.cancelImport();
	}
}