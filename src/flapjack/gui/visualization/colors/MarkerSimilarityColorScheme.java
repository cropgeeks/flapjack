package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

public class MarkerSimilarityColorScheme extends SimilarityColorScheme
{
	/** Empty constructor that is ONLY used for color customization purposes. */
	public MarkerSimilarityColorScheme() {}

	public MarkerSimilarityColorScheme(GTView view, int w, int h)
	{
		super(view, w, h);
	}

	public ColorState getState(int line, int marker)
	{
		int state = view.getState(line, marker);
		int comparisonIndex = view.getComparisonMarkerIndex();

		// If it's the index marker, return the darker version
		if (marker == comparisonIndex)
			return compStates.get(state);

		// Try to do the comparison
		if (comparisonIndex != -1)
		{
			int compState = view.getState(line, comparisonIndex);

			switch (lookupTable[state][compState])
			{
				case 1: return mtchStatesY.get(state);
				case 2: return mtchStatesN.get(state);
				case 3: return het1States.get(state);
				case 4: return het2States.get(state);
			}
		}

		// If it's not the same, or we can't do a comparison...
		return mtchStatesN.get(state);
	}

	public BufferedImage getSelectedImage(int line, int marker)
	{
		return getState(line, marker).getImage();
	}

	public BufferedImage getUnselectedImage(int line, int marker)
	{
		return getState(line, marker).getUnselectedImage();
	}

	public Color getColor(int line, int marker)
	{
		return getState(line, marker).getColor();
	}

	public int getModel()
		{ return MARKER_SIMILARITY; }

	public String toString()
		{ return RB.getString("gui.Actions.vizColorMarkerSim"); }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.MarkerSimilarityColorScheme");
	}

	public Vector<ColorSummary> getColorSummaries()
	{
		Vector<ColorSummary> colors = new Vector<ColorSummary>();

		colors.add(new ColorSummary(Prefs.visColorSimilarityState1Dark,
			RB.getString("gui.visualization.colors.MarkerSimilarityColorScheme.state1Dark")));
		colors.add(new ColorSummary(Prefs.visColorSimilarityState1,
			RB.getString("gui.visualization.colors.MarkerSimilarityColorScheme.state1")));
		colors.add(new ColorSummary(Prefs.visColorSimilarityState2,
			RB.getString("gui.visualization.colors.MarkerSimilarityColorScheme.state2")));

		return colors;
	}
}