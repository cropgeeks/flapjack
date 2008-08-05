package flapjack.data;

/**
 * Represents a "bookmark" which tracks an intersection within the dataset
 * between a line and a marker (and therefore the allele at that position).
 */
public class Bookmark extends XMLRoot
{
	private LineInfo lineInfo;
	private MarkerInfo markerInfo;

	public Bookmark()
	{
	}

	public Bookmark(LineInfo lineInfo, MarkerInfo markerInfo)
	{
		this.lineInfo = lineInfo;
		this.markerInfo = markerInfo;
	}

	void validate()
		throws NullPointerException
	{
		if (lineInfo == null || markerInfo == null)
			throw new NullPointerException();
	}


	// Methods required for XML serialization

	public LineInfo getLineInfo()
		{ return lineInfo; }

	public void setLineInfo(LineInfo lineInfo)
		{ this.lineInfo = lineInfo; }

	public MarkerInfo getMarkerInfo()
		{ return markerInfo; }

	public void setMarkerInfo(MarkerInfo markerInfo)
		{ this.markerInfo = markerInfo; }


	// Other methods
}