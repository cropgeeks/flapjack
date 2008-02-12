package flapjack.data;

import java.util.*;

public class GTView
{
	// For faster rendering, maintain a local cache of the data to be drawn
	// This saves having to query every line for the data by first telling it
	// what map we're looking it - this object simply holds data for all the
	// lines, but only for the map in question
	private Vector<GenotypeData> genotypeLines;

	private DataSet dataSet;
	private ChromosomeMap map;

	// Holds the index positions of the lines as they appear in the actual
	// dataset's vector of lines
	private Vector<Integer> lines;

	// Contains the name of the map above - stored in the xml so we can
	// reassociate the map properly (via Java references after deserialization)
	// because Castor's reference="true" feature isn't working (08/02/2008)
	private String mapName;


	public GTView()
	{
	}

	/**
	 * Creates a new view object that will view the map within the dataset.
	 */
	public GTView(DataSet dataSet, ChromosomeMap map)
	{
		this.dataSet = dataSet;
		this.map = map;

		mapName = map.getName();

		lines = new Vector<Integer>(dataSet.countLines());

		// For each (original) line in the dataset, we add the index of it to
		// are mapping for this view
		for (int i = 0; i < dataSet.countLines(); i++)
			lines.add(i);
	}


	// Methods required for XML serialization

	public String getMapName()
		{ return mapName; }

	public void setMapName(String mapName)
		{ this.mapName = mapName; }

	public Vector<Integer> getLines()
		{ return lines; }

	public void setLines(Vector<Integer> lines)
		{ this.lines = lines; }


	// Other methods

	void recreateReferences(DataSet dataSet, ChromosomeMap map)
	{
		// Because we can't currently (08/02/2008) use Castor for storing
		// references between objects within the XML, we need to scan through
		// all the views for the datasets and reassociate their dataSet/map
		// object references

		this.dataSet = dataSet;
		this.map = map;
	}

	public void cacheLines()
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

	public float getMapLength()
	{
		return map.getLength();
	}

	public Marker getMarker(int index)
	{
		// TODO: needs changed once we have a wrapper array for marker data
		return map.getMarkerByIndex(index);
	}

	public ChromosomeMap getChromosomeMap()
	{
		return map;
	}

	public void moveLine(int fromIndex, int toIndex)
	{
		// Check we're not out of bounds
		if (toIndex < 0 || fromIndex < 0)
			return;
		if (toIndex >= lines.size() || fromIndex >= lines.size())
			return;

		// Swap the lines
		int oldValue = lines.get(fromIndex);
		lines.set(fromIndex, lines.get(toIndex));
		lines.set(toIndex, oldValue);

		// And swap the cache too
		GenotypeData oldData = genotypeLines.get(fromIndex);
		genotypeLines.set(fromIndex, genotypeLines.get(toIndex));
		genotypeLines.set(toIndex, oldData);
	}
}