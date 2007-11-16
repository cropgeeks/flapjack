package flapjack.gui;

import java.io.*;

import flapjack.data.*;
import flapjack.io.*;

public class Flapjack
{
	public static void main(String[] args)
		throws Exception
	{
		getDataSet();
	}

	public static DataSet getDataSet()
		throws IOException, DataFormatException
	{
		DataSet dataSet = new DataSet();


		File mapFile = new File("..\\Data\\GVT_MAP_TEST.txt");
//		File mapFile = new File("..\\Data\\NEW_MAP_DATA_FOR_IAIN.txt");

		ChromosomeMapImporter mapImporter = new ChromosomeMapImporter(mapFile, dataSet);

		long s = System.currentTimeMillis();
		mapImporter.importMap();
		long e = System.currentTimeMillis();

		System.out.println("Map loaded in " + (e-s) + "ms");



		File genoFile = new File("..\\Data\\9574c52737e9bb23192d9537f269efd4.txt");
//		File genoFile = new File("..\\Data\\NEW_GENOTYPE_DATA_FOR_IAIN.txt");

		GenotypeDataImporter genoImporter = new GenotypeDataImporter(genoFile, dataSet);

		s = System.currentTimeMillis();
		genoImporter.importGenotypeData();
		e = System.currentTimeMillis();

		System.out.println("Genotype data loaded in " + (e-s) + "ms");

		return dataSet;
	}

}