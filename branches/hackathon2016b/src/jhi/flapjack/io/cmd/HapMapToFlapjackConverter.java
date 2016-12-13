package jhi.flapjack.io.cmd;

import java.io.*;
import java.util.*;
import java.util.stream.*;

/**
 * Used to convert a HapMap (version 3) formatted file to the Flapjack flat file map and genotype formats. The single
 * constructor accepts a {@link java.io.File} representing the HapMap file to be converted, as well as a
 * {@link java.io.File} each, representing the Flapjack map file and the Flapjack genotype file to be produced.
 */
public class HapMapToFlapjackConverter
{
	private static final String HAPMAP_HEADER_ID = "rs";

	// The HapMap input file
	private File hapMap;
	// The Flapjack output files
	private File map;
	private File genotypes;

	// Collections used during the conversion process
	private List<Marker> markers;
	private Line[] lines;

	private String separator;

	private static final String TAB_SEPARATOR = "\t";
	private static final String SPACE_SEPARATOR = "\\s+";

	public static void main(String[] args)
	{
		File genotypeFile = null;
		File mapFile = null;
		File hapMapFile = null;
		String separator = null;

		for (int i = 0; i < args.length; i++)
		{
			if (args[i].startsWith("-genotypes="))
				genotypeFile = new File(args[i].substring(11));
			if (args[i].startsWith("-separator="))
				separator = args[i].substring(11);
			if (args[i].startsWith("-map="))
				mapFile = new File(args[i].substring(5));
			if (args[i].startsWith("-hapmap="))
				hapMapFile = new File(args[i].substring(8));
		}

		if (genotypeFile == null || mapFile == null || hapMapFile == null || separator == null || separator != null && (!separator.equals("s") && !separator.equals("t")))
			printHelp();

		else
		{
			if (separator.equals("s"))
				separator = SPACE_SEPARATOR;
			else if (separator.equals("t"))
				separator = TAB_SEPARATOR;

			HapMapToFlapjackConverter toFlapjack = new HapMapToFlapjackConverter(hapMapFile, mapFile, genotypeFile, separator);
			toFlapjack.convert();
		}
	}

	/**
	 * Creates a HapMapToFlapjack object from the following parameters, the HapMap file to be processed, the Flapjack
	 * map file to be output and the Flapjack genotype file to be output. The HapMap file should already exist, whereas
	 * the two output files shouldn't.
	 *
	 * @param hapMap     the HapMap formatted file we are converting to Flapjack format files
	 * @param map        the Flapjack map file to be producecd by the converter
	 * @param genoytpyes the Flapjack genotype file to be produced by the converter
	 */
	public HapMapToFlapjackConverter(File hapMap, File map, File genoytpyes, String separator)
	{
		this.hapMap = hapMap;
		this.map = map;
		this.genotypes = genoytpyes;
		this.separator = separator;
	}

	/**
	 * Processes the input HapMap file into the two outputs of a Flapjack map file and a Flapajck genotype file.
	 */
	public void convert()
	{
		boolean processedSuccessfully = processHapMapFile();
		if (processedSuccessfully)
		{
			outputMapFile();
			outputGenotypeFile();
		}
	}

	/**
	 * Reads a HapMap (version 3?) file line by line, splits each line on whitespace separators and either passes the
	 * header line to be processed by {@link HapMapToFlapjackConverter#processHapMapFile} and all other lines to be processed by
	 * within this method.
	 */
	private boolean processHapMapFile()
	{
		System.out.println("Reading HapMap file");
		markers = new ArrayList<Marker>();

		try (BufferedReader reader = new BufferedReader(new FileReader(hapMap)))
		{
			int i = 0;
			String line;
			while ((line = reader.readLine()) != null)
			{
				String[] cols = line.split(separator);
				// The columns between index 11 and the final index represent either the line names in the case of a
				// header line, or the snp calls for the marker represented by the current line
				String[] genos = Arrays.copyOfRange(cols, 11, cols.length);

				if (line.startsWith(HAPMAP_HEADER_ID))
					processHeaderLine(genos);

				else
				{
					createMarker(cols[0], cols[2], cols[3]);
					addSnpCallsToLines(genos);
				}

				if (++i % 100 == 0)
					System.out.println("Processed: " + i + " lines");
			}

			System.out.println("Finished reading HapMap file");
			System.out.println("Total lines processed = " + i);
			System.out.println("Found " + markers.size() + " markers and " + lines.length + " accessions / lines");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Converts an array of line names into an array of {@link HapMapToFlapjackConverter.Line} objects.
	 *
	 * @param lineNames a String array of names for the lines found in the file
	 */
	private void processHeaderLine(String[] lineNames)
	{
		lines = new Line[lineNames.length];
		for (int i = 0; i < lineNames.length; i++)
			lines[i] = new Line(lineNames[i]);
	}

	/**
	 * Takes a marker name, chromosome, position and list of snpCalls and creates a marker and adds it to the list of
	 * markers held in the class.
	 *
	 * @param name       The String name of the marker we're creating
	 * @param chromosome The String name of the chromosome the marker is on
	 * @param position   The String position of the marker in its chromosome
	 */
	private void createMarker(String name, String chromosome, String position)
	{
		Marker marker = new Marker(name, chromosome, position);
		markers.add(marker);
	}

	/**
	 * Takes a String array of snp calls and adds them to the lines created from the header line. Makes the assumption
	 * that the snp calls are in the same order as the header line (as they would be in a properly formatted file).
	 *
	 * @param snpCalls
	 */
	private void addSnpCallsToLines(String[] snpCalls)
	{
		for (int i = 0; i < snpCalls.length; i++)
			lines[i].addSnpCall(convertAndCollapseSnpCall(snpCalls[i]));
	}

	private String convertAndCollapseSnpCall(String call)
	{
		String converted;

		if (call.length() == 1)
		{
			switch (call)
			{
				case "A":
					converted = "A";
					break;
				case "G":
					converted = "G";
					break;
				case "C":
					converted = "C";
					break;
				case "T":
					converted = "T";
					break;
				case "Y":
					converted = "C/T";
					break;
				case "R":
					converted = "A/G";
					break;
				case "W":
					converted = "A/T";
					break;
				case "S":
					converted = "G/C";
					break;
				case "K":
					converted = "T/G";
					break;
				case "M":
					converted = "C/A";
					break;
				default:
					converted = "N";
					break;
			}

			return converted;
		}

		if (call.charAt(0) == call.charAt(1))
			converted = call.substring(0, 1).equals("N") ? "-" : call.substring(0, 1);
		else
			converted = call.substring(0, 1) + "/" + call.substring(1, 2);

		return converted;
	}

	/**
	 * Creates a Flapjack map file (see <a href="http://google.com">https://ics.hutton.ac.uk/wiki/index.php/Flapjack_Help_-_Projects_and_Data_Formats</a>).
	 * Outputs the map header line, then iterates over the list of markers outputting each in turn to the Map
	 * {@link java.io.File} accepted by the constructor.
	 */
	private void outputMapFile()
	{
		System.out.println();
		System.out.println("Outputting map file: " + map.getAbsolutePath());
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(map))))
		{
			writer.println("# fjFile = MAP");
			markers.forEach(marker -> writer.println(marker.name() + "\t" + marker.chromosome() + "\t" + marker.position()));

			System.out.println("Finished outputting map file: " + map.getAbsolutePath());
			System.out.println("Wrote " + markers.size() + " markers to file");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Creates a Flapjack genotype file (see <a href="http://google.com">https://ics.hutton.ac.uk/wiki/index.php/Flapjack_Help_-_Projects_and_Data_Formats</a>).
	 * Outputs the genotype header line, then iterates over the array of lines outputting each in turn to the genotype
	 * {@link java.io.File} accepted by the constructor.
	 */
	private void outputGenotypeFile()
	{
		System.out.println();
		System.out.println("Outputting genotype file: " + genotypes.getAbsolutePath());

		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(genotypes))))
		{
			writer.println("# fjFile = GENOTYPE");
			// The first line in a genotype file has a tab, followed by a tab separated list of marker names
			markers.forEach(marker -> writer.print("\t" + marker.name()));
			writer.println();
			Stream.of(lines).forEach(line -> writer.println(line.name() + "\t" + line.snpCalls().stream().collect(Collectors.joining("\t"))));

			System.out.println("Finished outputting genotype file: " + genotypes.getAbsolutePath());
			System.out.println("Wrote " + lines.length + " accessions / lines to file");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	class Marker
	{
		private String name;
		private String chromosome;
		private String position;

		public Marker(String name, String chromosome, String position)
		{
			this.name = name;
			this.chromosome = chromosome;
			this.position = position;
		}

		public String name()
		{
			return name;
		}

		public String chromosome()
		{
			return chromosome;
		}

		public String position()
		{
			return position;
		}
	}

	class Line
	{
		private String name;
		private List<String> snpCalls;

		public Line(String name)
		{
			this.name = name;
			snpCalls = new ArrayList<String>();
		}

		public void addSnpCall(String call)
		{
			snpCalls.add(call);
		}

		public String name()
		{
			return name;
		}

		public List<String> snpCalls()
		{
			return snpCalls;
		}
	}

	private static void printHelp()
	{
		System.out.println("Usage: hapmap2flapjack <options>\n"
			+ " where valid options are:\n"
			+ "   -separator=<s or t>            (required separator used in input file)\n"
			+ "   -hapmap=<hapmap_file>          (required input file)\n"
			+ "   -map=<map_file>                (required output file)\n"
			+ "   -genotypes=<genotype_file>     (required output file)\n");
	}
}