// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;

import scri.commons.gui.*;

public class PedVerLinesAnalysis extends SimpleJob
{
	private GTViewSet viewSet;
	private AnalysisSet as;

	private ArrayList<Integer> parentIndices;

	private String name;

	public PedVerLinesAnalysis(GTViewSet viewSet, boolean[] selectedChromosomes, ArrayList<Integer> parentIndices, String name)
	{
		this(viewSet, selectedChromosomes, parentIndices);
		this.name = name;
	}

	public PedVerLinesAnalysis(GTViewSet viewSet, boolean[] selectedChromosomes, ArrayList<Integer> parentIndices)
	{
		this.viewSet = viewSet;
		this.parentIndices = parentIndices;

		moveParentsToTop();

		as = new AnalysisSet(viewSet)
			.withViews(selectedChromosomes)
			.withSelectedLines()
			.withSelectedMarkers();
	}

	public void runJob(int index)
		throws Exception
	{
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

		// Set the colour scheme to the similarity to line exact match scheme and set the comparison line equal to the
		// F1
//		viewSet.setColorScheme(ColorScheme.LINE_SIMILARITY_EXACT_MATCH);
//		viewSet.setComparisonLineIndex(testIndex);
//		viewSet.setComparisonLine(testLine.getLine());
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
}