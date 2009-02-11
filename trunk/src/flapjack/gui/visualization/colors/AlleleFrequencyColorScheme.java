package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.*;

public class AlleleFrequencyColorScheme extends ColorScheme
{
	private Vector<ColorState> hiStates = new Vector<ColorState>();
	private Vector<ColorState> loStates = new Vector<ColorState>();

	private float thresholdFrequency;

	/** Empty constructor that is ONLY used for color customization purposes. */
	public AlleleFrequencyColorScheme() {}

	public AlleleFrequencyColorScheme(GTView view, int w, int h)
	{
		super(view);

		// Copy the threshold value
		thresholdFrequency = view.getViewSet().getAlleleFrequencyThreshold();

		// Ensure frequencies have been calculated for the markers
		DataSet dataSet = view.getViewSet().getDataSet();
		new CalculateMarkerFrequencies(dataSet).calculate();

		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);
			ColorState cHi = null;
			ColorState cLo = null;

			// Use white for the default unknown state
			if (state.isUnknown())
				cHi = cLo = new SimpleColorState(state, Prefs.visColorBackground, w, h);

			// Homozygous states
			else if (state.isHomozygous())
			{
				cHi = new HomozygousColorState(state, Prefs.visColorHiFreqState, w, h);
				cLo = new HomozygousColorState(state, Prefs.visColorLoFreqState, w, h);
			}

			// Heterozygous states
			else
			{
				cHi = new HeterozygeousColorState(state, Prefs.visColorHiFreqState, Prefs.visColorHiFreqState, Prefs.visColorHiFreqState, w, h);
				cLo = new HeterozygeousColorState(state, Prefs.visColorLoFreqState, Prefs.visColorLoFreqState, Prefs.visColorLoFreqState, w, h);
			}

			hiStates.add(cHi);
			loStates.add(cLo);
		}
	}

	private ColorState getState(int line, int marker)
	{
		int state = view.getState(line, marker);

		if (view.getMarker(marker).getFrequencies()[state] <= thresholdFrequency)
			return loStates.get(state);

		else
			return hiStates.get(state);
	}

	public BufferedImage getSelectedImage(int line, int marker)
		{ return getState(line, marker).getImage(); }

	public BufferedImage getUnselectedImage(int line, int marker)
		{ return getState(line, marker).getUnselectedImage(); }

	public Color getColor(int line, int marker)
		{ return getState(line, marker).getColor(); }

	public int getModel()
		{ return ALLELE_FREQUENCY; }

	public String toString()
		{ return RB.getString("gui.Actions.vizColorAlleleFreq"); }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.AlleleFrequencyColorScheme");
	}

	public Vector<ColorSummary> getColorSummaries()
	{
		Vector<ColorSummary> colors = new Vector<ColorSummary>();

		colors.add(new ColorSummary(Prefs.visColorLoFreqState,
			RB.getString("gui.visualization.colors.AlleleFrequencyColorScheme.loFreqState")));
		colors.add(new ColorSummary(Prefs.visColorHiFreqState,
			RB.getString("gui.visualization.colors.AlleleFrequencyColorScheme.hiFreqState")));

		return colors;
	}

	public void setColorSummaries(Vector<ColorSummary> colors)
	{
		Prefs.visColorLoFreqState = colors.get(0).color;
		Prefs.visColorHiFreqState = colors.get(1).color;
	}
}