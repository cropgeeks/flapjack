package flapjack.data;

public class QTL extends Feature
{
	// QTL's exact position on the map (error margin will be min/max from parent)
	private float position;

	// LOD score, r^2, and magnitude of effect
	private float lod, r2, mag;

	private String trait;
	private String experiment;

	public QTL()
	{
	}

	public QTL(String name)
	{
		this.name = name;
	}

	public float getPosition()
		{ return position; }

	public void setPosition(float position)
		{ this.position = position; }

	public float getLod()
		{ return lod; }

	public void setLod(float lod)
		{ this.lod = lod; }

	public float getR2()
		{ return r2; }

	public void setR2(float r2)
		{ this.r2 = r2; }

	public float getMag()
		{ return mag; }

	public void setMag(float mag)
		{ this.mag = mag; }

	public String getTrait()
		{ return trait; }

	public void setTrait(String trait)
		{ this.trait = trait; }

	public String getExperiment()
		{ return experiment; }

	public void setExperiment(String experiment)
		{ this.experiment = experiment; }
}