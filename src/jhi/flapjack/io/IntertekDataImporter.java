// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;

import java.io.*;
import java.text.*;
import java.util.*;

import jhi.flapjack.data.*;

import scri.commons.io.*;
import scri.commons.gui.*;

public class IntertekDataImporter implements IGenotypeImporter
{
	private ProgressInputStream is;
	private File file;
	private DataSet dataSet;
	private StateTable stateTable;

	// Each marker's name is stored (only while loading) in a hashmap, along
	// with the index of the chromosome it is associated with
	private HashMap<String, MarkerIndex> markers;

	// Also track line names, for duplicate detection
	private HashMap<String, ArrayList<Line>> linesByName;

	// Stores known states as we find them so we don't have to keep working them
	// out for each allele
	private HashMap<String, Integer> states = new HashMap<>();

	private String ioMissingData;
	private String ioHeteroSeparator;

	private long lineCount = 0;
	private long markerCount;

	private boolean useByteStorage = true;

	private boolean isOK = true;

	private NumberFormat nf = NumberFormat.getInstance();

	private boolean mapWasProvided, fakeMapCreated;

	private ArrayList<String> pedigrees = new ArrayList<>();

	public IntertekDataImporter(File file, DataSet dataSet, HashMap<String, MarkerIndex> markers)
	{
		this.file = file;
		this.dataSet = dataSet;
		this.markers = markers;
		this.ioMissingData = "";
		this.ioHeteroSeparator = ":";

		stateTable = dataSet.getStateTable();
		mapWasProvided = markers.size() > 0;
	}

	@Override
	public void cleanUp()
	{
		markers.clear();
		linesByName.clear();
	}

	@Override
	public void cancelImport()
		{ isOK = false; }

	@Override
	public long getLineCount()
		{ return dataSet.getLines().size(); }

	@Override
	public long getAlleleCount()
		{ return markerCount; }

	@Override
	// Returns false if storing the data using byte arrays failed
	public boolean importGenotypeDataAsBytes()
		throws IOException, DataFormatException
	{
		return readData();
	}

	@Override
	public void importGenotypeDataAsInts()
		throws IOException, DataFormatException
	{
		useByteStorage = false;

		readData();
	}

	private boolean readData()
		throws IOException, DataFormatException
	{
		long s = System.currentTimeMillis();

		linesByName = new HashMap<String, ArrayList<Line>>();

		String sep = determineSeparator();

		is = new ProgressInputStream(new FileInputStream(file));

		BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

		String str = in.readLine();

		// Preprocess the file, looking for any header information
		while (str != null && str.startsWith("DNA \\ Assay") == false)
		{
			str = in.readLine();
			lineCount++;
		}

		// Split the first line up into an array of marker names (we ignore the
		// first element as it's a redundant column header: i=1 in loop below)
		String[] markerNames = str.split(sep);

		// Now work out the map indices of these markers and the indices within
		// the map itself. This speeds up loading by pre-caching this data so we
		// don't need to search per line, rather just once per file
		int[] mapIndex = new int[markerNames.length];
		int[] mkrIndex = new int[markerNames.length];

		for (int i = 1; i < markerNames.length && isOK; i++)
		{
			MarkerIndex index = queryMarker(markerNames[i].trim());

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

			lineCount++;

			String[] values = str.split(sep);

			if (values.length == 0)
				continue;

			// Check for duplicate line names
			String name = values[0].trim();
			if (linesByName.get(name) != null)
				throw new DataFormatException(RB.format("io.DataFormatException.duplicateLineError", name, lineCount + 1));
			else
				linesByName.put(name, new ArrayList<Line>());

			// Scan for "NTC" (non target control) data - ignore any lines that have this
			if (values.length > 1 && values[1].equals("NTC"))
				continue;

			Line line = dataSet.createLine(name, useByteStorage);
			linesByName.get(name).add(line);

			for (int i = 1; i < values.length; i++)
			{
				// Assuming a map was found that contains this marker...
				if (mapIndex[i] != -1)
				{
					String rawAlleles = values[i].trim();
					// Replace all "unknown" states with the missing data str
					rawAlleles = rawAlleles.replace("?", ioMissingData);
					rawAlleles = rawAlleles.replace("Uncallable", ioMissingData);
					rawAlleles = rawAlleles.replace("Missing", ioMissingData);
					rawAlleles = rawAlleles.replace("DUPE", ioMissingData);
					rawAlleles = rawAlleles.replace("Bad", ioMissingData);
					rawAlleles = rawAlleles.replace("Unused", ioMissingData);
					rawAlleles = rawAlleles.replace("empty", ioMissingData);
					rawAlleles = rawAlleles.replace("NA", ioMissingData);
					rawAlleles = rawAlleles.replace("NN", ioMissingData);

					// Intertek will code "A" as "A/A" - deal with it:
					String[] alleles = rawAlleles.split(ioHeteroSeparator);
					if (alleles.length == 2)
						if (alleles[0].equals(alleles[1]))
							rawAlleles = alleles[0];

					// Determine its various states
					Integer stateCode = states.get(rawAlleles);
					if (stateCode == null)
					{
						stateCode = stateTable.getStateCode(rawAlleles, true,
							ioMissingData, ioHeteroSeparator);
						states.put(rawAlleles, stateCode);
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

		return true;
	}

	private MarkerIndex queryMarker(String name)
	{
		// If a map was provided, then just use the hashtable
		if (mapWasProvided || fakeMapCreated)
			return markers.get(name);

		// Otherwise, we're into the special case for no map

		// Make sure it's not a duplicate marker
		if (markers.get(name) != null)
			return null;

		// Its position will just be based no how many we've added so far
		int position = markers.size();
		Marker marker = new Marker(name, position);

		// There's only one map, so just grab it and add the marker to it
		ChromosomeMap map = dataSet.getChromosomeMaps().get(0);
		map.addMarker(marker);
		// Updating its length as we go
		map.setLength(position);

		// We can set the ChrIndex to 0 as we know there's only one
		MarkerIndex mi = new MarkerIndex(0, markers.size());
		markers.put(marker.getName(), mi);

		return mi;
	}

	@Override
	public long getBytesRead()
		{ return (is == null) ? 0 : is.getBytesRead(); }

	public boolean isOK()
	{
		return isOK;
	}

	// Reads (to up) the first 25 lines of the file and determines if it's using
	// tab or comma seperation. Basically, if we see even just one tab, then
	// that's what we're going with
	private String determineSeparator()
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(file));

			String str = in.readLine();
			int count = 1;

			while (str != null && count < 25)
			{
				if (str.contains("\t"))
				{
					in.close();
					return "\t";
				}

				str = in.readLine();
				count++;
			}

			in.close();
		}
		catch (Exception e)
		{
		}

		return ",";
	}
}