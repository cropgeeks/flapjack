package flapjack.data;

import java.util.*;

public class GenotypeData
{
	// A reference to the chromsome this data applies to
	private ChromosomeMap map;

	private byte[] loci;

	public GenotypeData(ChromosomeMap map)
	{
		this.map = map;

		// How many markers do we need to hold data for?
		int size = map.countLoci();

		// Initialize the array to the correct size
		loci = new byte[size];
	}

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

	/**
	 * Returns the raw data array for quick access elsewhere (eg rendering).
	 */
	public byte[] getLociData()
		{ return loci; }

	public int countLoci()
		{ return loci.length; }
}