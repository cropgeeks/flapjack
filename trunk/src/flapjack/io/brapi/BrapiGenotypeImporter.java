// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.brapi;

import java.io.*;
import java.util.*;

import flapjack.data.*;
import flapjack.io.*;

import scri.commons.gui.*;

import hutton.brapi.resource.*;

public class BrapiGenotypeImporter implements IGenotypeImporter
{
	private DataSet dataSet;
	private StateTable stateTable;

	// Each marker's name is stored (only while loading) in a hashmap, along
	// with the index of the chromosome it is associated with
	private HashMap<String, MarkerIndex> markers;

	// Also track line names, for duplicate detection
	private HashMap<String, Line> lines;

	// Stores known states as we find them so we don't have to keep working them
	// out for each allele
	private HashMap<String, Integer> states = new HashMap<>();

	private String ioMissingData;
	private boolean ioUseHetSep;
	private String ioHeteroSeparator;

	private long alleleCount;

	private boolean useByteStorage = true;

	private boolean isOK = true;

	public BrapiGenotypeImporter(BrapiRequest request, DataSet dataSet, HashMap<String, MarkerIndex> markers,
		String ioMissingData, boolean ioUseHetSep, String ioHeteroSeparator)
	{
		this.dataSet = dataSet;
		this.markers = markers;
		this.ioMissingData = ioMissingData;
		this.ioUseHetSep = ioUseHetSep;
		this.ioHeteroSeparator = ioHeteroSeparator;

		stateTable = dataSet.getStateTable();
	}

	@Override
	public void cleanUp()
	{
		markers.clear();
		lines.clear();
	}

	@Override
	public void cancelImport()
		{ isOK = false; }

	@Override
	public long getLineCount()
		{ return lines != null ? lines.size() : 0; }

	@Override
	public long getMarkerCount()
		{ return alleleCount; }

	@Override
	public void importGenotypeData()
		throws IOException, DataFormatException
	{
		// If the first read failed, clear everything and start again using int
		// storage rather than byte
		if (readData() == false)
		{
			dataSet.getLines().clear();
			stateTable.resetTable();
			states.clear();
			useByteStorage = false;
			alleleCount = 0;

			readData();
		}
	}

	private boolean readData()
		throws IOException, DataFormatException
	{
		lines = new HashMap<String, Line>();

		GermplasmList list = BrapiClient.getGermplasms();

		System.out.println("Recevied info on " + list.getGermplasm().size() + " lines");

		for (Germplasm germplasm: list.getGermplasm())
		{
			// Check for duplicate line names
			String name = germplasm.getGermplasmName();
			if (name == null)
				name = "" + germplasm.getGermplasmId();

			if (lines.get(name) != null)
				throw new DataFormatException(RB.format("io.DataFormatException.duplicateLineError2", name));

			Line line = dataSet.createLine(name, useByteStorage);
			lines.put(name, line);

			// Grab the allele information

			System.out.println("ID: #" + germplasm.getGermplasmId() + "#");

			MarkerProfile profile = BrapiClient.getMarkerProfile(germplasm.getGermplasmId());

			// Not all lines will have a usable MarkerProfile - this is ok (as
			// the line will remain empty of alleles), although this needs to be
			// revisited if we ever try to build a map from line info alone
			if (profile == null)
				continue;

			HashMap<String,String> data = profile.getData();
			for (String markerName: data.keySet())
			{
				MarkerIndex index = markers.get(markerName);

				if (index != null)
				{
					String allele = data.get(markerName);

					// Determine its various states
					Integer stateCode = states.get(allele);
					if (stateCode == null)
					{
						stateCode = stateTable.getStateCode(allele, true,
							ioMissingData, ioUseHetSep, ioHeteroSeparator);
						states.put(allele, stateCode);
					}

					// Then apply them to the marker data
					line.setLoci(index.mapIndex, index.mkrIndex, stateCode);

					alleleCount++;
				}
			}

//			try { Thread.sleep(50); }
//			catch (Exception e) {}

			if (useByteStorage && stateTable.size() > 127)
				return false;

			if (isOK == false)
				break;
		}

/*		long s = System.currentTimeMillis();


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
			MarkerIndex index = markers.get(markerNames[i].trim());

			// Check that the marker does exists on map
			if (index != null)
			{
				mapIndex[i] = index.mapIndex;
				mkrIndex[i] = index.mkrIndex;
			}
			// This ensures a marker - in the .dat file - that isn't in the .map
			// file will be ignored
			else
				mapIndex[i] = mkrIndex[i] = -1;
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

			String[] values = str.trim().split("\t");

			if (values.length == 0)
				continue;

			// Check for duplicate line names
			String name = values[0].trim();
			if (lines.get(name) != null)
				throw new DataFormatException(RB.format("io.DataFormatException.duplicateLineError", name, lineCount+1));

			Line line = dataSet.createLine(name, useByteStorage);
			lines.put(name, line);

			for (int i = 1; i < values.length; i++)
			{
				// Assuming a map was found that contains this marker...
				if (mapIndex[i] != -1)
				{
					// Determine its various states
					Integer stateCode = states.get(values[i]);
					if (stateCode == null)
					{
						stateCode = stateTable.getStateCode(values[i], true,
							ioMissingData, ioUseHetSep, ioHeteroSeparator);
						states.put(values[i], stateCode);
					}

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

*/

		return true;
	}

	@Override
	public long getBytesRead()
		{ return 0; }
}