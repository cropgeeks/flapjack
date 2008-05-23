package flapjack.data;

/**
 * Wrapper class that represents a line within a view. We store a reference to
 * the line itself, along with its index position in the original dataset.
 */
public class LineInfo extends XMLRoot
{
	// A reference to the marker, and to its index within the original data
	Line line;
	int index;

	boolean selected = true;

	public LineInfo()
	{
	}

	LineInfo(Line line, int index)
	{
		this.line = line;
		this.index = index;
	}

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
}