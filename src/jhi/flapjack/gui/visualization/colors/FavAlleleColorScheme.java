// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class FavAlleleColorScheme extends SimilarityColorScheme
{
	private FavAlleleManager fm;

	/** Empty constructor that is ONLY used for color customization purposes. */
	public FavAlleleColorScheme() {}

	public FavAlleleColorScheme(GTView view, int w, int h)
	{
		super(view, w, h);

		het1States.clear();

		// Shorthand names for the colours (makes the code below more readable)
		Color sHz = Prefs.visColorNucleotideHZ;
		Color s1  = Prefs.visColorHetsAsH;

		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);

			ColorState het1;

			// Use white for the default unknown state
			if (state.isUnknown())
				het1 = new SimpleColorState(state, Prefs.visColorBackground, w, h);

				// Homozygous states
			else if (state.isHomozygous())
			{
				het1  = null;
			}

			// Heterozygous states
			else
			{
				het1  = new HeterozygeousColorState(state, sHz, s1, s1, w, h);
			}

			het1States.add(het1);
		}

		fm = view.getViewSet().getDataSet().getFavAlleleManager();
	}

	protected ColorState getState(int line, int marker)
	{
		// The state at this index
		int state = view.getState(line, marker);
		// And the actual marker represented for this column
		Marker mkr = view.getMarker(marker);

		// Favourite allele match?
		ArrayList<Integer> favAlleles = fm.getFavAlleles().get(mkr.getName());
		if (favAlleles != null)
		{
			for (int favAllele : favAlleles)
			{
				if (state == favAllele)
					return mtchStatesY.get(state);
			}
		}

		// Non-favourite allele match?
		ArrayList<Integer> unfavAlleles = fm.getUnfavAlleles().get(mkr.getName());
		if (unfavAlleles != null)
			for (int unfavAllele: unfavAlleles)
				if (state == unfavAllele)
					return mtchStatesN.get(state);

		if (favAlleles != null && unfavAlleles != null && stateTable.isHet(state))
			return het1States.get(state);

		// If it's not the same, or we can't do a comparison...
		return gsStates.get(state);
	}

	public BufferedImage getSelectedImage(int line, int marker, boolean underQTL)
	{
		return getState(line, marker).getImage(underQTL);
	}

	public BufferedImage getUnselectedImage(int line, int marker, boolean underQTL)
	{
		return getState(line, marker).getUnselectedImage(underQTL);
	}

	public Color getColor(int line, int marker)
	{
		return getState(line, marker).getColor();
	}

	public int getModel()
		{ return FAV_ALLELE; }

	public String toString()
		{ return RB.getString("gui.Actions.vizColorFavAllele"); }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.FavAlleleColorScheme");
	}

	public ArrayList<ColorSummary> getColorSummaries()
	{
		ArrayList<ColorSummary> colors = new ArrayList<>();

		colors.add(new ColorSummary(Prefs.visColorSimStateMatch, "MatchFavAllele"));
		colors.add(new ColorSummary(Prefs.visColorSimStateNoMatch, "MatchUnFavAllele"));
		colors.add(new ColorSummary(Prefs.visColorSimStateMissing, "NoMatch"));

		return colors;
	}
}