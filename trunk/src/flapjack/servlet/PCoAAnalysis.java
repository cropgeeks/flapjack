// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.zip.*;

import scri.commons.gui.*;

public class PCoAAnalysis implements FlapjackTask
{
	// Files storing the rScript and matrix to be worked on.
	private File rScript;
	private File matrix;
	// File to store the output of the analysis
	private File fit;

	private String rPath;

	public PCoAAnalysis(HttpServletRequest request, String rPath)
		throws ServletException, IOException
	{
		Part textfile = request.getPart("textfile");
		this.rPath = rPath;

		// Work out what everything is going to get called
		String id = SystemUtils.createGUID(32);
		rScript = new File(SystemUtils.getTempDirectory(), id + ".R");
		matrix = new File(SystemUtils.getTempDirectory(), id + ".matrix");
		fit  = new File(SystemUtils.getTempDirectory(), id + ".fit");

		// Save the simmatrix file to disk
		textfile.write(matrix.getPath());

		// Write out the R script, replacing its variables as needed
		writeScript(rScript, id);
	}

	private void writeScript(File rScript, String id)
		throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(
			getClass().getResourceAsStream("/src/arrr/PrincipalCoordinatesAnalysis.R")));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(rScript)));

		String str = null;
		while ((str = in.readLine()) != null)
		{
			str = str.replace("$MATRIX", id + ".matrix");
			str = str.replace("$FIT", id + ".fit");

			out.println(str);
		}

		in.close();
		out.close();
	}

	@Override
	public void writeResponse(HttpServletResponse response)
		throws IOException
	{
		response.setContentType("application/zip");
		ZipOutputStream zout = new ZipOutputStream(response.getOutputStream());

		// Send the fit
		zout.putNextEntry(new ZipEntry("fit.txt"));
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(fit));
		byte[] buffer = new byte[1024];
		for (int length = 0; (length = in.read(buffer)) > 0;)
			zout.write(buffer, 0, length);
		in.close();

		zout.close();
	}

	@Override
	public PCoAAnalysis call()
		throws Exception
	{
		RunR runner = new RunR(rPath, matrix.getParentFile(), rScript);
		runner.runR();

		return this;
	}
}