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
	private GenotypeDisplayPanel gdPanel;
	private OverviewDialog overviewDialog;

	WinMain()
	{
		setTitle(RB.getString("gui.WinMain.title"));

		gdPanel = new GenotypeDisplayPanel();

		overviewDialog = new OverviewDialog(this, gdPanel);
		overviewDialog.setVisible(true);
		gdPanel.setOverviewDialog(overviewDialog);

		add(gdPanel);

		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	public static DataSet getDataSet()
		throws IOException, DataFormatException
	{
		File mapFile = null;
		File genoFile = null;

		switch (Flapjack.DATASET)
		{
			case 1:
				mapFile = new File("data\\GVT_MAP_TEST.txt");
				genoFile = new File("data\\9574c52737e9bb23192d9537f269efd4.txt");
				break;
			case 2:
				mapFile = new File("data\\NEW_MAP_DATA_FOR_IAIN.txt");
				genoFile = new File("data\\NEW_GENOTYPE_DATA_FOR_IAIN.txt");
				break;
			case 3:
				mapFile = new File("data\\5000.map");
				genoFile = new File("data\\data_5000_100.txt");
				break;
			case 4:
				mapFile = new File("data\\5000.map");
				genoFile = new File("data\\data_5000_1000.txt");
				break;
			case 5:
				mapFile = new File("data\\5000.map");
				genoFile = new File("data\\data_5000_10000.txt");
				break;
			case 6:
				mapFile = new File("data\\5000.map");
				genoFile = new File("data\\data_5000_50000.txt");
				break;
			case 7:
				mapFile = new File("data\\illumina.map");
				genoFile = new File("data\\illumina.data");
				break;
		}

		return new DataLoadingDialog(mapFile, genoFile).getDataSet();
	}
}