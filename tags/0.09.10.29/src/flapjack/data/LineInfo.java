// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.data;

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


	// Other methods

	public String toString()
		{ return line.toString(); }
}