package flapjack.analysis;

import java.util.*;

import flapjack.data.*;

public class SimilaritySort
{
	private GTView view;

	public SimilaritySort(GTView view)
	{
		this.view = view;
	}

	public void run()
	{
		long s = System.currentTimeMillis();

		Vector<Integer> lines = view.getLines();

		// Create an array to hold the score for each line
		Vector<LineScore> scores = new Vector<LineScore>(lines.size());

		// Work out what those scores are
		for (int i = 0; i < view.getLineCount(); i++)
		{
			SimilarityScore ss = new SimilarityScore(view, 0, i, SimilarityScore.SIMPLE);

			SimilarityScore.Score score = ss.getScore();
			scores.add(new LineScore(i, score.score, score.nComparisons));
		}

		// Now sort the array based on those scores
		Collections.sort(scores);

		for (int i = 0; i < scores.size(); i++)
			lines.set(i, scores.get(i).index);

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