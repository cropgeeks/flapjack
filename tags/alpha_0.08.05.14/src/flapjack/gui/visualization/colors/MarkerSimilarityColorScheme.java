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

	public BufferedImage getImage(int line, int marker)
	{
		int state = view.getState(line, marker);

		// If it's the index marker, return the darker version
		if (marker == view.getComparisonMarkerIndex())
			return aStatesDark.get(state).getImage();

		// Otherwise do the comparison
		int compState = view.getState(line, view.getComparisonMarkerIndex());

		if (state == compState)
			return aStates.get(state).getImage();
		else
			return bStates.get(state).getImage();
	}

	public Color getColor(int line, int marker)
	{
		int state = view.getState(line, marker);

		// If it's the index marker, return the darker version
		if (marker == view.getComparisonMarkerIndex())
			return Prefs.visColorSimilarityState1Dark;

		// Otherwise do the comparison
		int compState = view.getState(line, view.getComparisonMarkerIndex());

		if (state == 0)
			return aStates.get(0).getColor();
		if (state == compState)
			return Prefs.visColorSimilarityState1;
		else
			return Prefs.visColorSimilarityState2;
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