// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.visualization.colors.*;

import scri.commons.gui.*;

public class PostImportOperations
{
	private DataSet dataSet;

	public PostImportOperations(DataSet dataSet)
	{
		this.dataSet = dataSet;
	}

	public void collapseHeterozygotes()
	{
		long s = System.currentTimeMillis();

		// Remove duplicate allele states
		CollapseHeterozygotes c = new CollapseHeterozygotes(dataSet);
		c.collapse();

		long e = System.currentTimeMillis();
		System.out.println("Genotypes collapsed in " + (e-s) + "ms");
	}

	public void setName(File importFile)
	{
		String name = importFile.getName();

		// Strip away the extension (if there is one)
		if (name.lastIndexOf(".") != -1)
			name = name.substring(0, name.lastIndexOf("."));

		setName(name);
	}

	public void setName(String name)
	{
		name += " " + dataSet.countLines() + "x" + dataSet.countGenuineMarkers();

		dataSet.setName(name);
	}

	public void createDefaultView()
	{
		// Create (and add) a default view of the dataset
		String name = RB.getString("gui.navpanel.VisualizationNode.defaultView");
		GTViewSet viewSet = new GTViewSet(dataSet, name);
		dataSet.getViewSets().add(viewSet);

		int hCount = dataSet.getStateTable().calculateHomozygousStateCount();


		// Nucleotide
		if (dataSet.getStateTable().containsNucleotides01())
			viewSet.setColorScheme(ColorScheme.NUCLEOTIDE01);
		else if (dataSet.getStateTable().containsNucleotides())
			viewSet.setColorScheme(ColorScheme.NUCLEOTIDE);

		// ABH
//		else if (dataSet.getStateTable().containsABHData())
//			viewSet.setColorScheme(ColorScheme.ABH_DATA);
		// Two colour
		else if (hCount > 0 && hCount < 10)
			viewSet.setColorScheme(ColorScheme.SIMPLE_TWO_COLOR);

		else if (dataSet.getBinnedData().containsBins())
			viewSet.setColorScheme(ColorScheme.BINNED_10);

		// Random
		else
			viewSet.setColorScheme(ColorScheme.RANDOM);
	}
}