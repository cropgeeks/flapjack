// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;

import scri.commons.gui.*;

public class PedVerLinesStats extends SimpleJob
{
	private AnalysisSet as;
	private StateTable stateTable;

	private int refIndex;
	private int testIndex;

	public PedVerLinesStats(AnalysisSet as, StateTable stateTable, int refIndex, int testIndex)
	{
		this.as = as;
		this.stateTable = stateTable;
		this.refIndex = refIndex;
		this.testIndex = testIndex;
	}

	public void runJob(int index)
		throws Exception
	{
		PedVerStats stats = new PedVerStats(as, stateTable);

		for (int lineIndex=0; lineIndex < as.lineCount(); lineIndex++)
		{
			LineInfo lineInfo = as.getLine(lineIndex);
			PedVerLinesLineStats lineStat = new PedVerLinesLineStats();
			lineInfo.results().setPedVerLinesStats(lineStat);

			int totalMarkers = stats.countAllelesForLine();
			int foundMarkers = stats.nonMissingAllelesForLine(lineIndex);
			int hetMarkers = stats.hetAllelesForLine(lineIndex);

			int totalMatches = stats.matchesAnyAlleleCount(testIndex, lineIndex);

			ArrayList<Integer> chrMatch = new ArrayList<>();
			for (int c = 0; c < as.viewCount(); c++)
				chrMatch.add(stats.matchesAlleleCountForView(c, testIndex, lineIndex));

			lineStat.setMarkerCount(foundMarkers);
			lineStat.setMissingPerc((1 - (foundMarkers / (float) totalMarkers)) * 100);
			lineStat.setHetCount(hetMarkers);
			lineStat.setHetPerc((hetMarkers / (float)foundMarkers) * 100);
			lineStat.setMatchCount(totalMatches);
			lineStat.setMatchPerc((totalMatches / (float)foundMarkers) * 100);
			lineStat.setChrMatchCount(chrMatch);
		}
	}
}