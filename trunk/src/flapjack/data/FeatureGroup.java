// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.data;

import java.util.*;

/** NOT an XML document element (ie, never saved to a .flapjack project) */
public class FeatureGroup implements Iterable<QTLInfo>
{
	private ArrayList<QTLInfo> qtls = new ArrayList<QTLInfo>();

	private float min, max;

	public FeatureGroup(QTLInfo qtlInfo)
	{
		this.min = qtlInfo.min();
		this.max = qtlInfo.max();

		qtls.add(qtlInfo);
	}

	public Iterator<QTLInfo> iterator()
		{ return qtls.iterator(); }

	public QTLInfo get(int index)
		throws ArrayIndexOutOfBoundsException
	{
		return qtls.get(index);
	}

	public void addFeature(QTLInfo feature)
	{
		if (feature.min() < min)
			min = feature.min();
		if (feature.max() > max)
			max = feature.max();

		qtls.add(0, feature);
	}

	public int size()
		{ return qtls.size(); }

	public float getMin()
		{ return min; }

	public float getMax()
		{ return max; }
}