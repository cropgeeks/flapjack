package flapjack.data;

import java.util.*;

public class GenotypeData extends XMLRoot
{
	// A reference to the chromsome this data applies to
	private ChromosomeMap map;

	private byte[] loci;

	public GenotypeData()
	{
	}

	public GenotypeData(ChromosomeMap map)
	{
		this.map = map;

		// How many markers do we need to hold data for?
		int size = map.countLoci();

		// Initialize the array to the correct size
		loci = new byte[size];
	}

	void validate()
		throws NullPointerException
	{
		if (map == null || loci == null)
			throw new NullPointerException();
	}


	// Methods required for XML serialization

	public ChromosomeMap getChromosomeMap()
		{ return map; }

	public void setChromosomeMap(ChromosomeMap map)
		{ this.map = map; }

	public byte[] getLoci()
		{ return loci; }

	public void setLoci(byte[] loci)
		{ this.loci = loci; }


	// Other methods

	void setLoci(int index, int stateCode)
	{
		loci[index] = (byte) stateCode;
	}

	public int getState(int index)
	{
		return loci[index];
	}

	boolean isGenotypeDataForMap(ChromosomeMap map)
	{
		return this.map == map;
	}

	public int countLoci()
		{ return loci.length; }
}