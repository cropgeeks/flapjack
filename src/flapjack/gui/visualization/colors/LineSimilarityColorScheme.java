package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

public class LineSimilarityColorScheme extends SimilarityColorScheme
{
	/** Empty constructor that is ONLY used for color customization purposes. */
	public LineSimilarityColorScheme() {}

	public LineSimilarityColorScheme(GTView view, int w, int h)
	{
		super(view, w, h);
	}

	private ColorState getState(int line, int marker)
	{
		int state = view.getState(line, marker);
		int comparisonIndex = view.getComparisonLineIndex();

		// If it's the index line, return the darker version
		if (line == comparisonIndex)
			return aStatesDark.get(state);

		// Try to do the comparison
		if (comparisonIndex != -1)
			if (state == view.getState(comparisonIndex, marker))
				return aStates.get(state);

		// If it's not the same, or we can't do a comparison...
		return bStates.get(state);
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
		{ return LINE_SIMILARITY; }

	public String toString()
		{ return RB.getString("gui.Actions.vizColorLineSim"); }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.LineSimilarityColorScheme");
	}

	public Vector<ColorSummary> getColorSummaries()
	{
		Vector<ColorSummary> colors = new Vector<ColorSummary>();

		colors.add(new ColorSummary(Prefs.visColorSimilarityState1Dark,
			RB.getString("gui.visualization.colors.LineSimilarityColorScheme.state1Dark")));
		colors.add(new ColorSummary(Prefs.visColorSimilarityState1,
			RB.getString("gui.visualization.colors.LineSimilarityColorScheme.state1")));
		colors.add(new ColorSummary(Prefs.visColorSimilarityState2,
			RB.getString("gui.visualization.colors.LineSimilarityColorScheme.state2")));

		return colors;
	}
}