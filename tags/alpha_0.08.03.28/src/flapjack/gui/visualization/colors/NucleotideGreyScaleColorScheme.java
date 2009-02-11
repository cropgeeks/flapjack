package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;

public class NucleotideGreyScaleColorScheme extends NucleotideColorScheme
{
	// The index of the line to compare all other lines with
	private int lineIndex;

	public NucleotideGreyScaleColorScheme(GTView view, int w, int h)
	{
		super(view, w, h);

		if (view.selectedLine != -1)
			lineIndex = view.selectedLine;
	}

	public BufferedImage getImage(int line, int marker)
	{
		int state = view.getState(line, marker);
		int compState = view.getState(lineIndex, marker);

		if (state == compState)
			return states.get(state).getImage();
		else
			return states.get(state).getGreyScaleImage();
	}

	public Color getColor(int line, int marker)
	{
		int state = view.getState(line, marker);
		int compState = view.getState(lineIndex, marker);

		if (state == compState)
			return states.get(state).getColor();
		else
			return states.get(state).getGreyScaleColor();
	}
}