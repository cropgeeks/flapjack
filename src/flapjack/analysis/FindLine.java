package flapjack.analysis;

import java.util.*;
import java.util.regex.*;

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
		for (int index = 0; index < view.getLineCount(); index++)
		{
			Line line = view.getLine(index);

			if (matches(line.getName(), str))
				results.add(new Result(line));
		}

		return results;
	}

	public static class Result
	{
		public Line line;

		Result(Line line)
		{
			this.line = line;
		}
	}
}