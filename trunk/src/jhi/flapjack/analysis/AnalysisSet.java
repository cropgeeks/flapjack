// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;
import java.util.stream.*;

import jhi.flapjack.data.*;

/**
 * This class is an attempt to organise the underlying data used by any analysis
 * in a better way than simply pulling it directly from GTView(Set) objects.
 * This is because a view holds information on all lines and all markers, but an
 * analysis is very likely only working on a subset (eg selected lines), so the
 * indices needed to get back to the original data will be wrong. This class
 * holds only the data needed by an analysis (created via the Builder methods)
 * and therefore knows about the subset indices *and* the original indices so
 * everything should match up.
 */
public class AnalysisSet
{
	private DataSet dataSet;
	private GTViewSet viewSet;

	private ArrayList<LineInfo> lines;
	private ArrayList<View> views;

	public AnalysisSet(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
		dataSet = viewSet.getDataSet();
	}

	public AnalysisSet withViews(boolean[] selectedChromosomes)
	{
		views = new ArrayList<View>();

		for (int i = 0; i < viewSet.getViews().size(); i++)
			if (selectedChromosomes == null || selectedChromosomes[i])
				views.add(new View(i));

		return this;
	}

	public AnalysisSet withSelectedLines()
	{
		lines = viewSet.getLines().stream()
			.filter(li -> li.getSelected())
			.filter(li -> li.getDuplicate() == false)
			.filter(li -> li.getLine() != dataSet.getDummyLine())
			.filter(li -> li.getLine() != dataSet.getSplitter())
			.collect(Collectors.toCollection(ArrayList::new));

		return this;
	}

	public AnalysisSet withSelectedMarkers()
	{
		for (View view: views)
		{
			ArrayList<MarkerInfo> allMarkers = viewSet.getView(view.chrIndex).getMarkers();
			view.markers = allMarkers.stream()
				.filter(mi -> mi.getSelected())
				.filter(mi -> !mi.dummyMarker())
				.collect(Collectors.toCollection(ArrayList::new));
		}

		return this;
	}

	/** Returns a count of the number of views held by this AnalysisSet. */
	public int getViewCount()
		{ return views.size(); }

	public ArrayList<LineInfo> getLines()
		{ return lines; }

	public ArrayList<MarkerInfo> getMarkers(int chrIndex)
		{ return views.get(chrIndex).markers; }

	public int getState(int view, int line, int marker)
	{
		return views.get(view).getState(line, marker);
	}

	/** Returns a count of all the alleles (markerCount x lineCount). */
	public long countAlleles()
	{
		long totalMarkers = 0;
		for (View view: views)
			totalMarkers += (long)view.markers.size();

		return totalMarkers * (long)lines.size();
	}

	private class View
	{
		private ArrayList<MarkerInfo> markers;
		private int chrIndex;

		View(int chrIndex)
		{
			this.chrIndex = chrIndex;
		}

		public int getState(int lineIndex, int markerIndex)
		{
			// Get the real index (in the original data) of the marker
			int mrkIndex = markers.get(markerIndex).getIndex();
			// And then look up the allele at this position for the line
			return lines.get(lineIndex).getState(chrIndex, mrkIndex);
		}
	}
}