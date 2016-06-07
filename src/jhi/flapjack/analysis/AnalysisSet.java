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

	public AnalysisSet withAllLines()
	{
		// Filters the GTViewSet's list of lines so we only get actual lines
		// (and not dummies, sort splitters, or duplicates)
		lines = viewSet.getLines().stream()
			.filter(li -> li.getDuplicate() == false)
			.filter(li -> li.getLine() != dataSet.getDummyLine())
			.filter(li -> li.getLine() != dataSet.getSplitter())
			.collect(Collectors.toCollection(ArrayList::new));

		return this;
	}

	public AnalysisSet withSelectedLines()
	{
		withAllLines();

		lines = lines.stream()
			.filter(li -> li.getSelected())
			.collect(Collectors.toCollection(ArrayList::new));

		return this;
	}

	public AnalysisSet withAllMarkers()
	{
		for (View view : views)
		{
			// Filters the GTView's list of markers so that we only get actual
			// markers, and not any of the dummy ones
			ArrayList<MarkerInfo> allMarkers = viewSet.getView(view.chrMapIndex).getMarkers();
			view.markers = allMarkers.stream()
				.filter(mi -> !mi.dummyMarker())
				.sorted()
				.collect(Collectors.toCollection(ArrayList::new));
		}

		return this;
	}

	public AnalysisSet withAllMarkersIncludingHidden()
	{
		withAllMarkers();

		for (View view : views)
		{
			ArrayList<MarkerInfo> hiddenMarkers = viewSet.getView(view.chrMapIndex).getHideMarkers();
			view.markers.addAll(hiddenMarkers.stream()
				.filter(mi -> !mi.dummyMarker())
				.collect(Collectors.toCollection(ArrayList::new)));

			Collections.sort(view.markers);
		}

		return this;
	}

	public AnalysisSet withSelectedMarkers()
	{
		withAllMarkers();

		for (View view: views)
		{
			view.markers = view.markers.stream()
				.filter(mi -> mi.getSelected())
				.collect(Collectors.toCollection(ArrayList::new));
		}

		return this;
	}

	public ArrayList<LineInfo> getLines()
		{ return lines; }

/*	public ArrayList<MarkerInfo> getMarkers(int chrIndex)
		{ return views.get(chrIndex).markers; }
*/
	public int getState(int view, int line, int marker)
	{
		return views.get(view).getState(line, marker);
	}

	public double mapLength(int view)
		{ return views.get(view).mapLength(); }

	/** Returns the index within the original DataSet of the ChromosomeMap/GTView
	 * currently at index 'view' in this AnalysisSet. */
	public int chrMapIndex(int view)
		{ return views.get(view).chrMapIndex(); }

	public ArrayList<QTLInfo> qtls(int view)
		{ return views.get(view).qtls(); }

	/** Returns a count of the number of views held by this AnalysisSet. */
	public int viewCount()
		{ return views.size(); }

	public int lineCount()
		{ return lines.size(); }

	public int markerCount(int chrIndex)
		{ return views.get(chrIndex).markers.size(); }

	public LineInfo getLine(int lineIndex)
		{ return lines.get(lineIndex); }

	public MarkerInfo getMarker(int chrIndex, int markerIndex)
		{ return views.get(chrIndex).markers.get(markerIndex); }

	public ArrayList<MarkerInfo> getMarkers(int chrIndex)
		{ return views.get(chrIndex).markers; }


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
		// The index of the chromosome in the original (full) DataSet
		private int chrMapIndex;

		View(int chrMapIndex)
		{
			this.chrMapIndex = chrMapIndex;
		}

		public int getState(int lineIndex, int markerIndex)
		{
			// Get the real index (in the original data) of the marker
			int mrkIndex = markers.get(markerIndex).getIndex();
			// And then look up the allele at this position for the line
			return lines.get(lineIndex).getState(chrMapIndex, mrkIndex);
		}

		// If the number of "getter" methods here starts to get above five, we
		// should rethink whether they should even be here, or whether
		// AnalysisSet simple returns a reference to the view/chromosomes/whatever

		public double mapLength()
		{
			return viewSet.getView(chrMapIndex).getChromosomeMap().getLength();
		}

		public int chrMapIndex()
			{ return chrMapIndex; }

		public ArrayList<QTLInfo> qtls()
			{ return viewSet.getView(chrMapIndex).getQTLs(); }
	}
}