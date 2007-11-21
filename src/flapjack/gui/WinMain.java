package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.visualization.*;
import flapjack.io.*;

public class WinMain extends JFrame
{
	WinMain()
	{
		setTitle(RB.getString("gui.WinMain.title"));

		add(new GenotypeDisplayPanel());

		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	public static DataSet getDataSet()
		throws IOException, DataFormatException
	{
		DataSet dataSet = new DataSet();


//		File mapFile = new File("..\\Data\\GVT_MAP_TEST.txt");
//		File mapFile = new File("..\\Data\\NEW_MAP_DATA_FOR_IAIN.txt");
		File mapFile = new File("..\\Data\\5000.map");

		ChromosomeMapImporter mapImporter = new ChromosomeMapImporter(mapFile, dataSet);

		long s = System.currentTimeMillis();
		mapImporter.importMap();
		long e = System.currentTimeMillis();

		System.out.println("Map loaded in " + (e-s) + "ms");



//		File genoFile = new File("..\\Data\\9574c52737e9bb23192d9537f269efd4.txt");
//		File genoFile = new File("..\\Data\\NEW_GENOTYPE_DATA_FOR_IAIN.txt");
		File genoFile = new File("..\\Data\\data_5000_10000.txt");

		GenotypeDataImporter genoImporter = new GenotypeDataImporter(genoFile, dataSet);

		s = System.currentTimeMillis();
		genoImporter.importGenotypeData();
		e = System.currentTimeMillis();

		System.out.println("Genotype data loaded in " + (e-s) + "ms");


//		new DataSetTest(dataSet).printDataSet();

		return dataSet;
	}
}