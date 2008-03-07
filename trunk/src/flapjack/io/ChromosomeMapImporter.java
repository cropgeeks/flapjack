package flapjack.io;

import java.io.*;

import flapjack.data.*;

public class ChromosomeMapImporter
{
	private File file;
	private DataSet dataSet;

	public ChromosomeMapImporter(File file, DataSet dataSet)
	{
		this.file = file;
		this.dataSet = dataSet;
	}

	public void importMap()
		throws IOException, DataFormatException
	{
		BufferedReader in = new BufferedReader(new FileReader(file));

		String str = null;

		while ((str = in.readLine()) != null)
		{
			String[] tokens = str.split("\t");

			// Parse out the marker's position
			float position = Float.parseFloat(tokens[2]);

			// (And its name), using them to create a new marker
			Marker marker = new Marker(tokens[0], position);

			// Check to see if this marker names already exists (in any map)?
			int mapIndex = dataSet.getMapIndexByMarkerName(marker.getName());
			if (mapIndex != -1)
			{
				System.out.println("Duplicate marker error: " + marker + " ("
					+ tokens[1] + ") already found in "
					+ dataSet.getMapByIndex(mapIndex).getName());
			}
			else
			{
				// Retrieve the map it should be added to
				ChromosomeMap map = dataSet.getMapByName(tokens[1], true);
				// And add it
				map.addMarker(marker);
			}
		}

		in.close();

		dataSet.sortChromosomeMaps();
	}
}