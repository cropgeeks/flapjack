package flapjack.analysis;

import java.util.regex.*;

/**
 * Abstract base class for string finding that is used by FindLine and
 * FindMarker. Contains the basic code to start the search and the pattern
 * matching methods. The subclasses contain the actual code for deciding *how*
 * to search.
 */
public abstract class StringFinder
{
	protected boolean foundMatch = false;

	protected boolean findNext = true;
	protected boolean matchCase = false;
	protected boolean useRegex = false;

	StringFinder(boolean findNext, boolean matchCase, boolean useRegex)
	{
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
	 * Searches the data to find an object whose name matches the string,
	 * optionally matching using a regular expression string.
	 * @param str the string to search for
	 * @return the index of the next matching object, or -1 if no match is found
	 */
	public int getIndex(String str)
	{
		int indexToReturn = search(str);

		if (indexToReturn != -1)
			foundMatch = true;

		return indexToReturn;
	}

	protected abstract int search(String str);

	protected boolean matches(String data, String pattern)
	{
		if (useRegex == false)
		{
			if (matchCase)
				return data.equals(pattern);
			else
				return data.toLowerCase().equals(pattern.toLowerCase());
		}

		Pattern p = null;

		if (matchCase)
			// Enable standard (case sensitive) pattern matching
			p = Pattern.compile(pattern);
		else
			// Enable case insensitive (unicode) pattern matching
			p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

		Matcher m = p.matcher(data);
		return m.matches();
	}
}