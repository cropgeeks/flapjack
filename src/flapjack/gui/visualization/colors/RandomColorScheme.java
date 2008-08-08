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

		int seed = view.getViewSet().getRandomColorSeed();

		// And random colors for everything else
		for (int i = 1; i < stateTable.size(); i++)
		{
			state = stateTable.getAlleleState(i);

			if (state.isHomozygous())
			{
				Color color = createRandomColor(state.toString(), seed);
				states.add(new HomozygousColorState(state, color, w, h));
			}
			else
			{
				Color c1 = createRandomColor(state.getState(0), seed);
				Color c2 = createRandomColor(state.getState(1), seed);

				states.add(new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, c1, c2, w, h));
			}
		}
	}

	protected Color createRandomColor(String str, int seed)
	{
		int value = 0;
		for (int i = 0; i < str.length(); i++)
			value += str.charAt(i);

		java.util.Random rnd = new java.util.Random(value+seed);

		int r = rnd.nextInt(255);
		int g = rnd.nextInt(255);
		int b = rnd.nextInt(255);

		return new Color(r, g, b);
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
		{ return RANDOM; }

	public String toString()
		{ return RB.getString("gui.Actions.vizColorRandom"); }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.RandomColorScheme");
	}

	public Vector<ColorSummary> getColorSummaries()
		{ return new Vector<ColorSummary>(); }

	public void setColorSummaries(Vector<ColorSummary> colors) {}
}