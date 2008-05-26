package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

public class SimpleTwoColorScheme extends ColorScheme
{
	protected Vector<ColorState> states = new Vector<ColorState>();

	/** Empty constructor that is ONLY used for color customization purposes. */
	public SimpleTwoColorScheme() {}

	public SimpleTwoColorScheme(GTView view, int w, int h)
	{
		super(view);

		// Only the first two homozygous states found will be assigned a color
		int homoCount = 0;

		// Only add colours for the first two states found
		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);

			Color color = (i == 1) ? Prefs.visColorSimple2State1 : Prefs.visColorSimple2State2;

			// Use white for the default unknown state
			if (state.isUnknown())
				states.add(new SimpleColorState(state, Prefs.visColorBackground, w, h));

			else if (homoCount < 2 && state.isHomozygous())
			{
				states.add(new HomozygousColorState(state, color, w, h));
				homoCount++;
			}

			// TODO: Use red for other states (of which there shouldn't be any?)
			else if (state.isHomozygous())
				states.add(new HomozygousColorState(state, Prefs.visColorSimple2Other, w, h));
			else
				states.add(new HeterozygeousColorState(state, Prefs.visColorSimple2Other, Prefs.visColorSimple2Other, Prefs.visColorSimple2Other, w, h));
		}
	}

	public BufferedImage getSelectedImage(int line, int marker)
	{
		int state = view.getState(line, marker);
		return states.get(state).getImage();
	}

	public BufferedImage getUnselectedImage(int line, int marker)
	{
		int state = view.getState(line, marker);
		return states.get(state).getUnselectedImage();
	}

	public Color getColor(int line, int marker)
	{
		int state = view.getState(line, marker);
		return states.get(state).getColor();
	}

	public int getModel()
		{ return SIMPLE_TWO_COLOR; }

	public String toString()
		{ return RB.getString("gui.Actions.vizColorSimple2Color"); }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.SimpleTwoColorScheme");
	}

	public Vector<ColorSummary> getColorSummaries()
	{
		Vector<ColorSummary> colors = new Vector<ColorSummary>();

		colors.add(new ColorSummary(Prefs.visColorSimple2State1,
			RB.getString("gui.visualization.colors.SimpleTwoColorScheme.state1")));
		colors.add(new ColorSummary(Prefs.visColorSimple2State2,
			RB.getString("gui.visualization.colors.SimpleTwoColorScheme.state2")));
		colors.add(new ColorSummary(Prefs.visColorSimple2Other,
			RB.getString("gui.visualization.colors.SimpleTwoColorScheme.other")));

		return colors;
	}

	public void setColorSummaries(Vector<ColorSummary> colors)
	{
		Prefs.visColorSimple2State1 = colors.get(0).color;
		Prefs.visColorSimple2State2 = colors.get(1).color;
		Prefs.visColorSimple2Other = colors.get(2).color;
	}
}