// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class Nucleotide01ColorScheme extends NucleotideColorScheme
{
	/** Empty constructor that is ONLY used for color customization purposes. */
	public Nucleotide01ColorScheme() {}

	public Nucleotide01ColorScheme(GTView view, int w, int h)
	{
		super(view, w, h);

		// Clear the colours as built by the normal Nucleotide class as we'll
		// want to define a new set below
		states.clear();

		// Set up the hash for mapping colours to alleles
		HashMap<String, Color> keys = new HashMap<>();
		keys.put("A", Prefs.visColorNucleotideA);
		keys.put("C", Prefs.visColorNucleotideC);
		keys.put("G", Prefs.visColorNucleotideG);
		keys.put("T", Prefs.visColorNucleotideT);
		keys.put("0", Prefs.visColorNucleotide0);
		keys.put("1", Prefs.visColorNucleotide1);

		buildStates(keys, w, h);
	}

	public int getModel()
		{ return NUCLEOTIDE01; }

	public String toString()
		{ return RB.getString("gui.Actions.vizColorNucleotide01"); }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.NucleotideColorScheme01");
	}

	public ArrayList<ColorSummary> getColorSummaries()
	{
		ArrayList<ColorSummary> colors = super.getColorSummaries();

		colors.add(4, new ColorSummary(Prefs.visColorNucleotide0,
			RB.getString("gui.visualization.colors.NucleotideColorScheme.0")));
		colors.add(5, new ColorSummary(Prefs.visColorNucleotide1,
			RB.getString("gui.visualization.colors.NucleotideColorScheme.1")));

		return colors;
	}

	public void setColorSummaries(ArrayList<ColorSummary> colors)
	{
		Prefs.visColorNucleotideA = colors.get(0).color;
		Prefs.visColorNucleotideC = colors.get(1).color;
		Prefs.visColorNucleotideG = colors.get(2).color;
		Prefs.visColorNucleotideT = colors.get(3).color;
		Prefs.visColorNucleotide0 = colors.get(4).color;
		Prefs.visColorNucleotide1 = colors.get(5).color;
		Prefs.visColorNucleotideHZ = colors.get(6).color;
		Prefs.visColorNucleotideOther = colors.get(7).color;

	}
}