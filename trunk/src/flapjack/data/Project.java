package flapjack.data;

import java.io.*;
import java.util.*;

public class Project
{
	// Temporary object used to track the (most recent) file this project was
	// opened from
	private File filename;

	// The datasets within the project
	private LinkedList<DataSet> datasets = new LinkedList<DataSet>();

	public Project()
	{
	}

/*	public LinkedList<DataSet> getDatasets()
	{
		return datasets;
	}

	public void setDatasets(LinkedList<DataSet> datasets)
	{
		this.datasets = datasets;
	}
*/
	public void addDataSet(DataSet dataSet)
	{
		this.datasets.add(dataSet);
	}

	void removeDataSet(DataSet dataSet)
	{
		datasets.remove(dataSet);
	}
}