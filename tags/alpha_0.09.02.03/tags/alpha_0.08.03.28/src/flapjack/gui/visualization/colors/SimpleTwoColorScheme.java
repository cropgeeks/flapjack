package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;

public class SimpleTwoColorScheme extends ColorScheme
{
	protected Vector<ColorState> states = new Vector<ColorState>();

	private static final Color COLOR_A = new Color(255, 120, 120);
	private static final Color COLOR_B = new Color(120, 255, 120);

	public SimpleTwoColorScheme(GTView view, int w, int h)
	{
		super(view);

		// Only the first two homozygous states found will be assigned a color
		int homoCount = 0;

		// Only add colours for the first two states found
		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);

			Color color = (i == 1) ? COLOR_A : COLOR_B;

			// Use white for the default unknown state
			if (state.isUnknown())
				states.add(new SimpleColorState(state, Color.white, w, h));

			else if (homoCount < 2 && state.isHomozygous())
			{
				states.add(new HomozygousColorState(state, color, w, h));
				homoCount++;
			}

			// TODO: Use red for other states (of which there shouldn't be any?)
			else if (state.isHomozygous())
				states.add(new HomozygousColorState(state, Color.red, w, h));
			else
				states.add(new HeterozygeousColorState(state, Color.red, Color.red, Color.red, w, h));
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