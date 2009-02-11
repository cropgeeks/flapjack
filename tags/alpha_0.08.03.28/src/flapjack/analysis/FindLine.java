package flapjack.analysis;

import java.util.regex.*;

import flapjack.data.*;

public class FindLine extends StringFinder
{
	private GTView view;

	// Current line index within the view
	private int index = 0;

	public FindLine(GTView view, boolean findNext, boolean matchCase, boolean useRegex)
	{
		super(findNext, matchCase, useRegex);

		this.view = view;
	}

	protected int search(String str)
	{
		// Modify the starting index based on previous results
		if (foundMatch && findNext)
			index++;
		else if (foundMatch && !findNext)
			index--;

		// Maintain a count of the search. Once all lines have been looked at
		// it means we didn't find a match
		int searchCount = 0;

		while (searchCount < view.getLineCount())
		{
			// If we've reached the end of the data, reset to the start...
			if (index >= view.getLineCount())
				index = 0;
			// Or, if we're searching backwards and have reached the start...
			else if (index < 0)
				index = view.getLineCount()-1;

			Line line = view.getLine(index);
			if (matches(line.getName(), str))
				return index;

			searchCount++;

			// Move forward (or back) one index position
			if (findNext)
				index++;
			else
				index--;
		}

		return -1;
	}
}