package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;

public class NucleotideColorScheme extends ColorScheme
{
	private Vector<ColorState> states = new Vector<ColorState>();

	private static final Color COLOR_A = new Color(153, 255, 153);
	private static final Color COLOR_C = new Color(255, 204, 153);
	private static final Color COLOR_G = new Color(255, 153, 153);
	private static final Color COLOR_T = new Color(153, 153, 255);

	public NucleotideColorScheme(GTView view, int w, int h)
	{
		super(view);

		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);
			ColorState c = null;

			// Use white for the default unknown state
			if (state.isUnknown())
				c = new ColorState(state, Color.white, w, h);

			else if (state.getRawData().equals("A"))
				c = new ColorState(state, COLOR_A, w, h);
			else if (state.getRawData().equals("C"))
				c = new ColorState(state, COLOR_C, w, h);
			else if (state.getRawData().equals("G"))
				c = new ColorState(state, COLOR_G, w, h);
			else if (state.getRawData().equals("T"))
				c = new ColorState(state, COLOR_T, w, h);

			// Use a random color for any additional (unexpected) states
			else
				c = new ColorState(state, null, w, h);

			states.add(c);
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