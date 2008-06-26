package flapjack.io;

import java.io.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

public class TraitImporter
{
	private File file;
	private DataSet dataSet;

	private Vector<Trait> traits = new Vector<Trait>();

	// While reading the file, we store the trait values in a temp hashtable so
	// no association with the line data happens until we're sure everything has
	// been read correctly. Each entry is stored according to its expected line
	// name, and is a list of trait values, one per trait across the columns
	private Hashtable<String, Vector<TraitValue>> hashtable;

	private boolean isOK = true;
	private int lineCount;

	public TraitImporter(File file, DataSet dataSet)
	{
		this.file = file;
		this.dataSet = dataSet;

		hashtable = new Hashtable<String, Vector<TraitValue>>();
	}

	public void importTraitData()
		throws IOException, DataFormatException
	{
		BufferedReader in = new BufferedReader(new FileReader(file));

		String str = in.readLine();
		String[] traitNames = str.split("\t");

		// Parse the first line and determine what the names for the traits are
		for (int i = 1; i < traitNames.length; i++)
			traits.add(new Trait(traitNames[i]));

		lineCount = 2;
		while ((str = in.readLine()) != null && str.length() > 0 && isOK)
		{
			String[] tokens = str.split("\t");

			// Fail if the data per line doesn't match the expected number
			if (tokens.length != traits.size() + 1)
				throw new DataFormatException(RB.format("io.DataFormatException.traitColumnError", lineCount));

			String lineName = tokens[0];
			Vector<TraitValue> values = new Vector<TraitValue>();

			// For each column...
			for (int i = 1; i < tokens.length; i++)
			{
				// Fetch the trait for this column
				Trait trait = traits.get(i-1);
				float value = trait.computeValue(tokens[i]);

				values.add(new TraitValue(trait, value));
			}

			hashtable.put(lineName, values);

			lineCount++;
		}

		in.close();

		if (isOK == false)
			return;

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
					line.getTraitValues().add(tv);

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

	public int getLineCount()
		{ return lineCount; }

	public void cancel()
		{ isOK = false; }
}