// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

public class FilterMarkersMissingFromLine extends SimpleJob
{
	private GTViewSet viewSet;
	private boolean[] selectedChromosomes;
	private int lineIndex;

	public FilterMarkersMissingFromLine(GTViewSet viewSet, boolean[] selectedChromosomes, int lineIndex)
	{
		this.viewSet = viewSet;
		this.selectedChromosomes = selectedChromosomes;
		this.lineIndex = lineIndex;
	}

	public void runJob(int index)
		throws Exception
	{
		AnalysisSet as = new AnalysisSet(viewSet)
			.withViews(selectedChromosomes)
			.withAllLines()
			.withSelectedMarkers();

		for (int i = 0; i < as.viewCount(); i++)
			maximum += as.markerCount(index);

		for (int view = 0; view < as.viewCount(); view++)
		{
			// For each marker...
			for (int marker = as.markerCount(view)-1; marker >= 0 && okToRun; marker--)
			{
				if (as.getState(view, lineIndex, marker) == 0)
				{
					MarkerInfo mi = as.getMarker(view, marker);
					as.getGTView(view).hideMarker(mi);
				}

				progress++;
			}
		}
	}
}