// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet.pcoa;

import java.io.*;

import jhi.flapjack.servlet.*;

public class PCoATask
{
	private String rPath;
	private String wrkDir;

	private String noDimensions;

	public static void main(String args[])
		throws Exception
	{
		PCoATask task = new PCoATask();

		task.rPath = args[0];
		task.wrkDir = args[1];
		task.noDimensions = args[2];

		task.run();
	}

	private void run()
		throws Exception
	{
		File rScript = new File(wrkDir, "script.R");
		File matrix = new File(wrkDir, "matrix.txt");
		File fit  = new File(wrkDir, "fit.txt");

		// Write out the R script, replacing its variables as needed
		writeScript(rScript);

		// Run R
		RunR runner = new RunR(rPath, matrix.getParentFile(), rScript);
		runner.runR();

		// And then make a zip of the results
//		zipResults();
	}

	private void writeScript(File rScript)
		throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(
			getClass().getResourceAsStream("/src/arrr/PrincipalCoordinatesAnalysis.R")));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(rScript)));

		String str;
		while ((str = in.readLine()) != null)
		{
			str = str.replace("$MATRIX", "matrix.txt");
			str = str.replace("$FIT", "fit.txt");
			str = str.replace("$K", noDimensions);

			out.println(str);
		}

		in.close();
		out.close();
	}
}
