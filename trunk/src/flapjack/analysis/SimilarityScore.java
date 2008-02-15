package flapjack.analysis;

import flapjack.data.*;

/**
 * Calculates a marker-based similarity score between two lines of data.
 */
public class SimilarityScore
{
	public static int SIMPLE  = 0;
	public static int JACCARD = 1;

	private GTView view;
	private int index1, index2;
	private int method = 1;

	private int a, b, c, d;

	public SimilarityScore(GTView view, int index1, int index2, int method)
	{
		this.view = view;
		this.index1 = index1;
		this.index2 = index2;
		this.method = method;
	}

	public Score getScore()
	{
		float nComparisons = 0;
		float score = 0;

		// For every marker across the genotype...
		for (int marker = 0; marker < view.getMarkerCount(); marker++)
		{
			int state1 = view.getState(index1, marker);
			int state2 = view.getState(index2, marker);

			// If either has no information, skip it
			if (state1 == 0 || state2 == 0)
				continue;

			// TODO: skip hetrozy?

			// Increment the score if they match
			if (state1 == state2)
				score++;

			// And count it as a comparison, regardless
			nComparisons++;
		}

		// TODO: a line with no data, will score 0/0 - problems?

		// The final score is the ratio of matches to comparable markers
		return new Score(score/nComparisons, nComparisons);
//		return score/nComparisons;
	}

	static class Score
	{
		float score;
		float nComparisons;

		Score(float score, float nComparisons)
		{
			this.score = score;
			this.nComparisons = nComparisons;
		}
	}
}