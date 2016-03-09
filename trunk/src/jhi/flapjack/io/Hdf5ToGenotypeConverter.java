/*
 * // Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
 * // reserved. Use is subject to the accompanying licence terms.
 */

package jhi.flapjack.io;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import ch.systemsx.cisd.hdf5.*;

/**
 * Created by gs40939 on 17/08/2015.
 */
public class Hdf5ToGenotypeConverter
{
	private final File hdf5File;
	private final List<String> lines;
	private final List<String> markers;
	private final boolean missingDataFilter;
	private final boolean heterozygousFilter;

	private HashMap<String, Integer> markerInds;

	private IHDF5Reader reader;

	private List<String> hdf5Lines;
	private List<String> hdf5Markers;

	public Hdf5ToGenotypeConverter(File hdf5File, List<String> lines, List<String> markers, boolean missingDataFilter, boolean heterozygousFilter)
	{
		// Setup input and output files
		this.hdf5File = hdf5File;
		this.lines = lines;
		this.markers = markers;

		// TODO: work out how we can implement these filters in a time efficient way
		this.missingDataFilter = missingDataFilter;
		this.heterozygousFilter = heterozygousFilter;
	}

	public static void main(String[] args)
	{
		// Read the lists of lines and markers to include in the output file
		try
		{
			List<String> lines = Files.readAllLines(new File(args[2]).toPath());
			List<String> markers = Files.readAllLines(new File(args[3]).toPath());
			Hdf5ToGenotypeConverter extractor = new Hdf5ToGenotypeConverter(new File(args[0]), lines, markers, Boolean.valueOf(args[4]), Boolean.valueOf(args[5]));
			extractor.readInput();
			extractor.extractData(args[1]);
		}
		catch (IOException e) { e.printStackTrace(); }
	}

	public void readInput()
	{
		reader = HDF5Factory.openForReading(hdf5File);

		long s = System.currentTimeMillis();
		hdf5Lines = reader.getGroupMembers("Lines");
		lines.retainAll(hdf5Lines);
		System.out.println("Read and filter lines: " + ((System.currentTimeMillis() - s) / 1000));

		// Load markers from HDF5 and find the incides of our loaded markers
		String[] hdf5MarkersArray = reader.readStringArray("Markers");
		hdf5Markers = Arrays.asList(hdf5MarkersArray);

		markerInds = new HashMap<>();
		for (int i=0; i < hdf5MarkersArray.length; i++)
			markerInds.put(hdf5MarkersArray[i], i);

		reader.close();
	}

	public void extractData(String outputFile)
	{
		System.out.println("Extract data start");

		long s = System.currentTimeMillis();
		List<Integer> markerIndices = markers.parallelStream().map(marker -> markerInds.get(marker)).collect(Collectors.toList());
		System.out.println("Read and map markerIndices: " + ((System.currentTimeMillis() - s) / 1000));

		reader = HDF5Factory.openForReading(hdf5File);

		String[] stateTable = reader.readStringArray("StateTable");

		// Write our output file line by line
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF8"))))
		{
			s = System.currentTimeMillis();
			// Write the header line of a Flapjack file
			writer.println(markers.parallelStream().collect(Collectors.joining("\t", "Accession/Marker\t", "")));
			System.out.println("Output header line: " + ((System.currentTimeMillis() - s) / 1000));

			s = System.currentTimeMillis();
			lines.stream().forEachOrdered(lineName ->
			{
				// Read in a line (all of its alleles from file)
				byte[] genotypes = reader.int8().readArray("Lines/" + lineName);
				String outputGenotypes = createGenotypeFlatFileString(lineName, genotypes, markerIndices, stateTable);
				writer.println(outputGenotypes);
			});
			System.out.println("Read and output lines: " + ((System.currentTimeMillis() - s) / 1000));
		}
		catch (IOException e) { e.printStackTrace(); }

		reader.close();

		System.out.println("Extract data end");
	}

	private String createGenotypeFlatFileString(String lineName, byte[] genotypes, List<Integer> markerIndices, String[] stateTable)
	{
		// Collect the alleles which match the line and markers we're looking for
		return markerIndices.stream()
			.map(index -> genotypes[index])
			.map(allele -> stateTable[allele])
			.collect(Collectors.joining("\t", lineName + "\t", ""));
	}

	public List<String> getKeptMarkers()
	{
		// Filter the markers from the hdf5 file so that we have a list of only those markers that were in both the hdf5
		// file and the list of desired markers / input list
		List<String> keptMarkers = hdf5Markers;
		keptMarkers.retainAll(markers);

		return keptMarkers;
	}
}