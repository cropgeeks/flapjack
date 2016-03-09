/*
 * // Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
 * // reserved. Use is subject to the accompanying licence terms.
 */

package jhi.flapjack.io;

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
		if (args.length == 2)
		{
			System.out.println("Assuming default missing data string and heterozygous separator.");

			File dat = new File(args[0]);
			File out = new File(args[1]);

			GenotypeToHdf5Converter converter = new GenotypeToHdf5Converter(dat, out);
			converter.convertToHdf5();
		}
		else
		{
			System.out.println("Usage: jhi.flapjack.io.hdf5.GenotypeToHdf5Converter data_file output_file");
		}
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
			System.out.println("Usage: jhi.flapjack.io.hdf5.GenotypeToHdf5Converter data_file output_file");
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
				String[] tokens = line.split("\t");
				String[] markers = Arrays.copyOfRange(tokens, 1, tokens.length);
				writer.string().writeArray(MARKERS, markers, HDF5GenericStorageFeatures.GENERIC_DEFLATE);

				while((line = reader.readLine()) != null)
				{
					if (counter % 1000 == 0)
						System.out.println("Processed: " + counter);

					String[] columns = line.split("\t");
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
}