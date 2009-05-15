package flapjack.io;

import java.io.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.*;
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

		name += " " + dataSet.countLines() + "x" + dataSet.countMarkers();

		dataSet.setName(name);
	}

	public void createDefaultView()
	{
		// Create (and add) a default view of the dataset
		String name = RB.getString("gui.navpanel.VisualizationNode.defaultView");
		GTViewSet viewSet = new GTViewSet(dataSet, name);
		dataSet.getViewSets().add(viewSet);

		int hCount = dataSet.getStateTable().getHomozygousStateCount();

		if (dataSet.getStateTable().containsNucleotides())
			viewSet.setColorScheme(ColorScheme.NUCLEOTIDE);
		else if (hCount > 0 && hCount < 10)
			viewSet.setColorScheme(ColorScheme.SIMPLE_TWO_COLOR);
		else
			viewSet.setColorScheme(ColorScheme.RANDOM);
	}

	public void calculateMarkerFrequencies()
	{
		// Pre-calculate allele frequencies on a per-locus basis
		CalculateMarkerFrequencies c = new CalculateMarkerFrequencies(dataSet);
		c.calculate();
	}
}