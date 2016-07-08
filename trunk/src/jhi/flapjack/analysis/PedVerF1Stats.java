// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.visualization.colors.*;

import scri.commons.gui.*;

public class PedVerF1Stats extends SimpleJob
{
	private GTViewSet viewSet;
	private AnalysisSet as;
	private StateTable stateTable;

	private int parent1Index;
	private int parent2Index;
	private int f1Index;

	private int f1HetCount = 0;
	private int totalMarkerCount = 0;
	private float f1PercentCount = 0;

	public PedVerF1Stats(GTViewSet viewSet, boolean[] selectedChromosomes, int parent1Index, int parent2Index, int f1Index)
	{
		this.viewSet = viewSet;
		this.stateTable = viewSet.getDataSet().getStateTable();
		this.parent1Index = parent1Index;
		this.parent2Index = parent2Index;
		this.f1Index = f1Index;

		moveParentsToTop();

		as = new AnalysisSet(viewSet)
		.withViews(selectedChromosomes)
		.withSelectedLines()
		.withSelectedMarkers();
	}

	private void moveParentsToTop()
	{
		LineInfo p1 = viewSet.getLines().get(parent1Index);
		LineInfo p2 = viewSet.getLines().get(parent2Index);
		LineInfo f1 = viewSet.getLines().get(f1Index);

		// Move the parent lines to the top of the display
		GTView view = viewSet.getView(0);
		view.moveLine(viewSet.getLines().indexOf(p1), 0);
		view.moveLine(viewSet.getLines().indexOf(p2), 1);
		// Move the f1 to just below the parents
		view.moveLine(viewSet.getLines().indexOf(f1), 2);

		// Set the colour scheme to the similarity to line exact match scheme and set the comparison line equal to the
		// F1
		viewSet.setColorScheme(ColorScheme.LINE_SIMILARITY_EXACT_MATCH);
		viewSet.setComparisonLineIndex(viewSet.getLines().indexOf(f1));
		viewSet.setComparisonLine(f1.getLine());
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
		LineInfo lineInfo = as.getLine(lineIndex);
		PedVerKnownParentsLineStats lineStat = new PedVerKnownParentsLineStats(lineInfo);
		lineInfo.results().setPedVerStats(lineStat);

		int foundMarkers = usableMarkerCount(lineIndex);
		int hetMarkers = hetMarkerCount(lineIndex);
		int p1Contained = containedInLine(lineIndex, parent1Index);
		int p2Contained = containedInLine(lineIndex, parent2Index);
		int matchesExpF1 = matchesExpF1(lineIndex);
		int missingCount = countMissingAlleles(lineIndex);

		lineStat.setLine(lineInfo);
		lineStat.setMarkerCount(foundMarkers);
		lineStat.setPercentMissing((missingCount / (float) totalMarkerCount) * 100);
		lineStat.setPercentDeviationFromExpected((1 - (foundMarkers / (float) totalMarkerCount)) * 100);
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

	// Loops over all the alleles in the expected F1 as identified by f1Index
	 // and counts the total number of usable markers and the total number of
	 // heterozygous alleles. Finally it calculates the percentage of alleles in
	 //the (expected) F1 line that are heterozygous.
	private void calculateExpectedF1Stats()
	{
		for (int c = 0; c < as.viewCount(); c++)
		{
			for (int m = 0; m < as.markerCount(c); m++)
			{
				if (isUsableMarker(c, f1Index, m))
				{
					totalMarkerCount++;

					int stateCode = as.getState(c, f1Index, m);
					if (stateTable.isHet(stateCode))
						f1HetCount++;
				}
			}
		}
		f1PercentCount = (f1HetCount / (float)totalMarkerCount) * 100;
	}

	// Checks to see if this allele is usable. It first checks that the allele
	// itself isn't unknown, then checks that the parental and f1 alleles at
	// this location aren't known. Finally it checks that the parental alleles
	// aren't hets at this location.
	private boolean isUsableMarker(int chr, int line, int marker)
	{
		return as.getState(chr, line, marker) != 0
			&& as.getState(chr, parent1Index, marker) != 0
			&& as.getState(chr, parent2Index, marker) != 0
			&& as.getState(chr, f1Index, marker) != 0
			&& !stateTable.isHet(as.getState(chr, parent1Index, marker))
			&& !stateTable.isHet(as.getState(chr, parent2Index, marker));
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

	private int countMissingAlleles(int lineIndex)
	{
		int missingCount = 0;
		for (int c = 0; c < as.viewCount(); c++)
		{
			for (int m = 0; m < as.markerCount(c); m++)
			{
				if (as.getState(c, lineIndex, m) == 0
					&& as.getState(c, parent1Index, m) != 0
					&& as.getState(c, parent2Index, m) != 0
					&& as.getState(c, f1Index, m) != 0
					&& !stateTable.isHet(as.getState(c, parent1Index, m))
					&& !stateTable.isHet(as.getState(c, parent2Index, m)))
				{
					missingCount++;
				}
			}
		}
		return missingCount;
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

	// Checks if the current allele has any partial match to a given comparison
	// line. It is likely the comparison line will be one of the two parental
	// lines.
	private int containedInLine(int line, int comparisonLine)
	{
		int contained = 0;

		for (int c = 0; c < as.viewCount(); c++)
		{
			for (int m = 0; m < as.markerCount(c); m++)
			{
				if (isUsableMarker(c, line, m))
				{
					// Compare state code of the current line with the equivalent in test line
					AlleleState testState = stateTable.getAlleleState(as.getState(c, comparisonLine, m));
					AlleleState currState = stateTable.getAlleleState(as.getState(c, line, m));

					if (currState.matchesAnyAllele(testState))
						contained++;
				}
			}
		}

		return contained;
	}

	private int matchesExpF1(int lineIndex)
	{
		int matchesExpF1 = 0;

		for (int c = 0; c < as.viewCount(); c++)
		{
			for (int m = 0; m < as.markerCount(c); m++)
			{
				if (isUsableMarker(c, lineIndex, m))
				{
					// Compare state code of the current line with the equivalent in test line
					AlleleState testState = stateTable.getAlleleState(as.getState(c, f1Index, m));
					AlleleState currState = stateTable.getAlleleState(as.getState(c, lineIndex, m));

					if (currState.matches(testState))
						matchesExpF1++;
				}
			}
		}
		return matchesExpF1;
	}
}