// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.analysis;

import flapjack.data.*;

/**
 * Calculates a marker-based similarity score between two lines of data.
 */
public class SimilarityScore
{
	private GTViewSet viewSet;
	private float[][] matrix;
	private int compLine, currLine;
	private boolean[] chromosomes;

	/**
	 * @param compLine the comparison line to compare the current line against
	 * @param currLine the current line to compute a score for
	 */
	public SimilarityScore(GTViewSet viewSet, float[][] matrix, int compLine, int currLine, boolean[] chromosomes)
	{
		this.viewSet = viewSet;
		this.matrix = matrix;
		this.compLine = compLine;
		this.currLine = currLine;
		this.chromosomes = chromosomes;
	}

	public Score getScore()
	{
		float nComparisons = 0;
		float score = 0;
		StringBuilder data = new StringBuilder();

		for (int viewIndex = 0; viewIndex < viewSet.chromosomeCount(); viewIndex++)
		{
			// Don't use chromosomes that aren't selected
			if(chromosomes[viewIndex] == false)
				continue;

			GTView view = viewSet.getView(viewIndex);

			// For every marker across the genotype...
			for (int marker = 0; marker < view.markerCount(); marker++)
			{
				// Don't count markers that aren't selected
				if (view.isMarkerSelected(marker) == false)
					continue;

				int state1 = view.getState(compLine, marker);
				int state2 = view.getState(currLine, marker);

				score += matrix[state1][state2];

				// If either has no information, skip it
				if (state1 == 0 || state2 == 0)
					continue;

				// Count it as a comparison, regardless of match
				nComparisons++;

				data.append(state2);
			}
		}

		if (nComparisons > 0)
			score = score / nComparisons;
		else
			score = 0;

		// The final score is the ratio of matches to comparable markers
		return new Score(score, nComparisons, data.toString());
	}

	static class Score
	{
		float score;
		float nComparisons;
		String data;

		Score(float score, float nComparisons, String data)
		{
			this.score = score;
			this.nComparisons = nComparisons;
			this.data = data;
		}
	}
}