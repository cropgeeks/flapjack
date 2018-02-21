// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.cmd;

import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class IntertekFavAlleleHeaderCreator
{
	private String genotypeFileName;
	private String intertekFileName;
	private String outputFileName;

	private Map<String, IntertekMarker> intertekMarkers;
	private HashSet<String> genotypeMarkers;

	public static void main(String[] args)
	{
		Options options = mainOptions();

		CommandLineParser parser = new DefaultParser();

		try
		{
			CommandLine cl = parser.parse(options, args);

			IntertekFavAlleleHeaderCreator intertekParser = new IntertekFavAlleleHeaderCreator(cl.getOptionValue("g"), cl.getOptionValue("i"), cl.getOptionValue("o"));
			intertekParser.parse();
		}
		catch (ParseException e)
		{
			printHelp("IntertekParser");

			e.printStackTrace();
		}
	}

	public IntertekFavAlleleHeaderCreator(String genotypeFileName, String intertekFileName, String outputFileName)
	{
		this.genotypeFileName = genotypeFileName;
		this.intertekFileName = intertekFileName;
		this.outputFileName = outputFileName;
	}

	public void parse()
	{
		readIntertekFile();

		// Check for existence of genotype file
		File genotypeFile = new File(genotypeFileName);
		if (!genotypeFile.exists())
		{
			System.out.println("Genotype file does not exist");
			System.exit(1);
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(genotypeFile));
			PrintWriter writer = new PrintWriter(new FileWriter(new File(outputFileName))))
		{
			String str = reader.readLine();

			// Preprocess the file, looking for any header information
			while (str.length() == 0 || str.startsWith("#"))
			{
				writer.println(str);

				str = reader.readLine();
			}

			genotypeMarkers = new HashSet<String>();

			String [] markerNames = str.split("\t");
			for (int i=1; i < markerNames.length; i++)
				genotypeMarkers.add(markerNames[i]);

			// TODO: This is where we should output the headers in the final version of this code. For now we'll output
			// the headers at the end of this loop.

			writer.println(str);

			// Write out the genotype data
			while ((str = reader.readLine()) != null)
				writer.println(str);

			// Output headers only for markers which are found in the genotype file
			for (String markerName : intertekMarkers.keySet())
			{
				if (genotypeMarkers.contains(markerName))
				{
					IntertekMarker intertekMarker = intertekMarkers.get(markerName);

					// TODO: re-write these cludgy statements to strip out entries which are empty or a "-"
					if(intertekMarker.getFavourableAlleles().stream().noneMatch(a -> a.isEmpty() || a.equals("-")))
						writer.println("# fjFavAllele\t" + intertekMarker.getName() + "\t" + intertekMarker.getFavourableAlleles().stream().filter(a -> !a.isEmpty() && !a.equals("-")).collect(Collectors.joining("\t")));
					if(intertekMarker.getUnfavourableAlleles().stream().noneMatch(a -> a.isEmpty() || a.equals("-")))
						writer.println("# fjUnfavAllele\t" + intertekMarker.getName() + "\t" + intertekMarker.getUnfavourableAlleles().stream().filter(a -> !a.isEmpty() && !a.equals("-")).collect(Collectors.joining("\t")));
					if (!intertekMarker.getAltName().isEmpty())
						writer.println("# fjAltMarkerName\t" + intertekMarker.getName() + "\t" + intertekMarker.getAltName());
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void readIntertekFile()
	{
		// Check for existence of intertek file
		File intertekFile = new File(intertekFileName);
		if (!intertekFile.exists())
		{
			System.out.println("Intertek file does not exist");
			System.exit(1);
		}

		intertekMarkers = new HashMap<String, IntertekMarker>();

		try (BufferedReader reader = new BufferedReader(new FileReader(intertekFile)))
		{
			String str = null;
			while ((str = reader.readLine()) != null)
			{
				if (str.toLowerCase().startsWith("intertek_snp_id"))
					continue;

				String[] tokens = str.split("\t");

				String markerName = tokens[0];
				String altName = tokens[1];

				String traitGene = tokens[2];
				// TODO: temporarily assuming only one favourable and unfavourable allele per marker
				String favourableAllele = tokens[3];
				String unfavourableAllele = tokens[4];

				intertekMarkers.put(markerName, new IntertekMarker(markerName, altName, traitGene, Collections.singletonList(favourableAllele), Collections.singletonList(unfavourableAllele)));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static Options mainOptions()
	{
		Options options = new Options();

		options.addRequiredOption("g", "genotype", true, "A filepath to a Flapjack formatted genotype file");
		options.addRequiredOption("i", "intertek", true, "A filepath to an Intertek formatted file with favourable allele information");
		options.addRequiredOption("o", "output", true, "A filepath to the name and location of the desired output file");

		return options;
	}

	private static void printHelp(String name)
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(name, mainOptions(), true);
	}

	public class IntertekMarker
	{
		private String name;
		private String altName;
		private String traitGene;
		private List<String> favourableAlleles;
		private List<String> unfavourableAlleles;

		public IntertekMarker(String name, String altName, String traitGene, List<String> favourableAlleles, List<String> unfavourableAlleles)
		{
			this.name = name;
			this.altName = altName;
			this.traitGene = traitGene;
			this.favourableAlleles = favourableAlleles;
			this.unfavourableAlleles = unfavourableAlleles;
		}

		public String getName()
			{ return name; }

		public void setName(String name)
			{ this.name = name; }

		public String getAltName()
			{ return altName; }

		public void setAltName(String altName)
			{ this.altName = altName; }

		public String getTraitGene()
			{ return traitGene; }

		public void setTraitGene(String traitGene)
			{ this.traitGene = traitGene; }

		public List<String> getFavourableAlleles()
			{ return favourableAlleles; }

		public void setFavourableAlleles(List<String> favourableAlleles)
			{ this.favourableAlleles = favourableAlleles; }

		public List<String> getUnfavourableAlleles()
			{ return unfavourableAlleles; }

		public void setUnfavourableAlleles(List<String> unfavourableAlleles)
			{ this.unfavourableAlleles = unfavourableAlleles; }
	}
}