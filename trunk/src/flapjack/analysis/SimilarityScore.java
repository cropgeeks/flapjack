package flapjack.analysis;

import flapjack.data.*;

/**
 * Calculates a marker-based similarity score between two lines of data.
 */
public class SimilarityScore
{
	private GTViewSet viewSet;
	private int compLine, currLine;
	private boolean[] chromosomes;

	/**
	 * @param compLine the comparison line to compare the current line against
	 * @param currLine the current line to compute a score for
	 */
	public SimilarityScore(GTViewSet viewSet, int compLine, int currLine, boolean[] chromosomes)
	{
		this.viewSet = viewSet;
		this.compLine = compLine;
		this.currLine = currLine;
		this.chromosomes = chromosomes;
	}

	public Score getScore()
	{
		float nComparisons = 0;
		float score = 0;

		for (int viewIndex = 0; viewIndex < viewSet.chromosomeCount(); viewIndex++)
		{
			// Don't use chromosomes that aren't selected
			if(chromosomes[viewIndex] == false)
				continue;

			GTView view = viewSet.getView(viewIndex);
			view.cacheLines();

			// For every marker across the genotype...
			for (int marker = 0; marker < view.getMarkerCount(); marker++)
			{
				// Don't count markers that aren't selected
				if (view.isMarkerSelected(marker) == false)
					continue;

				int state1 = view.getState(compLine, marker);
				int state2 = view.getState(currLine, marker);

				// If either has no information, skip it
				if (state1 == 0 || state2 == 0)
					continue;

				// TODO: skip heterozy?

				// Increment the score if they match
				if (state1 == state2)
					score++;

				// And count it as a comparison, regardless
				nComparisons++;
			}
		}

		// TODO: a line with no data, will score 0/0 - problems?

		// The final score is the ratio of matches to comparable markers
		return new Score(score/nComparisons, nComparisons);
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