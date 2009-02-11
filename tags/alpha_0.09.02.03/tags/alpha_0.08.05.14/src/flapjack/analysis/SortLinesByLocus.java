package flapjack.analysis;

import java.util.*;

import flapjack.data.*;

public class SortLinesByLocus implements ILineSorter
{
	private GTView view;

	// The line/locus index position of the locus we want to sort by
	private int lineIndex;
	private int lociIndex;

	private int linesScored = 0;

	public SortLinesByLocus(GTView view, int lineIndex, int lociIndex)
	{
		this.view = view;
		this.lineIndex = lineIndex;
		this.lociIndex = lociIndex;
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


		// Work out what those scores are
		int comparisonState = view.getState(lineIndex, lociIndex);

		for (int i = 0; i < view.getLineCount(); i++, linesScored++)
		{
			int state = view.getState(i, lociIndex);

			int score = state;
			if (state == 0 || state == comparisonState)
				score = -1;

			scores.add(new LineScore(view.getLines().get(i), score));

			if (i < 10)
				System.out.println("Score for " + view.getLine(i).getName() + " is " + score + "(" + state + ")");
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
			if (score < other.score)
				return -1;
			else if (score == other.score)
				return 0;
			else
				return 1;
		}
	}
}