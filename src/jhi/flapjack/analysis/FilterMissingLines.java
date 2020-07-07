// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

// Analysis to filter out lines which have a percentage of missing data greater than a given cutoff value
public class FilterMissingLines extends SimpleJob
{
	private GTViewSet viewSet;
	private boolean[] selectedChromosomes;
	private int cutoff;
	private int count;

	public FilterMissingLines(GTViewSet viewSet, boolean[] selectedChromosomes, int cutoff)
	{
		this.viewSet = viewSet;
		this.selectedChromosomes = selectedChromosomes;
		this.cutoff = cutoff;
	}

	public void runJob(int index)
		throws Exception
	{
		// Analysis runs over the selected lines and markers (we do this as standard)
		AnalysisSet as = new AnalysisSet(viewSet)
			.withViews(selectedChromosomes)
			.withSelectedLines()
			.withSelectedMarkers();

		// The total in this case is the number of lines multiplied by the number of markers
		for (int i = 0; i < as.viewCount(); i++)
			maximum += as.lineCount();

		for (int line=as.lineCount()-1; line >= 0; line--)
		{
			int allelesCount = 0;
			int missingCount = 0;

			for (int view=0; view < as.viewCount(); view++)
			{
				for (int marker=0; marker < as.markerCount(view); marker++)
				{
					if (as.getState(view, line, marker) == 0)
						missingCount++;

					allelesCount++;
				}
			}

			if ((missingCount / (float)allelesCount)*100 >= cutoff)
			{
				// TODO: What's the best way of getting to hideLine from here?
				as.getGTView(0).hideLine(line);
				count++;
			}

			progress++;
		}
	}

	public int getCount()
		{ return count; }
}