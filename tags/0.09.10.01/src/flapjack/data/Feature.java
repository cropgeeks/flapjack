// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.data;

import java.awt.Color;
import java.util.*;

public abstract class Feature extends XMLRoot implements Comparable<Feature>
{
	protected int dbKey;

	protected String name;
	protected float min, max;

	// Display-related variables
	protected boolean isVisible = true;
	protected boolean isAllowed = true;
	protected int red, green, blue;

	public Feature()
	{
	}


	// Methods required for XML serialization
	public int getDbKey()
		{ return dbKey; }

	public void setDbKey(int dbKey)
		{ this.dbKey = dbKey; }

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

	public Color getDisplayColor()
		{ return new Color(red, green, blue); }

	public void setDisplayColor(Color color)
	{
		red   = color.getRed();
		green = color.getGreen();
		blue  = color.getBlue();
	}
}