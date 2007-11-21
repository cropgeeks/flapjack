package flapjack.io;

import java.io.*;

import flapjack.data.*;

public class GenotypeDataImporter
{
	private File file;
	private DataSet dataSet;
	private StateTable stateTable;

	public GenotypeDataImporter(File file, DataSet dataSet)
	{
		this.file = file;
		this.dataSet = dataSet;

		stateTable = dataSet.getStateTable();
	}

	public void importGenotypeData()
		throws IOException, DataFormatException
	{
		BufferedReader in = new BufferedReader(new FileReader(file));

		String str = in.readLine();

		// Split the first line up into an array of marker names (we ignore the
		// first element as it's a redundant column header)
		String[] markers = str.split("\t");

		int lineNum = 0;
		long s = System.currentTimeMillis();

		while ((str = in.readLine()) != null)
		{
			if ((++lineNum) % 100 == 0)
			{
				System.out.println("Reading line " + lineNum + " (" + (System.currentTimeMillis()-s) + "ms)");
				s = System.currentTimeMillis();
			}

			String[] values = str.split("\t");

			Line line = dataSet.createLine(values[0]);

			for (int i = 1; i < values.length; i++)
			{
				String markerName = markers[i];

				// TODO: What if a marker exists in more than one map?
				int mapIndex = dataSet.getMapIndexByMarkerName(markerName);

				// Assuming a map is found that contains this marker...
				if (mapIndex != -1)
				{
					ChromosomeMap map = dataSet.getMapByIndex(mapIndex);

					// TODO: Why are so many markers found in the genotype file that
					// were not in the map file?

					// Work out how many times it appears
					int[] indices = map.getMarkerLocations(markerName);

					// Determine its various states
					int stateCode = stateTable.getStateCode(values[i], true);

					// Then apply them to each instance of the marker
					for (int lociIndex: indices)
						line.setLoci(mapIndex, lociIndex, stateCode);
				}
			}
		}

		in.close();

		dataSet.fake();
	}
}