package flapjack.analysis;

import java.util.*;

import flapjack.data.*;

public class SortLinesBySimilarity implements ILineSorter
{
	private GTViewSet viewSet;
	private Line comparisonLine;
	private boolean[] chromosomes;

	private int linesScored = 0;

	public SortLinesBySimilarity(GTViewSet viewSet, Line comparisonLine, boolean[] chromosomes)
	{
		this.viewSet = viewSet;
		this.comparisonLine = comparisonLine;
		this.chromosomes = chromosomes;
	}

	public int getMaximum()
		{ return viewSet.getView(0).getLineCount(); }

	public int getValue()
		{ return linesScored; }

	public void doSort()
	{
		long s = System.currentTimeMillis();

		// Access the first chromosome (just to get at the lines data)
		GTView view = viewSet.getView(0);

		// Store a local reference to the line ordering for quicker access
		Vector<LineInfo> lines = view.getViewSet().getLines();

		// Create an array to hold the score for each line
		Vector<LineScore> scores = new Vector<LineScore>(lines.size());

		// Find the comparison line (by index)
		int line = viewSet.indexOf(comparisonLine);

		System.out.println("Sorting using line " + line + " as comparison line");

		// Work out what those scores are
		for (int i = 0; i < view.getLineCount(); i++, linesScored++)
		{
			SimilarityScore ss = new SimilarityScore(viewSet, line, i, chromosomes);

			SimilarityScore.Score score = ss.getScore();
			scores.add(new LineScore(lines.get(i), score.score, score.nComparisons));
		}

		// Now sort the array based on those scores
		Collections.sort(scores);

		// Then create a new line ordering for the view
		LineInfo[] lineOrder = new LineInfo[scores.size()];
		for (int i = 0; i < scores.size(); i++)
			lineOrder[i] = scores.get(i).lineInfo;

		// And pass that order back to the view
		view.getViewSet().setLinesFromArray(lineOrder, true);
		view.getViewSet().setDisplayLineScores(true);

		System.out.println("Similarity sort in " + (System.currentTimeMillis()-s) + "ms");
	}

	private static class LineScore implements Comparable<LineScore>
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