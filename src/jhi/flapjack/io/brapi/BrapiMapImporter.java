// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.text.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.io.*;

import hutton.brapi.resource.*;

public class BrapiMapImporter implements IMapImporter
{
	BrapiRequest request;
	private DataSet dataSet;

	// Each marker's name is stored (only while loading) in a hashmap, along
	// with the index of the chromosome it is associated with
	private HashMap<String, MarkerIndex> markers = new HashMap<>();
	private long markerCount = 0;

	private LinkedList<String> duplicates = new LinkedList<>();

	private boolean isOK = true;

	public BrapiMapImporter(BrapiRequest request, DataSet dataSet)
	{
		this.request = request;
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
		NumberFormat nf = NumberFormat.getInstance();

		MapDetail mapDetail = BrapiClient.getMapDetail(request.getMapID());

		for (MapEntry me: mapDetail.getEntries())
		{
			// Each MapEntry represents a marker: its name, chromosome, and
			// location on chromosome

			float position = nf.parse(me.getLocation()).floatValue();
			String chromosome = me.getChromosome();

			Marker marker = new Marker(me.getMarkerName(), position);
	//		System.out.println(marker);


			// Check to see if this marker already exists (in any map)?
			MarkerIndex index = markers.get(marker.getName());
			if (index != null)
			{
				if (Prefs.warnDuplicateMarkers)
					duplicates.add(marker.getName() + "\t" + chromosome + "\t"
						+ dataSet.getMapByIndex(index.mapIndex).getName());
			}
			else
			{
				// Retrieve the map it should be added to
				ChromosomeMap.Wrapper w = dataSet.getMapByName(chromosome, true);
				// And add it
				w.map.addMarker(marker);

				// And store it in the hashmap too
				markers.put(marker.getName(), new MarkerIndex(w.index, 0));

				markerCount++;
			}
		}

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
	}

	@Override
	public LinkedList<String> getDuplicates()
		{ return duplicates; }

	@Override
	public long getBytesRead()
		{ return 0; }

	@Override
	public long getMarkerCount()
		{ return markerCount; }
}