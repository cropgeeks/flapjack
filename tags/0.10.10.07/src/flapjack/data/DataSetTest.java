// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.data;

import java.io.*;
import java.text.*;

import junit.framework.*;

import flapjack.io.*;

import scri.commons.file.*;

public class DataSetTest extends TestCase
{
	private DataSet dataSet;
	private StateTable stateTable;

	private BufferedWriter out;

	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("flapjack.data.DataSetTest");
	}

	private void load(File file1, File file2)
		throws Exception
	{
		dataSet = new DataSet();
		stateTable = dataSet.getStateTable();

		// Load the test data into the application
		ChromosomeMapImporter mapImporter
			= new ChromosomeMapImporter(file1, dataSet);
		GenotypeDataImporter genoImporter = new GenotypeDataImporter(file2,
			dataSet, mapImporter.getMarkersHashMap(), "", true, "/");

		mapImporter.importMap();
		genoImporter.importGenotypeData(false);
	}

	public void testLoadingTiny()
		throws Exception
	{
		File mapFile = new File("tests\\tiny.map");
		File genoFile = new File("tests\\tiny.data");
		load(mapFile, genoFile);

		File file1 = new File("tests\\tiny.test");
		File file2 = new File("tests\\tiny.junit");

		out = new BufferedWriter(new FileWriter(file1));
		printDataSet();

		assertEquals(FileUtils.readFile(file1), FileUtils.readFile(file2));
	}

	public void testLoading100x5000()
		throws Exception
	{
		File mapFile = new File("tests\\5000.map");
		File genoFile = new File("tests\\100x5000.data");
		load(mapFile, genoFile);

		File file1 = new File("tests\\100x5000.test");
		File file2 = new File("tests\\100x5000.junit");

		out = new BufferedWriter(new FileWriter(file1));
		printDataSet();

		assertEquals(FileUtils.readFile(file1), FileUtils.readFile(file2));
	}

/*
	public void testLoadingIllumina()
		throws Exception
	{
		File mapFile = new File("tests\\illumina.map");
		File genoFile = new File("tests\\illumina.data");
		load(mapFile, genoFile);

		File file1 = new File("tests\\illumina.test");
		File file2 = new File("tests\\illumina.junit");

		out = new BufferedWriter(new FileWriter(file1));
		printDataSet();

		assertEquals(FileUtils.readFile(file1), FileUtils.readFile(file2));
	}
*/

	public void printDataSet()
		throws IOException
	{
		int mapCount  = dataSet.countChromosomeMaps();
		int lineCount = dataSet.countLines();

		out.write(mapCount + " map(s) by " + lineCount + " line(s)");
		out.newLine();

		printStateTable();

		for (int i = 0; i < mapCount; i++)
		{
			ChromosomeMap map = dataSet.getMapByIndex(i);

			printMapHeaders(map);

			for (int j = 0; j < lineCount; j++)
			{
				Line line = dataSet.getLineByIndex(j);
				printLineData(line, map);
			}

			out.newLine();
		}

		out.close();
	}

	private void printStateTable()
		throws IOException
	{
		out.newLine();

		for (int i = 0; i < stateTable.size(); i++)
		{
			out.write(i + "\t" + stateTable.getAlleleState((short)i));
			out.newLine();
		}

		out.newLine();
	}

	private void printMapHeaders(ChromosomeMap map)
		throws IOException
	{
		DecimalFormat d = new DecimalFormat("0.00");

		out.write("Map " + map.getName() + " (" + map.countLoci() + " loci)");
		out.newLine();

		for (Marker marker: map)
			out.write("\t" + marker.getName() + " ("
				+ d.format(marker.getPosition()) + ")");

		out.newLine();
	}

	private void printLineData(Line line, ChromosomeMap map)
		throws IOException
	{
		out.write(line.getName());

		GenotypeData genoData = line.getGenotypeDataByMap(map);

		for (int i = 0; i < map.countLoci(); i++)
			out.write("\t" + genoData.getState(i));

		out.newLine();
	}
}