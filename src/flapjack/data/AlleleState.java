package flapjack.data;

public class AlleleState
{
	private String[] states;
	private String rawData;

	private boolean isHomozygous = true;

	public AlleleState()
	{
		states = new String[] { "UNKNOWN" };
		rawData = "";
	}

	public AlleleState(String rawData)
	{
		this.rawData = new String(rawData);

		states = rawData.split("/");

		if (states.length > 1)
			isHomozygous = false;
	}

	public boolean isHomozygous()
		{ return isHomozygous; }

	public String toString()
	{
		return rawData;
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

}