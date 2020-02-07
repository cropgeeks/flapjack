// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.io.*;
import java.net.*;
import java.util.*;
import jhi.brapi.api.genotyping.callsets.*;
import jhi.brapi.api.genotyping.variantsets.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.io.*;

//import jhi.brapi.api.markerprofiles.*;
import okhttp3.*;
import scri.commons.gui.*;
import scri.commons.io.*;

public class BrapiGenotypeImporter implements IGenotypeImporter
{
	private ProgressInputStream is;
	private DataImporter importer;
	private DataSet dataSet;
	private StateTable stateTable;

	// Each marker's name is stored (only while loading) in a hashmap, along
	// with the index of the chromosome it is associated with
	private HashMap<String, MarkerIndex> markers;
	private HashMap<String, MarkerIndex> markersByName;

	// Stores known states as we find them so we don't have to keep working them
	// out for each allele
	private HashMap<String, Integer> states = new HashMap<>();

	private String ioMissingData;
	private String ioHeteroSeparator;

	private long alleleCount;

	private boolean useByteStorage = true;

	private boolean isOK = true;

	private BrapiClient client;
	private boolean mapWasProvided;
	private boolean fakeMapCreated = false;

	public BrapiGenotypeImporter(DataImporter importer, BrapiClient client, DataSet dataSet, HashMap<String, MarkerIndex> markers,
		 HashMap<String, MarkerIndex> markersByName, String ioMissingData, String ioHeteroSeparator)
	{
		this.importer = importer;
		this.client = client;
		this.dataSet = dataSet;
		this.markers = markers;
		this.markersByName = markersByName;
		this.ioMissingData = ioMissingData;
		this.ioHeteroSeparator = ioHeteroSeparator;

		this.ioMissingData = "N";
		this.ioHeteroSeparator = "/";

		stateTable = dataSet.getStateTable();

		mapWasProvided = markersByName.size() > 0;
	}

	@Override
	public void cleanUp()
	{
		markers.clear();
	}

	@Override
	public void cancelImport()
	{
		isOK = false;
		client.cancel();
	}

	@Override
	public long getLineCount()
		{ return dataSet.getLines().size(); }

	@Override
	public long getMarkerCount()
		{ return alleleCount; }

	@Override
	// Returns false if storing the data using byte arrays failed
	public boolean importGenotypeDataAsBytes()
		throws Exception
	{
		return readData();
	}

	@Override
	public void importGenotypeDataAsInts()
		throws Exception
	{
		useByteStorage = false;

		readData();
	}

	private boolean readData()
		throws Exception
	{
		return readFlapjackFile();
	}

	private boolean readFlapjackFile()
		throws Exception
	{
		BufferedReader in = null;
		URI uri = null;

		VariantSet vSet = client.getVariantSet();
		for (Format f: vSet.getAvailableFormats())
		{
			if (f.getDataFormat().equals("Flapjack"))
				uri = new URI(f.getFileUrl());
		}

		// Temporary hack to at least return line name information
		// THIS SHOULD BE REMOVED POST ICRISAT HACKATHON
		if (uri == null)
		{
			for (CallSet callset: client.getCallsets())
				dataSet.createLine(callset.getCallSetName(), useByteStorage);

			return true;
		}

		// TODO: Better warning if no flapjack format/url found?


		if (isOK && uri != null)
		{
			Response response = client.getResponse(uri);
			String cl = response.header("Content-Length");

			long contentLength = 0;
			try { contentLength = Long.parseLong(cl); }
			catch (NumberFormatException ne) {}

			if (contentLength != 0)
			{
				importer.setTotalBytes(contentLength);
				importer.setMaximum();
			}

			// If the file is 500MB in size or larger
			if (cl != null && contentLength >= 524288000)
			{
				String size = FlapjackUtils.getSizeString(contentLength);
				String msg = RB.format("io.BrapiGenotypeImporter.largeFileMsg", size);
				String[] options = new String[]{RB.getString("gui.text.ok"), RB.getString("gui.text.cancel")};

				if (TaskDialog.show(msg, TaskDialog.QST, 1, options) != 0)
				{
					response.close();
					cancelImport();
					return false;
				}
			}

			is = new ProgressInputStream(response.body().byteStream());
			in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		}
		else
			return false;


		// The first line is a list of marker names
		String str;

		while ((str = in.readLine()) != null && !str.isEmpty() && isOK)
		{
			if (str.startsWith("#"))
				continue;

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

			while ((str = in.readLine()) != null && isOK)
			{
				if (str.length() == 0)
					continue;

				String[] values = str.split("\t");

				if (values.length == 0)
					continue;

				String name = values[0].trim();
				Line line = dataSet.createLine(name, useByteStorage);

				for (int i = 1; i < values.length && isOK; i++)
				{
					// Assuming a map was found that contains this marker...
					if (mapIndex[i] != -1)
					{
						// Determine its various states
						Integer stateCode = states.computeIfAbsent(values[i],
							a -> stateTable.getStateCode(a, true, ioMissingData, ioHeteroSeparator));

						// Then apply them to the marker data
						line.setLoci(mapIndex[i], mkrIndex[i], stateCode);

						alleleCount++;
					}
				}

				if (useByteStorage && stateTable.size() > 127)
					return false;
			}
		}

		in.close();

		return true;
	}

	@Override
	public long getBytesRead()
		{ return (is == null) ? 0 : is.getBytesRead(); }

	private MarkerIndex queryMarker(String name)
	{
		// If a map was provided, then just use the hashtable
		if (mapWasProvided || fakeMapCreated)
			return markersByName.get(name);

		// Otherwise, we're into the special case for no map

		// Make sure it's not a duplicate marker
		if (markersByName.get(name) != null)
			return null;

		// Its position will just be based no how many we've added so far
		int position = markersByName.size();
		Marker marker = new Marker(name, position);

		// There's only one map, so just grab it and add the marker to it
		ChromosomeMap map = dataSet.getChromosomeMaps().get(0);
		map.addMarker(marker);
		// Updating its length as we go
		map.setLength(position);

		// We can set the ChrIndex to 0 as we know there's only one
		MarkerIndex mi = new MarkerIndex(0, markersByName.size());
		markersByName.put(marker.getName(), mi);

		return mi;
	}

	public boolean isOK()
		{ return isOK; }
}