// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.data;

import java.util.*;

public class GraphData extends XMLRoot
{
	// The name of each graph
	private ArrayList<String> names = new ArrayList<String>();

	// The data for each graph
	private ArrayList<float[]> graphs = new ArrayList<float[]>();

	// The maximum value within each graph
	private ArrayList<Float> maxes = new ArrayList<Float>();

	private int markerCount;

	public GraphData()
	{
	}

	public GraphData(ChromosomeMap map)
	{
		markerCount = map.countLoci();
	}

	// Methods required for XML serialization

	public ArrayList<String> getNames()
		{ return names; }

	public void setNames(ArrayList<String> names)
		{ this.names = names; }

	public ArrayList<float[]> getGraphs()
		{ return graphs; }

	public void setGraphs(ArrayList<float[]> graphs)
		{ this.graphs = graphs; }

	public ArrayList<Float> getMaxes()
		{ return maxes; }

	public void setMaxes(ArrayList<Float> maxes)
		{ this.maxes = maxes; }


	// Other methods

	// Allocates the space for another graph (another trait basically) and adds
	// it to the list of existing graphs
	public void initNewGraph(String graphName)
	{
		float[] graphData = new float[markerCount];

		graphs.add(graphData);
		names.add(graphName);
		maxes.add(Float.MIN_VALUE);
	}

	public void setValue(int graphIndex, int mrkIndex, float value)
	{
		// Add the value...
		graphs.get(graphIndex)[mrkIndex] = value;

		// And check if it's the highest seen yet or not
		if (value > maxes.get(graphIndex))
			maxes.set(graphIndex, value);
	}
}