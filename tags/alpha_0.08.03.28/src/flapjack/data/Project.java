package flapjack.data;

import java.io.*;
import java.util.*;

public class Project
{
	// Temporary object used to track the (most recent) file this project was
	// opened from
	public File filename;

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

	/**
	 * Called after a project has been loaded to carry out any further tasks
	 * that may be needed to fully reinstate the project into its working state.
	 */
	public void runPostLoadingTasks()
	{
		for (DataSet dataSet: dataSets)
			dataSet.runPostLoadingTasks();
	}

	public void addDataSet(DataSet dataSet)
	{
		this.dataSets.add(dataSet);
	}

	void removeDataSet(DataSet dataSet)
	{
		dataSets.remove(dataSet);
	}
}