// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.text.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.io.*;

import jhi.brapi.resource.*;

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

		List<BrapiMarkerPosition> list = BrapiClient.getMapMarkerData(request.getMapID());

		HashMap<String,String> namesToIDs = new HashMap<>();

		for (BrapiMarkerPosition bm: list)
		{
			namesToIDs.put(bm.getMarkerName(), bm.getMarkerId());

			// Each MapEntry represents a marker: its name, chromosome, and
			// location on chromosome

			double position = nf.parse(bm.getLocation()).doubleValue();
			String chromosome = bm.getLinkageGroup();

			Marker marker = new Marker(bm.getMarkerName(), position);
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
				// NOTE *********
				// This is different from normal load code that uses names; here we#re using IDs as that's
				// what BRAPI returns, but we've used the hash above to map between names and IDs
				// *************
				markers.put(bm.getMarkerId(), new MarkerIndex(w.index, 0));

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
				MarkerIndex mi = markers.get(namesToIDs.get(marker.getName()));
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