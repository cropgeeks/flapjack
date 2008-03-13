package flapjack.io;

import java.io.*;

import flapjack.data.*;

public class GenotypeDataImporter
{
	private File file;
	private DataSet dataSet;
	private StateTable stateTable;

	private String ioMissingData;
	private String ioHeteroSeparator;

	private int lineCount;
	private int markerCount;

	public GenotypeDataImporter(File file, DataSet dataSet, String ioMissingData, String ioHeteroSeparator)
	{
		this.file = file;
		this.dataSet = dataSet;
		this.ioMissingData = ioMissingData;
		this.ioHeteroSeparator = ioHeteroSeparator;

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
		long s = System.currentTimeMillis();

		BufferedReader in = new BufferedReader(new FileReader(file));

		String str = in.readLine();

		// Split the first line up into an array of marker names (we ignore the
		// first element as it's a redundant column header)
		String[] markers = str.split("\t");

		// Now work out the map indices of these markers and the indices within
		// the map itself. This speeds up loading by pre-caching this data so we
		// don't need to search per line, rather just once per file
		int[] mapIndex = new int[markers.length];
		int[] markerIndex = new int[markers.length];

		for (int i = 1; i < markers.length; i++)
		{
			mapIndex[i] = dataSet.getMapIndexByMarkerName(markers[i]);
			markerIndex[i] = dataSet.getMapByIndex(mapIndex[i]).getMarkerIndex(markers[i]);
		}

		System.out.println("Map/marker cache created in " + (System.currentTimeMillis()-s) + "ms");
		s = System.currentTimeMillis();

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

					// Determine its various states
					int stateCode = stateTable.getStateCode(values[i], true,
						ioMissingData, ioHeteroSeparator);

					// Then apply them to the marker data
					line.setLoci(mapIndex[i], markerIndex[i], stateCode);

					markerCount++;
				}
			}
		}

		System.out.println(dataSet.countLines() + " lines by " + dataSet.countMarkers() + " loci");

		in.close();
	}
}