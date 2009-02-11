package flapjack.data;

public class QTL extends Feature
{
	private float position;
	private float lod;

	public QTL()
	{
	}

	public QTL(String name, float position, float min, float max, float lod)
	{
		this.name = name;
		this.position = position;
		this.min = min;
		this.max = max;
		this.lod = lod;
	}

	public float getPosition()
		{ return position; }

	public void setPosition(float position)
		{ this.position = position; }

	public float getLod()
		{ return lod; }

	public void setLod(float lod)
		{ this.lod = lod; }
}