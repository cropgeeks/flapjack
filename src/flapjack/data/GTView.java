package flapjack.data;

import java.util.*;

public class GTView
{
	// For faster rendering, maintain a local cache of the data to be drawn
	// This saves having to query every line for the data by first telling it
	// what map we're looking it - this object simply holds data for all the
	// lines, but only for the map in question
	Vector<GenotypeData> genotypeLines;

	private DataSet dataSet;
	private ChromosomeMap map;

	// Holds the index positions of the lines as they appear in the actual
	// dataset's vector of lines
	private Vector<Integer> lines;


	/**
	 * Creates a new view object that will view the map within the dataset.
	 */
	public GTView(DataSet dataSet, ChromosomeMap map)
	{
		this.dataSet = dataSet;
		this.map = map;
	}

	public void initialize()
	{
		lines = new Vector<Integer>(dataSet.countLines());

		// Normal order
		for (int i = 0; i < dataSet.countLines(); i++)
		// Reverse order
//		for (int i = dataSet.countLines()-1; i >= 0; i--)
			lines.add(i);

		cacheLines();
	}

	void cacheLines()
	{
		// Now cache as much data as possible to help speed rendering
		genotypeLines = new Vector<GenotypeData>(lines.size());

		for (int i = 0; i < lines.size(); i++)
		{
			Line line = dataSet.getLineByIndex(lines.get(i));
			GenotypeData data = line.getGenotypeDataByMap(map);

			genotypeLines.add(data);
		}
	}

	public Line getLine(int index)
	{
		return dataSet.getLineByIndex(lines.get(index));
	}

	/**
	 * Returns the state information at the position of the line and marker (for
	 * this view).
	 * @param line the index of the line to query
	 * @param marker the index of the marker to query
	 * @return the state information at the position of the line and marker
	 */
	public int getState(int line, int marker)
	{
		return genotypeLines.get(line).getState(marker);
	}

	public int getMarkerCount()
	{
		return map.countLoci();
	}

	public int getLineCount()
	{
		return lines.size();
	}

	public StateTable getStateTable()
	{
		return dataSet.getStateTable();
	}
}