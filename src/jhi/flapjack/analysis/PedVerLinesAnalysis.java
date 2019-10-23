// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.stream.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.visualization.colors.*;

import scri.commons.gui.*;

public class PedVerLinesAnalysis extends SimpleJob
{
	private GTViewSet viewSet;
	private boolean[] selectedChromosomes;
	private int p1Index;
	private int p2Index;
	private AnalysisSet as;
	private StateTable stateTable;

	private String name;

	public PedVerLinesAnalysis(GTViewSet viewSet, boolean[] selectedChromosomes, int p1Index, int p2Index, String name)
	{
		this(viewSet, selectedChromosomes, p1Index, p2Index);
		this.name = name;
	}

	public PedVerLinesAnalysis(GTViewSet viewSet, boolean[] selectedChromosomes, int p1Index, int p2Index)
	{
		this.viewSet = viewSet.createClone("", true);
		this.stateTable = viewSet.getDataSet().getStateTable();
		this.p1Index = p1Index;
		this.p2Index = p2Index;
		this.selectedChromosomes = selectedChromosomes;

		this.p1Index = p1Index;
		this.p2Index = p2Index;
	}

	public void runJob(int index)
		throws Exception
	{
		long s = System.currentTimeMillis();

		moveParentsToTop();

		as = new AnalysisSet(this.viewSet)
			.withViews(selectedChromosomes)
			.withSelectedLines()
			.withSelectedMarkers();

//		for (int lineIndex=0; lineIndex < as.lineCount(); lineIndex++)
		IntStream.range(0, as.lineCount()).parallel().forEach((lineIndex) ->
		{
			LineInfo lineInfo = as.getLine(lineIndex);

			PedVerLinesResult lineStat = new PedVerLinesResult();
			lineInfo.getResults().setPedVerLinesResult(lineStat);
			lineInfo.getResults().setName(name);

			int totalCount = 0;
			for (int view = 0; view < as.viewCount(); view++)
				totalCount += as.markerCount(view);

			int missingMarkerCount = as.missingMarkerCount(lineIndex);
			int markerCount = totalCount - missingMarkerCount;
			int hetCount = as.hetCount(lineIndex);

			double similarityParent1 = similarityToLine(lineIndex, p1Index);
			double similarityParent2 = similarityToLine(lineIndex, p2Index);
			double similarityToParents = similarityToParents(lineIndex);

			lineStat.setDataCount(markerCount);
			lineStat.setPercentData((markerCount / (double) totalCount) * 100);
			lineStat.setHetCount(hetCount);
			lineStat.setPercentHet((hetCount / (double) markerCount) * 100);
			lineStat.setSimilarityToP1(similarityParent1 * 100);
			lineStat.setSimilarityToP2(similarityParent2 * 100);
			lineStat.setSimilarityToParents(similarityToParents * 100);
		});

		prepareForVisualization();

		long e = System.currentTimeMillis();
		System.out.println("TIME: " + (e-s) + "ms");
	}

	private void moveParentsToTop()
	{
		LineInfo p1 = viewSet.getLines().get(p1Index);
		LineInfo p2 = viewSet.getLines().get(p2Index);

		// Mark the parents lines as sortToTop special cases
		p1.getResults().setSortToTop(true);
		p2.getResults().setSortToTop(true);

		// Remove them from the list
		viewSet.getLines().remove(p1);
		viewSet.getLines().remove(p2);

		// Then put them back in at the top
		viewSet.getLines().add(0, p1);
		viewSet.getLines().add(1, p2);
		// Move the f1 to just below the parents

		// Reset our indexes as we've moved the lines in the dataset
		p1Index = 0;
		p2Index = 1;
	}

	private boolean anyParentMissing(int view, int marker)
	{
		boolean missing = false;

		if (as.getState(view, p1Index, marker) == 0 || as.getState(view, p2Index, marker) == 0)
			missing = true;

		return missing;
	}

	private boolean anyParentMatches(int view, int marker, int lineState)
	{
		boolean matches = false;

		if (lineState == as.getState(view, p1Index, marker) || lineState == as.getState(view, p2Index, marker))
			matches = true;

		return matches;
	}

	private double similarityToLine(int line, int comparisonLine)
	{
		double score = 0;
		int nComps = 0;

		for (int c = 0; c < as.viewCount(); c++)
		{
			for (int m = 0; m < as.markerCount(c); m++)
			{
				nComps++;

				AlleleState s1 = stateTable.getAlleleState(as.getState(c, comparisonLine, m));
				AlleleState s2 = stateTable.getAlleleState(as.getState(c, line, m));

				if (s1.matches(s2))
					score += 1.0d;

					// TODO: This is only really correct for diploid data, as
					// A/T/A vs A/T/G should really score 0.6666
				else if (s1.matchesAnyAllele(s2))
					score += 0.5d;
				else
					score += 0;
			}
		}

		return nComps > 0 ? (score / (double)nComps) : 0;
	}

	private double similarityToParents(int line)
	{
		double score = 0;
		int nComps = 0;
		for (int c = 0; c < as.viewCount(); c++)
		{
			for (int m = 0; m < as.markerCount(c); m++)
			{
				AlleleState lineState = stateTable.getAlleleState(as.getState(c, line, m));
				AlleleState p1State = stateTable.getAlleleState(as.getState(c, p1Index, m));
				AlleleState p2State = stateTable.getAlleleState(as.getState(c, p2Index, m));

				String[] lineAlleles = allelesFromGenotype(lineState);
				String[] p1Alleles = allelesFromGenotype(p1State);
				String[] p2Alleles = allelesFromGenotype(p2State);

				if (lineAlleles[0].equals(p1Alleles[0]) || lineAlleles[0].equals(p2Alleles[0]))
					score += 0.5d;

				if (lineAlleles[1].equals(p1Alleles[1]) || lineAlleles[1].equals(p2Alleles[1]))
					score +=0.5d;

				nComps++;
			}
		}

		return nComps > 0 ? (score / (double)nComps) : 0;
	}

	private String[] allelesFromGenotype(AlleleState state)
	{
		String[] lineAlleles = new String[2];
		if (state.isHomozygous())
		{
			lineAlleles[0] = state.getState(0);
			lineAlleles[1] = state.getState(0);
		}
		else
		{
			lineAlleles[0] = state.getState(0);
			lineAlleles[1] = state.getState(1);
		}

		return lineAlleles;
	}

	private void prepareForVisualization()
	{
		changeColourScheme();
		addViewSetToDataSet();
	}

	private void changeColourScheme()
	{
		// Set the colour scheme to the sparent dual match scheme and update the
		// comparison lines and indices
		viewSet.setColorScheme(ColorScheme.SIMILARITY_TO_EACH_PARENT);
		viewSet.setComparisonLineIndex(p1Index);
		viewSet.setComparisonLine(viewSet.getLines().get(0).getLine());
		viewSet.setComparisonLineIndex2(p2Index);
		viewSet.setComparisonLine2(viewSet.getLines().get(1).getLine());
	}

	private void addViewSetToDataSet()
	{
		DataSet dataSet = viewSet.getDataSet();

		int id = dataSet.getNavPanelCounts().getOrDefault("pedVerLinesCount", 0) + 1;
		dataSet.getNavPanelCounts().put("pedVerLinesCount", id);
		viewSet.setName(RB.format("gui.MenuAnalysis.pedVerLines.view", id));

		// Add the results viewset to the dataset
		dataSet.getViewSets().add(viewSet);
	}

	public GTViewSet getViewSet()
		{ return viewSet; }
}