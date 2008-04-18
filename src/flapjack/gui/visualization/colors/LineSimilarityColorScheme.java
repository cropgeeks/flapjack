package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;

import flapjack.data.*;

public class LineSimilarityColorScheme extends SimilarityColorScheme
{
	public LineSimilarityColorScheme(GTView view, int w, int h)
	{
		super(view, w, h);
	}

	public BufferedImage getImage(int line, int marker)
	{
		int state = view.getState(line, marker);

		// If it's the index line, return the darker version
		if (line == view.getComparisonLineIndex())
			return aStatesDark.get(state).getImage();

		// Otherwise do the comparison
		int compState = view.getState(view.getComparisonLineIndex(), marker);

		if (state == compState)
			return aStates.get(state).getImage();
		else
			return bStates.get(state).getImage();
	}

	public Color getColor(int line, int marker)
	{
		int state = view.getState(line, marker);

		// If it's the index line, return the darker version
		if (line == view.getComparisonLineIndex())
			return COLOR_A_DRK;

		// Otherwise do the comparison
		int compState = view.getState(view.getComparisonLineIndex(), marker);

		if (state == 0)
			return aStates.get(0).getColor();
		if (state == compState)
			return COLOR_A;
		else
			return COLOR_B;
	}
}