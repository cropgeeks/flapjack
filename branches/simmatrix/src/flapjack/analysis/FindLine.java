// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.util.*;

import flapjack.data.*;

public class FindLine extends StringFinder
{
	private GTView view;

	public FindLine(GTView view, boolean matchCase, boolean useRegex)
	{
		super(matchCase, useRegex);

		this.view = view;
	}

	public LinkedList<Result> search(String str)
	{
		LinkedList<Result> results = new LinkedList<Result>();

		// Loop over all lines, looking for matches...
		for (int index = 0; index < view.lineCount(); index++)
		{
			LineInfo line = view.getLineInfo(index);

			// Don't match on dummy, or splitter lines
			if (view.isDummyLine(index) || view.isSplitter(index))
				continue;

			if (matches(line.getLine().getName(), str))
				results.add(new Result(line));
		}

		return results;
	}

	public static class Result
	{
		public LineInfo line;

		Result(LineInfo line)
		{
			this.line = line;
		}
	}
}