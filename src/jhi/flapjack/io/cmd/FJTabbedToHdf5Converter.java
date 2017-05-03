/*
 * // Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
 * // reserved. Use is subject to the accompanying licence terms.
 */

package jhi.flapjack.io.cmd;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import ch.systemsx.cisd.hdf5.*;

import org.apache.commons.cli.*;

public class FJTabbedToHdf5Converter
{
	private static final String LINES = "Lines";
	private static final String MARKERS = "Markers";

	private static final String DATA = "DataMatrix";

	private static final String STATE_TABLE = "StateTable";

	private File genotypeFile;
	private File hdf5File;

	public static void main(String args[])
	{
		System.out.println("Assuming default missing data string and heterozygous separator.");

		FJTabbedToHdf5Converter converter = new FJTabbedToHdf5Converter(args);
		converter.convertToHdf5();

		System.exit(0);
	}

	private FJTabbedToHdf5Converter(String[] args)
	{
		CmdOptions options = new CmdOptions()
			.withAdvancedOptions()
			.withGenotypeFile(true)
			.addRequiredOption("h", "hdf5", true, "FILE", "Required output file");

		try
		{
			CommandLine line = new DefaultParser().parse(options, args);

			if (line.hasOption("genotypes"))
				genotypeFile = new File(line.getOptionValue("genotypes"));
			if (line.hasOption("hdf5"))
				hdf5File = new File(line.getOptionValue("hdf5"));
		}
		catch (Exception e)
		{
			options.printHelp("FJTabbedToHdf5Converter");

			System.exit(1);
		}
	}

	public FJTabbedToHdf5Converter(File genotypeFile, File hdf5File)
	{
		this.genotypeFile = genotypeFile;
		this.hdf5File = hdf5File;
	}

	private void checkFileExists(File file)
	{
		if (!file.exists())
		{
			System.err.println("Genotype file doesn't exist. Please specify a valid genotype file.");
			printHelp();
		}
	}

	public void convertToHdf5()
	{
		checkFileExists(genotypeFile);

		long s = System.currentTimeMillis();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(genotypeFile), "UTF-8"));
			 // The second reader is just to get the number of rows
			 LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(new FileInputStream(genotypeFile), "UTF-8")))
		{
			// Delete old files with this name, because otherwise the new data will get appended to the old data
			if(hdf5File.exists() && hdf5File.isFile())
				hdf5File.delete();

			IHDF5Writer writer = HDF5Factory.open(hdf5File);
			LinkedHashMap<String, Byte> stateTable = new LinkedHashMap<>();
			stateTable.put("", (byte) 0);

			int counter = 0;

			// Count the number of header rows and skip them
			int offset = 0;
			String line = reader.readLine();
			while (line.length() == 0 || line.startsWith("#"))
			{
				offset++;
				line = reader.readLine();
			}

			// Skip to the end
			lineNumberReader.skip(Long.MAX_VALUE);

			// Get the number of actual data rows
			int nrOfRows = lineNumberReader.getLineNumber() - 1 - offset;

			// We need to generate a mapping between the marker indices in the
			// genotype file and those in the map file
			String[] tokens = line.split("\t", -1);
			String[] markers = Arrays.copyOfRange(tokens, 1, tokens.length);

			// Here we determine the size of the chunks within the matrix.
			// HDF5 has a hard limit of 4GB per chunk, so we need to set the chunk sizes appropriately.
			// IMPORTANT: If we ever move away from using bytes for the states, then this needs to be adjusted.
			long fourGig = 4L * 1024L * 1024L * 1024L;
			// The number of rows is at least one and then depends on the number of times we can fit all the markers into 4GB
			int accessionChunk = (int) Math.min(nrOfRows, Math.max(1, Math.floor(fourGig / (markers.length * 1d))));
			// The number of columns is at most the number of markers and if the row is more than 4GB, then it's  the maximal number of columns that fit in 4GB
			int markerChunk = (int) Math.min(markers.length, fourGig);

			// Create the matrix based on the number of rows and the number of markers
			writer.int8().createMatrix(DATA, nrOfRows, markers.length, accessionChunk, markerChunk);

			// Remember the line names
			List<String> lines = new ArrayList<>();

			while ((line = reader.readLine()) != null)
			{
				if (counter % 1000 == 0)
					System.out.println("Processed: " + counter);

				String[] columns = line.split("\t", -1);

				// Remember the line name
				lines.add(columns[0]);

				// The actual SNP calls are all but the first element of the split line
				String[] snpCalls = Arrays.copyOfRange(columns, 1, columns.length);
				Stream.of(snpCalls).forEach(token -> stateTable.putIfAbsent(token, (byte) stateTable.size()));

				Byte[] bytes = Stream.of(snpCalls).map(token -> stateTable.get(token)).toArray(Byte[]::new);
				byte[] outBytes = convertBytesToPrimitive(bytes);

				if (outBytes.length != markers.length)
					continue;

				// Place the array in a dummy 2d array
				byte[][] outMatrixBytes = new byte[1][outBytes.length];
				outMatrixBytes[0] = outBytes;

				// Write the row as a block to the matrix
				writer.int8().writeMatrixBlock(DATA, outMatrixBytes, counter, 0);
				counter++;
			}

			// Write the marker and line names as arrays
			writer.string().writeArray(MARKERS, markers, HDF5GenericStorageFeatures.GENERIC_DEFLATE);
			writer.string().writeArray(LINES, lines.toArray(new String[lines.size()]), HDF5GenericStorageFeatures.GENERIC_DEFLATE);

			// Write the state table
			writer.string().writeArray(STATE_TABLE, stateTable.keySet().toArray(new String[stateTable.keySet().size()]), HDF5GenericStorageFeatures.GENERIC_DEFLATE);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Took: " + ((System.currentTimeMillis() - s) / 1000f));
	}

	private byte[] convertBytesToPrimitive(Byte[] bytes)
	{
		byte[] outBytes = new byte[bytes.length];
		int i = 0;
		for (Byte b : bytes)
			outBytes[i++] = b;

		return outBytes;
	}

	private static void printHelp()
	{
		System.out.println("Usage: fj2hdf5 <options>\n"
			+ " where valid options are:\n"
			+ "   -genotypes=<genotypes_file>    (required input file)\n"
			+ "   -hdf5=<hdf5_file>              (required output file)\n");

		System.exit(1);
	}
}