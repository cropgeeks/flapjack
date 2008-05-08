package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

public class RandomColorScheme extends ColorScheme
{
	private Vector<ColorState> states = new Vector<ColorState>();

	/** Empty constructor that is ONLY used for color customization purposes. */
	public RandomColorScheme() {}

	public RandomColorScheme(GTView view, int w, int h)
	{
		super(view);

		// Use white for the default unknown state
		AlleleState state = stateTable.getAlleleState(0);
		states.add(new SimpleColorState(state, Prefs.visColorBackground, w, h));

		// And random colors for everything else
		for (int i = 1; i < stateTable.size(); i++)
		{
			state = stateTable.getAlleleState(i);
			states.add(new HomozygousColorState(state, null, w, h));
		}
	}

	public BufferedImage getImage(int line, int marker)
	{
		int state = view.getState(line, marker);
		return states.get(state).getImage();
	}

	public Color getColor(int line, int marker)
	{
		int state = view.getState(line, marker);
		return states.get(state).getColor();
	}

	public int getModel()
		{ return RANDOM; }

	public String toString()
		{ return RB.getString("gui.Actions.vizColorRandom"); }

	public String getDescription()
	{
		return "This colour scheme applies entirely random colours to each "
			+ "allele state found within your data. The colours are randomized "
			+ "using a seed that is regenerated each time this scheme is "
			+ "applied to the data.";
	}

	public Vector<ColorSummary> getColorSummaries()
		{ return new Vector<ColorSummary>(); }

	public void setColorSummaries(Vector<ColorSummary> colors) {}
}