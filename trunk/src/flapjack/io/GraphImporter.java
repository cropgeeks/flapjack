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
	// Temporary object to track which graphs exist - needed to ensure every
	// graph will exist across every chromosome, just in case some chromosomes
	// don't have markers with graph values
	private HashMap<String, String> names = new HashMap<String, String>();

	// Stores the graphs while loading occurs: index is "CHROMOSOMEINDEX_GRAPH"
	private HashMap<String, GraphData> graphs = new HashMap<String, GraphData>();


	public GraphImporter(File file, DataSet dataSet)
	{
		this.file = file;
		this.dataSet = dataSet;

		maximum = 5555;
	}

	public void runJob(int index)
		throws Exception
	{
		for (ChromosomeMap map: dataSet.getChromosomeMaps())
			map.getGraphs().clear();

		// Build the lookup table
		buildMarkerHash();

		// Read the file and populate the GraphData objects
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


			// Does this marker exist?
			MarkerIndex mIndex = markers.get(marker);
			if (mIndex == null)
				continue;

			// Do we have a graph that the value can be added to?
			if (names.get(graphName) == null)
			{
				// Initialize a new graph for each chromosome
				for (int i = 0; i < dataSet.getChromosomeMaps().size(); i++)
				{
					ChromosomeMap map = dataSet.getMapByIndex(i);
					GraphData graph = new GraphData(map, graphName);

					graphs.put(i + "_" + graphName, graph);
					map.getGraphs().add(graph);
				}

				names.put(graphName, graphName);
			}

			// Find the graph we want to add this value to
			String gIndex = mIndex.mapIndex + "_" + graphName;
			GraphData graph = graphs.get(gIndex);

			graph.setValue(mIndex.mkrIndex, value);
		}

		in.close();


		// Finally, apply the loaded data to the main data API
		if (okToRun)
			normalize();

		// Cancel all graphs added to the chromosomes
		else
			for (ChromosomeMap map: dataSet.getChromosomeMaps())
				map.getGraphs().clear();
	}

	private void normalize()
		throws Exception
	{
		// We first need to scan all graphs for their mins and maxes and then
		// apply those values so that the same min/max is used for that graph
		// across every chromosome
		for (int i = 0; i < names.size(); i++)
		{
			float min = Float.MAX_VALUE;
			float max = Float.MIN_VALUE;

			// Find the min/max over all the data for this graph
			for (ChromosomeMap map: dataSet.getChromosomeMaps())
			{
				min = Math.min(map.getGraphs().get(i).getMinimum(), min);
				max = Math.max(map.getGraphs().get(i).getMaximum(), max);
			}

			// Then set it back to each graph instance
			for (ChromosomeMap map: dataSet.getChromosomeMaps())
			{
				map.getGraphs().get(i).setMinimum(min);
				map.getGraphs().get(i).setMaximum(max);
			}
		}

		// Normalize the graphs across every chromosome
		for (ChromosomeMap map: dataSet.getChromosomeMaps())
			for (GraphData graph: map.getGraphs())
				graph.normalize();
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