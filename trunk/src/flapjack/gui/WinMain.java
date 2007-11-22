package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.dialog.*;
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

//		File mapFile = new File("..\\Data\\GVT_MAP_TEST.txt");
//		File mapFile = new File("..\\Data\\NEW_MAP_DATA_FOR_IAIN.txt");
		File mapFile = new File("..\\Data\\5000.map");


//		File genoFile = new File("..\\Data\\9574c52737e9bb23192d9537f269efd4.txt");
//		File genoFile = new File("..\\Data\\NEW_GENOTYPE_DATA_FOR_IAIN.txt");
//		File genoFile = new File("..\\Data\\data_5000_100.txt");
		File genoFile = new File("..\\Data\\data_5000_1000.txt");
//		File genoFile = new File("..\\Data\\data_5000_10000.txt");
//		File genoFile = new File("..\\Data\\data_5000_50000.txt");
//		File genoFile = new File("..\\Data\\data_5000_100000.txt");


		return new DataLoadingDialog(mapFile, genoFile).getDataSet();
	}
}