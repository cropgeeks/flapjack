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
			int score = getScore(i, 0);
			scores.add(new LineScore(i, score));
		}

		// Now sort the array based on those scores
		Collections.sort(scores);

		for (int i = 0; i < scores.size(); i++)
			lines.set(i, scores.get(i).index);

		System.out.println("Similarity sort in " + (System.currentTimeMillis()-s) + "ms");
	}

	// Calculates a score for a line by giving it 1 point for each matching
	// allele state it has with the comparison line
	private int getScore(int lineIndex, int comparisonIndex)
	{
		int score = 0;

		for (int i = 0; i < view.getMarkerCount(); i++)
			if (view.getState(lineIndex, i) == view.getState(comparisonIndex, i))
				score++;

		return score;
	}

	private static class LineScore implements Comparable<LineScore>
	{
		int index;
		int score;

		LineScore(int index, int score)
		{
			this.index = index;
			this.score = score;
		}

		public int compareTo(LineScore other)
		{
			if (score > other.score)
				return -1;
			else if (score == other.score)
				return 0;
			else
				return 1;
		}
	}
}