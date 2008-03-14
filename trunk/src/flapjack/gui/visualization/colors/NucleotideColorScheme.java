package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;

public class NucleotideColorScheme extends ColorScheme
{
	private Vector<ColorState> states = new Vector<ColorState>();

	private static final Color COLOR_A = new Color(120, 255, 120);
	private static final Color COLOR_C = new Color(255, 160, 120);
	private static final Color COLOR_G = new Color(255, 120, 120);
	private static final Color COLOR_T = new Color(120, 120, 255);

	private static final Color COLOR_HET = new Color(100, 100, 100);

	public NucleotideColorScheme(GTView view, int w, int h)
	{
		super(view);

		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);
			ColorState c = null;

			// Use white for the default unknown state
			if (state.isUnknown())
				c = new SimpleColorState(state, Color.white, w, h);

			// Homozygous states
			else if (state.isHomozygous())
			{
				if (state.getRawData().equals("A"))
					c = new HomozygousColorState(state, COLOR_A, w, h);
				else if (state.getRawData().equals("C"))
					c = new HomozygousColorState(state, COLOR_C, w, h);
				else if (state.getRawData().equals("G"))
					c = new HomozygousColorState(state, COLOR_G, w, h);
				else if (state.getRawData().equals("T"))
					c = new HomozygousColorState(state, COLOR_T, w, h);

				// Use a fixed (RED) color for any further unknown states
				else
					c = new HomozygousColorState(state, Color.red, w, h);
			}

			// Heterozygous states
			else
			{
				if (state.getState(0).equals("A") && state.getState(1).equals("G"))
					c = new HeterozygeousColorState(state, COLOR_HET, COLOR_A, COLOR_G, w, h);
				else if (state.getState(0).equals("A") && state.getState(1).equals("C"))
					c = new HeterozygeousColorState(state, COLOR_HET, COLOR_A, COLOR_C, w, h);
				else if (state.getState(0).equals("A") && state.getState(1).equals("T"))
					c = new HeterozygeousColorState(state, COLOR_HET, COLOR_A, COLOR_T, w, h);

				else if (state.getState(0).equals("G") && state.getState(1).equals("A"))
					c = new HeterozygeousColorState(state, COLOR_HET, COLOR_G, COLOR_A, w, h);
				else if (state.getState(0).equals("G") && state.getState(1).equals("C"))
					c = new HeterozygeousColorState(state, COLOR_HET, COLOR_G, COLOR_C, w, h);
				else if (state.getState(0).equals("G") && state.getState(1).equals("T"))
					c = new HeterozygeousColorState(state, COLOR_HET, COLOR_G, COLOR_T, w, h);

				else if (state.getState(0).equals("C") && state.getState(1).equals("A"))
					c = new HeterozygeousColorState(state, COLOR_HET, COLOR_C, COLOR_A, w, h);
				else if (state.getState(0).equals("C") && state.getState(1).equals("G"))
					c = new HeterozygeousColorState(state, COLOR_HET, COLOR_C, COLOR_G, w, h);
				else if (state.getState(0).equals("C") && state.getState(1).equals("T"))
					c = new HeterozygeousColorState(state, COLOR_HET, COLOR_C, COLOR_T, w, h);

				else if (state.getState(0).equals("T") && state.getState(1).equals("A"))
					c = new HeterozygeousColorState(state, COLOR_HET, COLOR_T, COLOR_A, w, h);
				else if (state.getState(0).equals("T") && state.getState(1).equals("G"))
					c = new HeterozygeousColorState(state, COLOR_HET, COLOR_T, COLOR_G, w, h);
				else if (state.getState(0).equals("T") && state.getState(1).equals("C"))
					c = new HeterozygeousColorState(state, COLOR_HET, COLOR_T, COLOR_C, w, h);


				// Use a fixed (RED) color for any further unknown states
				else
					c = new HomozygousColorState(state, Color.red, w, h);
			}

			states.add(c);
		}
	}

	public BufferedImage getImage(int line, int marker)
	{
		int state = view.getState(line, marker);
		return states.get(state).getImage();
	}

	public Color getColor(int line, int marker)
	{
		int state = view.getState(line, marker);
		return states.get(state).getColor();
	}
}