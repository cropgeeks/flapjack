// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.io.*;
import java.util.*;

import flapjack.data.*;

public class SortLinesExternally extends SortLines
{
	private File file;

	public SortLinesExternally(GTViewSet viewSet, File file)
	{
		super(viewSet);

		this.file = file;
	}

	@Override
	protected ArrayList<LineInfo> doSort(GTView view)
	{
		int numLines = view.lineCount();
		ArrayList<LineScore> scores = new ArrayList<>(numLines);
		// Give every line an empty index before we start. This copes with
		// the case where the external ordering doesn't contain matching lines
		for (int i = 0; i < numLines && okToRun; i++)
			scores.add(new LineScore(view.getLineInfo(i), numLines));

		try
		{
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str = null;

			int index = 1;
			while ((str = in.readLine()) != null)
			{
				// Search for this line...
				for (int i = 0; i < scores.size() && okToRun; i++, linesScored++)
				{
					if (scores.get(i).lineInfo.getLine().getName().equals(str))
					{
						scores.get(i).index = index++;
						break;
					}
				}
			}

			in.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		// Now sort the array based on those scores
		Collections.sort(scores);

		// Then create a new line ordering for the view
		ArrayList<LineInfo> lineOrder = new ArrayList<>(numLines);
		for (int i = 0; i < scores.size() && okToRun; i++)
			lineOrder.add(scores.get(i).lineInfo);

		return lineOrder;
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