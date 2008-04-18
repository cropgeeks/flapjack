package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;

import flapjack.data.*;

public class MarkerSimilarityColorScheme extends SimilarityColorScheme
{
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
			return COLOR_A_DRK;

		// Otherwise do the comparison
		int compState = view.getState(line, view.getComparisonMarkerIndex());

		if (state == 0)
			return aStates.get(0).getColor();
		if (state == compState)
			return COLOR_A;
		else
			return COLOR_B;
	}
}