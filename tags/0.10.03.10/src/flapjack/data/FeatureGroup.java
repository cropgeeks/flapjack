// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.data;

import java.util.*;

/** NOT an XML document element (ie, never saved to a .flapjack project) */
public class FeatureGroup implements Iterable<Feature>
{
	private ArrayList<Feature> features = new ArrayList<Feature>();

	private float min, max;

	public FeatureGroup(Feature feature)
	{
		this.min = feature.getMin();
		this.max = feature.getMax();

		features.add(feature);
	}

	public Iterator<Feature> iterator()
		{ return features.iterator(); }

	public Feature get(int index)
		throws ArrayIndexOutOfBoundsException
	{
		return features.get(index);
	}

	public void addFeature(Feature feature)
	{
		if (feature.getMin() < min)
			min = feature.getMin();
		if (feature.getMax() > max)
			max = feature.getMax();

		features.add(0, feature);
	}

	public int size()
		{ return features.size(); }

	public float getMin()
		{ return min; }

	public float getMax()
		{ return max; }
}