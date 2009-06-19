package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class NucleotideColorScheme extends ColorScheme
{
	protected Vector<ColorState> states = new Vector<ColorState>();

	/** Empty constructor that is ONLY used for color customization purposes. */
	public NucleotideColorScheme() {}

	public NucleotideColorScheme(GTView view, int w, int h)
	{
		super(view);

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
				if (state.getRawData().equals("A"))
					c = new HomozygousColorState(state, Prefs.visColorNucleotideA, w, h);
				else if (state.getRawData().equals("C"))
					c = new HomozygousColorState(state, Prefs.visColorNucleotideC, w, h);
				else if (state.getRawData().equals("G"))
					c = new HomozygousColorState(state, Prefs.visColorNucleotideG, w, h);
				else if (state.getRawData().equals("T"))
					c = new HomozygousColorState(state, Prefs.visColorNucleotideT, w, h);

				// Use a fixed color for any further unknown states
				else
					c = new HomozygousColorState(state, Prefs.visColorNucleotideOther, w, h);
			}

			// Heterozygous states
			else
			{
				if (state.getState(0).equals("A") && state.getState(1).equals("G"))
					c = new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, Prefs.visColorNucleotideA, Prefs.visColorNucleotideG, w, h);
				else if (state.getState(0).equals("A") && state.getState(1).equals("C"))
					c = new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, Prefs.visColorNucleotideA, Prefs.visColorNucleotideC, w, h);
				else if (state.getState(0).equals("A") && state.getState(1).equals("T"))
					c = new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, Prefs.visColorNucleotideA, Prefs.visColorNucleotideT, w, h);

				else if (state.getState(0).equals("G") && state.getState(1).equals("A"))
					c = new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, Prefs.visColorNucleotideG, Prefs.visColorNucleotideA, w, h);
				else if (state.getState(0).equals("G") && state.getState(1).equals("C"))
					c = new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, Prefs.visColorNucleotideG, Prefs.visColorNucleotideC, w, h);
				else if (state.getState(0).equals("G") && state.getState(1).equals("T"))
					c = new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, Prefs.visColorNucleotideG, Prefs.visColorNucleotideT, w, h);

				else if (state.getState(0).equals("C") && state.getState(1).equals("A"))
					c = new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, Prefs.visColorNucleotideC, Prefs.visColorNucleotideA, w, h);
				else if (state.getState(0).equals("C") && state.getState(1).equals("G"))
					c = new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, Prefs.visColorNucleotideC, Prefs.visColorNucleotideG, w, h);
				else if (state.getState(0).equals("C") && state.getState(1).equals("T"))
					c = new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, Prefs.visColorNucleotideC, Prefs.visColorNucleotideT, w, h);

				else if (state.getState(0).equals("T") && state.getState(1).equals("A"))
					c = new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, Prefs.visColorNucleotideT, Prefs.visColorNucleotideA, w, h);
				else if (state.getState(0).equals("T") && state.getState(1).equals("G"))
					c = new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, Prefs.visColorNucleotideT, Prefs.visColorNucleotideG, w, h);
				else if (state.getState(0).equals("T") && state.getState(1).equals("C"))
					c = new HeterozygeousColorState(state, Prefs.visColorNucleotideHZ, Prefs.visColorNucleotideT, Prefs.visColorNucleotideC, w, h);


				// Use a fixed color for any further unknown states
				else
					c = new HeterozygeousColorState(state, Prefs.visColorNucleotideOther, Prefs.visColorNucleotideOther, Prefs.visColorNucleotideOther, w, h);
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

	public Vector<ColorSummary> getColorSummaries()
	{
		Vector<ColorSummary> colors = new Vector<ColorSummary>();

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

	public void setColorSummaries(Vector<ColorSummary> colors)
	{
		Prefs.visColorNucleotideA = colors.get(0).color;
		Prefs.visColorNucleotideC = colors.get(1).color;
		Prefs.visColorNucleotideG = colors.get(2).color;
		Prefs.visColorNucleotideT = colors.get(3).color;
		Prefs.visColorNucleotideHZ = colors.get(4).color;
		Prefs.visColorNucleotideOther = colors.get(5).color;
	}
}