// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.data;

import java.util.*;

public class GTView extends XMLRoot
{
	// For faster rendering, maintain a local cache of the data to be drawn
	// This saves having to query every line for the data by first telling it
	// what map we're looking it - this object simply holds data for all the
	// lines, but only for the map in question
	private ArrayList<GenotypeData> genotypeLines;

	// This view forms part of the viewSet:
	private GTViewSet viewSet;
	// ...specifically being a view upon the chromosome:
	private ChromosomeMap map;

	// Holds the index positions of the markers as they appear in the actual
	// dataset's vector of markers
	private ArrayList<MarkerInfo> markers = new ArrayList<MarkerInfo>();
	// Holds markers that we don't currently want visible
	private ArrayList<MarkerInfo> hideMarkers = new ArrayList<MarkerInfo>();

	private ArrayList<QTLInfo> qtls = new ArrayList<QTLInfo>();

	// Marker and line currently under the mouse (-1 if not)
	public int mouseOverLine = -1;
	public int mouseOverMarker = -1;

	// For comparisons between markers, we need to know the marker itself:
	private Marker comparisonMarker = null;
	// And its current index
	private int comparisonMarkerIndex = -1;

	// Tracks whether the markers are in map order or not
	private boolean markersOrdered = false;

	public GTView()
	{
	}

	/**
	 * Creates a new view object that will view the map within the dataset.
	 */
	public GTView(GTViewSet viewSet, ChromosomeMap map, boolean isNew)
	{
		this.viewSet = viewSet;
		this.map = map;

		// For each (original) marker in the map...
		markers = new ArrayList<MarkerInfo>(map.countLoci());
		for (int i = 0; i < map.countLoci(); i++)
		{
			Marker m = map.getMarkerByIndex(i);
			markers.add(new MarkerInfo(m, i));
		}
		markers.trimToSize();

		if (isNew)
			markersOrdered = true;
	}

	void validate()
		throws NullPointerException
	{
		if (viewSet == null || map == null || markers == null)
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

	public ArrayList<MarkerInfo> getMarkers()
		{ return markers; }

	public void setMarkers(ArrayList<MarkerInfo> markers)
		{ this.markers = markers; }

	public ArrayList<MarkerInfo> getHideMarkers()
		{ return hideMarkers; }

	public void setHideMarkers(ArrayList<MarkerInfo> hideMarkers)
		{ this.hideMarkers = hideMarkers; }

	public Marker getComparisonMarker()
		{ return comparisonMarker; }

	public void setComparisonMarker(Marker comparisonMarker)
		{ this.comparisonMarker = comparisonMarker; }

	public int getComparisonMarkerIndex()
		{ return comparisonMarkerIndex; }

	public void setComparisonMarkerIndex(int comparisonMarkerIndex)
		{ this.comparisonMarkerIndex = comparisonMarkerIndex; }

	public boolean getMarkersOrdered()
		{ return markersOrdered; }

	public void setMarkersOrdered(boolean markersOrdered)
		{ this.markersOrdered = markersOrdered; }

	public ArrayList<QTLInfo> getQTLs()
		{ return qtls; }

	public void setQTLs(ArrayList<QTLInfo> qtl)
		{ this.qtls = qtl; }

	// Other methods

	public void cacheLines()
	{
		// Now cache as much data as possible to help speed rendering
		genotypeLines = new ArrayList<GenotypeData>(viewSet.lines.size());

		for (int i = 0; i < viewSet.lines.size(); i++)
		{
			Line line = viewSet.lines.get(i).line;
			GenotypeData data = line.getGenotypeDataByMap(map);

			genotypeLines.add(data);
		}
	}

	/**
	 * Returns the Line at the specified index position within this view.
	 */
	public Line getLine(int index)
	{
		return viewSet.lines.get(index).line;
	}

	/**
	 * Returns the LineInfo at the specified index position within this view.
	 */
	public LineInfo getLineInfo(int index)
	{
		return viewSet.lines.get(index);
	}

	/**
	 * Returns the state information at the position of the line and marker (for
	 * this view).
	 * @param line the index of the line to query
	 * @param marker the index of the marker to query
	 * @return the state information at the position of the line and marker
	 */
	public int getState(int line, int marker)
		throws ArrayIndexOutOfBoundsException
	{
		return genotypeLines.get(line).getState(markers.get(marker).index);
	}

	public int markerCount()
	{
		return markers.size();
	}

	public int lineCount()
	{
		return viewSet.lines.size();
	}

	public float mapLength()
	{
		return map.getLength();
	}

	public Marker getMarker(int index)
	{
		return markers.get(index).marker;
	}

	/**
	 * Returns the MarkerInfo at the specified index position within this view.
	 */
	public MarkerInfo getMarkerInfo(int index)
	{
		return markers.get(index);
	}

	public void moveLine(int fromIndex, int toIndex)
	{
		// Check we're not out of bounds
		if (toIndex < 0 || fromIndex < 0)
			return;
		if (toIndex >= viewSet.lines.size() || fromIndex >= viewSet.lines.size())
			return;

		// Swap the lines
		LineInfo oldValue = viewSet.lines.get(fromIndex);
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
		MarkerInfo oldValue = markers.get(fromIndex);
		markers.set(fromIndex, markers.get(toIndex));
		markers.set(toIndex, oldValue);
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
	 * Should be called to ensure the comparison line/marker index values are
	 * still correct after major changes to ordering of them within the view.
	 */
	public void updateComparisons()
	{
		// Try to find the new index for the comparison line
		viewSet.comparisonLineIndex = -1;

		for (int i = 0; i < viewSet.lines.size(); i++)
			if (viewSet.lines.get(i).line == viewSet.comparisonLine)
			{
				viewSet.comparisonLineIndex = i;
				break;
			}


		// Try to find the new index for the comparison marker
		comparisonMarkerIndex = -1;

		for (int i = 0; i < markers.size(); i++)
			if (markers.get(i).marker == comparisonMarker)
			{
				comparisonMarkerIndex = i;
				break;
			}
	}

	/**
	 * Returns an array holding all the marker data from either the visible
	 * list of markers when getVisible=true or the hidden list of markers when
	 * getVisible=false.
	 */
	public MarkerInfo[] getMarkersAsArray(boolean getVisible)
	{
		if (getVisible)
			return markers.toArray(new MarkerInfo[] {});
		else
			return hideMarkers.toArray(new MarkerInfo[] {});
	}

	/**
	 * Takes an array of marker data and uses it to either populate the visible
	 * list of markers when setVisible=true or the hidden list of of markers
	 * when setVisible=false.
	 */
	public void setMarkersFromArray(MarkerInfo[] array, boolean setVisible)
	{
		if (setVisible)
		{
			markers.clear();
			for (MarkerInfo mi: array)
				markers.add(mi);
		}
		else
		{
			hideMarkers.clear();
			for (MarkerInfo mi: array)
				hideMarkers.add(mi);
		}
	}

	// Some of the operations in this method may need to be performed like a
	// "deep copy", as we want the clone to have its own copy of everything and
	// not just references back to the originals
	GTView createClone(GTViewSet clonedViewSet, boolean cloneHidden)
	{
		GTView clone = new GTView(clonedViewSet, map, false);

		// Clone the visible markers
		clone.markers.clear();
		for (MarkerInfo mi : markers)
			clone.markers.add(new MarkerInfo(mi));

		// Clone the hidden markers
		if (cloneHidden)
		{
			for (MarkerInfo mi : hideMarkers)
				clone.hideMarkers.add(new MarkerInfo(mi));
		}

		// Clone the QTLInfos
		ArrayList<QTLInfo> clonedQTLs = new ArrayList<QTLInfo>();
		for (QTLInfo qtl: qtls)
			clonedQTLs.add(new QTLInfo(qtl));
		clone.setQTLs(clonedQTLs);

		clone.comparisonMarker = comparisonMarker;
		clone.comparisonMarkerIndex = comparisonMarkerIndex;
		clone.markersOrdered = markersOrdered;

		return clone;
	}

	/**
	 * Returns the VIEW index position of the given marker, or -1 if it wasn't
	 * found.
	 */
	public int indexOf(Marker marker)
	{
		for (int i = 0; i < markers.size(); i++)
			if (markers.get(i).marker == marker)
				return i;

		return -1;
	}

	public boolean isMarkerSelected(int index)
		{ return markers.get(index).selected; }

	public boolean isLineSelected(int index)
		{ return viewSet.lines.get(index).selected; }

	/**
	 * Toggles and returns the selection state of the marker at the given index.
	 */
	public boolean toggleMarkerState(int index)
	{
		if (markers.get(index).dummyMarker())
			return true;


		MarkerInfo mi = markers.get(index);

		mi.selected = !mi.selected;
		return mi.selected;
	}

	/**
	 * Toggles and returns the selection state of the line at the given index.
	 */
	public boolean toggleLineState(int index)
	{
		LineInfo mi = viewSet.lines.get(index);

		mi.selected = !mi.selected;
		return mi.selected;
	}

	public void setMarkerState(int index, boolean selectionState)
	{
		if (markers.get(index).dummyMarker() == false)
			markers.get(index).selected = selectionState;
	}

	public void setLineState(int index, boolean selectionState)
		{ viewSet.lines.get(index).selected = selectionState; }

	public int countSelectedMarkers()
	{
		int count = 0;
		for (MarkerInfo mi: markers)
			if (mi.selected && !mi.dummyMarker())
				count++;

		return count;
	}

	public int countSelectedLines()
	{
		int count = 0;
		for (LineInfo li: viewSet.lines)
			if (li.selected)
				count++;

		return count;
	}

	public int hiddenMarkerCount()
		{ return hideMarkers.size(); }

	public int hiddenLineCount()
		{ return viewSet.hideLines.size(); }

	/** Hides all selected or unselected markers, depending on the parameter. */
	public void hideMarkers(boolean hideSelected)
	{
		for (int i = 0; i < markers.size(); i++)
		{
			// Don't hide what we don't want hidden (!!)
			if (markers.get(i).selected != hideSelected)
				continue;

			// Hide, but always keep at least one marker visible
			if (markers.size() > 1)
			{
				hideMarkers.add(markers.remove(i));
				i--;
			}
			else
				return;
		}
	}

	/** Hides all selected or unselected lines, depending on the parameter. */
	public void hideLines(boolean hideSelected)
	{
		for (int i = 0; i < viewSet.lines.size(); i++)
		{
			// Don't hide what we don't want hidden (!!)
			if (viewSet.lines.get(i).selected != hideSelected)
				continue;

			// Hide, but always keep at least one line visible
			if (viewSet.lines.size() > 1)
			{
				viewSet.hideLines.add(viewSet.lines.remove(i));
				i--;
			}
			else
				return;
		}
	}

	/** Hides a single marker. */
	public void hideMarker(int index)
	{
		if (index < 0 || index >= markers.size())
			return;

		if (markers.get(index).dummyMarker())
			return;

		hideMarkers.add(markers.remove(index));
	}

	/** Hides a single line. */
	public void hideLine(int index)
	{
		if (index < 0 || index >= viewSet.lines.size())
			return;

		viewSet.hideLines.add(viewSet.lines.remove(index));
	}

	/** Restores all hidden markers to the view. */
	public void restoreHiddenMarkers()
	{
		while (hideMarkers.size() > 0)
		{
			MarkerInfo mi = hideMarkers.remove(0);
			float position = mi.marker.getPosition();

			int insertAt = 0;
			// Search for the best position to restore this marker to
			for (; insertAt < markers.size(); insertAt++)
				if (markers.get(insertAt).marker.getPosition() >= position)
					break;

			markers.add(insertAt, mi);
		}
	}

	/** Restores all hidden lines to the view. */
	public void restoreHiddenLines()
	{
		while (viewSet.hideLines.size() > 0)
		{
			LineInfo li = viewSet.hideLines.remove(0);
			viewSet.lines.add(li);
		}
	}

	public boolean isDummyLine(int lineInfoIndex)
	{
		LineInfo info = viewSet.lines.get(lineInfoIndex);

		return info.getLine() == viewSet.getDataSet().getDummyLine();
	}

	public boolean isSplitter(int lineInfoIndex)
	{
		LineInfo info = viewSet.lines.get(lineInfoIndex);

		return info.getLine() == viewSet.getDataSet().getSplitter();
	}

	public boolean isDuplicate(int lineInfoIndex)
	{
		return viewSet.lines.get(lineInfoIndex).duplicate;
	}

	/**
	 * Returns a human count of the number of markers in this view. If it's a
	 * normal chromosome, it'll just be a count of the markers, but if it's a
	 * super chromosome, then it'll be the count minus the number of dummys.
	 */
	public int countGenuineMarkers()
	{
		if (map.isSpecialChromosome() == false)
			return markerCount();

		int count = 0;
		for (MarkerInfo mi: markers)
			if (mi.dummyMarker() == false)
				count++;

		return count;
	}

	public int getSplitterIndex()
	{
		int linecount = lineCount();
		for (int i=0; i < linecount; i++)
			if (getLine(i) == viewSet.getDataSet().getSplitter())
				return i;

		return -1;
	}

	public boolean hasDummyLines()
	{
		int lineCount = lineCount();

		for (int i=0; i < lineCount; i++)
			if (viewSet.lines.get(i).line == viewSet.getDataSet().getDummyLine())
				return true;

		return false;
	}
}