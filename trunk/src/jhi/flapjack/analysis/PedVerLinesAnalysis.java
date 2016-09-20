// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.visualization.colors.*;

import scri.commons.gui.*;

public class PedVerLinesAnalysis extends SimpleJob
{
	private GTViewSet viewSet;
	private AnalysisSet as;
	private StateTable stateTable;

	private int refIndex;
	private int testIndex;

	public PedVerLinesAnalysis(GTViewSet viewSet, boolean[] selectedChromosomes, int refIndex, int testIndex)
	{
		this.viewSet = viewSet;
		this.stateTable = viewSet.getDataSet().getStateTable();
		this.refIndex = refIndex;
		this.testIndex = testIndex;

		moveRefAndTestToTop();

		as = new AnalysisSet(viewSet)
			.withViews(selectedChromosomes)
			.withSelectedLines()
			.withSelectedMarkers();
	}

	public void runJob(int index)
		throws Exception
	{
		int totalMarkerCount = calculateTotalMarkerCount();

		for (int lineIndex=0; lineIndex < as.lineCount(); lineIndex++)
		{
			LineInfo lineInfo = as.getLine(lineIndex);

			PedVerLinesResult lineStat = new PedVerLinesResult();
			lineInfo.getResults().setPedVerLinesResult(lineStat);

			int foundMarkers = usableMarkerCount(lineIndex);
			double missingPerc = (1 - (foundMarkers / (double) totalMarkerCount)) * 100;
			int hetMarkers = hetMarkerCount(lineIndex);
			double hetPerc = (hetMarkers / (double)foundMarkers) * 100;
			int totalMatches = matchesTestLine(lineIndex);
			ArrayList<Integer> chrMatch = new ArrayList<>();
			for (int c = 0; c < as.viewCount(); c++)
				chrMatch.add(matchesAlleleCountForView(c, lineIndex));

			lineStat.setMarkerCount(foundMarkers);
			lineStat.setMissingPerc(missingPerc);
			lineStat.setHetCount(hetMarkers);
			lineStat.setHetPerc(hetPerc);
			lineStat.setMatchCount(totalMatches);
			lineStat.setMatchPerc((totalMatches / (float)foundMarkers) * 100);
			lineStat.setChrMatchCount(chrMatch);
		}
	}

	private void moveRefAndTestToTop()
	{
		LineInfo refLine = viewSet.getLines().get(refIndex);
		LineInfo testLine = viewSet.getLines().get(testIndex);

		// Mark the parents lines as sortToTop special cases
		refLine.getResults().setSortToTop(true);
		testLine.getResults().setSortToTop(true);

		// Move the reference and test lines to the top of the display
		viewSet.moveLine(viewSet.getLines().indexOf(refLine), 0);
		viewSet.moveLine(viewSet.getLines().indexOf(testLine), 1);

		// Reset our indexes as we've moved the lines in the dataset
		refIndex = 0;
		testIndex = 1;

		// Set the colour scheme to the similarity to line exact match scheme and set the comparison line equal to the
		// F1
		viewSet.setColorScheme(ColorScheme.LINE_SIMILARITY_EXACT_MATCH);
		viewSet.setComparisonLineIndex(viewSet.getLines().indexOf(testLine));
		viewSet.setComparisonLine(testLine.getLine());
	}

	private boolean isUsableMarker(int chr, int lineIndex, int marker)
	{
		return as.getState(chr, testIndex, marker) != 0 &&
			as.getState(chr, lineIndex, marker) != 0;
	}

	// Loops over all the alleles in the expected F1 as identified by f1Index
	// and counts the total number of usable markers and the total number of
	// heterozygous alleles. Finally it calculates the percentage of alleles in
	//the (expected) F1 line that are heterozygous.
	private int calculateTotalMarkerCount()
	{
		int totalMarkerCount = 0;

		for (int c = 0; c < as.viewCount(); c++)
			for (int m = 0; m < as.markerCount(c); m++)
				if (isUsableMarker(c, testIndex, m))
					totalMarkerCount++;

		return totalMarkerCount;
	}

	private int usableMarkerCount(int lineIndex)
	{
		int foundMarkers = 0;

		for (int c = 0; c < as.viewCount(); c++)
			for (int m = 0; m < as.markerCount(c); m++)
				if (isUsableMarker(c, lineIndex, m))
					foundMarkers++;

		return foundMarkers;
	}

	private int hetMarkerCount(int lineIndex)
	{
		int hetMarkers = 0;

		for (int c = 0; c < as.viewCount(); c++)
			for (int m = 0; m < as.markerCount(c); m++)
				if (isUsableMarker(c, lineIndex, m) && stateTable.isHet(as.getState(c, lineIndex, m)))
					hetMarkers++;

		return hetMarkers;
	}

	private int matchesTestLine(int lineIndex)
	{
		int matchesExpF1 = 0;

		for (int c = 0; c < as.viewCount(); c++)
		{
			for (int m = 0; m < as.markerCount(c); m++)
			{
				if (isUsableMarker(c, lineIndex, m))
				{
					// Compare state code of the current line with the equivalent in test line
					AlleleState testState = stateTable.getAlleleState(as.getState(c, testIndex, m));
					AlleleState currState = stateTable.getAlleleState(as.getState(c, lineIndex, m));

					if (currState.matchesAnyAllele(testState))
						matchesExpF1++;
				}
			}
		}
		return matchesExpF1;
	}

	public int matchesAlleleCountForView(int view, int lineIndex)
	{
		int matchesCount = 0;

		for (int m = 0; m < as.markerCount(view); m++)
		{
			if (isUsableMarker(view, lineIndex, m))
			{
				// Compare state code of the current line with the equivalent in test line
				AlleleState testState = stateTable.getAlleleState(as.getState(view, testIndex, m));
				AlleleState currState = stateTable.getAlleleState(as.getState(view, lineIndex, m));

				if (currState.matchesAnyAllele(testState))
					matchesCount++;
			}
		}

		return matchesCount;
	}
}