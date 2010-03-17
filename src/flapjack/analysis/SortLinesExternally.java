// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.io.*;
import java.util.*;

import flapjack.data.*;

public class SortLinesExternally extends SimpleJob
{
	private GTViewSet viewSet;
	private File file;

	public SortLinesExternally(GTViewSet viewSet, File file)
	{
		this.viewSet = viewSet;
		this.file = file;

		maximum = viewSet.getView(0).getLineCount();
	}

	public int getValue()
		{ return 0; }

	public void runJob(int jobIndex)
		throws Exception
	{
		// Access the first chromosome (just to get at the lines data)
		GTView view = viewSet.getView(0);

		// Create an array to hold the score for each line
		int numLines = view.getLineCount();
		ArrayList<LineScore> scores = new ArrayList<LineScore>(numLines);

		// Give every line an empty index before we start. This copes with
		// the case where the external ordering doesn't contain matching lines
		for (int i = 0; i < numLines; i++)
			scores.add(new LineScore(view.getLineInfo(i), numLines));


		BufferedReader in = new BufferedReader(new FileReader(file));
		String str = null;

		int index = 1;
		while ((str = in.readLine()) != null)
		{
			// Search for this line...
			for (int i = 0; i < scores.size(); i++)
			{
				if (scores.get(i).lineInfo.getLine().getName().equals(str))
				{
					scores.get(i).index = index++;
					break;
				}
			}
		}

		in.close();

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
		int index;

		LineScore(LineInfo lineInfo, int index)
		{
			this.lineInfo = lineInfo;
			this.index = index;
		}

		public int compareTo(LineScore other)
		{
			if (index < other.index)
				return -1;
			else if (index == other.index)
				return 0;
			else
				return 1;
		}
	}
}