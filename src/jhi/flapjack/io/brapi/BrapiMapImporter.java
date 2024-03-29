// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.text.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.io.*;

import uk.ac.hutton.ics.brapi.resource.genotyping.map.MarkerPosition;

public class BrapiMapImporter implements IMapImporter
{
	private BrapiClient client;
	private DataSet dataSet;

	// Each marker's name is stored (only while loading) in a hashmap, along
	// with the index of the chromosome it is associated with
	private HashMap<String, MarkerIndex> markersByDBId = new HashMap<>();
	private HashMap<String, MarkerIndex> markersByName = new HashMap<>();
	private long markerCount = 0;

	private LinkedList<String> duplicates = new LinkedList<>();

	private boolean isOK = true;

	public BrapiMapImporter(BrapiClient client, DataSet dataSet)
	{
		this.client = client;
		this.dataSet = dataSet;
	}

	@Override
	public HashMap<String, MarkerIndex> getMarkersHashMap()
		{ return markersByName; }

	@Override
	public void cancelImport()
		{ isOK = false; }

	@Override
	public void importMap()
		throws Exception
	{
		if (Prefs.guiBrAPIUseMaps == false)
		{
			// Create a "fake" map to hold any markers that are found in the
			// genotype file instead
			dataSet.getMapByName("1", true);

			return;
		}

		NumberFormat nf = NumberFormat.getInstance();

		List<MarkerPosition> list = client.getMapMarkerData();

		HashMap<String,String> namesToIDs = new HashMap<>();

		for (MarkerPosition bm: list)
		{
			namesToIDs.put(bm.getVariantName(), bm.getVariantDbId());

			// Each MapEntry represents a marker: its name, chromosome, and
			// location on chromosome

			double position = bm.getPosition();
			String chromosome = bm.getLinkageGroupName();

			Marker marker = new Marker(bm.getVariantName(), position);

			// Check to see if this marker already exists (in any map)?
			MarkerIndex index = markersByDBId.get(bm.getVariantDbId());
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
				MarkerIndex mi = new MarkerIndex(w.index, 0);
				markersByDBId.put(bm.getVariantDbId(), mi);
				markersByName.put(bm.getVariantName(), mi);

				markerCount++;
			}
		}

		// If we can retrieve information on a map's chromosome lengths (via the
		// BRAPI maps/id call, do so and set the length for each chromosome
		if (isOK && client.hasMapsMapDbId())
		{
// TODO: BrAPI v2 fixes - will have to call /maps/{id}/{linkagegroup} [n] times
			// to get the max position for each chromosome
//			GenomeMap md = client.getMapMetaData();
//			for (BrapiLinkageGroup group : md.getData())
//			{
//				ChromosomeMap.Wrapper wrapper = dataSet.getMapByName(group.getLinkageGroupName(), false);
//				wrapper.map.setLength(group.getMaxPosition());
//			}
		}

		if (isOK)
			dataSet.orderMarkersWithinMaps();

		Collections.sort(dataSet.getChromosomeMaps());

		System.out.println("markers.size() = " + markersByDBId.size());

		// Once the data is loaded, we need to update the hashmap with the
		// index (within each map) of each marker, so that the genotype importer
		// can use it during its loading
		short mapIndex = 0;
		for (ChromosomeMap map: dataSet.getChromosomeMaps())
		{
			int mkrIndex = 0;
			for (Marker marker: map)
			{
				MarkerIndex mi = markersByDBId.get(namesToIDs.get(marker.getName()));
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