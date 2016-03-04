// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;

import jhi.flapjack.data.*;

// TODO: this class doesn't appear to be working correctly
// Plus: technically, a greyscale colour scheme needs to be a GS version of
// whatever scheme best fits the data, not just the nucleotide stuff as this is
public class MarkerSimilarityGSColorScheme extends NucleotideColorScheme
{
	public MarkerSimilarityGSColorScheme(GTView view, int w, int h)
	{
		super(view, w, h);
	}

	public BufferedImage getImage(int line, int marker)
	{
		int state = view.getState(line, marker);
		int compState = view.getState(line, view.getComparisonMarkerIndex());

		if (state == compState)
			return states.get(state).getImage();
		else
			return states.get(state).getGreyScaleImage();
	}

	public Color getColor(int line, int marker)
	{
		int state = view.getState(line, marker);
		int compState = view.getState(line, view.getComparisonMarkerIndex());

		if (state == compState)
			return states.get(state).getColor();
		else
			return states.get(state).getGreyScaleColor();
	}
}