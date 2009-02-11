package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;

public class RandomColorScheme extends ColorScheme
{
	private Vector<ColorState> states = new Vector<ColorState>();

	public RandomColorScheme(GTView view, int w, int h)
	{
		super(view);

		// Use white for the default unknown state
		AlleleState state = stateTable.getAlleleState(0);
		states.add(new SimpleColorState(state, Color.white, w, h));

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
}