// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;

import java.text.*;
import java.io.*;
import java.lang.reflect.*;
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
	private DataSet dataSet = new DataSet();

	// To load the map file...
	private IMapImporter mapImporter;

	// To load the genotype file...
	private File genoFile;
	private IGenotypeImporter genoImporter;

	private BrapiClient client;

	private long totalBytes;
	private static int MAX_SCALE = 5555;

	private boolean usePrefs;

	// Tab-delimited text loading
	public DataImporter(File mapFile, File genoFile, boolean usePrefs)
	{
		this.genoFile = genoFile;
		this.usePrefs = usePrefs;

		if (mapFile != null)
			totalBytes += mapFile.length();
		totalBytes = genoFile.length();

		maximum = MAX_SCALE;

		mapImporter = new ChromosomeMapImporter(mapFile, dataSet);
	}

	// BRAPI loading
	public DataImporter(BrapiClient client, boolean usePrefs)
	{
		this.client = client;
		this.usePrefs = usePrefs;

		mapImporter = new BrapiMapImporter(client, dataSet);
	}

	public DataSet getDataSet()
		{ return dataSet; }

	@Override
	public void runJob(int jobIndex) throws Exception
	{
		long s = System.currentTimeMillis();
		// Read the map
		mapImporter.importMap();

		// Read the genotype data
		genoImporter = setupGenotypeImport();
		if (genoImporter.importGenotypeDataAsBytes() == false)
		{
			if (genoImporter.isOK())
			{
				genoImporter = setupGenotypeImport();
				genoImporter.importGenotypeDataAsInts();
			}
			else
			{
				cancelJob();
				return;
			}
		}
		genoImporter.cleanUp();

		if (genoImporter.isOK() == false)
		{
			cancelJob();
			return;
		}

		if (Prefs.ioMakeAllChromosome && dataSet.countChromosomeMaps() > 1)
			dataSet.createSuperChromosome(RB.getString("io.DataImporter.allChromosomes"));

		if(okToRun)
		{
			// Post-import stuff...
			PostImportOperations pio = new PostImportOperations(dataSet);
			pio.setName(genoFile);

			// Collapse (eg) A/A into A
			pio.collapseHomzEncodedAsHet();
			// Collapse heterozyous states
			if (Prefs.ioHeteroCollapse)
				pio.optimizeStateTable();

			pio.createDefaultView(false);
		}

		System.gc();

		if (Prefs.warnDuplicateMarkers && okToRun)
			displayDuplicates();

		System.out.println("Time taken: " + (System.currentTimeMillis() - s) + " ms");
	}

	private IGenotypeImporter setupGenotypeImport()
	{
		// "Normal"...
		if (Prefs.guiImportType == IMPORT_CLASSIC)
		{
			// Can we determine what type of genotype file this is?
			FlapjackFile f = new FlapjackFile(genoFile.getPath());
			f.canDetermineType();

			if (f.getType() != FlapjackFile.INTERTEK)
			{
				// Initializes the data importer, passing it the required options, either
				// from the preferences (if a user file is being opened) or with preset
				// options if we're loading the sample file (which has a set format)
				if (usePrefs)
					return new GenotypeDataImporter(genoFile, dataSet,
						mapImporter.getMarkersHashMap(), Prefs.ioMissingData,
						Prefs.ioHeteroSeparator, Prefs.ioTransposed, Prefs.ioAllowDupLines);
				else
					return new GenotypeDataImporter(genoFile, dataSet,
						mapImporter.getMarkersHashMap(), "-", "/", false, false);
			}
			else
			{
				return new IntertekDataImporter(genoFile, dataSet, mapImporter.getMarkersHashMap());
			}
		}
		// Or BrAPI
		else
		{
			BrapiMapImporter bMapImporter = (BrapiMapImporter) mapImporter;
			return new BrapiGenotypeImporter(this, client, dataSet,
				bMapImporter.getMarkersHashMap(), bMapImporter.getMarkersByName(),
				Prefs.ioMissingData, Prefs.ioHeteroSeparator);
		}
	}

	private void displayDuplicates()
	{
		if (mapImporter.getDuplicates().isEmpty())
			return;

		Runnable r = () -> { new DuplicateMarkersDialog(mapImporter.getDuplicates()); };

		try { SwingUtilities.invokeAndWait(r); }
		catch (InterruptedException | InvocationTargetException e) {}
	}

	@Override
	public int getMaximum()
		{ return MAX_SCALE; }


	@Override
	public int getValue()
	{
		long mapBytes = mapImporter.getBytesRead();
		long genoBytes = genoImporter.getBytesRead();
		long bytesRead = mapBytes + genoBytes;

		return Math.round((bytesRead / (float) totalBytes) * MAX_SCALE);
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

	public void setTotalBytes(long totalBytes)
	{
		this.totalBytes = totalBytes;
		maximum = MAX_SCALE;
	}
}