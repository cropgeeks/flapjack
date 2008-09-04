package flapjack.data;

public class Marker extends XMLRoot implements Comparable<Marker>
{
	private String name;
	private float position;
	private int dbKey;

	// (Might) hold summary information on the frequency of each allele state
	// for this marker
	private float[] frequencies;

	public Marker()
	{
	}

	public Marker(String name, float position)
	{
		this.name = new String(name);
		this.position = position;
	}

	void validate()
		throws NullPointerException
	{
		if (name == null)
			throw new NullPointerException();
	}


	// Methods required for XML serialization

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public float getPosition()
		{ return position; }

	public void setPosition(float position)
		{ this.position = position; }

	public int getDbKey()
		{ return dbKey; }

	public void setDbKey(int dbKey)
		{ this.dbKey = dbKey; }

	public float[] getFrequencies()
		{ return frequencies; }

	public void setFrequencies(float[] frequencies)
		{ this.frequencies = frequencies; }


	// Other methods

	public String toString()
		{ return name; }

	public int compareTo(Marker marker)
	{
		if (marker.position > position)
			return -1;
		else if (marker.position == position)
			return 0;
		else
			return 1;
	}

	/**
	 * Returns true if allele frequency statistics have been calculated and are
	 * available for this marker.
	 */
	public boolean frequenciesAvailable()
	{
		return (frequencies != null);
	}
}