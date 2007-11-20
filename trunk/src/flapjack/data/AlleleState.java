package flapjack.data;

import java.awt.*;

public class AlleleState
{
	private String[] states;
	private boolean isHomozygous = true;

	// TODO: Store color data
	// private Color color;

	private int r, g, b;
	private Color color;

	public AlleleState()
	{
		states = new String[] { "UNKNOWN" };

		r = g = b = 255;
		color = new Color(r, g, b);
	}

	public AlleleState(String[] states)
	{
		this.states = states;

		if (states.length > 1)
			isHomozygous = false;

		createColor();
	}

	private void createColor()
	{
		java.util.Random rnd = new java.util.Random();

		r = rnd.nextInt(255);
		g = rnd.nextInt(255);
		b = rnd.nextInt(255);

		color = new Color(r, g, b);
	}

	public Color getColor()
		{ return color; }

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