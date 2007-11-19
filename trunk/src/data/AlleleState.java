package flapjack.data;

public class AlleleState
{
	private String[] states;
	private boolean isHomozygous = true;

	// TODO: Store color data
	// private Color color;

	public AlleleState()
	{
		states = new String[] { "UNKNOWN" };
	}

	public AlleleState(String[] states)
	{
		this.states = states;

		if (states.length > 1)
			isHomozygous = false;
	}

	public boolean isHomozygous()
		{ return isHomozygous; }

	public String toString()
	{
		String str = states[0];

		for (int i = 1; i < states.length; i++)
			str += "/" + states[i];

		return str;
	}

	/**
	 * Returns true if this allele state contains the same information as the
	 * other allele state.
	 */
	boolean matchesAlleleState(AlleleState code)
	{
		if (states.length != code.states.length)
			return false;

		for (int i = 0; i < states.length; i++)
			if (states[i].equals(code.states[i]) == false)
				return false;

		return true;
	}
}