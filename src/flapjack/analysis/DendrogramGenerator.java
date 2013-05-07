// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.io.*;
import java.util.*;

import flapjack.data.*;
import flapjack.servlet.*;

import scri.commons.gui.*;

public class DendrogramGenerator extends SimpleJob
{
	private GTViewSet viewSet;

	public DendrogramGenerator(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
	}

	public void runJob(int index)
		throws Exception
	{
		// Send the data to the servlet
		ArrayList<ArrayList<Float>> lineScores = viewSet.lineScores;

		StringBuilder sb1 = new StringBuilder();

		// TODO: unsafe (needs to be actual list of line names)
		int lineCount = lineScores.get(lineScores.size()-1).size();

		for (int i = 0; i < lineCount; i++)
			sb1.append((i == 0 ? "":"\t") + "LINE-" + (i+1));
		sb1.append(System.getProperty("line.separator"));

		for (int i = 0; i < lineCount; i++)
		{
			StringBuilder sb2 = new StringBuilder();

			for (int j = 0; j < lineCount; j++)
			{
				if (j <= i)
					sb2.append("\t" + lineScores.get(i).get(j));
				else
					sb2.append("\t" + lineScores.get(j).get(i));
			}

			sb1.append(sb2.toString().trim());
			sb1.append(System.getProperty("line.separator"));
		}


		// Servlet has to run R...
		DendrogramClient client = new DendrogramClient();
		client.uploadFile(sb1, lineCount);

		// Retrieve the data (hopefully a png) back from the servlet
	}
}