// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

public class FilterMissingMarkers extends SimpleJob
{
	private GTViewSet viewSet;
	private boolean[] selectedChromosomes;
	private int cutoff;
	private int count;

	public FilterMissingMarkers(GTViewSet viewSet, boolean[] selectedChromosomes, int cutoff)
	{
		this.viewSet = viewSet;
		this.selectedChromosomes = selectedChromosomes;
		this.cutoff = cutoff;
	}

	public int getCount()
		{ return count; }

	public void runJob(int index)
		throws Exception
	{
		AnalysisSet as = new AnalysisSet(viewSet)
			.withViews(selectedChromosomes)
			.withSelectedLines()
			.withSelectedMarkers();

		for (int i = 0; i < as.viewCount(); i++)
			maximum += as.markerCount(i);

		for (int view = 0; view < as.viewCount(); view++)
		{
			boolean isSpecialChromosome = as.getGTView(view).getChromosomeMap().isSpecialChromosome();

			// For each marker...
			for (int i = as.markerCount(view)-1; i >= 0 && okToRun; i--)
			{
				int allelesCount = 0;
				int missingCount = 0;

				// Count how many alleles are missing across all the lines...
				for (int j = 0; j < as.lineCount() && okToRun; j++)
				{
					if (as.getState(view, j, i) == 0)
						missingCount++;

					allelesCount++;
				}

//				System.out.println("Marker " + i + " missing " + missingCount + " / " + allelesCount + " ("
//					+ ((missingCount / (float)allelesCount)*100));

				// And if the percentage of missing ones is >= cutoff, then
				// remove it from the visible set
				if ((missingCount / (float)allelesCount)*100 >= cutoff)
				{
					MarkerInfo mi = as.getMarker(view, i);
					as.getGTView(view).hideMarker(mi);

					if (isSpecialChromosome == false)
						count++;
				}

				progress++;
			}
		}
	}
}