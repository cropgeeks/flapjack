package flapjack.data;

import java.text.*;
import java.util.*;

public class Trait extends XMLRoot
{
	private static NumberFormat nf = NumberFormat.getInstance();

	private String name;
	private Vector<String> categories = new Vector<String>();

	// Used while importing to track the highest and lowest values assignd to
	// any of the trait values associated with this trait
	float min = Float.MAX_VALUE;
	float max = Float.MIN_VALUE;

	public Trait()
	{
	}

	void validate()
		throws NullPointerException
	{
		if (name == null)
			throw new NullPointerException();
	}

	public Trait(String name)
	{
		this.name = name;
	}

	public String toString()
		{ return name; }

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public Vector<String> getCategories()
		{ return categories; }

	public void setCategories(Vector<String> categories)
		{ this.categories = categories; }


	/**
	 * Parses an input string to determine if it is a numerical or categorical
	 * value. Numerical values are returned as is, categorical are first checked
	 * against the table of known inputs and assigned a lookup value (which is
	 * then returned).
	 */
	public float computeValue(String token)
	{
		float value = getNumCatValue(token);

		if (value < min)
			min = value;
		if (value > max)
			max = value;

		System.out.println(name + ": min now: " + min);
		System.out.println(name + ": max now: " + max);

		return value;
	}

	private float getNumCatValue(String token)
	{
		try
		{
			// If it's a number, it should parse and can be returned
			return nf.parse(token).floatValue();
		}
		catch (Exception e) {}

		// If not, then we need to check (and/or create) a category for it
		for (int i = 0; i < categories.size(); i++)
			// If it exists, then return the index of it
			if (categories.get(i).equals(token))
				return i;

		// Otherwise, add it to the end of the list and return that index
		categories.add(token);
		return categories.size()-1;
	}

	public boolean traitIsNumerical()
		{ return categories.size() == 0; }

	/**
	 * Returns a string suitable for display based on the categorical value
	 * held by the passed trait value.
	 */
	public String format(TraitValue tv)
	{
		int index = (int) tv.getValue();

		try { return categories.get(index); }
		catch (ArrayIndexOutOfBoundsException e)
		{
			return "UNDEFINED";
		}
	}
}