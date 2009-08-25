// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.util.*;
import java.text.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class ChromosomeMapImporter
{
	private File file;
	private DataSet dataSet;

	private LinkedList<String> duplicates = new LinkedList<String>();

	private boolean isOK = true;

	public ChromosomeMapImporter(File file, DataSet dataSet)
	{
		this.file = file;
		this.dataSet = dataSet;
	}

	public void cancelImport()
		{ isOK = false; }

	public void importMap()
		throws IOException, DataFormatException
	{
		BufferedReader in = new BufferedReader(new FileReader(file));
		NumberFormat nf = NumberFormat.getInstance();

		String str = null;
		int linesRead = 1;

		while ((str = in.readLine()) != null && isOK)
		{
			if (str.length() == 0)
				continue;

			String[] tokens = str.split("\t");

			if (tokens.length != 3)
				throw new DataFormatException(RB.format("io.DataFormatException.mapTokenError", file, tokens.length, linesRead));

			// Parse out the marker's position
			float position = 0;
			try { position = nf.parse(tokens[2]).floatValue(); }
			catch (Exception e)	{
				throw new DataFormatException(RB.format("io.DataFormatException.parseDistanceError", file, tokens[2], linesRead));
			}

			// (And its name), using them to create a new marker
			Marker marker = new Marker(tokens[0], position);


			// Check to see if this marker names already exists (in any map)?
			int mapIndex = dataSet.getMapIndexByMarkerName(marker.getName());
			if (mapIndex != -1)
			{
				duplicates.add(marker.getName() + "\t" + tokens[1] + "\t"
					+ dataSet.getMapByIndex(mapIndex).getName());
			}
			else
			{
				// Retrieve the map it should be added to
				ChromosomeMap map = dataSet.getMapByName(tokens[1], true);
				// And add it
				map.addMarker(marker);
			}

			linesRead++;
		}

		in.close();

		if (isOK)
			dataSet.sortChromosomeMaps();
	}

	public LinkedList<String> getDuplicates()
		{ return duplicates; }
}