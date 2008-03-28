package flapjack.analysis;

import java.io.*;

import junit.framework.*;

import flapjack.data.*;
import flapjack.io.*;

public class FindLineTest extends TestCase
{
	private DataSet dataSet = new DataSet();

	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("flapjack.data.DataSetTest");
	}

	private void load()
		throws Exception
	{
		File mapFile = new File("tests\\5000.map");
		File genoFile = new File("tests\\100x5000.data");

		ChromosomeMapImporter mapImporter
			= new ChromosomeMapImporter(mapFile, dataSet);
		GenotypeDataImporter genoImporter
			= new GenotypeDataImporter(genoFile, dataSet, "", "/");

		mapImporter.importMap();
		genoImporter.importGenotypeData();
	}

	public void testFindingMarkers()
		throws Exception
	{
		load();

		GTViewSet viewSet = new GTViewSet(dataSet, "Default View");

		FindLine finder = new FindLine(viewSet.getView(0), true, false, false);

		// Test basic string matching
		assertEquals(finder.getIndex("LINE1"), 0);
		assertEquals(finder.getIndex("LINE10"), 9);
		assertEquals(finder.getIndex("LINE50"), 49);
		assertEquals(finder.getIndex("LINE100"), 99);
		assertEquals(finder.getIndex("NOSUCHLINE"), -1);
		// This should test wrapping from the end to the start
		assertEquals(finder.getIndex("LINE5"), 4);
		assertEquals(finder.getIndex("LINE25"), 24);
		// And go back now...
		finder.setFindNext(false);
		assertEquals(finder.getIndex("LINE20"), 19);
		assertEquals(finder.getIndex("LINE1"), 0);


		// Reset the index
		finder = new FindLine(viewSet.getView(0), true, false, true);

		// Test regex pattern matching
		assertEquals(finder.getIndex("LINE.*"), 0);
		assertEquals(finder.getIndex("LINE9.*"), 8);
		finder.setFindNext(false);
		assertEquals(finder.getIndex("LINE5.*"), 4);
		assertEquals(finder.getIndex("LINE.*"), 3);
	}
}