// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.util.*;

import flapjack.data.*;

import scri.commons.io.*;
import scri.commons.gui.*;

public class TraitImporter extends SimpleJob
{
	private ProgressInputStream is;
	private File file;
	private DataSet dataSet;

	private ArrayList<Trait> traits = new ArrayList<>();

	// While reading the file, we store the trait values in a temp hashtable so
	// no association with the line data happens until we're sure everything has
	// been read correctly. Each entry is stored according to its expected line
	// name, and is a list of trait values, one per trait across the columns
	private HashMap<String, ArrayList<TraitValue>> hashtable;

	private int traitsCount, traitsRead;

	public TraitImporter(File file, DataSet dataSet)
	{
		this.file = file;
		this.dataSet = dataSet;

		hashtable = new HashMap<String, ArrayList<TraitValue>>();

		maximum = 5555;
	}

	public void runJob(int index)
		throws IOException, DataFormatException
	{
		is = new ProgressInputStream(new FileInputStream(file));
		BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

		// Skip over any comment lines
		String str = in.readLine();
		while (str != null && str.startsWith("#"))
			str = in.readLine();


		// Parse the first line and determine what the names for the traits are
		String[] traitNames = str.split("\t");
		for (int i = 1; i < traitNames.length; i++)
			traits.add(new Trait(traitNames[i]));


		for (int line = 2; (str = in.readLine()) != null && okToRun; line++)
		{
			String[] tokens = str.split("\t", -1);

			// Special case to handle the *optional* 2nd line that contains
			// experiment labelling for each trait
			if (line == 2 && str.length() > 0 && tokens[0].length() == 0)
			{
				for (int i = 1; i < tokens.length; i++)
					traits.get(i-1).setExperiment(tokens[i]);

				continue;
			}

			if (str.length() == 0)
				continue;

			// Fail if the data per line doesn't match the expected number
			if (tokens.length != traits.size() + 1)
				throw new DataFormatException(RB.format("io.DataFormatException.traitColumnError", line));

			String lineName = tokens[0];
			ArrayList<TraitValue> values = new ArrayList<>();

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
//			traitsRead++;
		}

		in.close();

		if (okToRun == false)
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

			ArrayList<String> categories = trait.getCategories();

			// Take a copy of the current category order
			String[] oldCats = categories.toArray(new String[] {});
			// Sort the categories
			Collections.sort(categories);

			// Now rewrite each trait value to have the new correct lookup value
			Set<String> e = hashtable.keySet();
			for (String traitString : e)
			{
				TraitValue tv = hashtable.get(traitString).get(i);
				float newVal = categories.indexOf(oldCats[(int)tv.getValue()]);
				tv.setValue(newVal);
			}
		}
	}

	private void applyToDataSet()
	{
		// If everything was read in correctly, apply the traits to the dataset
		for (Trait trait: traits)
		{
			dataSet.getTraits().add(trait);
			traitsCount++;
		}

		// For each line, see if data for it exists...
		for (Line line: dataSet.getLines())
		{
			ArrayList<TraitValue> traitValues = hashtable.get(line.getName());

			// If data *does* exist for this line
			if (traitValues != null)
				for (TraitValue tv: traitValues)
				{
					// First update the value with its normalized score
					tv.computeNormal();
					// Then add it
					line.getTraitValues().add(tv);
					traitsRead++;
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

	public int getValue()
	{
		if (is == null)
			return 0;

		return Math.round(is.getBytesRead() / (float) file.length() * 5555);
	}

	public int getTraitsCount()
		{ return traitsCount; }

	public int getTraitsRead()
		{ return traitsRead; }
}