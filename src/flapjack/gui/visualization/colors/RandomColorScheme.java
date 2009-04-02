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

		// Temp storage for the colors as we "invent" them
		Hashtable<String, Color> hashtable = new Hashtable<String, Color>();

		// Use white for the default unknown state
		AlleleState state = stateTable.getAlleleState(0);
		states.add(new SimpleColorState(state, Prefs.visColorBackground, w, h));

		int seed = view.getViewSet().getRandomColorSeed();

		float colorsNeeded = stateTable.calculateUniqueStateCount();
//		float colorSpacing = WebsafePalette.getColorCount() / colorsNeeded;
		float colorSpacing = 1 / colorsNeeded;

		float fColor = 1 / 50000f * seed;
//		fColor = seed;


		// And random colors for everything else
		for (int i = 1; i < stateTable.size(); i++)
		{
			state = stateTable.getAlleleState(i);

			if (state.isHomozygous())
			{
				Color color = hashtable.get(state.toString());
				if (color == null)
				{
					color = Color.getHSBColor(fColor, 0.5f, 1);
//					color = WebsafePalette.getColor((int)fColor);
					fColor += colorSpacing;

					hashtable.put(state.toString(), color);
				}

				states.add(new HomozygousColorState(state, color, w, h));
			}
			else
			{
				// Get the color for the first het half
				Color c1 = hashtable.get(state.getState(0));
				if (c1 == null)
				{
					c1 = Color.getHSBColor(fColor, 0.5f, 1);
//					c1 = WebsafePalette.getColor((int)fColor);
					fColor += colorSpacing;

					hashtable.put(state.getState(0), c1);
				}

				// Get the color for the second het half
				Color c2 = hashtable.get(state.getState(1));
				if (c2 == null)
				{
					c2 = Color.getHSBColor(fColor, 0.5f, 1);
//					c2 = WebsafePalette.getColor((int)fColor);
					fColor += colorSpacing;

					hashtable.put(state.getState(1), c2);
				}

				states.add(new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, c1, c2, w, h));
			}
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