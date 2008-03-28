package flapjack.analysis;

import java.util.regex.*;

import flapjack.data.*;

public class FindLine
{
	private GTView view;

	// Current index within the dataset
	private int index = 0;
	// Was anything successfully found?
	private boolean foundMatch = false;

	private boolean findNext = true;
	private boolean matchCase = false;
	private boolean useRegex = false;

	public FindLine(GTView view, boolean findNext, boolean matchCase, boolean useRegex)
	{
		this.view = view;

		setFindNext(findNext);
		setMatchCase(matchCase);
		setUseRegex(useRegex);
	}

	public void setFindNext(boolean findNext)
		{ this.findNext = findNext; }

	public void setMatchCase(boolean matchCase)
		{ this.matchCase = matchCase; }

	public void setUseRegex(boolean useRegex)
		{ this.useRegex = useRegex; }

	/**
	 * Searches through all the lines to find one whose name matches the string,
	 * optionally matching using a regular expression string.
	 * @param str the string to search for
	 * @return the index of the next matching line, or -1 if no match is found
	 */
	public int getIndex(String str)
	{
		int indexToReturn = search(str);

		if (indexToReturn != -1)
			foundMatch = true;

		return indexToReturn;
	}

	private int search(String str)
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
			if (matches(line, str))
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

	private boolean matches(Line line, String str)
	{
		if (useRegex == false)
		{
			if (matchCase)
				return line.getName().equals(str);
			else
				return line.getName().toLowerCase().equals(str.toLowerCase());
		}

		Pattern p = null;

		if (matchCase)
			// Enable standard (case sensitive) pattern matching
			p = Pattern.compile(str);
		else
			// Enable case insensitive (unicode) pattern matching
			p = Pattern.compile(str, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

		Matcher m = p.matcher(line.getName());
		return m.matches();
	}
}