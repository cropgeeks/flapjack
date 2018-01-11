// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

import java.util.*;
import java.util.stream.Collectors;

public class GTView extends XMLRoot
{
	// This view forms part of the viewSet:
	private GTViewSet viewSet;
	// ...specifically being a view upon the chromosome:
	private ChromosomeMap map;
	// Quick reference to this chromosome's index
	private int chrIndex;

	// Holds the index positions of the markers as they appear in the actual
	// dataset's vector of markers
	private ArrayList<MarkerInfo> markers = new ArrayList<>();
	// Holds markers that we don't currently want visible
	private ArrayList<MarkerInfo> hideMarkers = new ArrayList<>();

	private ArrayList<QTLInfo> qtls = new ArrayList<>();

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
		chrIndex = viewSet.getDataSet().getChromosomeMaps().indexOf(map);

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

		// 19/04/2016 - added a reference to the viewSet's lines, so this
		// ensure's existing views loaded from a project get the reference too
		chrIndex = viewSet.getDataSet().getChromosomeMaps().indexOf(map);
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
	 * @param lineIndex the index of the line to query
	 * @param markerIndex the index of the marker to query
	 * @return the state information at the position of the line and marker
	 */
	public int getState(int lineIndex, int markerIndex)
		throws ArrayIndexOutOfBoundsException
	{
		return viewSet.lines.get(lineIndex).getState(chrIndex, markers.get(markerIndex).index);
	}

	public int markerCount()
	{
		return markers.size();
	}

	public int lineCount()
	{
		return viewSet.lines.size();
	}

	public double mapLength()
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
		viewSet.initializeComparisons();

		if (comparisonMarkerIndex != -1)
			comparisonMarker = getMarker(comparisonMarkerIndex);
	}

	/**
	 * Should be called to ensure the comparison line/marker index values are
	 * still correct after major changes to ordering of them within the view.
	 */
	public void updateComparisons()
	{
		viewSet.updateComparisons();

		// Try to find the new index for the comparison marker
		comparisonMarkerIndex = -1;

		for (int i = 0; i < markers.size(); i++)
		{
			if (markers.get(i).marker == comparisonMarker)
			{
				comparisonMarkerIndex = i;
				break;
			}
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
	public GTView createClone(GTViewSet clonedViewSet, boolean selectedMrkrsOnly)
	{
		GTView clone = new GTView(clonedViewSet, map, false);

		// Clone the visible markers
		clone.markers.clear();
		for (MarkerInfo mi : markers)
			if (!selectedMrkrsOnly || (selectedMrkrsOnly && mi.getSelected()))
				clone.markers.add(new MarkerInfo(mi));

		// Clone the hidden markers
		if (selectedMrkrsOnly == false)
			for (MarkerInfo mi : hideMarkers)
				clone.hideMarkers.add(new MarkerInfo(mi));

		// Clone the QTLInfos
		ArrayList<QTLInfo> clonedQTLs = new ArrayList<>();
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
		mi.selectMarkerAndLinkedMarker(!mi.getSelected());
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
			markers.get(index).selectMarkerAndLinkedMarker(selectionState);
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

	/** Hides all selected or unselected markers, depending on the parameter. */
	public void hideMarkers(boolean hideSelected)
	{
		// As the hideMarkers list needs to grow bit by bit, just make it big
		hideMarkers.ensureCapacity(markers.size());		int nullCount = 0;

		for (int i = 0; i < markers.size(); i++)
		{
			MarkerInfo mi = markers.get(i);

			// Should we hide this marker?
			if (mi.selected == hideSelected && mi.dummyMarker() == false)
			{
				hideMarkers.add(mi);
				markers.set(i, null);
			}
		}

		// Now copy the kept markers into a new array - this is thousands of times
		// quicker than removing/adding markers in the loop above, which would
		// cause the ArrayLists to be recreated unnecessarily
		ArrayList<MarkerInfo> markers2 = new ArrayList<>(markers.size());
		for (int i = 0; i < markers.size(); i++)
			if (markers.get(i) != null)
				markers2.add(markers.get(i));

		markers = markers2;

		// Then trim down any left over elements
		markers.trimToSize();
		hideMarkers.trimToSize();
	}

	/** Hides all selected or unselected lines, depending on the parameter. */
	public void hideLines(boolean hideSelected)
	{
		// As the hideMarkers list needs to grow bit by bit, just make it big
		viewSet.hideLines.ensureCapacity(viewSet.lines.size());

		for (int i = 0; i < viewSet.lines.size(); i++)
		{
			// Should we hide this line?
			if (viewSet.lines.get(i).selected == hideSelected)
			{
				LineInfo lineInfo = viewSet.lines.get(i);
				lineInfo.setVisibility(LineInfo.HIDDEN);
				viewSet.hideLines.add(lineInfo);
				viewSet.lines.set(i, null);
			}
		}

		ArrayList<LineInfo> lines = new ArrayList<>(viewSet.lines.size());
		for (int i = 0; i < viewSet.lines.size(); i++)
			if (viewSet.lines.get(i) != null)
				lines.add(viewSet.lines.get(i));

		viewSet.lines.clear();
		viewSet.lines.addAll(lines);

		// Then trim down any left over elements
		viewSet.lines.trimToSize();
		viewSet.hideLines.trimToSize();

		viewSet.tableHandler().copyViewToTable(false);
	}

	/** Hides a single marker. */
	public void hideMarker(MarkerInfo mi)
	{
		markers.remove(mi);
		hideMarkers.add(mi);
	}

	/** Hides a single line. */
	public void hideLine(int index)
	{
		if (index < 0 || index >= viewSet.lines.size())
			return;

		LineInfo lineInfo = viewSet.lines.remove(index);
		lineInfo.setVisibility(LineInfo.HIDDEN);
		viewSet.hideLines.add(lineInfo);

		viewSet.tableHandler().copyViewToTable(false);
	}

	/** Restores all hidden markers to the view. */
	public void restoreHiddenMarkers()
	{
		while (hideMarkers.size() > 0)
		{
			MarkerInfo mi = hideMarkers.remove(0);
			double position = mi.marker.getPosition();

			int insertAt = 0;
			// Search for the best position to restore this marker to
			for (; insertAt < markers.size(); insertAt++)
				if (markers.get(insertAt).marker.getPosition() >= position)
					break;

			markers.add(insertAt, mi);
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

	void linkMarkerInfos()
	{
		if (!map.isSpecialChromosome())
			return;

		int specialMarkerIndex = 0;
		// Loop over the real views so that we can get links to the
		// MarkerInfos in the original views
		for (GTView realView : viewSet.getViews())
		{
			ChromosomeMap realMap = realView.map;
			if (!realMap.isSpecialChromosome())
			{
				// Loop over the markers in the real view, increment our
				// count of markers on the super chromosome as well
				for (int i=0; i < realView.markerCount(); i++, specialMarkerIndex++)
				{
					// Set up our link betweeen the MarkerInfo objects
					MarkerInfo specialMarkerInfo = getMarkerInfo(specialMarkerIndex);
					MarkerInfo realMarkerInfo = realView.getMarkerInfo(i);
					realMarkerInfo.setLinkedMarkerInfo(specialMarkerInfo);
					specialMarkerInfo.setLinkedMarkerInfo(realMarkerInfo);
				}
				// Skip dummy markers (we don't need to link these)
				specialMarkerIndex += DataSet.DUMMY_COUNT;
			}
		}
	}
}