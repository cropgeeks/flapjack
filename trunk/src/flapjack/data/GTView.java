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

	// Marker and line currently under the mouse (-1 if not)
	public int mouseOverLine = -1;
	public int mouseOverMarker = -1;

	// For comparisons between markers, we need to know the marker itself:
	private Marker comparisonMarker;
	// And its current index
	private int comparisonMarkerIndex;

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

	public Marker getComparisonMarker()
		{ return comparisonMarker; }

	public void setComparisonMarker(Marker comparisonMarker)
		{ this.comparisonMarker = comparisonMarker; }

	public int getComparisonMarkerIndex()
		{ return comparisonMarkerIndex; }

	public void setComparisonMarkerIndex(int comparisonMarkerIndex)
		{ this.comparisonMarkerIndex = comparisonMarkerIndex; }


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

		// But also check and deal with the comparison line being moved
		if (viewSet.comparisonLineIndex == fromIndex)
			viewSet.comparisonLineIndex = toIndex;
		else if (viewSet.comparisonLineIndex == toIndex)
			viewSet.comparisonLineIndex = fromIndex;

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

		// But also check and deal with the comparison marker being moved
		if (comparisonMarkerIndex == fromIndex)
			comparisonMarkerIndex = toIndex;
		else if (comparisonMarkerIndex == toIndex)
			comparisonMarkerIndex = fromIndex;

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

	public void initializeComparisons()
	{
		// When we want to do line comparisons, we need to track both the line's
		// index and the actual line (reference). That way, if its index is
		// changed by external methods (sorting), the full list can be searched
		// to find where it is now

		// Internal methods for reordering lines can maintain the tracking ok

		if (mouseOverLine != -1)
			viewSet.comparisonLineIndex = mouseOverLine;
		else
			viewSet.comparisonLineIndex = 0;

		viewSet.comparisonLine = getLine(viewSet.comparisonLineIndex);

		if (mouseOverMarker != -1)
			comparisonMarkerIndex = mouseOverMarker;
		else
			comparisonMarkerIndex = 0;

		comparisonMarker = getMarker(comparisonMarkerIndex);
	}

	/**
	 * Should be called to ensures the comparison line index value is still
	 * correct after major changes to the ordering of lines within the view.
	 */
	public void updateComparisons()
	{
		for (int index: viewSet.lines)
			if (getLine(index) == viewSet.comparisonLine)
				viewSet.comparisonLineIndex = index;

		for (int index: markers)
			if (getMarker(index) == comparisonMarker)
				comparisonMarkerIndex = index;
	}

	public int getComparisonLineIndex()
		{ return viewSet.comparisonLineIndex; }

	public int[] getMarkersAsArray()
	{
		int[] array = new int[markers.size()];

		for (int i = 0; i < array.length; i++)
			array[i] = markers.get(i);

		return array;
	}

	public void setMarkersFromArray(int[] array)
	{
		markers.clear();

		for (int i: array)
			markers.add(i);
	}

	GTView createClone(GTViewSet clonedViewSet)
	{
		GTView clone = new GTView(clonedViewSet, map);

		clone.setMarkersFromArray(getMarkersAsArray());
		clone.comparisonMarker = comparisonMarker;
		clone.comparisonMarkerIndex = comparisonMarkerIndex;

		return clone;
	}

	/**
	 * Returns the index position of the given marker, or -1 if it wasn't found.
	 */
	public int indexOf(Marker marker)
	{
		for (int i = 0; i < markers.size(); i++)
			if (map.getMarkerByIndex(markers.get(i)) == marker)
				return i;

		return -1;
	}
}