// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

// Analysis to filter out lines which have a percentage of homozygous data greater than a given cutoff value
public class FilterHomozygousLines extends SimpleJob
{
	private final GTViewSet viewSet;
	private final StateTable stateTable;
	private final boolean[] selectedChromosomes;
	private final int cutoff;
	private int count;

	public FilterHomozygousLines(GTViewSet viewSet, boolean[] selectedChromosomes, int cutoff)
	{
		this.viewSet = viewSet;
		this.stateTable = this.viewSet.getDataSet().getStateTable();
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

		// The total in this case is the number of lines across all views
		for (int i = 0; i < as.viewCount(); i++)
			maximum += as.lineCount();

		for (int line=as.lineCount()-1; line > 0; line--)
		{
			int allelesCount = 0;
			int homozygousCount = 0;

			for (int view=0; view < as.viewCount(); view++)
			{
				for (int marker=0; marker < as.markerCount(view); marker++)
				{
					if (stateTable.isHom(as.getState(view, line, marker)))
						homozygousCount++;

					allelesCount++;
				}
			}

			if ((homozygousCount / (float)allelesCount)*100 >= cutoff)
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