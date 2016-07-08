/*
 * // Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
 * // reserved. Use is subject to the accompanying licence terms.
 */

package jhi.flapjack.io.cmd;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import ch.systemsx.cisd.hdf5.*;

public class GenotypeToHdf5Converter
{
	private static final String LINES = "Lines/";
	private static final String MARKERS = "Markers";

	private static final String HEADER_STRING = "Accession/Marker";

	private static final String STATE_TABLE = "StateTable";

	private File genotypeFile;
	private File hdf5File;

	public static void main(String args[])
	{
		File genotypeFile = null;
		File hdf5File = null;

		System.out.println("Assuming default missing data string and heterozygous separator.");

		for (int i = 0; i < args.length; i++)
		{
			if (args[i].startsWith("-genotypes="))
				genotypeFile = new File(args[i].substring(11));
			if (args[i].startsWith("-hdf5="))
				hdf5File = new File(args[i].substring(6));
		}

		if (genotypeFile == null || hdf5File == null)
		{
			printHelp();
			return;
		}

		GenotypeToHdf5Converter converter = new GenotypeToHdf5Converter(genotypeFile, hdf5File);
		converter.convertToHdf5();
	}

	public GenotypeToHdf5Converter(File genotypeFile, File hdf5File)
	{
		this.genotypeFile = genotypeFile;
		this.hdf5File = hdf5File;
	}

	private boolean checkFileExists(File file)
	{
		if (file.exists())
			return true;

		else
		{
			System.out.println("Genotype file doesn't exist. Please specify a valid genotype file.");
			printHelp();
			return false;
		}
	}

	void convertToHdf5()
	{
		if (checkFileExists(genotypeFile))
		{
			long s = System.currentTimeMillis();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(genotypeFile), "UTF-8")))
			{
				IHDF5Writer writer = HDF5Factory.open(hdf5File);
				LinkedHashMap<String, Byte> stateTable = new LinkedHashMap<>();
				stateTable.put("", (byte) 0);

				int counter = 0;

				String line = reader.readLine();
				while (line.length() == 0 || line.startsWith("#"))
				{
					line = reader.readLine();
				}

				// We need to generate a mapping between the marker indices in the
				// genotype file and those in the map file
				String[] tokens = line.split("\t", -1);
				String[] markers = Arrays.copyOfRange(tokens, 1, tokens.length);
				writer.string().writeArray(MARKERS, markers, HDF5GenericStorageFeatures.GENERIC_DEFLATE);

				while((line = reader.readLine()) != null)
				{
					if (counter % 1000 == 0)
						System.out.println("Processed: " + counter);

					String[] columns = line.split("\t", -1);
					// The actual SNP calls are all but the first element of the
					// split line
					String[] snpCalls = Arrays.copyOfRange(columns, 1, columns.length);
					Stream.of(snpCalls).forEach(token -> stateTable.putIfAbsent(token, (byte) stateTable.size()));

					Byte[] bytes = Stream.of(snpCalls).map(token -> stateTable.get(token)).toArray(Byte[]::new);
					byte[] outBytes = convertBytesToPrimitive(bytes);

					if (outBytes.length != markers.length)
						continue;

					String genoName = columns[0].replaceAll("/", "_");
					writer.int8().writeArray(LINES + genoName, outBytes, HDF5IntStorageFeatures.INT_DEFLATE);
					counter++;
				}
				writer.string().writeArray(STATE_TABLE, stateTable.keySet().toArray(new String[stateTable.keySet().size()]), HDF5GenericStorageFeatures.GENERIC_DEFLATE);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			System.out.println("Took: " + ((System.currentTimeMillis()-s)/1000));
		}
	}

	private byte[] convertBytesToPrimitive(Byte[] bytes)
	{
		byte[] outBytes = new byte[bytes.length];
		int i = 0;
		for (Byte b : bytes)
			outBytes[i++] = b.byteValue();

		return outBytes;
	}

	private static void printHelp()
	{
		System.out.println("Usage: geno2hdf5 <options>\n"
			+ " where valid options are:\n"
			+ "   -genotypes=<genotypes_file>    (required input file)\n"
			+ "   -hdf5=<hdf5_file>              (required output file)\n");
	}
}