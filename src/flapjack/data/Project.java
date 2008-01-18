package flapjack.data;

import java.io.*;
import java.util.*;

public class Project
{
	// Temporary object used to track the (most recent) file this project was
	// opened from
	private File filename;

	// The datasets within the project
	private Vector<DataSet> dataSets = new Vector<DataSet>();

	public Project()
	{
	}


	// Methods required for XML serialization

	public Vector<DataSet> getDataSets()
		{ return dataSets; }

	public void setDataSets(Vector<DataSet> dataSets)
		{ this.dataSets = dataSets; }


	// Other methods

	public void addDataSet(DataSet dataSet)
	{
		this.dataSets.add(dataSet);
	}

	void removeDataSet(DataSet dataSet)
	{
		dataSets.remove(dataSet);
	}
}