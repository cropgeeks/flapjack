// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;

import jhi.flapjack.data.*;

public class PCoAResult
{
	// Tracks the indices of the lines originally used to generate this matrix
	// This will be the same object held by the SimMatrix used to run this PCoA
	private ArrayList<LineInfo> lineInfos = new ArrayList<>();

	// Tracks the values (one array per 'line' of data)
	private ArrayList<float[]> data = new ArrayList<>();

	public PCoAResult(ArrayList<LineInfo> lineInfos)
	{
		this.lineInfos = lineInfos;
	}

	public void addDataRow(float[] values)
	{
		data.add(values);
	}

	public ArrayList<LineInfo> getLineInfos()
	{
		return lineInfos;
	}

	public ArrayList<float[]> getData()
	{
		return data;
	}
}