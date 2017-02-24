/*
 * // Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
 * // reserved. Use is subject to the accompanying licence terms.
 */

package jhi.flapjack.io.cmd;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import ch.systemsx.cisd.hdf5.*;

public class Hdf5ToFJTabbedConverter
{
	private static final String LINES   = "Lines";
	private static final String MARKERS = "Markers";

	private static final String DATA = "DataMatrix";

	private static final String STATE_TABLE = "StateTable";

	private File hdf5File;
	private LinkedHashSet<String> lines = null;
	private LinkedHashSet<String> markers = null;
	private boolean missingDataFilter = false;
	private boolean heterozygousFilter = false;

//	private Map<String, String> linesReplaced;

	private HashMap<String, Integer> lineInds;
	private HashMap<String, Integer> markerInds;

	private IHDF5Reader reader;

	private LinkedHashSet<String> hdf5Lines;
	private LinkedHashSet<String> hdf5Markers;

	private String outputFilePath;

	public static void main(String[] args)
	{
		Hdf5ToFJTabbedConverter extractor = new Hdf5ToFJTabbedConverter(args);
		extractor.readInput();
		extractor.extractData("");

		System.exit(0);
	}

	private Hdf5ToFJTabbedConverter(String[] args)
	{
		try
		{
			for (String arg : args)
			{
				if (arg.startsWith("-hdf5="))
					hdf5File = new File(arg.substring(6));
				if (arg.startsWith("-genotypes="))
					outputFilePath = arg.substring(11);
				if (arg.startsWith("-lines="))
					lines = new LinkedHashSet<>(Files.readAllLines(new File(arg.substring(7)).toPath()));
				if (arg.startsWith("-markers="))
					markers = new LinkedHashSet<>(Files.readAllLines(new File(arg.substring(9)).toPath()));
				if (arg.startsWith("-missingFilter="))
					missingDataFilter = Boolean.valueOf(arg.substring(15));
				if (arg.startsWith("-heterozygousFilter="))
					heterozygousFilter = Boolean.valueOf(arg.substring(21));
			}

			if (hdf5File == null || outputFilePath == null || lines == null || markers == null)
			{
				printHelp();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();

			printHelp();
		}
	}

	public Hdf5ToFJTabbedConverter(File hdf5File, List<String> lines, List<String> markers, String outputFilePath, boolean missingDataFilter, boolean heterozygousFilter)
	{
		// Setup input and output files
		this.hdf5File = hdf5File;
		this.lines = new LinkedHashSet<String>(lines);
		this.markers = new LinkedHashSet<String>(markers);
		this.outputFilePath = outputFilePath;

		// TODO: work out how we can implement these filters in a time efficient way
		this.missingDataFilter = missingDataFilter;
		this.heterozygousFilter = heterozygousFilter;
	}

	public void readInput()
	{
		reader = HDF5Factory.openForReading(hdf5File);

		long s = System.currentTimeMillis();

		System.out.println();
		System.out.println("Hdf5 file opened for reading: " + (System.currentTimeMillis() - s) + " (ms)");

		s = System.currentTimeMillis();
		// Load lines from HDF5 and find the indices of our loaded lines
		String[] hdf5LinesArray = reader.readStringArray(LINES);
		hdf5Lines = new LinkedHashSet<String>(Arrays.asList(hdf5LinesArray));

		if (lines == null)
			lines = hdf5Lines;
		else
			lines = lines.stream().filter(line -> hdf5Lines.contains(line)).collect(Collectors.toCollection(LinkedHashSet::new));

		lineInds = new HashMap<>();
		for (int i = 0; i < hdf5LinesArray.length; i++)
			lineInds.put(hdf5LinesArray[i], i);

		System.out.println();
		System.out.println("Read and filtered lines: " + (System.currentTimeMillis() - s) + " (ms)");

		s = System.currentTimeMillis();
		// Load markers from HDF5 and find the indices of our loaded markers
		String[] hdf5MarkersArray = reader.readStringArray(MARKERS);
		hdf5Markers = new LinkedHashSet<String>(Arrays.asList(hdf5MarkersArray));

		if (markers == null)
			markers = hdf5Markers;
		else
			markers = markers.stream().filter(marker -> hdf5Markers.contains(marker)).collect(Collectors.toCollection(LinkedHashSet::new));

		markerInds = new HashMap<>();
		for (int i = 0; i < hdf5MarkersArray.length; i++)
			markerInds.put(hdf5MarkersArray[i], i);

		System.out.println();
		System.out.println("Read and filtered markers: " + (System.currentTimeMillis() - s) + " (ms)");

		reader.close();
	}

	public void extractData(String headerLines)
	{
		System.out.println();
		long s = System.currentTimeMillis();
		List<Integer> markerIndices = markers.parallelStream().map(marker -> markerInds.get(marker)).collect(Collectors.toList());
		System.out.println("Read and mapped markers: " + (System.currentTimeMillis() - s) + " (ms)");

		reader = HDF5Factory.openForReading(hdf5File);

		s = System.currentTimeMillis();
		String[] stateTable = reader.readStringArray(STATE_TABLE);
		System.out.println("Read statetable: " + (System.currentTimeMillis() - s) + " (ms)");

		// Write our output file line by line
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath), "UTF8"))))
		{
			// Write header for drag and drop
			writer.println("# fjFile = GENOTYPE");

			// Output any extra header lines that have been provided such as db link urls
			if (!headerLines.isEmpty())
				writer.print(headerLines);

			// Write the header line of a Flapjack file
			writer.println(markers.parallelStream().collect(Collectors.joining("\t", "Accession/Marker\t", "")));

			s = System.currentTimeMillis();

			lines.stream().forEachOrdered(lineName ->
			{
				// Read in a line (all of its alleles from file)
				// Get from DATA, 1 row, markerInds.size() columns, start from row lineInds.get(lineName) and column 0.
				// The resulting 2d array only contains one 1d array. Take that as the lines genotype data.
				byte[] genotypes = reader.int8().readMatrixBlock(DATA, 1, markerInds.size(), lineInds.get(lineName), 0)[0];
				String outputGenotypes = createGenotypeFlatFileString(lineName, genotypes, markerIndices, stateTable);
				writer.println(outputGenotypes);
			});
			System.out.println("Output lines to genotype file: " + (System.currentTimeMillis() - s) + " (ms)");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		reader.close();

		System.out.println();
		System.out.println("HDF5 file converted to Flapjack genotype format");
	}

	private String createGenotypeFlatFileString(String lineName, byte[] genotypes, List<Integer> markerIndices, String[] stateTable)
	{
		// Collect the alleles which match the line and markers we're looking for
		return markerIndices.parallelStream()
			.map(index -> genotypes[index])
			.map(allele -> stateTable[allele])
			.collect(Collectors.joining("\t", lineName + "\t", ""));
	}

	public LinkedHashSet<String> getKeptMarkers()
	{
		// Filter the markers from the hdf5 file so that we have a list of only those markers that were in both the hdf5
		// file and the list of desired markers / input list
		LinkedHashSet<String> keptMarkers = hdf5Markers;
		keptMarkers.retainAll(markers);

		return keptMarkers;
	}

	private static void printHelp()
	{
		System.out.println("Usage: hdf52fj <options>\n"
			+ " where valid options are:\n"
			+ "   -hdf5=<hdf5_file>                               (required input file)\n"
			+ "   -lines=<file_list_of_lines_one_per_line>        (required input file)\n"
			+ "   -markers=<file_list_of_markers_one_per_line>    (required input file)\n"
			+ "   -missing_filter=<true/false>                    (required input string)\n"
			+ "   -heterozygous_filter=<true/false>               (required input string)\n"
			+ "   -genotypes=<genotypes_file>                     (required output file)\n");

		System.exit(1);
	}
}