// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.data;

import java.util.*;

import scri.commons.*;

public class GraphData extends XMLRoot
{
	// The name of this graph
	private String name;

	// The data for the graph - stored as normalized values
	private float[] data;

	// The maximum and minimum values within the data
	private float min = Float.MAX_VALUE, max = Float.MIN_VALUE;

	public GraphData()
	{
	}

	public GraphData(ChromosomeMap map, String name)
	{
		data = new float[map.countLoci()];
		this.name = name;
	}

	// Methods required for XML serialization

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public String getData()
		{ return MatrixXML.arrayToString(data); }

	public void setData(String dataStr)
		{ this.data = MatrixXML.stringToFloatArray(dataStr); }

	public float getMinimum()
		{ return min; }

	public void setMinimum(float minimum)
		{ min = minimum; }

	public float getMaximum()
		{ return max; }

	public void setMaximum(float maximum)
		{ max = maximum; }


	// Other methods

	public void setValue(int mrkIndex, float value)
	{
		// Add the value...
		data[mrkIndex] = value;

		// Check if it's the lowest or highest value seen yet
		min = Math.min(value, min);
		max = Math.max(value, max);
	}

	public void normalize()
	{
		for (int d = 0; d < data.length; d++)
		{
			// Normalize the value...
			data[d] = (data[d] - min) / (max - min);

			if (Float.isNaN(data[d]))
				data[d] = 0;
		}
	}

	public float getRealValueAt(int mIndex)
	{
		// Get the value for it from the graph data
		float value = data[mIndex];

		// "Unnormalize" it back to its original value
		value = (value * (max-min)) + min;

		return value;
	}

	public float[] data()
		{ return data; }
}