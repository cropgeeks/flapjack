// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.cmd;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.apache.commons.cli.*;

public class VcfToFJTabbedConverter
{
	private File vcfFile;
	private File mapFile;
	private File genotypeFile;

	private static final int FIRST_ACCESSION_COL = 9;
	private static final int BUFFER_SIZE = 5000;

	private final ArrayList<String[]> buffer = new ArrayList<>(BUFFER_SIZE);
	private final ArrayList<File> fileCache = new ArrayList<>();

	private int cols;

	public static void main(String[] args)
	{
		CmdOptions options = new CmdOptions()
			.withAdvancedOptions()
			.withGenotypeFile(true)
			.withMapFile(true)
			.addRequiredOption("v", "vcf", true, "FILE", "Required input file (in vcf format)");

		try
		{
			CommandLine line = new DefaultParser().parse(options, args);

			File mapFile = options.getMapFile(line);
			File genotypeFile = options.getGenotypeFile(line);
			File vcfFile = null;
			if (line.hasOption("v"))
				vcfFile = new File(line.getOptionValue("v"));

			VcfToFJTabbedConverter toGenotype = new VcfToFJTabbedConverter(vcfFile, mapFile, genotypeFile);
			toGenotype.convert();

			System.exit(0);
		}
		catch (Exception e)
		{
			options.printHelp("VcfToFJTabbedConverter");

			System.exit(1);
		}
	}

	public VcfToFJTabbedConverter(File vcfFile, File mapFile, File genotypeFile)
	{
		this.vcfFile = vcfFile;
		this.mapFile = mapFile;
		this.genotypeFile = genotypeFile;
	}

	public void convert()
	{
		try (BufferedReader reader = new BufferedReader(new FileReader(vcfFile));
			PrintWriter mapWriter = new PrintWriter(mapFile))
		{
			mapWriter.println("# fjFile = MAP");
			String line;
			while ((line = reader.readLine()) != null)
			{
				// Skip all headers
				// TODO: parse out chromsome lengths if they are available in ##CONTIG lines
				if (!line.startsWith("##"))
				{
					// Parse out the header line and get the list of accession names
					if (line.startsWith("#CHROM"))
					{
						String[] accessions = getAccessions(line);
						buffer.add(accessions);

						cols = accessions.length;

						initFileCache();
					}
					// Assume we have proper data (i.e. info on markers and their variant calls)
					else
					{
						String[] markerData = line.split("\t", -1);

						// Output marker data to mapFile
						String markerName = markerData[0].substring(3) + "_" + markerData[1];
						String chrName = markerData[0];
						String markerPos = markerData[1];

						mapWriter.println(markerName + "\t" + chrName + "\t" + markerPos);

						String[] genotypes = new String[markerData.length- FIRST_ACCESSION_COL + 1];
						genotypes[0] = markerName;

						for (int i=0; i < genotypes.length-1; i++)
							genotypes[i+1] = getGenotype(markerData[i + FIRST_ACCESSION_COL], markerData[3], markerData[4]);

						buffer.add(genotypes);

						// If we've filled our buffer output temporary files and reset the buffer
						if (buffer.size() == BUFFER_SIZE)
							writeCache();
					}
				}
			}
			// Flush the buffer as it may still be holding data
			writeCache();

			// Output the genotype file of the Flapjack format
			writeData();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	private String[] getAccessions(String line)
	{
		String[] headers = line.split("\t", -1);

		String[] accessions = new String[headers.length-9 + 1];
		accessions[0] = "Accession/Marker";
		System.arraycopy(headers, 9, accessions, 1, accessions.length -1);

		return accessions;
	}

	private String getGenotype(String cell, String ref, String alt)
	{
		String[] encoding = cell.split(":")[0].split("/");
		if (encoding.length == 2)
		{
			String allele1 = getAllele(encoding[0], ref, alt);
			String allele2 = getAllele(encoding[1], ref, alt);

			// Ignore cases with variants longer than 1 in length (no use to Flapjack)
			if (allele1.length() <= 1 && allele2.length() <= 1)
			{
				// Return an decoded allele in FJ format
				if (allele1.isEmpty())
					return allele2;
				else if (allele2.isEmpty())
					return allele1;
				else
					return Objects.equals(allele1, allele2) ? allele1 : allele1 + "/" + allele2;
			}
		}

		return "";
	}

	private String getAllele(String encoded, String ref, String alt)
	{
		String allele = "";

		try
		{
			int num = Integer.parseInt(encoded);
			// Alt can have more than one entry, take the one indicated by the number parsed from the encoded string
			allele = num == 0 ? ref : alt.split(",")[num-1];
		}
		catch (NumberFormatException | NullPointerException e) { }

		return allele;
	}

	private void initFileCache()
	{
		// Initialise a cache full of pointers to temp files for each column of data
		fileCache.clear();

		File tmp = new File(System.getProperty("java.io.tmpdir"));
		for (int i = 0; i < cols; i++)
		{
			File file = new File(tmp, "_transpose_temp_" + i);
			file.delete();

			fileCache.add(file);
		}
	}

	private void writeCache()
		throws Exception
	{
		for (int i = 0; i < cols; i++)
		{
			try (BufferedWriter out = new BufferedWriter(new FileWriter(fileCache.get(i), true)))
			{
				for (String[] aBuffer : buffer)
					out.write(aBuffer[i] + "\t");
			}
			catch (IOException e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		buffer.clear();
	}

	/**
	 * This is where we write our Flapjack style genotype file by reading each file (which represents a transposed
	 * column of data from the original VCF file) and simply outputing the file as a line of the new Flapjack
	 * genotype file.
	 *
	 * @throws Exception
	 */
	private void writeData()
		throws Exception
	{
		try (PrintWriter writer = new PrintWriter(genotypeFile))
		{
			writer.println("# fjFile = GENOTYPE");

			fileCache.forEach(file ->
			{
				try (BufferedReader reader = new BufferedReader(new FileReader(file)))
				{
					String line = reader.readLine();
					// Trim off an exepected single trailing tab from the line before output
					writer.println(line.substring(0, line.length()-1));
					file.delete();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					System.exit(1);
				}
			});
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
}