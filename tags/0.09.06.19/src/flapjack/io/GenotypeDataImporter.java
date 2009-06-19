package flapjack.io;

import java.io.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class GenotypeDataImporter
{
	private File file;
	private DataSet dataSet;
	private StateTable stateTable;

	private String ioMissingData;
	private boolean ioUseHetSep;
	private String ioHeteroSeparator;

	private int lineCount = 1;
	private int markerCount;

	private boolean useByteStorage = true;

	private boolean isOK = true;

	public GenotypeDataImporter(File file, DataSet dataSet, String ioMissingData, boolean ioUseHetSep, String ioHeteroSeparator)
	{
		this.file = file;
		this.dataSet = dataSet;
		this.ioMissingData = ioMissingData;
		this.ioUseHetSep = ioUseHetSep;
		this.ioHeteroSeparator = ioHeteroSeparator;

		stateTable = dataSet.getStateTable();
	}

	public void cancelImport()
		{ isOK = false; }

	public File getFile()
		{ return file; }

	public int getLineCount()
		{ return lineCount; }

	public int getMarkerCount()
		{ return markerCount; }

	public void importGenotypeData()
		throws IOException, DataFormatException
	{
		if (readData() == false)
		{
			dataSet.getLines().clear();
			stateTable.resetTable();
			useByteStorage = false;

			lineCount = 1;
			readData();
		}
	}

	private boolean readData()
		throws IOException, DataFormatException
	{
		long s = System.currentTimeMillis();

		BufferedReader in = new BufferedReader(new FileReader(file));

		String str = in.readLine();

		// Preprocess the file, looking for any header information
		while (str.length() == 0 || str.startsWith("#"))
		{
			processHeader(str);
			str = in.readLine();

			++lineCount;
		}

		// Split the first line up into an array of marker names (we ignore the
		// first element as it's a redundant column header: i=1 in loop below)
		String[] markers = str.split("\t");

		// Now work out the map indices of these markers and the indices within
		// the map itself. This speeds up loading by pre-caching this data so we
		// don't need to search per line, rather just once per file
		int[] mapIndex = new int[markers.length];
		int[] markerIndex = new int[markers.length];

		for (int i = 1; i < markers.length && isOK; i++)
		{
			mapIndex[i] = dataSet.getMapIndexByMarkerName(markers[i]);

			// Check at this point that the marker exists in the map
			if (mapIndex[i] != -1)
				markerIndex[i] = dataSet.getMapByIndex(mapIndex[i]).getMarkerIndex(markers[i]);
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
			for (Line line: dataSet.getLines())
				if (line.getName().equals(values[0]))
					throw new DataFormatException(RB.format("io.DataFormatException.duplicateLineError", values[0], lineCount));

			Line line = dataSet.createLine(values[0], useByteStorage);

			for (int i = 1; i < values.length; i++)
			{
				// Assuming a map was found that contains this marker...
				if (mapIndex[i] != -1)
				{
					ChromosomeMap map = dataSet.getMapByIndex(mapIndex[i]);

					// Determine its various states
					int stateCode = stateTable.getStateCode(values[i], true,
						ioMissingData, ioUseHetSep, ioHeteroSeparator);

					// Then apply them to the marker data
					line.setLoci(mapIndex[i], markerIndex[i], stateCode);

					markerCount++;
				}
			}

			if (useByteStorage && stateTable.size() > 127)
				return false;
		}

		in.close();

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
}