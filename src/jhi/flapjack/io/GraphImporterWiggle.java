// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;

import java.io.*;
import java.util.*;

import jhi.flapjack.data.*;

public class GraphImporterWiggle extends GraphImporter
{
	public GraphImporterWiggle(File file, DataSet dataSet)
	{
		super(file, dataSet);
	}

	@Override
	protected void parseFile()
		throws Exception
	{
		String graphName = "";
		String chrom = "";

		while ((str = in.readLine()) != null && okToRun)
		{
			if (str.length() == 0 || str.startsWith("#"))
				continue;

			// Parse track header
			else if (str.startsWith("track type=wiggle_0"))
				graphName = parseName(str);

			// Parse chromosome
			else if (str.startsWith("variableStep"))
				chrom = parseChrom(str);

			else
				addToGraph(chrom, graphName);
		}
	}

	private void addToGraph(String chrom, String graphName)
		throws Exception
	{
		String[] tokens = str.split(" ");
		if (tokens.length == 2)
		{
			double markerPos = nf.parse(tokens[0]).doubleValue();
			float value = nf.parse(tokens[1]).floatValue();

			String marker = getMarkerNameAtPosition(chrom, markerPos);

			if (graphName.length() > 0 && marker.length() > 0)
				addToGraph(marker, graphName, value);
		}
	}

	private String getMarkerNameAtPosition(String chrom, double pos)
		throws Exception
	{
		String markerName = "";

		// Get the marker name from the relevant chromosome map
		for (ChromosomeMap m : dataSet.getChromosomeMaps())
		{
			if (m.getName().equals(chrom))
			{
				int result = Collections.binarySearch(m.getMarkers(), new Marker("", pos));
				markerName = result != -1 ? m.getMarkerByIndex(result).getName() : "";
			}
		}

		return markerName;
	}

	private String parseName(String str)
	{
		String name = "";
		String[] vals = str.split("\\s");

		for (String s : vals)
			if (s.startsWith("name="))
				name = s.substring(s.indexOf('=')+2, s.length()-1);

		return name;
	}

	private String parseChrom(String str)
	{
		String chrom = "";
		String[] vals = str.split("\\s");

		for (String s : vals)
			if (s.startsWith("chrom="))
				chrom = s.substring(s.indexOf('=')+1);

		return chrom;
	}
}