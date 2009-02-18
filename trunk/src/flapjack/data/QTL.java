package flapjack.data;

public class QTL extends Feature
{
	// QTL's exact position on the map (error margin will be min/max from parent)
	private float position;

	// Names and values (to be used for things like LOD, r^2, etc)
	private String[] vNames;
	private String[] values;

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

	public String[] getVNames()
		{ return vNames; }

	public void setVNames(String[] vNames)
		{ this.vNames = vNames; }

	public String[] getValues()
		{ return values; }

	public void setValues(String[] values)
		{ this.values = values; }

	public String getTrait()
		{ return trait; }

	public void setTrait(String trait)
		{ this.trait = trait; }

	public String getExperiment()
		{ return experiment; }

	public void setExperiment(String experiment)
		{ this.experiment = experiment; }
}