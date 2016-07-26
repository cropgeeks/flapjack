// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;

import java.io.*;
import java.util.*;
import java.text.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.io.*;
import scri.commons.gui.*;

public class ChromosomeMapImporter implements IMapImporter
{
	private ProgressInputStream is;
	private File file;
	private DataSet dataSet;

	// Each marker's name is stored (only while loading) in a hashmap, along
	// with the index of the chromosome it is associated with
	private HashMap<String, MarkerIndex> markers = new HashMap<>();
	private long markerCount = 0;

	private LinkedList<String> duplicates = new LinkedList<>();

	private boolean isOK = true;

	public ChromosomeMapImporter(File file, DataSet dataSet)
	{
		this.file = file;
		this.dataSet = dataSet;
	}

	@Override
	public HashMap<String, MarkerIndex> getMarkersHashMap()
		{ return markers; }

	@Override
	public void cancelImport()
		{ isOK = false; }

	@Override
	public void importMap()
		throws Exception
	{
		if (file == null)
		{
			// Create a "fake" map to hold any markers that are found in the
			// genotype file instead
			dataSet.getMapByName("1", true);

			return;
		}

		is = new ProgressInputStream(new FileInputStream(file));

		BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		NumberFormat nf = NumberFormat.getInstance();

		String str = null;
		int linesRead = 0;

		while ((str = in.readLine()) != null && isOK)
		{
			linesRead++;

			if (str.isEmpty() || str.startsWith("#"))
				continue;

			String[] tokens = str.trim().split("\t");

			// Each line needs to be either be 2 or 3 columns
			if (tokens.length < 2 || tokens.length > 3)
				throw new DataFormatException(RB.format("io.DataFormatException.mapTokenError", file));

			// Dealing with a chromosome
			if (tokens.length == 2)
			{
				String name = tokens[0].trim();

				// Parse out its length
				double length = 0;
				try { length = nf.parse(tokens[1]).doubleValue(); }
				catch (Exception e)	{
					throw new DataFormatException(RB.format("io.DataFormatException.parseLengthError", file, tokens[1], linesRead));
				}

				ChromosomeMap.Wrapper w = dataSet.getMapByName(name, true);
				w.map.setLength(length);
			}

			// Dealing with a marker
			else if (tokens.length == 3)
			{
				// Parse out the marker's position
				double position = 0;
				try { position = nf.parse(tokens[2]).doubleValue(); }
				catch (Exception e)	{
					throw new DataFormatException(RB.format("io.DataFormatException.parseDistanceError", file, tokens[2], linesRead));
				}

				// (And its name), using them to create a new marker
				Marker marker = new Marker(tokens[0].trim(), position);

				// Check to see if this marker already exists (in any map)?
				MarkerIndex index = markers.get(marker.getName());
				if (index != null)
				{
					if (Prefs.warnDuplicateMarkers)
						duplicates.add(marker.getName() + "\t" + tokens[1] + "\t"
							+ dataSet.getMapByIndex(index.mapIndex).getName());
				}
				else
				{
					// Retrieve the map it should be added to
					ChromosomeMap.Wrapper w = dataSet.getMapByName(tokens[1], true);
					// And add it
					w.map.addMarker(marker);

					// And store it in the hashmap too
					markers.put(marker.getName(), new MarkerIndex(w.index, 0));

					markerCount++;
				}
			}
		}

		in.close();

		if (isOK)
			dataSet.orderMarkersWithinMaps();

		Collections.sort(dataSet.getChromosomeMaps());

		System.out.println("markers.size() = " + markers.size());

		// Once the data is loaded, we need to update the hashmap with the
		// index (within each map) of each marker, so that the genotype importer
		// can use it during its loading
		short mapIndex = 0;
		for (ChromosomeMap map: dataSet.getChromosomeMaps())
		{
			int mkrIndex = 0;
			for (Marker marker: map)
			{
				MarkerIndex mi = markers.get(marker.getName());
				mi.mkrIndex = mkrIndex++;
				mi.mapIndex = mapIndex;
			}

			mapIndex++;
		}

		System.out.println("assigned marker indexes");
	}

	@Override
	public LinkedList<String> getDuplicates()
		{ return duplicates; }

	@Override
	public long getBytesRead()
		{ return (is == null) ? 0 : is.getBytesRead(); }

	@Override
	public long getMarkerCount()
		{ return markerCount; }
}