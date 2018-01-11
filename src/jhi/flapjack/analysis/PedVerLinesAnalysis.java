// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;

import jhi.flapjack.gui.visualization.colors.ColorScheme;
import scri.commons.gui.*;

public class PedVerLinesAnalysis extends SimpleJob
{
	private GTViewSet viewSet;
	private boolean[] selectedChromosomes;
	private int p1Index;
	private int p2Index;
	private ArrayList<Integer> parentIndices;

	private AnalysisSet as;

	private String name;

	public PedVerLinesAnalysis(GTViewSet viewSet, boolean[] selectedChromosomes, int p1Index, int p2Index, String name)
	{
		this(viewSet, selectedChromosomes, p1Index, p2Index);
		this.name = name;
	}

	public PedVerLinesAnalysis(GTViewSet viewSet, boolean[] selectedChromosomes, int p1Index, int p2Index)
	{
		this.viewSet = viewSet.createClone("", true);
		this.p1Index = p1Index;
		this.p2Index = p2Index;
		this.selectedChromosomes = selectedChromosomes;

		this.parentIndices = new ArrayList<>();
		this.parentIndices.add(p1Index);
		this.parentIndices.add(p2Index);

		moveParentsToTop();
	}

	public void runJob(int index)
		throws Exception
	{
		as = new AnalysisSet(this.viewSet)
			.withViews(selectedChromosomes)
			.withSelectedLines()
			.withSelectedMarkers();

		for (int lineIndex=0; lineIndex < as.lineCount(); lineIndex++)
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

			ArrayList<PedVerLinesParentScore> parentScores = getParentScoresForLine(lineIndex);

			int dataTotalMatch = dataTotalMatch(lineIndex);
			int totalMatch = totalMatch(lineIndex);

			lineStat.setDataCount(totalCount);
			lineStat.setMissingCount(missingMarkerCount);
			lineStat.setMarkerCount(markerCount);
			lineStat.setPercentMissing((missingMarkerCount / (double) totalCount) * 100);
			lineStat.setHetCount(hetCount);
			lineStat.setPercentHet((hetCount / (double) markerCount) * 100);
			lineStat.setParentScores(parentScores);
			lineStat.setDataTotalMatch(dataTotalMatch);
			lineStat.setTotalMatch(totalMatch);
			lineStat.setPercentTotalMatch((totalMatch / (double) dataTotalMatch) * 100);
		}

		prepareForVisualization();
	}

	private void moveParentsToTop()
	{
		ArrayList<LineInfo> parentLines = new ArrayList<>();
		for (Integer parentIndex : parentIndices)
			parentLines.add(viewSet.getLines().get(parentIndex));

		// Mark the parent lines as sortToTop special cases
		parentLines.forEach(line -> line.getResults().setSortToTop(true));

		// Remove the parents from the viewset
		viewSet.getLines().removeAll(parentLines);

		// Then put the parents back in at the top and update our parentIndices
		// as we've moved the parent lines in the dataset
		for (int i=0; i < parentLines.size(); i++)
		{
			viewSet.getLines().add(i, parentLines.get(i));
			parentIndices.set(i, i);
		}
	}

	private ArrayList<PedVerLinesParentScore> getParentScoresForLine(int lineIndex)
	{
		ArrayList<PedVerLinesParentScore> parentScores = new ArrayList<>();
		for (Integer parent : parentIndices)
			parentScores.add(new PedVerLinesParentScore());

		for (int i=0; i < parentScores.size(); i++)
		{
			PedVerLinesParentScore parentScore = parentScores.get(i);
			int dataCount = 0;
			int matchCount = 0;

			for (int c = 0; c < as.viewCount(); c++)
			{
				for (int m = 0; m < as.markerCount(c); m++)
				{
					int lineState = as.getState(c, lineIndex, m);
					int parentState = as.getState(c, parentIndices.get(i), m);

					if (lineState != 0 && parentState != 0)
						dataCount++;

					if (lineState == parentState && lineState != 0)
						matchCount++;
				}
			}

			parentScore.setDataParentMatch(dataCount);
			parentScore.setMatchParentCount(matchCount);
			parentScore.setMatchParentPercent((matchCount / (double) dataCount) * 100);
		}

		return parentScores;
	}

	private int dataTotalMatch(int lineIndex)
	{
		int dataTotalMatch = 0;
		for (int c = 0; c < as.viewCount(); c++)
		{
			for (int m = 0; m < as.markerCount(c); m++)
			{
				int lineState = as.getState(c, lineIndex, m);
				if (lineState != 0 && !anyParentMissing(c, m))
					dataTotalMatch++;
			}
		}
		return dataTotalMatch;
	}

	private int totalMatch(int lineIndex)
	{
		int totalMatch = 0;
		for (int c = 0; c < as.viewCount(); c++)
		{
			for (int m = 0; m < as.markerCount(c); m++)
			{
				int lineState = as.getState(c, lineIndex, m);
				if (!anyParentMissing(c, m) && anyParentMatches(c, m , lineState))
					totalMatch++;
			}
		}
		return totalMatch;
	}

	private boolean anyParentMissing(int view, int marker)
	{
		boolean missing = false;

		for (int i=0; i < parentIndices.size(); i++)
			if (as.getState(view, i, marker) == 0)
				missing = true;

		return missing;
	}

	private boolean anyParentMatches(int view, int marker, int lineState)
	{
		boolean matches = false;

		for (Integer parentIndex : parentIndices)
			if (lineState == as.getState(view, parentIndex, marker))
				matches = true;

		return matches;
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
		viewSet.setColorScheme(ColorScheme.PARENT_DUAL);
		viewSet.setComparisonLineIndex(parentIndices.get(0));
		viewSet.setComparisonLine(viewSet.getLines().get(0).getLine());
		viewSet.setComparisonLineIndex2(parentIndices.get(1));
		viewSet.setComparisonLine2(viewSet.getLines().get(1).getLine());
	}

	private void addViewSetToDataSet()
	{
		DataSet dataSet = viewSet.getDataSet();

		viewSet.setName("PedVerLines View");

		// Add the results viewset to the dataset
		dataSet.getViewSets().add(viewSet);
	}

	public GTViewSet getViewSet()
		{ return viewSet; }
}