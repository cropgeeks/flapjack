// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;

import java.io.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.visualization.colors.*;
import jhi.flapjack.io.brapi.*;

import scri.commons.gui.*;

public class PostImportOperations
{
	private DataSet dataSet;

	public PostImportOperations(DataSet dataSet)
	{
		this.dataSet = dataSet;
	}

	// Collapse A/A instances into A
	public void collapseHomzEncodedAsHet()
	{
		OptimizeStateTable c = new OptimizeStateTable(dataSet);
		c.collapseHomzEncodedAsHet();
	}

	// Collapse A/T instances into T/A (remove duplicate allele states)
	public void optimizeStateTable()
	{
		OptimizeStateTable c = new OptimizeStateTable(dataSet);
		c.optimize(false);
	}

	public void setName(File importFile, BrapiClient client)
	{
		if (importFile != null)
		{
			String name = importFile.getName();

			// Strip away the extension (if there is one)
			if (name.lastIndexOf(".") != -1)
				name = name.substring(0, name.lastIndexOf("."));

			setName(name);
		}
		else if (client != null)
		{
			setName(client.getVariantSetName());
		}
	}

	public void setName(String name)
	{
		name += " (" + dataSet.countLines() + "x" + dataSet.countGenuineMarkers() + ")";

		dataSet.setName(name);
	}

	public void createDefaultView(boolean forceNucScheme)
	{
		// Create (and add) a default view of the dataset
		String name = RB.getString("gui.navpanel.VisualizationNode.defaultView");
		GTViewSet viewSet = new GTViewSet(dataSet, name);
		dataSet.getViewSets().add(viewSet);

		StateTable st = dataSet.getStateTable();

		int hCount = st.calculateHomozygousStateCount();


		if (forceNucScheme)
		{
			viewSet.setColorScheme(ColorScheme.NUCLEOTIDE01);
			return;
		}

		// Nucleotide
		if (st.containsNucleotides01())
			viewSet.setColorScheme(ColorScheme.NUCLEOTIDE01);
		else if (st.containsNucleotides())
			viewSet.setColorScheme(ColorScheme.NUCLEOTIDE);

		else if (st.containsMagic())
			viewSet.setColorScheme(ColorScheme.MAGIC);

		else if (dataSet.getBinnedData().containsBins() || st.containsBins())
			viewSet.setColorScheme(ColorScheme.BINNED_10);

		// ABH
		else if (dataSet.getStateTable().containsABHData())
			viewSet.setColorScheme(ColorScheme.ABH_DATA);
		// Two colour
		else if (hCount > 0 && hCount < 10)
			viewSet.setColorScheme(ColorScheme.SIMPLE_TWO_COLOR);

		// Random
		else
			viewSet.setColorScheme(ColorScheme.RANDOM);
	}
}