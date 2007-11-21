package flapjack.data;

import java.awt.*;

public class AlleleState
{
	private String[] states;
	private String rawData;

	private boolean isHomozygous = true;

	// TODO: Store color data
	// private Color color;

	private int r, g, b;
	private Color color;

	public AlleleState()
	{
		states = new String[] { "UNKNOWN" };
		rawData = "";

		r = g = b = 255;
		color = new Color(r, g, b);
	}

	public AlleleState(String rawData)
	{
		this.rawData = rawData;

		states = rawData.split("/");

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