// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.io.*;
import java.util.*;

import scri.commons.gui.*;

// Loads a list of Strings from file which represent either a marker name, or a
// line name.
public class ExternalSelection extends SimpleJob
{
	private final File input;

	private ArrayList<String> selection;

	public ExternalSelection(File input)
	{
		this.input = input;
	}

	@Override
	public void runJob(int i)
		throws Exception
	{
		selection = new ArrayList<>();
		try (BufferedReader in = new BufferedReader(new FileReader(input)))
		{
			String line;
			while ((line = in.readLine()) != null)
			{
				// Skip any header lines that may be found in a genotype or map
				// file.
				if (line.startsWith("#") || line.startsWith("\t"))
					continue;

				selection.add(line.split("\t")[0].trim());
			}
		}
		catch (IOException e) { e.printStackTrace(); }
	}

	public ArrayList<String> selectionStrings()
	{
		return selection;
	}
}