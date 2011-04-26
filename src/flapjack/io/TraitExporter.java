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
		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		ArrayList<Trait> traits = dataSet.getTraits();

		if (traits.isEmpty())
			return;

		// Write out the header for the file
		String header = RB.getString("gui.traits.TraitsTableModel.line");
		for (Trait trait : dataSet.getTraits())
			header += ("\t" + trait.getName());

		out.write(header);
		out.newLine();

		// Now output each line of the data file
		for (Line line: dataSet.getLines())
		{
			String output = line.getName();
			for (TraitValue value : line.getTraitValues())
			{
				if (value.isDefined())
				{
					// Deal with the two different kinds of trait correctly
					if (value.getTrait().traitIsNumerical())
						output += ("\t" + nf.format(value.getValue()));
					else
						output += ("\t" +value.getTrait().format(value));
				}
				else
					output += "\t";
			}

			out.write(output);
			out.newLine();
		}
		out.close();
	}
}
