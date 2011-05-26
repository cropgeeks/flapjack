package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

/**
 * Abstract base class for all color schemes.
 */
public abstract class ColorScheme
{
	public static final int NUCLEOTIDE = 1;
	public static final int LINE_SIMILARITY = 2;
	public static final int LINE_SIMILARITY_GS = 3;
	public static final int MARKER_SIMILARITY = 4;
	public static final int MARKER_SIMILARITY_GS = 5;
	public static final int SIMPLE_TWO_COLOR = 6;
	public static final int ALLELE_FREQUENCY = 7;

	public static final int RANDOM = 50;

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

	public abstract BufferedImage getSelectedImage(int line, int marker);

	public abstract BufferedImage getUnselectedImage(int line, int marker);

	public abstract Color getColor(int line, int marker);

	public abstract int getModel();

	public abstract Vector<ColorSummary> getColorSummaries();

	public abstract void setColorSummaries(Vector<ColorSummary> colors);

	public abstract String getDescription();

	/**
	 * Returns the list of colors that are non-specific to a scheme, that is,
	 * all colors in use elsewhere (backgrounds, highlights, etc).
	 */
	public static Vector<ColorSummary> getStandardColorSummaries()
	{
		Vector<ColorSummary> colors = new Vector<ColorSummary>();

		colors.add(new ColorSummary(Prefs.visColorBackground,
			RB.getString("gui.visualization.colors.ColorScheme.background")));
		colors.add(new ColorSummary(Prefs.visColorOverviewOutline,
			RB.getString("gui.visualization.colors.ColorScheme.overviewOutline")));
		colors.add(new ColorSummary(Prefs.visColorOverviewFill,
			RB.getString("gui.visualization.colors.ColorScheme.overviewFill")));
		colors.add(new ColorSummary(Prefs.visColorText,
			RB.getString("gui.visualization.colors.ColorScheme.canvasText")));

		return colors;
	}

	public static void setStandardColorSummaries(Vector<ColorSummary> colors)
	{
		Prefs.visColorBackground = colors.get(0).color;
		Prefs.visColorOverviewOutline = colors.get(1).color;
		Prefs.visColorOverviewFill = colors.get(2).color;
		Prefs.visColorText = colors.get(3).color;
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