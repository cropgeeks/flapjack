package flapjack.data;

public class AlleleState extends XMLRoot
{
	private String[] states;
	private String rawData;

	private boolean isHomozygous = true;

	public AlleleState()
	{
	}

	public AlleleState(String rawData, String ioHeteroString)
	{
		this.rawData = new String(rawData.toUpperCase());

		states = rawData.split(ioHeteroString);

		if (states.length > 1)
			isHomozygous = false;
	}

	void validate()
		throws NullPointerException
	{
		if (states == null || rawData == null)
			throw new NullPointerException();
	}


	// Methods required for XML serialization

	public String[] getStates()
		{ return states; }

	public void setStates(String[] states)
		{ this.states = states; }

	public String getRawData()
		{ return rawData; }

	public void setRawData(String rawData)
		{ this.rawData = rawData; }

	public boolean isHomozygous()
		{ return isHomozygous; }

	public void setHomozygous(boolean isHomozygous)
		{ this.isHomozygous = isHomozygous; }


	// Other methods

	public String toString()
	{
		return rawData;
	}

	public String getState(int index)
	{
		return states[index];
	}

	/**
	 * Returns true if this allele state contains the same information as the
	 * other allele state. (eg, A/A/T has 2 As and 1 T and should match A/T/A
	 * or T/A/A).
	 */
	public boolean matches(AlleleState other)
	{
		String[] otherStates = other.states;

		if (states.length != otherStates.length)
			return false;

		// Count the number of times each allele appears and compare that count
		// between the two AlleleState objects
		for (int i = 0; i < states.length; i++)
			if (countState(states[i]) == other.countState(states[i]) == false)
				return false;

		return true;
	}

	boolean matchesAlleleState(String rawData)
	{
		return this.rawData.equalsIgnoreCase(rawData);
	}

	public boolean isUnknown()
		{ return rawData.equals(""); }

	// Returns a count of the number of times this allele appears in this data
	// (eg, will return 2 for A/A/T on a search of A)
	private int countState(String allele)
	{
		int count = 0;
		for (String s: states)
			if (s.equals(allele))
				count++;

		return count;
	}
}