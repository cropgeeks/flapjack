package flapjack.data;

import java.io.*;
import java.text.*;

public class DataSetTest
{
	private DataSet dataSet;
	private StateTable stateTable;

	private BufferedWriter out;

	public DataSetTest(DataSet dataSet)
	{
		this.dataSet = dataSet;
		this.stateTable = dataSet.getStateTable();

//		out = new BufferedWriter(new PrintWriter(System.out));
		try { out = new BufferedWriter(new FileWriter("t.txt")); }
		catch (IOException e) { System.out.println(e); }
	}

	public void printDataSet()
		throws IOException
	{
		int mapCount  = dataSet.countChromosomeMaps();
		int lineCount = dataSet.countLines();

		out.write(mapCount + " map(s) by " + lineCount + " line(s)");
		out.newLine();

		printStateTable();

		for (int i = 0; i < mapCount; i++)
		{
			ChromosomeMap map = dataSet.getMapByIndex(i);

			printMapHeaders(map);

			for (int j = 0; j < lineCount; j++)
			{
				Line line = dataSet.getLineByIndex(j);
				printLineData(line, map);
			}

			out.newLine();
		}

		out.close();
	}

	private void printStateTable()
		throws IOException
	{
		out.newLine();

		for (int i = 0; i < stateTable.size(); i++)
		{
			out.write(i + "\t" + stateTable.getAlleleState((short)i));
			out.newLine();
		}

		out.newLine();
	}

	private void printMapHeaders(ChromosomeMap map)
		throws IOException
	{
		DecimalFormat d = new DecimalFormat("0.00");

		out.write("Map " + map.getName() + " (" + map.countLoci() + " loci)");
		out.newLine();

		for (Marker marker: map)
			out.write("\t" + marker.getName() + " ("
				+ d.format(marker.getPosition()) + ")");

		out.newLine();
	}

	private void printLineData(Line line, ChromosomeMap map)
		throws IOException
	{
		out.write(line.getName());

		GenotypeData genoData = line.getGenotypeDataByMap(map);

		for (int i = 0; i < map.countLoci(); i++)
			out.write("\t" + genoData.getState(i));

		out.newLine();
	}
}