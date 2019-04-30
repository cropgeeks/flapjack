// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.io.*;
import java.util.*;

import junit.framework.*;

import jhi.flapjack.data.*;
import jhi.flapjack.io.*;

public class FindLineTest extends TestCase
{
	private DataSet dataSet = new DataSet();

	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("flapjack.analysis.FindLineTest");
	}

	private void load()
		throws Exception
	{
		File mapFile = new File("tests\\5000.map");
		File genoFile = new File("tests\\100x5000.data");

		ChromosomeMapImporter mapImporter
			= new ChromosomeMapImporter(mapFile, dataSet);
		GenotypeDataImporter genoImporter = new GenotypeDataImporter(genoFile,
			dataSet, mapImporter.getMarkersHashMap(), "", "/", false, false);

		mapImporter.importMap();
		genoImporter.importGenotypeDataAsBytes();
	}

	public void testFindingLines()
		throws Exception
	{
		load();

		GTViewSet viewSet = new GTViewSet(dataSet, "Default View");

		FindLine finder = new FindLine(viewSet.getView(0), false, false);

		LinkedList<FindLine.Result> results = finder.search("LINE1");
		assertEquals(results.get(0).line.getLine().getName(), "LINE1");

		// Basic search for LINE10
		results = finder.search("LINE10");
		assertEquals(results.get(0).line.getLine().getName(), "LINE10");

		// Basic search for LINE50
		results = finder.search("LINE50");
		assertEquals(results.get(0).line.getLine().getName(), "LINE50");

		// Basic search for a line that doesn't exist
		results = finder.search("NOSUCHLINE");
		assertEquals(results.size(), 0);


		// Regular expressions
		finder = new FindLine(viewSet.getView(0), false, true);

		// Should find all lines
		results = finder.search("LINE.*");
		assertEquals(results.size(), 100);
		for (int i = 0; i < results.size(); i++)
			assertEquals(results.get(i).line.getLine().getName(), "LINE" + (i+1));

		// Find all lines with "3" as the first part of the number
		results = finder.search("LINE3.*");
		assertEquals(results.size(), 11);
		assertEquals(results.get(0).line.getLine().getName(), "LINE3");
		assertEquals(results.get(1).line.getLine().getName(), "LINE30");
		assertEquals(results.get(2).line.getLine().getName(), "LINE31");
		assertEquals(results.get(10).line.getLine().getName(), "LINE39");
	}
}