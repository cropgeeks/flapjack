// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.io.*;
import java.util.*;

import junit.framework.*;

import jhi.flapjack.data.*;
import jhi.flapjack.io.*;

public class DataSummaryTest extends TestCase
{
	private DataSet dataSet = new DataSet();

	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("flapjack.analysis.DataSummaryTest");
	}

	private void load()
		throws Exception
	{
		File mapFile = new File("tests\\tiny.map");
		File genoFile = new File("tests\\tiny.data");

		ChromosomeMapImporter mapImporter
			= new ChromosomeMapImporter(mapFile, dataSet);
		GenotypeDataImporter genoImporter = new GenotypeDataImporter(genoFile,
			dataSet, mapImporter.getMarkersHashMap(), "", true, "/", false);

		mapImporter.importMap();
		genoImporter.importGenotypeData();
	}

	public void testStatistics()
		throws Exception
	{
		load();

		GTViewSet viewSet = new GTViewSet(dataSet, "Default View");

		boolean[] selectedChromosomes = new boolean[] { true };

		DataSummary statistics = new DataSummary(viewSet);
		statistics.runJob(0);

		ArrayList<long[]> results = statistics.getResults();

		// This viewset only has one view
		long[] array = results.get(0);


		assertEquals(array[0], 0);	// # of unknowns
		assertEquals(array[1], 4);	// # of A
		assertEquals(array[2], 9);	// # of G
		assertEquals(array[3], 3);	// # of C
		assertEquals(array[4], 3);	// # of T
		assertEquals(array[5], 4);	// # of A/G
		assertEquals(array[6], 1);	// # of F
		assertEquals(array[7], 1);	// # of A/T
	}
}