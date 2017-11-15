// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.visualization.colors.*;

import scri.commons.gui.*;

public class PedVerF1sAnalysis extends SimpleJob
{
	private GTViewSet viewSet;
	private AnalysisSet as;
	private StateTable stateTable;

	private int parent1Index;
	private int parent2Index;
	private boolean simulateF1;
	private int f1Index;
	private boolean[] selectedChromosomes;
	private String name;

	private int f1HetCount = 0;
	private int totalMarkerCount = 0;
	private double f1PercentCount = 0;

	public PedVerF1sAnalysis(GTViewSet viewSet, boolean[] selectedChromosomes, int parent1Index, int parent2Index, boolean simulateF1, int f1Index, String name)
	{
		this.viewSet =  viewSet.createClone("", true);
		this.selectedChromosomes = selectedChromosomes;
		this.stateTable = viewSet.getDataSet().getStateTable();
		this.parent1Index = parent1Index;
		this.parent2Index = parent2Index;
		this.simulateF1 = simulateF1;
		this.f1Index = f1Index;
		this.name = name;
	}

	public void runJob(int index)
		throws Exception
	{
		if (simulateF1)
		{
			SimulateF1 f1Sim = new SimulateF1(viewSet, parent1Index, parent2Index);
			// TODO: have the F1 simulation track as part of this SimpleJob
			f1Sim.runJob(0);
			f1Index = f1Sim.getF1Index();
		}

		as = new AnalysisSet(viewSet)
			.withViews(selectedChromosomes)
			.withSelectedLines()
			.withSelectedMarkers();

		calculateExpectedF1Stats();

		for (int l=0; l < as.lineCount(); l++)
			calculateStatsForLine(l);

		prepareForVisualization();
	}

	private PedVerF1sResult calculateStatsForLine(int lineIndex)
	{
		LineInfo lineInfo = as.getLine(lineIndex);
		PedVerF1sResult lineStat = new PedVerF1sResult();
		lineInfo.getResults().setPedVerF1sResult(lineStat);
		lineInfo.getResults().setName(name);

		int foundMarkers = usableMarkerCount(lineIndex);
		int hetMarkers = hetMarkerCount(lineIndex);
		int p1Contained = containedInLine(lineIndex, parent1Index);
		int p2Contained = containedInLine(lineIndex, parent2Index);
		int matchesExpF1 = matchesExpF1(lineIndex);
		int missingCount = countMissingAlleles(lineIndex);

		lineStat.setMarkerCount(foundMarkers);
		lineStat.setPercentMissing((missingCount / (double) totalMarkerCount) * 100);
		lineStat.setHeterozygousCount(hetMarkers);
		lineStat.setPercentHeterozygous((hetMarkers / (double)foundMarkers) * 100);
		lineStat.setPercentDeviationFromExpected(f1PercentCount - ((hetMarkers / (double)foundMarkers) * 100));
		lineStat.setCountP1Contained(p1Contained);
		lineStat.setPercentP1Contained((p1Contained / (double)foundMarkers) * 100);
		lineStat.setCountP2Contained(p2Contained);
		lineStat.setPercentP2Contained((p2Contained / (double)foundMarkers) * 100);
		lineStat.setCountAlleleMatchExpected(matchesExpF1);
		lineStat.setPercentAlleleMatchExpected((matchesExpF1 / (double)foundMarkers) * 100);

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
		f1PercentCount = (f1HetCount / (double)totalMarkerCount) * 100;
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
			&& stateTable.isHom(as.getState(chr, parent1Index, marker))
			&& stateTable.isHom(as.getState(chr, parent2Index, marker));
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

	private void prepareForVisualization()
	{
		prepareParentsForVisualization();
		changeColourScheme();
		addViewSetToDataSet();
	}

	private void prepareParentsForVisualization()
	{
		LineInfo p1 = viewSet.getLines().get(parent1Index);
		LineInfo p2 = viewSet.getLines().get(parent2Index);
		LineInfo f1 = viewSet.getLines().get(f1Index);

		// Mark the parents lines as sortToTop special cases
		p1.getResults().setSortToTop(true);
		p2.getResults().setSortToTop(true);
		f1.getResults().setSortToTop(true);

		// Remove them from the list
		viewSet.getLines().remove(p1);
		viewSet.getLines().remove(p2);
		viewSet.getLines().remove(f1);

		// Then put them back in at the top
		viewSet.getLines().add(0, p1);
		viewSet.getLines().add(1, p2);
		// Move the f1 to just below the parents
		viewSet.getLines().add(2, f1);

		// Reset our indexes as we've moved the lines in the dataset
		parent1Index = 0;
		parent2Index = 1;
		f1Index = 2;
	}

	private void changeColourScheme()
	{
		// Set the colour scheme to the similarity to line exact match scheme
		// and set the comparison line equal to the F1
		viewSet.setColorScheme(ColorScheme.LINE_SIMILARITY_EXACT_MATCH);
		viewSet.setComparisonLineIndex(f1Index);
		viewSet.setComparisonLine(viewSet.getLines().get(f1Index).getLine());
	}

	private void addViewSetToDataSet()
	{
		DataSet dataSet = viewSet.getDataSet();

		viewSet.setName(name);

		// Create new NavPanel components to hold the results
		dataSet.getViewSets().add(viewSet);
	}

	public GTViewSet getViewSet()
		{ return viewSet; }
}