// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.file.*;
import scri.commons.gui.*;

public class TraitImporter implements ITrackableJob
{
	private ProgressInputStream is;
	private File file;
	private DataSet dataSet;

	private Vector<Trait> traits = new Vector<Trait>();

	// While reading the file, we store the trait values in a temp hashtable so
	// no association with the line data happens until we're sure everything has
	// been read correctly. Each entry is stored according to its expected line
	// name, and is a list of trait values, one per trait across the columns
	private Hashtable<String, Vector<TraitValue>> hashtable;

	private boolean isOK = true;

	public TraitImporter(File file, DataSet dataSet)
	{
		this.file = file;
		this.dataSet = dataSet;

		hashtable = new Hashtable<String, Vector<TraitValue>>();
	}

	public void runJob()
		throws IOException, DataFormatException
	{
		is = new ProgressInputStream(new FileInputStream(file));
		BufferedReader in = new BufferedReader(new InputStreamReader(is));

		String str = in.readLine();
		String[] traitNames = str.split("\t");

		// Parse the first line and determine what the names for the traits are
		for (int i = 1; i < traitNames.length; i++)
			traits.add(new Trait(traitNames[i]));

		for (int line = 1; (str = in.readLine()) != null && isOK; line++)
		{
			if (str.length() == 0)
				continue;

			String[] tokens = str.split("\t", -1);

			// Fail if the data per line doesn't match the expected number
			if (tokens.length != traits.size() + 1)
				throw new DataFormatException(RB.format("io.DataFormatException.traitColumnError", line));

			String lineName = tokens[0];
			Vector<TraitValue> values = new Vector<TraitValue>();

			// For each column...
			for (int i = 1; i < tokens.length; i++)
			{
				// Fetch the trait for this column
				Trait trait = traits.get(i-1);

				// Check for non-defined
				if (tokens[i].length() == 0)
					values.add(new TraitValue(trait));
				else
				{
					try
					{
						float value = trait.computeValue(tokens[i]);
						values.add(new TraitValue(trait, value));
					}
					catch (Exception e)
					{
						if (e.getMessage().equals("NumericalReadError"))
							throw new DataFormatException(RB.format("io.DataFormatException.traitNumCatError", line, trait.getName()));
					}
				}
			}

			hashtable.put(lineName, values);
		}

		in.close();

		if (isOK == false)
			return;

		// TODO: This *doesn't* have to run if we decide otherwise
		sortCategoricalValues();

		applyToDataSet();
	}

	// Pre-sorts all categorical traits so that the lookup table is ordered
	// alphabetically by the category name
	private void sortCategoricalValues()
	{
		// For each trait
		for (int i = 0; i < traits.size(); i++)
		{
			Trait trait = traits.get(i);
			if (trait.traitIsNumerical())
				continue;

			Vector<String> categories = trait.getCategories();

			// Take a copy of the current category order
			String[] oldCats = categories.toArray(new String[] {});
			// Sort the categories
			Collections.sort(categories);

			// Now rewrite each trait value to have the new correct lookup value
			Enumeration<String> e = hashtable.keys();
			while (e.hasMoreElements())
			{
				TraitValue tv = hashtable.get(e.nextElement()).get(i);
				float newVal = categories.indexOf(oldCats[(int)tv.getValue()]);
				tv.setValue(newVal);
			}
		}
	}

	private void applyToDataSet()
	{
		// If everything was read in correctly, apply the traits to the dataset
		for (Trait trait: traits)
			dataSet.getTraits().add(trait);

		// For each line, see if data for it exists...
		for (Line line: dataSet.getLines())
		{
			Vector<TraitValue> traitValues = hashtable.get(line.getName());

			// If data *does* exist for this line
			if (traitValues != null)
				for (TraitValue tv: traitValues)
				{
					// First update the value with its normalized score
					tv.computeNormal();
					// Then add it
					line.getTraitValues().add(tv);
				}

			// If it doesn't, then still add TraitValues, but use dummy ones
			else
			{
				for (int i = 0; i < traits.size(); i++)
				{
					Trait trait = traits.get(i);
					line.getTraitValues().add(new TraitValue(trait));
				}
			}
		}
	}

	public boolean isIndeterminate()
		{ return false; }

	public int getMaximum()
		{ return 5000; }

	public int getValue()
	{
		if (is == null)
			return 0;

		return (int) (is.getBytesRead() / (float) file.length()) * 5000;
	}

	public void cancelJob()
		{ isOK = false; }
}