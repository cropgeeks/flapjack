// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.cmd;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Transpose
{
	private static final int CACHELINES = 5000;

	private List<String[]> data = new ArrayList<>(CACHELINES);
	private int maxCols = 0;
	private List<File> cache = new ArrayList<>();

	private static File folder;

	public static void main(String[] args) throws IOException
	{
		long s = System.currentTimeMillis();

		folder = Files.createTempDirectory("transpose").toFile();
		folder.deleteOnExit();

		Transpose t = new Transpose();
		System.out.println("Reading input...");
		t.readData(args[0]);
		System.out.println("Read time: " + (System.currentTimeMillis() - s) + "ms");
		System.out.println("Transposing output...");
		t.writeData(args[1]);

		long e = System.currentTimeMillis();
		System.out.println("Time: " + (e - s) + "ms");
	}

	private void readData(String filename) throws IOException
	{
		try (BufferedReader in = new BufferedReader(new FileReader(filename)))
		{
			String str;
			int line = 0;
			long s = System.currentTimeMillis();
			while (((str = in.readLine()) != null) && (str.length() > 0))
			{
				this.data.add(str.split("\t", -1));
				line++;
				if (this.data.size() == CACHELINES)
				{
					System.out.println(line + " - read:  " + (System.currentTimeMillis() - s) + "ms");

					s = System.currentTimeMillis();
					writeCache();
					System.out.println(line + " - cache: " + (System.currentTimeMillis() - s) + "ms");

					s = System.currentTimeMillis();
				}
			}
			writeCache();
		}
	}

	private void writeCache() throws IOException
	{
		int cols = this.data.get(0).length;
		this.maxCols = cols;
		if (this.cache.size() == 0)
		{
			for (int i = 0; i < cols; i++)
			{
				File file = new File(folder, "_transpose_temp_" + i);
				file.delete();

				this.cache.add(file);
			}
		}
		for (int i = 0; i < cols; i++)
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(cache.get(i), true));
			for (String[] aData : data)
			{
				try
				{
					out.write(aData[i] + "\t");
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					e.printStackTrace();
				}
			}
			out.close();
		}
		data.clear();
	}

	private void writeData(String filename) throws IOException
	{
		try (BufferedWriter out = new BufferedWriter(new FileWriter(filename)))
		{
			for (int i = 0; i < maxCols; i++)
			{
				BufferedReader in = new BufferedReader(new FileReader(cache.get(i)));
				String line = in.readLine();
				out.write(line.substring(0, line.length() - 1));
				if(i < maxCols - 1)
					out.newLine();
				in.close();

				cache.get(i).delete();
			}
		}
	}
}