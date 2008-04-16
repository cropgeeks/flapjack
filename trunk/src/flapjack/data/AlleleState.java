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
	 * other allele state.
	 */
/*	boolean matchesAlleleState(String[] otherStates)
	{
		if (states.length != otherStates.length)
			return false;

		for (int i = 0; i < states.length; i++)
			if (states[i].equals(otherStates[i]) == false)
				return false;

		return true;
	}
*/

	boolean matchesAlleleState(String rawData)
	{
		return this.rawData.equals(rawData);
	}

	public boolean isUnknown()
		{ return rawData.equals(""); }
}