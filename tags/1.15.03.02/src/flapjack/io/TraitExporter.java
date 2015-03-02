// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.text.*;
import java.util.*;

import flapjack.data.*;

import scri.commons.gui.*;

public class TraitExporter extends SimpleJob
{
	private NumberFormat nf = NumberFormat.getInstance();

	private File file;
	private DataSet dataSet;

	public TraitExporter(DataSet dataSet, File file)
	{
		this.file = file;
		this.dataSet = dataSet;

		nf.setMaximumFractionDigits(6);
	}

	public void runJob(int index) throws Exception
	{
		ArrayList<Trait> traits = dataSet.getTraits();

		if (traits.isEmpty())
			return;

		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		// File header for drag and drop detection
		out.write("# fjFile = PHENOTYPE");
		out.newLine();

		// Write out the header for the file
		StringBuilder header = new StringBuilder();
		for (Trait trait : dataSet.getTraits())
			header.append("\t" + trait.getName());
		out.write(header.toString());
		out.newLine();

		// Write out the experiment header
		StringBuilder experiment = new StringBuilder();
		for (Trait trait : dataSet.getTraits())
			experiment.append("\t" + trait.getExperiment());
		out.write(experiment.toString());
		out.newLine();

		// Now output each line of the data file
		for (Line line: dataSet.getLines())
		{
			StringBuilder output = new StringBuilder(line.getName());
			for (TraitValue value : line.getTraitValues())
			{
				if (value.isDefined())
				{
					// Deal with the two different kinds of trait correctly
					if (value.getTrait().traitIsNumerical())
						output.append("\t" + nf.format(value.getValue()));
					else
						output.append("\t" +value.getTrait().format(value));
				}
				else
					output.append("\t");
			}

			out.write(output.toString());
			out.newLine();
		}
		out.close();
	}
}