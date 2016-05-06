// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;

import scri.commons.gui.*;

public class PedVerStats extends SimpleJob
{
	private AnalysisSet as;
	private StateTable stateTable;

	private LineInfo parent1Info;
	private LineInfo parent2Info;
	private LineInfo f1LineInfo;

	private PedVerKnownParentsResults result;

	private int f1HetCount = 0;
	private int totalMarkerCount = 0;
	private float f1PercentCount = 0;

	public PedVerStats(AnalysisSet as, StateTable stateTable, LineInfo parent1Info, LineInfo parent2Info, LineInfo f1LineInfo)
	{
		this.as = as;
		this.stateTable = stateTable;
		this.parent1Info = parent1Info;
		this.parent2Info = parent2Info;
		this.f1LineInfo = f1LineInfo;
	}

	public void runJob(int index)
		throws Exception
	{
		calculateStats(as);
	}

	private void calculateStats(AnalysisSet as)
	{
		calculateExpectedF1Stats(as);

		ArrayList<PedVerKnownParentsLineStats> statsArrayList = new ArrayList<>();

		for (int l=0; l < as.lineCount(); l++)
		{
			if (as.getLines().indexOf(parent1Info) == l || as.getLines().indexOf(parent2Info) == l || as.getLines().indexOf(f1LineInfo) == l)
				continue;

			PedVerKnownParentsLineStats lineStat = calculateStatsForLine(as, l);
			statsArrayList.add(lineStat);
		}

		result = new PedVerKnownParentsResults(totalMarkerCount, f1HetCount, f1PercentCount, statsArrayList);
	}

	private PedVerKnownParentsLineStats calculateStatsForLine(AnalysisSet as1, int l)
	{
		PedVerKnownParentsLineStats lineStat = new PedVerKnownParentsLineStats(as1.getLine(l));

		int foundMarkers = 0;
		int hetMarkers = 0;
		int p1Contained = 0;
		int p2Contained = 0;
		int matchesExpF1 = 0;

		for (int c = 0; c < as1.viewCount(); c++)
		{
			for (int m = 0; m < as1.markerCount(c); m++)
			{
				int code = as1.getState(c, l, m);

				// Skip unknown states
				if (code == 0)
					continue;

				// Otherwise we have an allele so increment foundMarkers
				if (code > 0)
					foundMarkers++;

				// If the allele is heterozygous increment the het counter
				if (stateTable.isHet(code))
					hetMarkers++;

				// Compare state code of the current allele with the equivalent
				// in both parents and the expected F1
				AlleleState p1State = stateTable.getAlleleState(as1.getState(c, 0, m));
				AlleleState p2State = stateTable.getAlleleState(as1.getState(c, 1, m));
				AlleleState expF1State = stateTable.getAlleleState(f1LineInfo.getState(c, m));
				AlleleState currState = stateTable.getAlleleState(code);

				if (currState.matchesAnyAllele(p1State))
					p1Contained++;

				if (currState.matchesAnyAllele(p2State))
					p2Contained++;

				if (currState.matches(expF1State))
					matchesExpF1++;
			}
		}

		lineStat.setLine(as1.getLine(l));
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

	private void calculateExpectedF1Stats(AnalysisSet as1)
	{
		for (int c = 0; c < as1.viewCount(); c++)
		{
			totalMarkerCount += as1.markerCount(c);
			for (int m = 0; m < as1.markerCount(c); m++)
			{
				int stateCode = f1LineInfo.getState(c, m);
				if (stateTable.isHet(stateCode))
					f1HetCount++;
			}
		}
		f1PercentCount = (f1HetCount / (float)totalMarkerCount) * 100;
	}

	public PedVerKnownParentsResults getResult()
	{
		return result;
	}
}