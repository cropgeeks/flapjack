package flapjack.data;

import java.util.*;

public class GenotypeData
{
	// A reference to the chromsome this data applies to
	private ChromosomeMap map;

	private short[] loci;

	public GenotypeData(ChromosomeMap map)
	{
		this.map = map;

		// How many markers do we need to hold data for?
		int size = map.countLoci();

		// Initialize the array to the correct size
		loci = new short[size];
	}

	void setLoci(int index, short stateCode)
	{
		loci[index] = stateCode;
	}

	short getState(int index)
	{
		return loci[index];
	}

	boolean isGenotypeDataForMap(ChromosomeMap map)
	{
		return this.map == map;
	}
}