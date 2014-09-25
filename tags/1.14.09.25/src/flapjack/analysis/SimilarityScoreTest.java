// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.io.*;

import junit.framework.*;

import flapjack.data.*;
import flapjack.io.*;

public class SimilarityScoreTest extends TestCase
{
	private static DataSet dataSet = new DataSet();
	private static GTViewSet viewSet;

	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("flapjack.analysis.SimilarityScoreTest");
	}

	private void load()
		throws Exception
	{
		File mapFile = new File("tests\\tiny.map");
		File genoFile = new File("tests\\tiny.data");

		ChromosomeMapImporter mapImporter
			= new ChromosomeMapImporter(mapFile, dataSet);
		GenotypeDataImporter genoImporter = new GenotypeDataImporter(genoFile,
			dataSet, mapImporter.getMarkersHashMap(), "", true, "/");

		mapImporter.importMap();
		genoImporter.importGenotypeData(false);
	}

	public void testSimilarityScores()
		throws Exception
	{
		load();

	 	viewSet = new GTViewSet(dataSet, "Default View");
	 	viewSet.getView(0).cacheLines();

	 	float[][] matrix = dataSet.getStateTable().calculateSimilarityMatrix();

		// Test the first line (score of 5.0, 5 comparisons)
	 	SimilarityScore score = new SimilarityScore(viewSet, matrix, new boolean[] { true } );
	 	assertEquals(score.getScore(0, 0).score, 1.0f);

	 	// Test the second line (score of 2.0, 5 comparisons)
	 	score = new SimilarityScore(viewSet, matrix, new boolean[] { true } );
	 	assertEquals(score.getScore(0, 1).score, 2.0f / 5f);

	 	// Test the third line (score of 2.5, 5 comparisons)
	 	score = new SimilarityScore(viewSet, matrix, new boolean[] { true } );
	 	assertEquals(score.getScore(0, 2).score, 2.5f / 5f);

	 	// Test the fourth line (score of 3.0, 5 comparisons)
	 	score = new SimilarityScore(viewSet, matrix, new boolean[] { true } );
	 	assertEquals(score.getScore(0, 3).score, 3.0f / 5f);

	 	// Test the fifth line (score of 1.0, 5 comparisons)
	 	score = new SimilarityScore(viewSet, matrix, new boolean[] { true } );
	 	assertEquals(score.getScore(0, 4).score, 1.0f / 5f);
	}
}