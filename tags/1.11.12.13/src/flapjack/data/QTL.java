// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.data;

import java.awt.*;

public class QTL extends XMLRoot implements Comparable<QTL>
{
	// Variables originally inherited from Feature.java
	protected String name;
	protected float min, max;

	// Display-related variables
	protected boolean isVisible = true;
	protected boolean isAllowed = true;
	protected int red, green, blue;



	// QTL's exact position on the map (error margin will be min/max from parent)
	private float position;

	// A reference to the chromosome it is on
	private ChromosomeMap chromosome;

	// Names and values (to be used for things like LOD, r^2, etc)
	private String[] vNames = new String[0];
	private String[] values = new String[0];

	private String trait;
	private String experiment;

	public QTL()
	{
	}

	public QTL(String name)
	{
		this.name = name;
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

	public boolean isVisible()
		{ return isVisible; }

	public void setVisible(boolean isVisible)
		{ this.isVisible = isVisible; }

	public boolean isAllowed()
		{ return isAllowed; }

	public void setAllowed(boolean isAllowed)
		{ this.isAllowed = isAllowed; }

	public int getRed()
		{ return red; }

	public void setRed(int red)
		{ this.red = red; }

	public int getGreen()
		{ return green; }

	public void setGreen(int green)
		{ this.green = green; }

	public int getBlue()
		{ return blue; }

	public void setBlue(int blue)
		{ this.blue = blue; }

	public float getPosition()
		{ return position; }

	public void setPosition(float position)
		{ this.position = position; }

	public ChromosomeMap getChromosomeMap()
		{ return chromosome; }

	public void setChromosomeMap(ChromosomeMap chromosome)
		{ this.chromosome = chromosome; }

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


	// Other methods

	public int compareTo(QTL f)
	{
		if (min < f.min)
			return -1;
		else if (min == f.min)
			return 0;
		else
			return 1;
	}

	public Color getDisplayColor()
		{ return new Color(red, green, blue); }

	private void setColor(Color color)
	{
		red   = color.getRed();
		green = color.getGreen();
		blue  = color.getBlue();
	}

	// This is called by the import code that will have worked out a colour for
	// this feature based on (in a QTL's case) its trait.
	public void setDisplayColor(Color color)
	{
		setColor(color);

		for (int i = 0; i < vNames.length; i++)
		{
			if (vNames[i].toUpperCase().equals("RGB"))
			{
				try
				{
					setColor(Color.decode(values[i]));
				}
				catch (Exception e) {}

				break;
			}
		}
	}
}