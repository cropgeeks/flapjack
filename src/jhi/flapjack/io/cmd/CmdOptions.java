package jhi.flapjack.io.cmd;

import java.io.File;
import java.util.*;

import jhi.flapjack.io.FlapjackFile;
import org.apache.commons.cli.*;

class CmdOptions extends Options
{
	private static final String GENOTYPES = "g";
	private static final String GENOTYPES_LONG = "genotypes";
	private static final String MAP = "m";
	private static final String MAP_LONG = "map";
	private static final String PROJECT = "p";
	private static final String PROJECT_LONG = "project";
	private static final String TRAITS = "t";
	private static final String TRAITS_LONG = "traits";
	private static final String QTLS = "q";
	private static final String QTLS_LONG = "qtls";
	private static final String OUTPUT = "o";
	private static final String OUTPUT_LONG = "output";

	private static final String FILE_ARG = "FILE";
	private static final String FILE_PATH_ARG = "FILEPATH";

	CmdOptions withAdvancedOptions()
	{
		addOption("A", "all-chromosomes", false, "Duplicate all markers onto a single All Chromosomes chromosome for side-by-side viewing");
		addOption("C", "collapse-heteozygotes", false, "Don't distinguish between heterozygous alleles (eg treat A/T the same as T/A)");
		addOption("N", "no-heterozygote-separator", false, "Heterozygotes are not separated by a string");
		addOption("S", "heterozygous-separator", true, "The string used to separate heterozygous alleles");
		addOption("M", "missing-data", true, "The string used to represent missing data (use \"\" for empty string)");
		addOption("T", "transposed", false, "Genotype data is transposed compared to Flapjack's default");
		addOption("D", "decimal-english", false, "Optional input parameter");

		return this;
	}

	DataImportSettings getDataImportSettings(CommandLine line)
	{
		DataImportSettings settings = new DataImportSettings();

		if (line.hasOption("missing-data"))
			settings.setMissingData(line.getOptionValue("missing-data"));
		if (line.hasOption("heterozygous-separator"))
			settings.setHetSep(line.getOptionValue("heterozygous-separator"));

		settings.setUseHetSep(!line.hasOption("no-heterozygote-separator"));
		settings.setMakeAllChrom(line.hasOption("all-chromosomes"));

		return settings;
	}

	CreateProjectSettings getCreateProjectSettings(CommandLine line)
	{
		File genotypes = getGenotypeFile(line);
		File map = getMapFile(line);
		File traits = getTraitFile(line);
		File qtls = getQtlFile(line);
		FlapjackFile project = getProjectFlapjackFile(line);

		return new CreateProjectSettings(genotypes, map, traits, qtls, project);
	}

	CmdOptions withGenotypeFile(boolean required)
	{
		return addOption(GENOTYPES, GENOTYPES_LONG, true, FILE_ARG, "Genotype file", required);
	}

	CmdOptions withMapFile(boolean required)
	{
		return addOption(MAP, MAP_LONG, true, FILE_ARG, "Map file", required);
	}

	CmdOptions withTraitFile(boolean required)
	{
		return addOption(TRAITS, TRAITS_LONG, true, FILE_ARG, "Trait file", required);
	}

	CmdOptions withQtlFile(boolean required)
	{
		return addOption(QTLS, QTLS_LONG, true, FILE_ARG, "QTL file", required);
	}

	CmdOptions withProjectFile(boolean required)
	{
		return addOption(PROJECT, PROJECT_LONG, true, FILE_ARG, "Project file", required);
	}

	CmdOptions withOutputPath(boolean required)
	{
		return addOption(OUTPUT, OUTPUT_LONG, true, FILE_PATH_ARG, "Desired output filepath", required);
	}

	CmdOptions addRequiredOption(String opt, String longOpt, boolean hasArg, String argName, String description)
	{
		return addOption(opt, longOpt, hasArg, argName, description, true);
	}

	CmdOptions addOption(String opt, String longOpt, boolean hasArg, String argName, String description)
	{
		return addOption(opt, longOpt, hasArg, argName, description, false);
	}

	private CmdOptions addOption(String opt, String longOpt, boolean hasArg, String argName, String description, boolean required)
	{
		Option option = new Option(opt, longOpt, hasArg, description);
		option.setArgName(argName);
		option.setRequired(required);

		addOption(option);

		return this;
	}

	File getGenotypeFile(CommandLine line)
	{
		File genotypes = null;

		if (line.hasOption(GENOTYPES))
			genotypes = new File(line.getOptionValue(GENOTYPES));

		return genotypes;
	}

	FlapjackFile getProjectFlapjackFile(CommandLine line)
	{
		FlapjackFile project = null;

		if (line.hasOption(PROJECT))
			project = new FlapjackFile(line.getOptionValue(PROJECT));

		return project;
	}

	File getMapFile(CommandLine line)
	{
		File map = null;

		if (line.hasOption(MAP))
			map = new File(line.getOptionValue(MAP));

		return map;
	}

	File getTraitFile(CommandLine line)
	{
		File traits = null;

		if (line.hasOption(TRAITS))
			traits = new File(line.getOptionValue(TRAITS));

		return traits;
	}

	File getQtlFile(CommandLine line)
	{
		File qtls = null;

		if (line.hasOption(QTLS))
			qtls = new File(line.getOptionValue(QTLS));

		return qtls;
	}

	String getOutputPath(CommandLine line)
	{
		return line.getOptionValue(OUTPUT);
	}

	void printHelp(String name)
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.setOptionComparator(new OptionComparator());
		formatter.printHelp(name, this, true);
	}

	private static class OptionComparator implements Comparator<Option>
	{
		public int compare(Option o1, Option o2)
		{
			if (o1.isRequired() && !o2.isRequired())
				return -1;

			else if (o1.isRequired() && o2.isRequired() || !o1.isRequired() && !o2.isRequired())
			{
				String o1Key = o1.getOpt() == null ? o1.getLongOpt() : o1.getOpt();
				String o2Key = o2.getOpt() == null ? o2.getLongOpt() : o2.getOpt();
				return o1Key.compareToIgnoreCase(o2Key);
			}

			else
				return 1;
		}
	}
}