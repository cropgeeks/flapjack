// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.file.*;
import scri.commons.gui.*;

public class GenotypeDataImporter
{
	private ProgressInputStream is;
	private File file;
	private DataSet dataSet;
	private StateTable stateTable;

	// Each marker's name is stored (only while loading) in a hashmap, along
	// with the index of the chromosome it is associated with
	private HashMap<String, MarkerIndex> markers;

	// Also track line names, for duplicate detection
	private HashMap<String, Line> lines;

	private String ioMissingData;
	private boolean ioUseHetSep;
	private String ioHeteroSeparator;

	private long lineCount = 0;
	private long markerCount;

	private boolean useByteStorage = true;

	private boolean isOK = true;

	public GenotypeDataImporter(File file, DataSet dataSet, HashMap<String, MarkerIndex> markers,
		String ioMissingData, boolean ioUseHetSep, String ioHeteroSeparator)
	{
		this.file = file;
		this.dataSet = dataSet;
		this.markers = markers;
		this.ioMissingData = ioMissingData;
		this.ioUseHetSep = ioUseHetSep;
		this.ioHeteroSeparator = ioHeteroSeparator;

		stateTable = dataSet.getStateTable();
	}

	public void cleanUp()
	{
		markers.clear();
		lines.clear();
	}

	public void cancelImport()
		{ isOK = false; }

	public File getFile()
		{ return file; }

	public long getLineCount()
		{ return lineCount; }

	public long getMarkerCount()
		{ return markerCount; }

	public void importGenotypeData()
		throws IOException, DataFormatException
	{
		if (readData() == false)
		{
			dataSet.getLines().clear();
			stateTable.resetTable();
			useByteStorage = false;

			lineCount = 0;
			readData();
		}
	}

	private boolean readData()
		throws IOException, DataFormatException
	{
		long s = System.currentTimeMillis();

		lines = new HashMap<String, Line>();

		is = new ProgressInputStream(new FileInputStream(file));

		BufferedReader in = new BufferedReader(new InputStreamReader(is));

		String str = in.readLine();

		// Preprocess the file, looking for any header information
		while (str.length() == 0 || str.startsWith("#"))
		{
			processHeader(str);
			str = in.readLine();

			lineCount++;
		}

		// Split the first line up into an array of marker names (we ignore the
		// first element as it's a redundant column header: i=1 in loop below)
		String[] markerNames = str.split("\t");

		// Now work out the map indices of these markers and the indices within
		// the map itself. This speeds up loading by pre-caching this data so we
		// don't need to search per line, rather just once per file
		int[] mapIndex = new int[markerNames.length];
		int[] mkrIndex = new int[markerNames.length];

		for (int i = 1; i < markerNames.length && isOK; i++)
		{
			MarkerIndex index = markers.get(markerNames[i]);

			// Check that the marker does exists on map
			if (index != null)
			{
				mapIndex[i] = index.mapIndex;
				mkrIndex[i] = index.mkrIndex;
			}
		}

		System.out.println("Map/marker cache created in " + (System.currentTimeMillis()-s) + "ms");
		s = System.currentTimeMillis();

		while ((str = in.readLine()) != null && isOK)
		{
			if (str.length() == 0)
				continue;

			if ((++lineCount) % 100 == 0)
			{
				System.out.println("Reading line " + lineCount + " (" + (System.currentTimeMillis()-s) + "ms)");
				s = System.currentTimeMillis();
			}

			String[] values = str.split("\t");

			if (values.length == 0)
				continue;

			// Check for duplicate line names
			if (lines.get(values[0]) != null)
					throw new DataFormatException(RB.format("io.DataFormatException.duplicateLineError", values[0], lineCount+1));

			Line line = dataSet.createLine(values[0], useByteStorage);
			lines.put(line.getName(), line);

			for (int i = 1; i < values.length; i++)
			{
				// Assuming a map was found that contains this marker...
				if (mapIndex[i] != -1)
				{
					// Determine its various states
					int stateCode = stateTable.getStateCode(values[i], true,
						ioMissingData, ioUseHetSep, ioHeteroSeparator);

					// Then apply them to the marker data
					line.setLoci(mapIndex[i], mkrIndex[i], stateCode);

					markerCount++;
				}
			}

			if (useByteStorage && stateTable.size() > 127)
				return false;
		}

		in.close();

		markers.clear();

		return true;
	}

	private void processHeader(String str)
	{
		try
		{
			String key = str.substring(1, str.indexOf("=")).trim();
			String value = str.substring(str.indexOf("=")+1).trim();

			// fjDatabaseLineSearch = a URL for querying line information
			if (key.equals("fjDatabaseLineSearch"))
				dataSet.getDbAssociation().setLineSearch(value);

			// fjDatabaseMarkerSearch = a URL for querying marker information
			if (key.equals("fjDatabaseMarkerSearch"))
				dataSet.getDbAssociation().setMarkerSearch(value);
		}
		catch (Exception e)
		{
			System.out.println("Invalid header: " + str);
		}
	}

	public long getBytesRead()
		{ return (is == null) ? 0 : is.getBytesRead(); }

	// Quite a lot of duplication of work done in readData() is there a way to
	// clean this up?
	private boolean readTransposedData()
			throws IOException, DataFormatException
	{
		long s = System.currentTimeMillis();

		lines = new HashMap<String, Line>();

		is = new ProgressInputStream(new FileInputStream(file));

		BufferedReader in = new BufferedReader(new InputStreamReader(is));

		String str = in.readLine();

		// Preprocess the file, looking for any header information
		while (str.length() == 0 || str.startsWith("#"))
		{
			processHeader(str);
			str = in.readLine();

			lineCount++;
		}

		// Split the first line into line names
		String [] lineNames = str.split("\t");

		// Loop over the line names ensuring there are no duplicates
		for (int i = 1; i < lineNames.length && isOK; i++)
		{
			if (lines.get(lineNames[i]) != null)
					throw new DataFormatException(RB.format("io.DataFormatException.duplicateLineError", lineNames[i], lineCount+1));
			else
			{
				// Create the line and add it to our lines hashmap
				Line line = dataSet.createLine(lineNames[i], useByteStorage);
				lines.put(line.getName(), line);
			}
		}

		// This is now a loop over the markers
		while ((str = in.readLine()) != null && isOK)
		{
			if (str.length() == 0)
				continue;

			if ((++lineCount) % 100 == 0)
			{
				System.out.println("Reading line " + lineCount + " (" + (System.currentTimeMillis()-s) + "ms)");
				s = System.currentTimeMillis();
			}

			String[] values = str.split("\t");

			if (values.length == 0)
				continue;

			for (int i = 1; i < values.length; i++)
			{
				MarkerIndex index = markers.get(values[0]);

				// Assuming a map was found that contains this marker...
				if (index != null && index.mapIndex != -1)
				{
					ChromosomeMap map = dataSet.getMapByIndex(index.mapIndex);

					// Determine its various states
					int stateCode = stateTable.getStateCode(values[i], true,
						ioMissingData, ioUseHetSep, ioHeteroSeparator);

					// Then apply them to the marker data
					lines.get(lineNames[i]).setLoci(index.mapIndex, index.mkrIndex, stateCode);

					markerCount++;
				}
			}

			if (useByteStorage && stateTable.size() > 127)
				return false;
		}

		in.close();

		markers.clear();
		return true;
	}
}