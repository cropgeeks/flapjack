package flapjack.data;

public class QTL extends XMLRoot
{
	private String name;

	private float position;
	private float rangeMin, rangeMax;
	private float lod;


	public QTL()
	{
	}

	public QTL(String name, float position, float rangeMin, float rangeMax, float lod)
	{
		this.name = name;
		this.position = position;
		this.rangeMin = rangeMin;
		this.rangeMax = rangeMax;
		this.lod = lod;
	}

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public float getPosition()
		{ return position; }

	public void setPosition(float position)
		{ this.position = position; }

	public float getRangeMin()
		{ return rangeMin; }

	public void setRangeMin(float rangeMin)
		{ this.rangeMin = rangeMin; }

	public float getRangeMax()
		{ return rangeMax; }

	public void setRangeMax(float rangeMax)
		{ this.rangeMax = rangeMax; }

	public float getLod()
		{ return lod; }

	public void setLod(float lod)
		{ this.lod = lod; }
}