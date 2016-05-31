package jhi.flapjack.analysis;

import jhi.flapjack.data.*;

/**
 * Created by gs40939 on 31/05/2016.
 */
public class PedVerStats
{
	private AnalysisSet as;
	private StateTable stateTable;

	public PedVerStats(AnalysisSet as, StateTable stateTable)
	{
		this.as = as;
		this.stateTable = stateTable;
	}

	public int countAllelesForLine()
	{
		int totalMarkers = 0;
		for (int i = 0; i < as.viewCount(); i++)
			totalMarkers += as.markerCount(i);

		return totalMarkers;
	}

	public int nonMissingAllelesForLine(int lineIndex)
	{
		int nonMissing = 0;
		for (int c = 0; c < as.viewCount(); c++)
			for (int m = 0; m < as.markerCount(c); m++)
				if (as.getState(c, lineIndex, m) != 0)
					nonMissing++;

		return nonMissing;
	}

	public int hetAllelesForLine(int lineIndex)
	{
		int hetCount = 0;
		for (int c = 0; c < as.viewCount(); c++)
			for (int m = 0; m < as.markerCount(c); m++)
				if (stateTable.isHet(as.getState(c, lineIndex, m)))
					hetCount++;

		return hetCount;
	}

	public int matchesAnyAlleleCount(int comparisonLineIndex, int lineIndex)
	{
		int matchesCount = 0;

		for (int c = 0; c < as.viewCount(); c++)
			matchesCount += matchesAnyAlleleCountForView(c, comparisonLineIndex, lineIndex);

		return matchesCount;
	}

	public int matchesAnyAlleleCountForView(int view, int comparisonLineIndex, int lineIndex)
	{
		int matchesCount = 0;

		for (int m = 0; m < as.markerCount(view); m++)
		{
			// Compare state code of the current line with the equivalent in test line
			AlleleState testState = stateTable.getAlleleState(as.getState(view, comparisonLineIndex, m));
			AlleleState currState = stateTable.getAlleleState(as.getState(view, lineIndex, m));

			if (currState.matchesAnyAllele(testState))
				matchesCount++;
		}

		return matchesCount;
	}

	public int matchesAlleleCount(int comparisonLineIndex, int lineIndex)
	{
		int matchesCount = 0;

		for (int c = 0; c < as.viewCount(); c++)
			matchesCount += matchesAlleleCountForView(c, comparisonLineIndex, lineIndex);

		return matchesCount;
	}

	public int matchesAlleleCountForView(int view, int comparisonLineIndex, int lineIndex)
	{
		int matchesCount = 0;

		for (int m = 0; m < as.markerCount(view); m++)
		{
			// Compare state code of the current line with the equivalent in test line
			AlleleState testState = stateTable.getAlleleState(as.getState(view, comparisonLineIndex, m));
			AlleleState currState = stateTable.getAlleleState(as.getState(view, lineIndex, m));

			if (currState.matches(testState))
				matchesCount++;
		}

		return matchesCount;
	}
}