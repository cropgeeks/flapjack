package flapjack.data;

import java.util.*;

public class GTView extends XMLRoot
{
	// For faster rendering, maintain a local cache of the data to be drawn
	// This saves having to query every line for the data by first telling it
	// what map we're looking it - this object simply holds data for all the
	// lines, but only for the map in question
	private Vector<GenotypeData> genotypeLines;

	// This view forms part of the viewSet:
	private GTViewSet viewSet;
	// ...specifically being a view upon the chromosome:
	private ChromosomeMap map;

	// Holds the index positions of the markers as they appear in the actual
	// dataset's vector of markers
	private Vector<Integer> markers;

	// Marker and line to be highlighted
	public int selectedLine = -1;
	public int selectedMarker = -1;

	public GTView()
	{
	}

	/**
	 * Creates a new view object that will view the map within the dataset.
	 */
	public GTView(GTViewSet viewSet, ChromosomeMap map)
	{
		this.viewSet = viewSet;
		this.map = map;

		// For each (original) marker in the map...
		markers = new Vector<Integer>(map.countLoci());
		for (int i = 0; i < map.countLoci(); i++)
			markers.add(i);
	}

	void validate()
		throws NullPointerException
	{
		if (viewSet == null || map == null)
			throw new NullPointerException();
	}


	// Methods required for XML serialization

	public GTViewSet getViewSet()
		{ return viewSet; }

	public void setViewSet(GTViewSet viewSet)
		{ this.viewSet = viewSet; }

	public ChromosomeMap getChromosomeMap()
		{ return map; }

	public void setChromosomeMap(ChromosomeMap map)
		{ this.map = map; }

	public Vector<Integer> getMarkers()
		{ return markers; }

	public void setMarkers(Vector<Integer> markers)
		{ this.markers = markers; }



	// Other methods

	public Vector<Integer> getLines()
		{ return viewSet.lines; }

	public void cacheLines()
	{
		// Now cache as much data as possible to help speed rendering
		genotypeLines = new Vector<GenotypeData>(viewSet.lines.size());

		DataSet dataSet = viewSet.getDataSet();

		for (int i = 0; i < viewSet.lines.size(); i++)
		{
			Line line = dataSet.getLineByIndex(viewSet.lines.get(i));
			GenotypeData data = line.getGenotypeDataByMap(map);

			genotypeLines.add(data);
		}
	}

	public Line getLine(int index)
	{
		return viewSet.getDataSet().getLineByIndex(viewSet.lines.get(index));
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
		return genotypeLines.get(line).getState(markers.get(marker));
	}

	public int getMarkerCount()
	{
		return markers.size();
	}

	public int getLineCount()
	{
		return viewSet.lines.size();
	}

	public StateTable getStateTable()
	{
		return viewSet.getDataSet().getStateTable();
	}

	public float getMapLength()
	{
		return map.getLength();
	}

	public Marker getMarker(int index)
	{
		return map.getMarkerByIndex(markers.get(index));
	}

	public void moveLine(int fromIndex, int toIndex)
	{
		// Check we're not out of bounds
		if (toIndex < 0 || fromIndex < 0)
			return;
		if (toIndex >= viewSet.lines.size() || fromIndex >= viewSet.lines.size())
			return;

		// Swap the lines
		int oldValue = viewSet.lines.get(fromIndex);
		viewSet.lines.set(fromIndex, viewSet.lines.get(toIndex));
		viewSet.lines.set(toIndex, oldValue);

		// And swap the cache too
		GenotypeData oldData = genotypeLines.get(fromIndex);
		genotypeLines.set(fromIndex, genotypeLines.get(toIndex));
		genotypeLines.set(toIndex, oldData);
	}

	public void moveMarker(int fromIndex, int toIndex)
	{
		// Check we're not out of bounds
		if (toIndex < 0 || fromIndex < 0)
			return;
		if (toIndex >= markers.size() || fromIndex >= markers.size())
			return;

		// Swap the lines
		int oldValue = markers.get(fromIndex);
		markers.set(fromIndex, markers.get(toIndex));
		markers.set(toIndex, oldValue);
	}

	public void hideMarker(int index)
	{
		if (index < 0 || index >= markers.size())
			return;

		markers.remove(index);
	}
}