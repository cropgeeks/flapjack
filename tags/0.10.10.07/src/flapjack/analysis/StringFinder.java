// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

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
	protected boolean matchCase = false;
	protected boolean useRegex = false;

	StringFinder(boolean matchCase, boolean useRegex)
	{
		setMatchCase(matchCase);
		setUseRegex(useRegex);
	}

	public void setMatchCase(boolean matchCase)
		{ this.matchCase = matchCase; }

	public void setUseRegex(boolean useRegex)
		{ this.useRegex = useRegex; }

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