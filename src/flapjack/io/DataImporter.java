// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io;

import java.text.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
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
	private IMapImporter mapImporter;

	// To load the genotype file...
	private File genoFile;
	private IGenotypeImporter genoImporter;

	private File hdf5File;


	private long totalBytes;

	private boolean usePrefs;

	public DataImporter(File mapFile, File genoFile, boolean usePrefs)
	{
		this.genoFile = genoFile;
		this.usePrefs = usePrefs;
		totalBytes = mapFile.length() + genoFile.length();

		maximum = 5555;

		mapImporter = new ChromosomeMapImporter(mapFile, dataSet);
	}

	public DataImporter(File hdf5File, boolean usePrefs)
	{
		this.hdf5File = hdf5File;
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

			// Collapse heterozyous states
			if (Prefs.ioHeteroCollapse)
				pio.collapseHeterozygotes();

			pio.createDefaultView();
		}

		System.gc();

		if (Prefs.warnDuplicateMarkers && okToRun)
			displayDuplicates();

		System.out.println("Time taken: " + (System.currentTimeMillis() - s) + " ms");
	}

	private void setupGenotypeImport()
	{
		if (Prefs.guiUseHDF5)
		{
			ArrayList<Integer> markerChromosomes = ((Hdf5ChromosomeMapImporter)mapImporter).markerChromosomes();
			genoImporter = new Hdf5GenotypeDataImporter(hdf5File, dataSet, mapImporter.getMarkersHashMap(), markerChromosomes);
		}
		else
		{
			// Initializes the data importer, passing it the required options, either
			// from the preferences (if a user file is being opened) or with preset
			// options if we're loading the sample file (which has a set format)
			if (usePrefs)
				genoImporter = new GenotypeDataImporter(genoFile, dataSet, mapImporter.getMarkersHashMap(), Prefs.ioMissingData, Prefs.ioUseHetSep, Prefs.ioHeteroSeparator, Prefs.ioTransposed);
			else
				genoImporter = new GenotypeDataImporter(genoFile, dataSet, mapImporter.getMarkersHashMap(), "-", true, "/", Prefs.ioTransposed);
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