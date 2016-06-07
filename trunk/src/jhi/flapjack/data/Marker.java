// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

public class Marker extends XMLRoot implements Comparable<Marker>
{
	private String name;

	// The marker's position on the chromosome
	private double position;
	// And its "actual" position, if different (used by all-chromsome markers)
	private double realPosition;

	// (Might) hold summary information on the frequency of each allele state
	// for this marker. The String[] array holds the names of each allele state.
	private float[] frequencies;
	private static String[] alleles;

	public Marker()
	{
	}

	public Marker(String name, double position)
	{
		this.name = new String(name);
		this.position = position;

		realPosition = position;
	}

	public Marker(String name, double position, double realPosition)
	{
		this.name = new String(name);
		this.position = position;
		this.realPosition = realPosition;
	}

	public Marker(boolean dummy, double position)
	{
		this.name = "DUMMYMARKER";
		this.position = position;

		realPosition = -1000;
	}

	void validate()
		throws NullPointerException
	{
		if (name == null)
			throw new NullPointerException();

		// This copes with existing (pre 01/06/2010) projects that don't have
		// the realPosition variable set. DummyMarkers have a realPosition set
		// to -1000 which we want to leave as is. All others should be reset
		if (realPosition < 0 && realPosition != -1000)
			realPosition = position;
	}


	// Methods required for XML serialization

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public double getPosition()
		{ return position; }

	public void setPosition(double position)
		{ this.position = position; }

	public double getRealPosition()
		{ return realPosition; }

	public void setRealPosition(double realPosition)
		{ this.realPosition = realPosition; }



	// Other methods

	public String toString()
		{ return name; }

	public float[] frequencies()
		{ return frequencies; }

	public void setFrequencies(float[] frequencies)
		{ this.frequencies = frequencies; }

	public static String[] alleles()
		{ return alleles; }

	public static void setAlleles(String[] array)
		{ alleles = array; }

	public int compareTo(Marker marker)
	{
		if (marker.position > position)
			return -1;
		else if (marker.position == position)
			return 0;
		else
			return 1;
	}

	public boolean dummyMarker()
		{ return realPosition == -1000; }
}