// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;

import scri.commons.gui.*;

public class PedVerF1Stats extends SimpleJob
{
	private AnalysisSet as;
	private StateTable stateTable;

	private int parent1Index;
	private int parent2Index;
	private int f1Index;

	private int f1HetCount = 0;
	private int totalMarkerCount = 0;
	private float f1PercentCount = 0;

	public PedVerF1Stats(AnalysisSet as, StateTable stateTable, int parent1Index, int parent2Index, int f1Index)
	{
		this.as = as;
		this.stateTable = stateTable;
		this.parent1Index = parent1Index;
		this.parent2Index = parent2Index;
		this.f1Index = f1Index;
	}

	public void runJob(int index)
		throws Exception
	{
		calculateStats(as);
	}

	private void calculateStats(AnalysisSet as)
	{
		calculateExpectedF1Stats();

		for (int l=0; l < as.lineCount(); l++)
			calculateStatsForLine(l);
	}

	private PedVerKnownParentsLineStats calculateStatsForLine(int lineIndex)
	{
		PedVerStats stats = new PedVerStats(as, stateTable);

		LineInfo lineInfo = as.getLine(lineIndex);
		PedVerKnownParentsLineStats lineStat = new PedVerKnownParentsLineStats(lineInfo);
		lineInfo.results().setPedVerStats(lineStat);

		int foundMarkers = stats.nonMissingAllelesForLine(lineIndex);
		int hetMarkers = stats.hetAllelesForLine(lineIndex);
		int p1Contained = stats.matchesAnyAlleleCount(parent1Index, lineIndex);
		int p2Contained = stats.matchesAnyAlleleCount(parent2Index, lineIndex);
		int matchesExpF1 = stats.matchesAlleleCount(f1Index, lineIndex);

		lineStat.setLine(lineInfo);
		lineStat.setMarkerCount(foundMarkers);
		lineStat.setPercentMissing((1 - (foundMarkers / (float) totalMarkerCount)) * 100);
		lineStat.setHeterozygousCount(hetMarkers);
		lineStat.setPercentHeterozygous((hetMarkers / (float)foundMarkers) * 100);
		lineStat.setPercentDeviationFromExpected(f1PercentCount - ((hetMarkers / (float)foundMarkers) * 100));
		lineStat.setCountP1Contained(p1Contained);
		lineStat.setPercentP1Contained((p1Contained / (float)foundMarkers) * 100);
		lineStat.setCountP2Contained(p2Contained);
		lineStat.setPercentP2Contained((p2Contained / (float)foundMarkers) * 100);
		lineStat.setCountAlleleMatchExpected(matchesExpF1);
		lineStat.setPercentAlleleMatchExpected((matchesExpF1 / (float)foundMarkers) * 100);

		return lineStat;
	}

	private void calculateExpectedF1Stats()
	{
		for (int c = 0; c < as.viewCount(); c++)
		{
			totalMarkerCount += as.markerCount(c);
			for (int m = 0; m < as.markerCount(c); m++)
			{
				int stateCode = as.getState(c, f1Index, m);
				if (stateTable.isHet(stateCode))
					f1HetCount++;
			}
		}
		f1PercentCount = (f1HetCount / (float)totalMarkerCount) * 100;
	}

	public PedVerKnownParentsResults getResult()
	{
		return null;
	}
}