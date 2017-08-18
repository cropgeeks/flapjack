// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;

import java.io.*;
import java.text.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.io.*;
import scri.commons.gui.*;

public class GenotypeDataImporter implements IGenotypeImporter
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

	private boolean isTransposed;
	private boolean allowDuplicates;

	private NumberFormat nf = NumberFormat.getInstance();

	private boolean mapWasProvided, fakeMapCreated;

	private ArrayList<String> pedigrees = new ArrayList<>();

	public GenotypeDataImporter(File file, DataSet dataSet, HashMap<String, MarkerIndex> markers,
		String ioMissingData, String ioHeteroSeparator, boolean isTransposed, boolean allowDuplicates)
	{
		this.file = file;
		this.dataSet = dataSet;
		this.markers = markers;
		this.ioMissingData = ioMissingData;
		this.ioHeteroSeparator = ioHeteroSeparator;
		this.isTransposed = isTransposed;
		this.allowDuplicates = allowDuplicates;

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
	public long getMarkerCount()
		{ return markerCount; }

	@Override
	public void importGenotypeData()
		throws IOException, DataFormatException
	{
		if (readData(isTransposed) == false)
		{
			dataSet.getLines().clear();
			stateTable.resetTable();
			states.clear();
			useByteStorage = false;

			lineCount = 0;

			readData(isTransposed);
		}
	}

	private boolean readData(boolean isTransposed)
		throws IOException, DataFormatException
	{
		boolean completed;

		if (isTransposed)
			completed = readTransposedData();
		else
			completed = readNonTransposedData();

		// If we parsed out any pedigree information we need to populate the
		// pedigree manager to build relationships between lines

		if (pedigrees.size() > 0)
		{
//			System.out.println("PEDIGREES:");
//			for (String p: pedigrees)
//				System.out.println("  " + p);

			dataSet.getPedManager().create(pedigrees, linesByName);
		}

		return completed;
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

	private boolean readNonTransposedData()
		throws IOException, DataFormatException
	{
		long s = System.currentTimeMillis();

		linesByName = new HashMap<String, ArrayList<Line>>();

		is = new ProgressInputStream(new FileInputStream(file));

		BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

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

			if ((++lineCount) % 100 == 0)
			{
				System.out.println("Reading line " + lineCount + " (" + (System.currentTimeMillis()-s) + "ms)");
				s = System.currentTimeMillis();
			}

			String[] values = str.split("\t");

			if (values.length == 0)
				continue;

			// Check for duplicate line names
			String name = values[0].trim();
			if (linesByName.get(name) != null)
			{
				if (allowDuplicates == false)
					throw new DataFormatException(RB.format("io.DataFormatException.duplicateLineError", name, lineCount + 1));
			}
			else
			{
				linesByName.put(name, new ArrayList<Line>());
			}

			Line line = dataSet.createLine(name, useByteStorage);
			linesByName.get(name).add(line);

			for (int i = 1; i < values.length; i++)
			{
				// Assuming a map was found that contains this marker...
				if (mapIndex[i] != -1)
				{
					String rawAlleles = values[i].trim();
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

	private void processHeader(String str)
	{
		// TODO re-work this code as bin data looks like a header, but doesn't
		// have an equals sign.

		if (str.indexOf('=') != -1)
		{
			try
			{
				String key = str.substring(1, str.indexOf('=')).trim();
				String value = str.substring(str.indexOf('=')+1).trim();

				// fjDatabaseLineSearch = a URL for querying line information
				if (key.equals("fjDatabaseLineSearch"))
					dataSet.getDbAssociation().setLineSearch(value);

				// fjDatabaseMarkerSearch = a URL for querying marker information
				if (key.equals("fjDatabaseMarkerSearch"))
					dataSet.getDbAssociation().setMarkerSearch(value);

				// fjDatabaseGroupPreview = a URL for creating a group in germinate / another db system
				if (key.equals("fjDatabaseGroupPreview"))
					dataSet.getDbAssociation().setGroupPreview(value);

				// fjDatabaseGroupUpload = a URL for sending a group to germinate / another db system
				if (key.equals("fjDatabaseGroupUpload"))
					dataSet.getDbAssociation().setGroupUpload(value);
			}
			catch (Exception e)
			{
				System.out.println("Invalid header: " + str);
			}
		}

		// Otherwise we have bin data
		else if (str.startsWith("# bin"))
		{
			String[] tokens = str.split("\t");
			int index = Integer.parseInt(tokens[1]);

			try
			{
				float min = nf.parse(tokens[2]).floatValue();
				float max = nf.parse(tokens[3]).floatValue();
				dataSet.getBinnedData().addBin(index, min, max);

			} catch (Exception e) {}
		}

		else if (str.toLowerCase().startsWith("# fjpedigree"))
		{
			String[] tokens = str.split("\t");

			if (tokens.length >= 4)
			{
				// More than one parent per line may be found, but add each one
				// as its own triplet set
				for (int i = 3; i < tokens.length; i++)
				{
					StringJoiner ped = new StringJoiner("\t");
					ped.add(tokens[1])    // progeny
						.add(tokens[i])   // parent
						.add(tokens[2]);  // type

					pedigrees.add(ped.toString());
				}
			}
		}
	}

	@Override
	public long getBytesRead()
		{ return (is == null) ? 0 : is.getBytesRead(); }

	// Quite a lot of duplication of work done in readData() is there a way to
	// clean this up?
	private boolean readTransposedData()
		throws IOException, DataFormatException
	{
		long s = System.currentTimeMillis();

		if (mapWasProvided == false)
			createFakeMapForTransposedData();
		linesByName = new HashMap<String, ArrayList<Line>>();

		is = new ProgressInputStream(new FileInputStream(file));

		BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

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
			lineNames[i] = lineNames[i].trim();

			if (linesByName.get(lineNames[i]) != null)
			{
				if (allowDuplicates == false)
					throw new DataFormatException(RB.format("io.DataFormatException.duplicateLineError", lineNames[i], lineCount+1));
			}
			else
			{
				linesByName.put(lineNames[i], new ArrayList<Line>());
			}

			// Create the line and add it to our lines hashmap
			Line line = dataSet.createLine(lineNames[i], useByteStorage);
			linesByName.get(lineNames[i]).add(line);
		}

		// This is now a loop over the markers
		while ((str = in.readLine()) != null && isOK)
		{
			if (str.length() == 0 || str.startsWith("#"))
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
				MarkerIndex index = queryMarker(values[0].trim());

				// Assuming a map was found that contains this marker...
				if (index != null && index.mapIndex != -1)
				{
					String rawAlleles = values[i].trim();
					// Determine its various states
					Integer stateCode = states.get(rawAlleles);
					if (stateCode == null)
					{
						stateCode = stateTable.getStateCode(rawAlleles, true,
							ioMissingData, ioHeteroSeparator);
						states.put(rawAlleles, stateCode);
					}

					// Then apply them to the marker data
					dataSet.getLines().get(i-1).setLoci(index.mapIndex, index.mkrIndex, stateCode);

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

	private void createFakeMapForTransposedData()
		throws IOException
	{
		BufferedReader in = new BufferedReader(
			new InputStreamReader(new FileInputStream(file), "UTF-8"));

		String str = null;

		// Skip the headers (this will also read the first proper line that
		// should be the line names
		while ((str = in.readLine()) != null)
		{
			if (str.isEmpty() || str.startsWith("#"))
				continue;
			else
				break;
		}

		// The rest of the file should be: MARKER_NAME\tVALUE\tVALUE etc
		while ((str = in.readLine()) != null)
		{
			if (str.isEmpty())
				continue;

			String[] values = str.split("\t");
			if (values.length == 0)
				continue;

			queryMarker(values[0].trim());
		}

		fakeMapCreated = true;

		in.close();
	}
}