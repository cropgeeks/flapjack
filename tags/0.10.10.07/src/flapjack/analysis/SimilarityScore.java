// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.analysis;

import flapjack.data.*;

/**
 * Calculates a marker-based similarity score between two lines of data.
 */
public class SimilarityScore
{
	private GTViewSet viewSet;
	private StateTable st;
	private int compLine, currLine;
	private boolean[] chromosomes;

	/**
	 * @param compLine the comparison line to compare the current line against
	 * @param currLine the current line to compute a score for
	 */
	public SimilarityScore(GTViewSet viewSet, StateTable st, int compLine, int currLine, boolean[] chromosomes)
	{
		this.viewSet = viewSet;
		this.st = st;
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

				AlleleState s1 = st.getAlleleState(state1);
				AlleleState s2 = st.getAlleleState(state2);

				// Increment the score if they match
				if (state1 == state2)
					score += 1.0f;

				// Half-increment if, for example, A matches A/T
				else if (s1.matches(s2))
					score += 0.5f;

				// Count it as a comparison, regardless of match
				nComparisons++;
			}
		}

		if (nComparisons > 0)
			score = score / nComparisons;
		else
			score = 0;

		// The final score is the ratio of matches to comparable markers
		return new Score(score, nComparisons);
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