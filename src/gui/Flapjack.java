package flapjack.gui;

import java.io.*;

import flapjack.data.*;
import flapjack.io.*;

public class Flapjack
{
	public static void main(String[] args)
		throws IOException, DataFormatException
	{
		DataSet dataSet = new DataSet();

		File mapFile = new File("..\\Data\\GVT_MAP_TEST.txt");

		ChromosomeMapImporter mapImporter = new ChromosomeMapImporter(mapFile, dataSet);

		long s = System.currentTimeMillis();
		mapImporter.importMap();
		long e = System.currentTimeMillis();

		System.out.println("Map loaded in " + (e-s) + "ms");
	}
}