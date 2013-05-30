// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.util.*;

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

	public SimilarityScore(GTViewSet viewSet, float[][] matrix, boolean[] chromosomes)
	{
		this.viewSet = viewSet;
		this.matrix = matrix;
		this.chromosomes = chromosomes;
	}

	/**
	 * @param compLine the comparison line to compare the current line against
	 * @param currLine the current line to compute a score for
	 */
	public Score getScore(int compLine, int currLine)
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
			int markerCount = view.markerCount();
			for (int marker = 0; marker < markerCount; marker++)
			{
				// Don't count markers that aren't selected
				if (view.isMarkerSelected(marker) == false)
					continue;

				int state1 = view.getState(compLine, marker);
				int state2 = view.getState(currLine, marker);

				// If either has no information, skip it
				if (state1 == 0 || state2 == 0)
					continue;

				score += matrix[state1][state2];

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

	public float getScore(ArrayList<GenotypeData> currLineData, ArrayList<GenotypeData> compLineData, ArrayList<int[]> mkrData)
	{
		float nComparisons = 0;
		float score = 0;

		for (int i = 0; i < currLineData.size(); i++)
		{
			GenotypeData currLine = currLineData.get(i);
			GenotypeData compLine = compLineData.get(i);
			int[] markers = mkrData.get(i);

			for (int j = 0; j < markers.length; j++)
			{
				int state1 = currLine.getState(markers[j]);
				int state2 = compLine.getState(markers[j]);

				// If either has no information, skip it
//				if (state1 == 0 || state2 == 0)
//					continue;

				score += matrix[state1][state2];

				// Count it as a comparison, regardless of match
				nComparisons++;
			}
		}

		if (nComparisons > 0)
			score = score / nComparisons;
		else
			score = 0;

		return score;
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