// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.util.*;

import flapjack.data.*;

public class SortLinesAlphabetically implements ILineSorter
{
	private GTViewSet viewSet;

	public SortLinesAlphabetically(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
	}

	public int getMaximum()
		{ return viewSet.getView(0).getLineCount(); }

	public int getValue()
		{ return 0; }

	public void doSort()
	{
		// Access the first chromosome (just to get at the lines data)
		GTView view = viewSet.getView(0);

		// Create an array to hold the score for each line
		int numLines = view.getLineCount();
		ArrayList<LineScore> scores = new ArrayList<LineScore>(numLines);

		// Work out what those scores are
		for (int i = 0; i < numLines; i++)
		{
			LineInfo line = view.getLineInfo(i);
			scores.add(new LineScore(line));
		}

		// Now sort the array based on those scores
		Collections.sort(scores);

		// Then create a new line ordering for the view
		LineInfo[] lineOrder = new LineInfo[scores.size()];
		for (int i = 0; i < scores.size(); i++)
			lineOrder[i] = scores.get(i).lineInfo;

		// And pass that order back to the view
		view.getViewSet().setLinesFromArray(lineOrder, true);
	}

	private class LineScore implements Comparable<LineScore>
	{
		LineInfo lineInfo;

		LineScore(LineInfo lineInfo)
		{
			this.lineInfo = lineInfo;
		}

		public int compareTo(LineScore other)
		{
			return  lineInfo.getLine().getName().compareToIgnoreCase(
				other.lineInfo.getLine().getName());
		}
	}
}