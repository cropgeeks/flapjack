package flapjack.analysis;

import java.io.*;
import java.util.*;

import junit.framework.*;

import flapjack.data.*;
import flapjack.io.*;

public class AlleleStatisticsTest extends TestCase
{
	private DataSet dataSet = new DataSet();

	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("flapjack.analysis.AlleleStatisticsTest");
	}

	private void load()
		throws Exception
	{
		File mapFile = new File("tests\\tiny.map");
		File genoFile = new File("tests\\tiny.data");

		ChromosomeMapImporter mapImporter
			= new ChromosomeMapImporter(mapFile, dataSet);
		GenotypeDataImporter genoImporter
			= new GenotypeDataImporter(genoFile, dataSet, "", "/");

		mapImporter.importMap();
		genoImporter.importGenotypeData();
	}

	public void testStatistics()
		throws Exception
	{
		load();

		GTViewSet viewSet = new GTViewSet(dataSet, "Default View");

		AlleleStatistics statistics = new AlleleStatistics(viewSet);
		statistics.runJob();

		Vector<int[]> results = statistics.getResults();

		// This viewset only has one view
		int[] array = results.get(0);


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