// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.data;

/**
 * Represents a "bookmark" which tracks an intersection within the dataset
 * between a line and a marker (and therefore the allele at that position).
 */
public class Bookmark extends XMLRoot
{
	private ChromosomeMap chromosome;
	private Line line;
	private Marker marker;

	public Bookmark()
	{
	}

	public Bookmark(ChromosomeMap chromosome, Line line, Marker marker)
	{
		this.chromosome = chromosome;
		this.line = line;
		this.marker = marker;
	}

	void validate()
		throws NullPointerException
	{
		if (chromosome == null || line == null || marker == null)
			throw new NullPointerException();
	}


	// Methods required for XML serialization

	public ChromosomeMap getChromosome()
		{ return chromosome; }

	public void setChromosome(ChromosomeMap chromosome)
		{ this.chromosome = chromosome; }

	public Line getLine()
		{ return line; }

	public void setLine(Line line)
		{ this.line = line; }

	public Marker getMarker()
		{ return marker; }

	public void setMarker(Marker marker)
		{ this.marker = marker; }


	// Other methods

	public static boolean allowBookmarking(GTView view)
	{
		// Is the bookmark going to be over an allele?
		if (view.mouseOverLine < 0 || view.mouseOverLine >= view.getLineCount())
			return false;
		if (view.mouseOverMarker < 0 || view.mouseOverMarker >= view.getMarkerCount())
			return false;

		// Is the bookmark on a non-dummy marker?
		Marker marker = view.getMarker(view.mouseOverMarker);

		return (marker.dummyMarker() == false);
	}
}