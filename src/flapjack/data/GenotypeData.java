package flapjack.data;

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
		throws ArrayIndexOutOfBoundsException
	{
		return loci[index];
	}

	boolean isGenotypeDataForMap(ChromosomeMap map)
	{
		return this.map == map;
	}

	public int countLoci()
		{ return loci.length; }

	// Collapses all instances of s2 to have the same value of s1 (basically
	// overwrites all s2 values to be the same as s1)
	void collapseStates(int s1, int s2)
	{
		byte b1 = (byte) s1;
		byte b2 = (byte) s2;

		for (int i = 0; i < loci.length; i++)
			if (loci[i] == b2)
				loci[i] = b1;
	}
}