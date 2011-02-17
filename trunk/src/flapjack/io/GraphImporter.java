// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.text.*;
import java.util.*;

import flapjack.data.*;

import scri.commons.file.*;
import scri.commons.gui.*;

public class GraphImporter extends SimpleJob
{
	private NumberFormat nf = NumberFormat.getInstance();

	private ProgressInputStream is;
	private File file;
	private DataSet dataSet;

	// Temporary object to hold a lookup table of marker data
	private HashMap<String, MarkerIndex> markers = new HashMap<String, MarkerIndex>();
	// Temporary object to map graphs to index locations
	private HashMap<String, Integer> names = new HashMap<String, Integer>();

	private ArrayList<GraphData> chromosomes = new ArrayList<GraphData>();

	public GraphImporter(File file, DataSet dataSet)
	{
		this.file = file;
		this.dataSet = dataSet;

		maximum = 5555;

		this.file = new File("E:\\Data\\Flapjack\\PAUL GRAPH DATA\\sample_genome_scans_james_cockram.txt");
	}

	public void runJob(int index)
		throws Exception
	{
		// Build the lookup table
		buildMarkerHash();

		// Start by making a GraphData object for each chromosome
		for (ChromosomeMap map: dataSet.getChromosomeMaps())
			chromosomes.add(new GraphData(map));


		// Now read the file and populate the GraphData objects
		is = new ProgressInputStream(new FileInputStream(file));
		BufferedReader in = new BufferedReader(new InputStreamReader(is));

		String str = null;

		while ((str = in.readLine()) != null && okToRun)
		{
			if (str.length() == 0)
				continue;

			// Three columns: MARKER -- TRAIT -- VALUE
			String[] tokens = str.split("\t", -1);
			String marker = tokens[0];
			String graphName = tokens[1];
			float value = nf.parse(tokens[2]).floatValue();

			// Is this a new graph? If so, allocate space for it (in each
			// chromosome). If not, we'll just use its existing index
			Integer gIndex = names.get(graphName);
			if (gIndex == null)
			{
				for (GraphData graphData: chromosomes)
					graphData.initNewGraph(graphName);

				gIndex = names.size();
				names.put(graphName, names.size());
			}

			MarkerIndex mIndex = markers.get(marker);
			if (mIndex != null)
			{
				GraphData data = chromosomes.get(mIndex.mapIndex);
				data.setValue(gIndex, mIndex.mkrIndex, value);
			}
		}

		in.close();

		// Finally, apply the loaded data to the main data API
		if (okToRun)
		{
			for (int i = 0; i < chromosomes.size(); i++)
			{
				// Normalize the imported data before assigning it
				chromosomes.get(i).normalize();

				ChromosomeMap map = dataSet.getChromosomeMaps().get(i);
				map.setGraphData(chromosomes.get(i));
			}
		}
	}

	// Stores EVERY marker from every chromosome into a hash table that can then
	// be used to find a marker by name, with instant access to its location
	private void buildMarkerHash()
		throws Exception
	{
		long s = System.currentTimeMillis();

		int mapIndex = 0;
		for (ChromosomeMap map: dataSet.getChromosomeMaps())
		{
			int mrkIndex = 0;
			for (Marker marker: map.getMarkers())
				markers.put(marker.getName(), new MarkerIndex(mapIndex, mrkIndex++));

			mapIndex++;
		}

		long e = System.currentTimeMillis();
		System.out.println("Marker index build in " + (e-s) + "ms");
	}

	public int getValue()
	{
		if (is == null)
			return 0;

		return Math.round(is.getBytesRead() / (float) file.length() * 5555);
	}
}