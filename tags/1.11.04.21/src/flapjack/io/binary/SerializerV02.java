// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.io.binary;

import java.io.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.io.*;

// V2 serialization adds support for graphs.

// - The graph data is all self-contained within the GraphData class
// - ChromosomeMap now contains an array list of GraphData objects
// - Each GTViewSet holds an integer for the selected graph

class SerializerV02 extends SerializerV01
{
	SerializerV02(DataInputStream in, DataOutputStream out)
		{ super(in, out); }

	protected void saveChromosomeMap(ChromosomeMap map, DataSet dataSet)
		throws Exception
	{
		super.saveChromosomeMap(map, dataSet);

		// Number of graphs
		out.writeInt(map.getGraphs().size());
		// Graphs
		for (GraphData graph: map.getGraphs())
			saveGraphData(graph);
	}

	protected ChromosomeMap loadChromosomeMap()
		throws Exception
	{
		ChromosomeMap map = super.loadChromosomeMap();

		// Number of graphs
		int graphCount = in.readInt();
		// Graph data
		for (int i = 0; i < graphCount; i++)
			map.getGraphs().add(loadGraphData());

		return map;
	}

	protected void saveGTViewSet(GTViewSet viewSet)
		throws Exception
	{
		super.saveGTViewSet(viewSet);

		// Selected graph index
		out.writeInt(viewSet.getGraphIndex());
	}

	protected GTViewSet loadGTViewSet(DataSet dataSet)
		throws Exception
	{
		GTViewSet viewSet = super.loadGTViewSet(dataSet);

		// Selected graph index
		viewSet.setGraphIndex(in.readInt());

		return viewSet;
	}

	protected void saveGraphData(GraphData graph)
		throws Exception
	{
		// Name
		writeString(graph.getName());

		// Min and max
		out.writeFloat(graph.getMinimum());
		out.writeFloat(graph.getMaximum());

		// Threshold
		out.writeBoolean(graph.getHasThreshold());
		out.writeFloat(graph.getThreshold());

		// Data
		float[] data = graph.data();
		out.writeInt(data.length);
		for (float f: data)
			out.writeFloat(f);
	}

	protected GraphData loadGraphData()
		throws Exception
	{
		GraphData graph = new GraphData();

		// Name
		graph.setName(readString());

		// Min and max
		graph.setMinimum(in.readFloat());
		graph.setMaximum(in.readFloat());

		// Threshold
		graph.setHasThreshold(in.readBoolean());
		graph.setThreshold(in.readFloat());

		// Data
		float[] data = new float[in.readInt()];
		for (int i = 0; i < data.length; i++)
			data[i] = in.readFloat();
		graph.setArrayData(data);

		return graph;
	}
}