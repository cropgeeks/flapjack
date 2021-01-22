// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class FavAlleleColorScheme extends ColorScheme
{
	private FavAlleleManager fm;

	// States that exactly match the comparison
	protected ArrayList<ColorState> mtchStatesY = new ArrayList<>();			// eg A matches A, A/T matches A/T
	// States that don't exactly match the comparison
	protected ArrayList<ColorState> mtchStatesN = new ArrayList<>();			// eg A doesn't match T, A/T doesn't match C/G

	protected ArrayList<ColorState> favUnfavStates = new ArrayList<>();
	protected ArrayList<ColorState> favMismatchStates = new ArrayList<>();

	protected ArrayList<ColorState> unfavFavStates = new ArrayList<>();
	protected ArrayList<ColorState> unfavMistmatchStates = new ArrayList<>();

	protected ArrayList<ColorState> mismatchFavStates = new ArrayList<>();
	protected ArrayList<ColorState> mismatchUnfavStates = new ArrayList<>();

	// Greyscale states for when the comparison state is missing
	protected ArrayList<ColorState> gsStates = new ArrayList<>();				// eg A could match MISSING, but we don't know

	// A lookup table which disambiguates heterozygous genotypes allowing us to match against their homozygous allele equiavalents
	protected int[][] lookupTable;


	/** Empty constructor that is ONLY used for color customization purposes. */
	public FavAlleleColorScheme() {}

	public FavAlleleColorScheme(GTView view, int w, int h)
	{
		super(view);

		// Shorthand names for the colours (makes the code below more readable)
		Color sHz = Prefs.visColorNucleotideHZ;
		Color matchFav = Prefs.visColorSimStateMatch;
		Color matchUnfav = Prefs.visColorSimStateNoMatch;
		Color mismatch  = Prefs.visColorSimStateMissing;

		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);

			ColorState mtchY, mtchN, favUnfav, favMis, unfavFav, unfavMis, misFav, misUnfav, gs;

			// Use white for the default unknown state
			if (state.isUnknown())
				mtchY = mtchN = favUnfav = favMis = unfavFav = unfavMis = misFav = misUnfav = gs = new SimpleColorState(state, Prefs.visColorBackground, w, h);

			// Homozygous states
			else if (state.isHomozygous())
			{
				mtchY = new HomozygousColorState(state, matchFav, w, h);
				mtchN = new HomozygousColorState(state, matchUnfav, w, h);
				favUnfav = null;
				favMis = null;
				unfavFav = null;
				unfavMis = null;
				misFav = null;
				misUnfav = null;
				gs = new HomozygousColorState(state, mismatch, w, h);
			}

			// Heterozygous states
			else
			{
				mtchY = new HeterozygeousColorState(state, sHz, matchFav, matchFav, w, h);
				mtchN = new HeterozygeousColorState(state, sHz, matchUnfav, matchUnfav, w, h);
				favUnfav = new HeterozygeousColorState(state, sHz, matchFav, matchUnfav, w, h);
				favMis = new HeterozygeousColorState(state, sHz, matchFav, mismatch, w, h);
				unfavFav = new HeterozygeousColorState(state, sHz, matchUnfav, matchFav, w, h);
				unfavMis = new HeterozygeousColorState(state, sHz, matchUnfav, mismatch, w, h);
				misFav = new HeterozygeousColorState(state, sHz, mismatch, matchFav, w, h);
				misUnfav = new HeterozygeousColorState(state, sHz, mismatch, matchUnfav, w, h);
				gs = new HeterozygeousColorState(state, sHz, mismatch, mismatch, w, h);
			}

			mtchStatesY.add(mtchY);
			mtchStatesN.add(mtchN);
			favUnfavStates.add(favUnfav);
			favMismatchStates.add(favMis);
			unfavFavStates.add(unfavFav);
			unfavMistmatchStates.add(unfavMis);
			mismatchFavStates.add(misFav);
			mismatchUnfavStates.add(misUnfav);
			gsStates.add(gs);
		}

		// Grab the favourable allele manager so that we can compare alleles to the favourable alleles and choose
		// colours appropriately
		fm = view.getViewSet().getDataSet().getFavAlleleManager();

		lookupTable = stateTable.createAlleleLookupTable();
	}

	protected ColorState getState(int line, int marker)
	{
		// The state at this index
		int state = view.getState(line, marker);
		// And the actual marker represented for this column
		Marker mkr = view.getMarker(marker);

		// Favourite allele match?
		ArrayList<Integer> favAlleles = fm.getFavAlleles().getOrDefault(mkr.getName(), new ArrayList<>());
		ArrayList<Integer> unfavAlleles = fm.getUnfavAlleles().getOrDefault(mkr.getName(), new ArrayList<>());
		// A state representing the genotype to be rendered
		AlleleState genotype = stateTable.getAlleleState(state);

		// Homozygous alleles: first attempt to match favourable alleles, then if no match is found attempt to match
		// unfavourable alleles
		if (genotype.isHomozygous())
		{
			for (int allele : favAlleles)
				if (allele == state)
					return mtchStatesY.get(state);

			for (int allele : unfavAlleles)
				if (allele == state)
					return mtchStatesN.get(state);
		}
		// Deal with heterozygous alleles
		else
		{
			boolean favMatchAllele1 = matchesAllele(favAlleles, state, 0);
			boolean favMatchAllele2 = matchesAllele(favAlleles, state, 1);

			boolean unfavMatchAllele1 = matchesAllele(unfavAlleles, state, 0);
			boolean unfavMatchAllele2 = matchesAllele(unfavAlleles, state, 1);

			if (favMatchAllele1)
				return unfavMatchAllele2 ? favUnfavStates.get(state) : favMismatchStates.get(state);

			else if (unfavMatchAllele1)
				return favMatchAllele2 ? unfavFavStates.get(state) : unfavMistmatchStates.get(state);

			else
				return favMatchAllele2 ? mismatchFavStates.get(state) : mismatchUnfavStates.get(state);
		}

		// If it's not the same, or we can't do a comparison...
		return gsStates.get(state);
	}

	// Method that takes a list of genotypes that are either favourable or unfavourable alleles. It then tries to match
	// these against a state in the Flapjack state table that has been split into its individual alleles as opposed to
	// just the genotype e.g. [A] becomes [A][A], [A/T] becomes [A][T]. If a match is found we return true, otherwise
	// we return false
	private boolean matchesAllele(ArrayList<Integer> alleleList, int stateCode, int alleleInGenotype)
	{
		boolean matches = false;

		for (int allele : alleleList)
			if (lookupTable[stateCode][alleleInGenotype] == allele)
				matches = true;

		return matches;
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

	public void setColorSummaries(ArrayList<ColorSummary> colors)
	{
		Prefs.visColorSimStateMatch = colors.get(0).color;
		Prefs.visColorSimStateNoMatch = colors.get(1).color;
		Prefs.visColorSimStateMissing = colors.get(2).color;
	}

	private void createLookupTable()
	{
		// Create a lookup table which has two slots for each genotype in the state table this allows us to reconstitute
		// heterozygous alleles into a form where each half can be easily compared to favourable alleles from the GOBII
		// QTL format (i.e. each het is split into two homozygous half genotypes...)
		int count = stateTable.size();
		lookupTable = new int[count][2];

		// Prefill the array with -1s which will be used to denote states which can't be found in the statetable
		for (int i = 0; i < count; i++)
			for (int j = 0; j < 2; j++)
				lookupTable[i][j] = -1;

		// Iterate over the state table creating the two slot array entry for each state in the table
		for (int i = 0; i < count; i++)
		{
			for (int j = 0; j < 2; j++)
			{
				// Get the string values of the allele states (e.g. 'A', or 'A''T')
				AlleleState state = stateTable.getAlleleState(i);
				String[] stringAlleles = state.getStates();

				// We may only hav a hom allele so we can't assume we have two strings here
				if (j < stringAlleles.length)
				{
					// Make a temp state to check against the statetable
					AlleleState newState = new AlleleState(stringAlleles[j], "/");
					int stateCode = -1;
					for (int k = 0; k < stateTable.size(); k++)
						if (stateTable.getAlleleState(k).matches(newState))
							stateCode = k;

					lookupTable[i][j] = stateCode;

					// If this was a homozygous genotype manually add its second allele
					if (stringAlleles.length == 1)
						lookupTable[i][1] = stateCode;
				}
			}
		}
	}
}