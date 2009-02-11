package flapjack.data;

import java.util.*;

public abstract class Feature extends XMLRoot implements Comparable<Feature>
{
	protected String name;
	protected float min, max;

	public Feature()
	{
	}


	// Methods required for XML serialization

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public float getMin()
		{ return min; }

	public void setMin(float min)
		{ this.min = min; }

	public float getMax()
		{ return max; }

	public void setMax(float max)
		{ this.max = max; }


	// Other methods

	public int compareTo(Feature f)
	{
		if (min < f.min)
			return -1;
		else if (min == f.min)
			return 0;
		else
			return 1;
	}
}