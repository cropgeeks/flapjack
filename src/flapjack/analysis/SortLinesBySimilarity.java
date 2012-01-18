// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.util.*;

import flapjack.data.*;

public class SortLinesBySimilarity extends SortLines
{
	private Line comparisonLine;
	private boolean[] chromosomes;

	public SortLinesBySimilarity(GTViewSet viewSet, Line comparisonLine, boolean[] chromosomes)
	{
		super(viewSet);
		this.comparisonLine = comparisonLine;
		this.chromosomes = chromosomes;
	}

	@Override
	public void runJob(int jobIndex)
	{
		long s = System.currentTimeMillis();

		// Pre-cache the information that will be needed
		for (GTView view: viewSet.getViews())
			view.cacheLines();

		super.runJob(jobIndex);

		viewSet.setDisplayLineScores(true);
		System.out.println("Similarity sort in " + (System.currentTimeMillis()-s) + "ms");
	}

	@Override
	protected ArrayList<LineInfo> doSort(GTView view)
	{
		ArrayList<LineScore> scores = new ArrayList<LineScore>();
		StateTable st = viewSet.getDataSet().getStateTable();

		int line = viewSet.indexOf(comparisonLine);
		System.out.println("Sorting using line " + line + " as comparison line");

		ArrayList<LineInfo> lines = view.getViewSet().getLines();

		// Work out what those scores are
		for (int i = 0; i < lines.size() && okToRun; i++, linesScored++)
		{
			SimilarityScore ss = new SimilarityScore(viewSet, st, line, i, chromosomes);

			SimilarityScore.Score score = ss.getScore();
			scores.add(new LineScore(lines.get(i), score.score, score.nComparisons));
		}

		// Now sort the array based on those scores
		Collections.sort(scores);

		// Then create a new line ordering for the view
		ArrayList<LineInfo> lineOrder = new ArrayList<LineInfo>(view.lineCount());
		for (int i = 0; i < scores.size() && okToRun; i++)
			lineOrder.add(scores.get(i).lineInfo);

		return lineOrder;
	}

	private class LineScore implements Comparable<LineScore>
	{
		LineInfo lineInfo;
		float score;
		float nComparisons;

		LineScore(LineInfo lineInfo, float score, float nComparisons)
		{
			this.lineInfo = lineInfo;
			this.score = score;
			this.nComparisons = nComparisons;

			lineInfo.setScore(score);
		}

		public int compareTo(LineScore other)
		{
			// Double nested sort - sort on score ratio first, but if the score
			// matches, also sort on the number of comparisons that were
			// possible, so a line with 3/3 will score less than one with 4/4
			// (even though they both have a ratio of 1.0)
			if (score > other.score)
				return -1;
			else if (score == other.score)
			{
				if (nComparisons > other.nComparisons)
					return -1;
				else if (nComparisons == other.nComparisons)
					return 0;
				else
					return 1;
			}
			else
				return 1;
		}
	}
}