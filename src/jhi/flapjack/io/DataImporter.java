// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;

import java.text.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.dialog.*;
import jhi.flapjack.io.brapi.*;

import scri.commons.gui.*;

/**
 * Dialog that appears during the importing of data. Shows a progress bar and
 * information/stats on the loading process and the data being read.
 */
public class DataImporter extends SimpleJob
{
	public static final int IMPORT_BRAPI = 1;
	public static final int IMPORT_CLASSIC = 0;
	public static final int IMPORT_HDF5 = 2;
	private DataSet dataSet = new DataSet();

	// To load the map file...
	private IMapImporter mapImporter;

	// To load the genotype file...
	private File genoFile;
	private IGenotypeImporter genoImporter;

	private File hdf5File;
	private BrapiClient client;

	private long totalBytes;

	private boolean usePrefs;

	// Tab-delimited text loading
	public DataImporter(File mapFile, File genoFile, boolean usePrefs)
	{
		this.genoFile = genoFile;
		this.usePrefs = usePrefs;

		if (mapFile != null)
			totalBytes += mapFile.length();
		totalBytes = genoFile.length();

		maximum = 5555;

		mapImporter = new ChromosomeMapImporter(mapFile, dataSet);
	}

	// BRAPI loading
	public DataImporter(BrapiClient client, boolean usePrefs)
	{
		this.client = client;
		this.usePrefs = usePrefs;

		mapImporter = new BrapiMapImporter(client, dataSet);
	}

	// HDF5 loading
	public DataImporter(File hdf5File, boolean usePrefs)
	{
		this.hdf5File = hdf5File;
		this.usePrefs = usePrefs;

		mapImporter = new Hdf5ChromosomeMapImporter(hdf5File, dataSet);
	}

	public DataSet getDataSet()
		{ return dataSet; }

	@Override
	public void runJob(int jobIndex) throws Exception
	{
		long s = System.currentTimeMillis();
		// Read the map
		mapImporter.importMap();

		setupGenotypeImport();

		// Read the genotype data
		genoImporter.importGenotypeData();
		genoImporter.cleanUp();

		if (Prefs.ioMakeAllChromosome)
			dataSet.createSuperChromosome(RB.getString("io.DataImporter.allChromosomes"));

		if(okToRun)
		{
			// Post-import stuff...
			PostImportOperations pio = new PostImportOperations(dataSet);
			File imported = genoFile != null ? genoFile : hdf5File;
			pio.setName(imported);

			// Collapse (eg) A/A into A
			pio.collapseHomzEncodedAsHet();
			// Collapse heterozyous states
			if (Prefs.ioHeteroCollapse)
				pio.optimizeStateTable();

			pio.createDefaultView();
		}

		System.gc();

		if (Prefs.warnDuplicateMarkers && okToRun)
			displayDuplicates();

		System.out.println("Time taken: " + (System.currentTimeMillis() - s) + " ms");
	}

	private void setupGenotypeImport()
	{
		switch (Prefs.guiImportType)
		{
			case IMPORT_CLASSIC:
			{
				// Initializes the data importer, passing it the required options, either
				// from the preferences (if a user file is being opened) or with preset
				// options if we're loading the sample file (which has a set format)
				if (usePrefs)
					genoImporter = new GenotypeDataImporter(genoFile, dataSet,
						mapImporter.getMarkersHashMap(), Prefs.ioMissingData,
						Prefs.ioUseHetSep, Prefs.ioHeteroSeparator, Prefs.ioTransposed);
				else
					genoImporter = new GenotypeDataImporter(genoFile, dataSet,
						mapImporter.getMarkersHashMap(), "-", true, "/",
						Prefs.ioTransposed);

				break;
			}

			case IMPORT_BRAPI:
			{
				genoImporter = new BrapiGenotypeImporter(client, dataSet,
					mapImporter.getMarkersHashMap(), Prefs.ioMissingData,
						Prefs.ioUseHetSep, Prefs.ioHeteroSeparator);

				break;
			}

			case IMPORT_HDF5:
			{
				ArrayList<Integer> markerChromosomes = ((Hdf5ChromosomeMapImporter)mapImporter).markerChromosomes();
				genoImporter = new Hdf5GenotypeDataImporter(hdf5File, dataSet, mapImporter.getMarkersHashMap(), markerChromosomes);

				break;
			}
		}
	}

	private void displayDuplicates()
	{
		if (mapImporter.getDuplicates().size() == 0)
			return;

		Runnable r = () -> { new DuplicateMarkersDialog(mapImporter.getDuplicates()); };

		try { SwingUtilities.invokeAndWait(r); }
		catch (InterruptedException | InvocationTargetException e) {}
	}

	@Override
	public int getMaximum()
		{ return 5555; }


	@Override
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

	@Override
	public void cancelJob()
	{
		super.cancelJob();

		mapImporter.cancelImport();
		genoImporter.cancelImport();
	}
}