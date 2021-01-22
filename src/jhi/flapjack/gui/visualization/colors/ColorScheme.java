// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

/**
 * Abstract base class for all color schemes.
 */
public abstract class ColorScheme
{
	public static final int NUCLEOTIDE = 1;
	public static final int LINE_SIMILARITY = 2;
//	public static final int LINE_SIMILARITY_GS = 3;  This functionality was removed on 09/05/2016 after being disabled on 18/04/2008
	public static final int MARKER_SIMILARITY = 4;
//	public static final int MARKER_SIMILARITY_GS = 5; This functionality was removed on 09/05/2016 after being disabled on 18/04/2008
	public static final int SIMPLE_TWO_COLOR = 6;
	public static final int ALLELE_FREQUENCY = 7;
	public static final int ABH_DATA = 8;
	public static final int BINNED_10 = 9;
	public static final int NUCLEOTIDE01 = 10;
	public static final int MAGIC = 11;
	public static final int LINE_SIMILARITY_EXACT_MATCH = 12;
	public static final int SIMILARITY_TO_EACH_PARENT = 13;
	public static final int LINE_SIMILARITY_ANY_MATCH = 14;
	public static final int SIMILARITY_TO_EITHER_PARENT = 15;
	public static final int FAV_ALLELE = 16;

	// Random (HSB model)
	public static final int RANDOM = 50;
	// Random (Websafe Palette model)
	public static final int RANDOM_WSP = 51;

	protected GTView view;
	protected StateTable stateTable;

	ColorScheme()
	{
	}

	ColorScheme(GTView view)
	{
		this.view = view;
		stateTable = view.getViewSet().getDataSet().getStateTable();
	}

	public abstract BufferedImage getSelectedImage(int line, int marker, boolean underQTL);

	public abstract BufferedImage getUnselectedImage(int line, int marker, boolean underQTL);

	public abstract Color getColor(int line, int marker);

	public abstract int getModel();

	public abstract ArrayList<ColorSummary> getColorSummaries();

	public abstract void setColorSummaries(ArrayList<ColorSummary> colors);

	public abstract String getDescription();

	/**
	 * Returns the list of colors that are non-specific to a scheme, that is,
	 * all colors in use elsewhere (backgrounds, highlights, etc).
	 */
	public static ArrayList<ColorSummary> getStandardColorSummaries()
	{
		ArrayList<ColorSummary> colors = new ArrayList<>();

		colors.add(new ColorSummary(Prefs.visColorBackground,
			RB.getString("gui.visualization.colors.ColorScheme.background")));
		colors.add(new ColorSummary(Prefs.visColorOverviewOutline,
			RB.getString("gui.visualization.colors.ColorScheme.overviewOutline")));
		colors.add(new ColorSummary(Prefs.visColorOverviewFill,
			RB.getString("gui.visualization.colors.ColorScheme.overviewFill")));
		colors.add(new ColorSummary(Prefs.visColorText,
			RB.getString("gui.visualization.colors.ColorScheme.canvasText")));
		colors.add(new ColorSummary(Prefs.visColorHeatmapHigh,
			RB.getString("gui.visualization.colors.ColorScheme.heatmapHigh")));
		colors.add(new ColorSummary(Prefs.visColorHeatmapLow,
			RB.getString("gui.visualization.colors.ColorScheme.heatmapLow")));
		colors.add(new ColorSummary(Prefs.visColorHetsAsH,
			RB.getString("gui.visualization.colors.ColorScheme.visColorHetsAsH")));

		return colors;
	}

	public static void setStandardColorSummaries(ArrayList<ColorSummary> colors)
	{
		Prefs.visColorBackground = colors.get(0).color;
		Prefs.visColorOverviewOutline = colors.get(1).color;
		Prefs.visColorOverviewFill = colors.get(2).color;
		Prefs.visColorText = colors.get(3).color;
		Prefs.visColorHeatmapHigh = colors.get(4).color;
		Prefs.visColorHeatmapLow = colors.get(5).color;
		Prefs.visColorHetsAsH = colors.get(6).color;
	}

	public static class ColorSummary
	{
		public Color color;
		public String name;

		ColorSummary(Color color, String name)
		{
			this.color = color;
			this.name = name;
		}

		public String toString()
		{
			return name;
		}
	}
}