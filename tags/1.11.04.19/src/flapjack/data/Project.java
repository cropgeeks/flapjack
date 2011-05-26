// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.data;

import java.io.*;
import java.util.*;

import flapjack.io.*;

public class Project extends XMLRoot
{
	// Temporary object used to track the (most recent) file this project was
	// opened from
	public FlapjackFile fjFile;

	public int format = -1;

	// The datasets within the project
	private ArrayList<DataSet> dataSets = new ArrayList<DataSet>();

	public Project()
	{
	}

	public void validate()
	{
		System.out.print("Validating project...");
		for (DataSet dataSet: dataSets)
			dataSet.validate();
		System.out.println("ok");
	}

	// Methods required for XML serialization

	public ArrayList<DataSet> getDataSets()
		{ return dataSets; }

	public void setDataSets(ArrayList<DataSet> dataSets)
		{ this.dataSets = dataSets; }


	// Other methods

	public void addDataSet(DataSet dataSet)
	{
		this.dataSets.add(dataSet);
	}

	public void removeDataSet(DataSet dataSet)
	{
		dataSets.remove(dataSet);
	}
}