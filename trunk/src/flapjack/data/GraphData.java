// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.data;

import java.util.*;

public class GraphData extends XMLRoot
{
	// The name of each graph
	private ArrayList<String> names = new ArrayList<String>();

	// The data for each graph - stored as normalized values
	private ArrayList<float[]> graphs = new ArrayList<float[]>();

	// The maximum value within each graph
	private ArrayList<Float> maxs = new ArrayList<Float>();
	// The minimum value within each graph
	private ArrayList<Float> mins = new ArrayList<Float>();

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

	public ArrayList<Float> getMaxs()
		{ return maxs; }

	public void setMaxs(ArrayList<Float> maxs)
		{ this.maxs = maxs; }

	public ArrayList<Float> getMins()
		{ return mins; }

	public void setMins(ArrayList<Float> mins)
		{ this.mins = mins; }


	// Other methods

	// Allocates the space for another graph (another trait basically) and adds
	// it to the list of existing graphs
	public void initNewGraph(String graphName)
	{
		float[] graphData = new float[markerCount];

		graphs.add(graphData);
		names.add(graphName);
		maxs.add(Float.MIN_VALUE);
		mins.add(Float.MAX_VALUE);
	}

	public void setValue(int graphIndex, int mrkIndex, float value)
	{
		// Add the value...
		graphs.get(graphIndex)[mrkIndex] = value;

		// Check if it's the highest or lowest seen yet or not
		if (value > maxs.get(graphIndex))
			maxs.set(graphIndex, value);

		if (value < mins.get(graphIndex))
			mins.set(graphIndex, value);
	}

	public void normalize()
	{
		// For each graph
		for (int i = 0; i < graphs.size(); i++)
		{
			float[] data = graphs.get(i);
			float min = mins.get(i);
			float max = maxs.get(i);

			for (int d = 0; d < data.length; d++)
			{
				// Normalize the value...
				data[d] = (data[d] - min) / (max - min);

				if (Float.isNaN(data[d]))
					data[d] = 0;
			}
		}
	}
}