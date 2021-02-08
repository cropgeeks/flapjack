// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;
import java.util.stream.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.pedigree.*;

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
	private ArrayList<ViewInfo> views;

	public AnalysisSet(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
		dataSet = viewSet.getDataSet();
	}

	public AnalysisSet withViews(boolean[] selectedChromosomes)
	{
		views = new ArrayList<ViewInfo>();

		for (int i = 0; i < viewSet.getViews().size(); i++)
			if (selectedChromosomes == null || selectedChromosomes[i])
				views.add(new ViewInfo(viewSet.getView(i)));

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
		for (ViewInfo vInfo : views)
		{
			// Filters the GTView's list of markers so that we only get actual
			// markers, and not any of the dummy ones
			ArrayList<MarkerInfo> allMarkers = vInfo.view.getMarkers();
			vInfo.markers = allMarkers.stream()
				.filter(mi -> !mi.dummyMarker())
				.sorted()
				.collect(Collectors.toCollection(ArrayList::new));
		}

		return this;
	}

	public AnalysisSet withAllMarkersIncludingHidden()
	{
		withAllMarkers();

		for (ViewInfo vInfo : views)
		{
			ArrayList<MarkerInfo> hiddenMarkers = vInfo.view.getHideMarkers();
			vInfo.markers.addAll(hiddenMarkers.stream()
				.filter(mi -> !mi.dummyMarker())
				.collect(Collectors.toCollection(ArrayList::new)));

			Collections.sort(vInfo.markers);
		}

		return this;
	}

	public AnalysisSet withSelectedMarkers()
	{
		withAllMarkers();

		for (ViewInfo vInfo: views)
		{
			vInfo.markers = vInfo.markers.stream()
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

	public GTView getGTView(int view)
		{ return views.get(view).getGTView(); }

	public double mapLength(int view)
		{ return views.get(view).mapLength(); }

	public ArrayList<QTLInfo> qtls(int view)
		{ return views.get(view).qtls(); }

	/** Returns a count of the number of views held by this AnalysisSet. */
	public int viewCount()
		{ return views.size(); }

	public int lineCount()
		{ return lines.size(); }

	public int markerCount(int view)
		{ return views.get(view).markers.size(); }

	public LineInfo getLine(int lineIndex)
		{ return lines.get(lineIndex); }

	public MarkerInfo getMarker(int view, int markerIndex)
		{ return views.get(view).markers.get(markerIndex); }

	public ArrayList<MarkerInfo> getMarkers(int view)
		{ return views.get(view).markers; }

	public int hetCount(int lineIndex)
	{
		StateTable stateTable = dataSet.getStateTable();

		int hetCount = 0;

		for (int c = 0; c < viewCount(); c++)
			for (int m = 0; m < markerCount(c); m++)
				if (stateTable.isHet(getState(c, lineIndex, m)))
					hetCount++;

		return hetCount;
	}

	public int missingMarkerCount(int lineIndex)
	{
		int missingCount = 0;

		for (int c = 0; c < viewCount(); c++)
			for (int m = 0; m < markerCount(c); m++)
				if (getState(c, lineIndex, m) == 0)
					missingCount++;

		return missingCount;
	}

	/** Returns a count of all the alleles (markerCount x lineCount). */
	public long countAlleles()
	{
		long totalMarkers = 0;
		for (ViewInfo view: views)
			totalMarkers += (long)view.markers.size();

		return totalMarkers * (long)lines.size();
	}

	public int bestParentIndex(int pedLineInfoType, int excludedLineIndex)
	{
		PedManager pm = dataSet.getPedManager();

		int bestIndex = -1;

		List<LineInfo> parents = new ArrayList<>();

		for (LineInfo line : lines)
			if (pm.isType(line, pedLineInfoType))
				parents.add(line);

		int maxMarkerCount = 0;

		for (LineInfo parent : parents)
		{
			int lineIndex = lines.indexOf(parent);
			if (lineIndex != excludedLineIndex)
			{
				int markerCount = 0;

				for (int viewIndex = 0; viewIndex < viewCount(); viewIndex++)
					for (int markerIndex = 0; markerIndex < markerCount(viewIndex); markerIndex++)
						if (getState(viewIndex, lineIndex, markerIndex) > 0)
							markerCount++;

				if (markerCount > maxMarkerCount)
				{
					maxMarkerCount = markerCount;
					bestIndex = lineIndex;
				}
			}
		}

		return bestIndex;
	}

	private class ViewInfo
	{
		private ArrayList<MarkerInfo> markers;
		// The index of the chromosome in the original (full) DataSet
		// ** DO NOT EXPOSE THIS OUTSIDE OF THIS ViewInfo CLASS ***
		private int chrMapIndex;
		// ** DO NOT EXPOSE THIS OUTSIDE OF THIS ViewInfo CLASS ***
		// The danger is it being used in a subsetted viewSet with less chromosomes
		// meaning this index is not valid for that subset. It only works when
		// going back to the original data (eg a list of chromsomes from the
		// DataSet itself). It's safer to just expose the GTView (see below)

		private GTView view;

		ViewInfo(GTView view)
		{
			this.view = view;

			// We need this index, because we need to query the correct
			// GenotypeData object within the original Line class
			this.chrMapIndex = viewSet.getDataSet().getChromosomeMaps()
				.indexOf(view.getChromosomeMap());
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

		GTView getGTView()
			{ return view; }

		double mapLength()
		{
			return view.getChromosomeMap().getLength();
		}

		ArrayList<QTLInfo> qtls()
			{ return view.getQTLs(); }
	}
}