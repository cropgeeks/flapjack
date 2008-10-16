package flapjack.data;

public abstract class Feature extends XMLRoot
{
	protected String name;
	protected float min, max;


	public Feature()
	{
	}

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
}