// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;

import flapjack.data.*;

public class LineSimilarityGSColorScheme extends NucleotideColorScheme
{
	public LineSimilarityGSColorScheme(GTView view, int w, int h)
	{
		super(view, w, h);
	}

	public BufferedImage getImage(int line, int marker)
	{
		int state = view.getState(line, marker);
		int compState = view.getState(view.getViewSet().getComparisonLineIndex(), marker);

		if (state == compState)
			return states.get(state).getImage();
		else
			return states.get(state).getGreyScaleImage();
	}

	public Color getColor(int line, int marker)
	{
		int state = view.getState(line, marker);
		int compState = view.getState(view.getViewSet().getComparisonLineIndex(), marker);

		if (state == compState)
			return states.get(state).getColor();
		else
			return states.get(state).getGreyScaleColor();
	}
}