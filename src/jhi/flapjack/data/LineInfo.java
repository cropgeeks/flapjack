// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

/**
 * Wrapper class that represents a line within a view. We store a reference to
 * the line itself, along with its index position in the original dataset.
 */
public class LineInfo extends XMLRoot
{
	// A reference to the line, and to its index within the original data
	Line line;
	int index;

	// Is this line selected or not
	boolean selected = true;
	// Is this line a duplicate of a 'real' line or not
	boolean duplicate = false;

	// A 'score' associated with this line (probably from a sort/rank)
	float score;

	public LineInfo()
	{
	}

	LineInfo(Line line, int index)
	{
		this.line = line;
		this.index = index;
	}

	// Copy constructor to be used for creating deep copies of line infos.
	LineInfo(LineInfo lineInfo)
	{
		this.line = lineInfo.line;
		this.index = lineInfo.index;
		this.selected = lineInfo.selected;
		this.duplicate = lineInfo.duplicate;
		this.score = lineInfo.score;
	}


	// Methods required for XML serialization

	public Line getLine()
		{ return line; }

	public void setLine(Line line)
		{ this.line = line; }

	public int getIndex()
		{ return index; }

	public void setIndex(int index)
		{ this.index = index; }

	public boolean getSelected()
		{ return selected; }

	public void setSelected(boolean selected)
		{ this.selected = selected; }

	public float getScore()
		{ return score; }

	public void setScore(float score)
		{ this.score = score; }

	public boolean getDuplicate()
		{ return duplicate; }

	public void setDuplicate(boolean duplicate)
		{ this.duplicate = duplicate; }


	// Other methods

	public String toString()
	{
		if (!duplicate)
			return line.toString();
		else
			return line.toString() + "*";
	}

	public String name()
	{
		return toString();
	}

	LineInfo makeDuplicate()
	{
		LineInfo duplicate = new LineInfo(this);
		duplicate.duplicate = true;

		return duplicate;
	}
}