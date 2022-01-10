// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

import java.util.*;

import jhi.flapjack.io.*;

public class Project extends XMLRoot
{
	// Temporary object used to track the (most recent) file this project was
	// opened from
	public FlapjackFile fjFile;

	// The datasets within the project
	private ArrayList<DataSet> dataSets = new ArrayList<>();

	private String treeState = "";

	private int[] treeSelectedRows;

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

	public String getTreeState()
		{ return treeState; }

	public void setTreeState(String treeState)
		{ this.treeState = treeState; }

	public int[] getTreeSelectedRows()
		{ return treeSelectedRows; }

	public void setTreeSelectedRows(int[] treeSelectedRows)
		{ this.treeSelectedRows = treeSelectedRows; }


	// Other methods

	public void addDataSet(DataSet dataSet)
	{
		this.dataSets.add(dataSet);
	}

	public void removeDataSet(DataSet dataSet)
	{
		dataSets.remove(dataSet);
	}

	public ArrayList<GTViewSet> retrieveAllViews()
	{
		ArrayList<GTViewSet> views = new ArrayList<>();

		for (DataSet ds: dataSets)
			for (GTViewSet viewSet: ds.getViewSets())
			{
				// if (view linked to results table) then skip
				if (viewSet.getTableHandler().model() != null)
					continue;

				views.add(viewSet);
			}

		return views;
	}
}