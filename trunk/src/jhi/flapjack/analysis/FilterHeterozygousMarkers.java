// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import jhi.flapjack.data.GTViewSet;
import jhi.flapjack.data.MarkerInfo;
import jhi.flapjack.data.StateTable;
import scri.commons.gui.SimpleJob;

public class FilterHeterozygousMarkers extends SimpleJob
{
	private GTViewSet viewSet;
	private boolean[] selectedChromosomes;
	private int cutoff;
	private int count;

	public FilterHeterozygousMarkers(GTViewSet viewSet, boolean[] selectedChromosomes, int cutoff)
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

		StateTable stateTable = viewSet.getDataSet().getStateTable();

		for (int view = 0; view < as.viewCount(); view++)
		{
			boolean isSpecialChromosome = as.getGTView(view).getChromosomeMap().isSpecialChromosome();

			// For each marker...
			for (int i = as.markerCount(view)-1; i >= 0 && okToRun; i--)
			{
				int allelesCount = 0;
				int hetCount = 0;

				// Count how many alleles are heterozygous across all the lines...
				for (int j = 0; j < as.lineCount() && okToRun; j++)
				{
					if (stateTable.isHet(as.getState(view, j, i)))
						hetCount++;

					allelesCount++;
				}

				// And if the percentage of heterozygous ones is >= cutoff, then
				// remove it from the visible set
				if ((hetCount / (float)allelesCount)*100 >= cutoff)
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