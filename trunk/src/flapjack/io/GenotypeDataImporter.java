package flapjack.io;

import java.io.*;

import flapjack.data.*;

public class GenotypeDataImporter
{
	private File file;
	private DataSet dataSet;
	private StateTable stateTable;

	private int lineCount;
	private int markerCount;

	public GenotypeDataImporter(File file, DataSet dataSet)
	{
		this.file = file;
		this.dataSet = dataSet;

		stateTable = dataSet.getStateTable();
	}

	public File getFile()
		{ return file; }

	public int getLineCount()
		{ return lineCount; }

	public int getMarkerCount()
		{ return markerCount; }

	public void importGenotypeData()
		throws IOException, DataFormatException
	{
		BufferedReader in = new BufferedReader(new FileReader(file));

		String str = in.readLine();

		// Split the first line up into an array of marker names (we ignore the
		// first element as it's a redundant column header)
		String[] markers = str.split("\t");

		// Now work out the map indices of these markers
		// TODO: What if a marker exists in more than one map?
		int[] mapIndex = new int[markers.length];
		for (int i = 1; i < markers.length; i++)
			mapIndex[i] = dataSet.getMapIndexByMarkerName(markers[i]);

		long s = System.currentTimeMillis();

		while ((str = in.readLine()) != null)
		{
			if ((++lineCount) % 100 == 0)
			{
				System.out.println("Reading line " + lineCount + " (" + (System.currentTimeMillis()-s) + "ms)");
				s = System.currentTimeMillis();

				System.gc();
			}

			String[] values = str.split("\t");

			Line line = dataSet.createLine(values[0]);

			for (int i = 1; i < values.length; i++)
			{
				// Assuming a map is found that contains this marker...
				if (mapIndex[i] != -1)
				{
					ChromosomeMap map = dataSet.getMapByIndex(mapIndex[i]);

					// TODO: Why are so many markers found in the genotype file that
					// were not in the map file?

					// Work out how many times it appears
					int[] indices = map.getMarkerLocations(markers[i]);

					// Determine its various states
					int stateCode = stateTable.getStateCode(values[i], true);

					// Then apply them to each instance of the marker
					for (int lociIndex: indices)
						line.setLoci(mapIndex[i], lociIndex, stateCode);

					markerCount++;
				}
			}
		}

		in.close();
	}
}