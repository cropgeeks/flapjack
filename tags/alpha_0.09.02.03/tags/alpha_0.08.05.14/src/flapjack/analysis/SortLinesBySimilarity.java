package flapjack.analysis;

import java.util.*;

import flapjack.data.*;

public class SortLinesBySimilarity implements ILineSorter
{
	private GTView view;
	private int line;

	private int linesScored = 0;

	public SortLinesBySimilarity(GTView view, int line)
	{
		this.view = view;
		this.line = line;
	}

	public int getMaximum()
		{ return view.getLineCount(); }

	public int getValue()
		{ return linesScored; }

	public void doSort()
	{
		long s = System.currentTimeMillis();

		// Access the line data - we HAVE to use this reference and CANNOT
		// reassign it, otherwise all the other views that refer to it will no
		// longer point to the correct object
		Vector<Integer> lines = view.getLines();

		// Create an array to hold the score for each line
		Vector<LineScore> scores = new Vector<LineScore>(lines.size());

		System.out.println("Sorting using line " + line + " as comparison line");


		// Work out what those scores are
		for (int i = 0; i < view.getLineCount(); i++, linesScored++)
		{
			SimilarityScore ss = new SimilarityScore(view, line, i);

			SimilarityScore.Score score = ss.getScore();
			scores.add(new LineScore(view.getLines().get(i), score.score, score.nComparisons));

//			if (i <5)
//				System.out.println("Score for " + view.getLine(i).getName() + " is " + score.score + " with " + score.nComparisons + " comparisons possible");
		}

		// Now sort the array based on those scores
		Collections.sort(scores);

		lines.clear();
		for (int i = 0; i < scores.size(); i++)
			lines.add(scores.get(i).index);

		// Because we've reordered the view (without it knowing), we MUST let
		// it know that it has to search for its comparison line's new position
		view.updateComparisons();

		System.out.println("Similarity sort in " + (System.currentTimeMillis()-s) + "ms");



		// Quick all-by-all test
/*		s = System.currentTimeMillis();
		int count = 0;
		for (int i = 0; i < view.getLineCount(); i++)
			for (int j = i; j < view.getLineCount(); j++)
			{
				SimilarityScore ss = new SimilarityScore(view, i, j, SimilarityScore.JACCARD);
				ss.getScore();
				count++;
			}

		System.out.println("Matrix in " + (System.currentTimeMillis()-s) + "ms");
		System.out.println("Matrix ran " + count + " comparisons");
*/
	}

	private static class LineScore implements Comparable<LineScore>
	{
		int index;
		float score;
		float nComparisons;

		LineScore(int index, float score, float nComparisons)
		{
			this.index = index;
			this.score = score;
			this.nComparisons = nComparisons;
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