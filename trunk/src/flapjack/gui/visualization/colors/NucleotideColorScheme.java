// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class NucleotideColorScheme extends ColorScheme
{
	protected ArrayList<ColorState> states = new ArrayList<>();

	/** Empty constructor that is ONLY used for color customization purposes. */
	public NucleotideColorScheme() {}

	public NucleotideColorScheme(GTView view, int w, int h)
	{
		super(view);

		// Set up the hash for mapping colours to alleles
		HashMap<String, Color> keys = new HashMap<>();
		keys.put("A", Prefs.visColorNucleotideA);
		keys.put("C", Prefs.visColorNucleotideC);
		keys.put("G", Prefs.visColorNucleotideG);
		keys.put("T", Prefs.visColorNucleotideT);

		buildStates(keys, w, h);
	}

	protected void buildStates(HashMap<String, Color> keys, int w, int h)
	{
		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);
			ColorState c = null;

			// Use white for the default unknown state
			if (state.isUnknown())
				c = new SimpleColorState(state, Prefs.visColorBackground, w, h);

			// Homozygous states
			else if (state.isHomozygous())
			{
				Color c1 = keys.get(state.getRawData());
				if (c1 == null)
					c1 = Prefs.visColorNucleotideOther;

				c = new HomozygousColorState(state, c1, w, h);
			}

			// Heterozygous states
			else
			{
				Color c1 = keys.get(state.getState(0));
				Color c2 = keys.get(state.getState(1));

				if (c1 == null)
					c1 = Prefs.visColorNucleotideOther;
				if (c2 == null)
					c2 = Prefs.visColorNucleotideOther;

				c = new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, c1, c2, w, h);
			}

			states.add(c);
		}
	}

	public BufferedImage getSelectedImage(int line, int marker)
	{
		int state = view.getState(line, marker);
		return states.get(state).getImage();
	}

	public BufferedImage getUnselectedImage(int line, int marker)
	{
		int state = view.getState(line, marker);
		return states.get(state).getUnselectedImage();
	}

	public Color getColor(int line, int marker)
	{
		int state = view.getState(line, marker);
		return states.get(state).getColor();
	}

	public int getModel()
		{ return NUCLEOTIDE; }

	public String toString()
		{ return RB.getString("gui.Actions.vizColorNucleotide"); }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.NucleotideColorScheme");
	}

	public ArrayList<ColorSummary> getColorSummaries()
	{
		ArrayList<ColorSummary> colors = new ArrayList<>();

		colors.add(new ColorSummary(Prefs.visColorNucleotideA,
			RB.getString("gui.visualization.colors.NucleotideColorScheme.a")));
		colors.add(new ColorSummary(Prefs.visColorNucleotideC,
			RB.getString("gui.visualization.colors.NucleotideColorScheme.c")));
		colors.add(new ColorSummary(Prefs.visColorNucleotideG,
			RB.getString("gui.visualization.colors.NucleotideColorScheme.g")));
		colors.add(new ColorSummary(Prefs.visColorNucleotideT,
			RB.getString("gui.visualization.colors.NucleotideColorScheme.t")));
		colors.add(new ColorSummary(Prefs.visColorNucleotideHZ,
			RB.getString("gui.visualization.colors.NucleotideColorScheme.hz")));
		colors.add(new ColorSummary(Prefs.visColorNucleotideOther,
			RB.getString("gui.visualization.colors.NucleotideColorScheme.other")));

		return colors;
	}

	public void setColorSummaries(ArrayList<ColorSummary> colors)
	{
		Prefs.visColorNucleotideA = colors.get(0).color;
		Prefs.visColorNucleotideC = colors.get(1).color;
		Prefs.visColorNucleotideG = colors.get(2).color;
		Prefs.visColorNucleotideT = colors.get(3).color;
		Prefs.visColorNucleotideHZ = colors.get(4).color;
		Prefs.visColorNucleotideOther = colors.get(5).color;
	}
}