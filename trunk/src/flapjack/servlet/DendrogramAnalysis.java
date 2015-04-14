// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.servlet;

import java.io.*;
import java.util.zip.*;
import javax.servlet.*;
import javax.servlet.http.*;

import scri.commons.gui.*;
import scri.commons.io.*;

public class DendrogramAnalysis implements FlapjackTask
{
	// Files storing the rScript and matrix to be worked on.
	private File rScript;
	private File matrix;
	// Files to store the output of the analysis
	private File order;
	private File png;
	private File pdf;

	private String rPath;

	public DendrogramAnalysis(HttpServletRequest request, String rPath)
		throws ServletException, IOException
	{
		Part textfile = request.getPart("textfile");
		this.rPath = rPath;

		// Work out what everything is going to get called
		String id = SystemUtils.createGUID(32);
		rScript = new File(FileUtils.getTempDirectory(), id + ".R");
		matrix = new File(FileUtils.getTempDirectory(), id + ".matrix");
		order  = new File(FileUtils.getTempDirectory(), id + ".order");
		png = new File(FileUtils.getTempDirectory(), id + ".png");
		pdf = new File(FileUtils.getTempDirectory(), id + ".pdf");

		// Save the simmatrix file to disk
		textfile.write(matrix.getPath());

		// Get the number of lines
		int lineCount = Integer.parseInt(request.getParameter("lineCount"));

		// Write out the R script, replacing its variables as needed
		writeScript(rScript, id, lineCount);
	}

	@Override
	public void writeResponse(HttpServletResponse response)
		throws IOException
	{
		response.setContentType("application/zip");
		ZipOutputStream zout = new ZipOutputStream(response.getOutputStream());

		// Send the png
		zout.putNextEntry(new ZipEntry("dendrogram.png"));
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(png));
		byte[] buffer = new byte[1024];
		for (int length = 0; (length = in.read(buffer)) > 0;)
			zout.write(buffer, 0, length);
		in.close();

		// Send the pdf
		zout.putNextEntry(new ZipEntry("dendrogram.pdf"));
		in = new BufferedInputStream(new FileInputStream(pdf));
		buffer = new byte[1024];
		for (int length = 0; (length = in.read(buffer)) > 0;)
			zout.write(buffer, 0, length);
		in.close();

		// Send the order
		zout.putNextEntry(new ZipEntry("order.txt"));
		in = new BufferedInputStream(new FileInputStream(order));
		buffer = new byte[1024];
		for (int length = 0; (length = in.read(buffer)) > 0;)
			zout.write(buffer, 0, length);
		in.close();

		zout.close();


		// Tomcat picks its own temp folder for io.tmpdir, so it's probably best
		// to clean up when finished rather than assuming Tomcat will
		rScript.delete();
		matrix.delete();
		order.delete();
		png.delete();
		pdf.delete();
	}

	private void writeScript(File rScript, String id, int lineCount)
		throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(
			getClass().getResourceAsStream("/src/arrr/Dendrogram.R")));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(rScript)));

		// Work out what width the image should be
		int pngWidth = 12 * lineCount;
		pngWidth = (pngWidth < 500) ? 500 : pngWidth;
		int pdfWidth = (int) Math.ceil(lineCount / 4);

		String str = null;
		while ((str = in.readLine()) != null)
		{
			str = str.replace("$MATRIX", id + ".matrix");
			str = str.replace("$ORDER", id + ".order");
			str = str.replace("$PNG_FILE", id + ".png");
			str = str.replace("$PDF_FILE", id + ".pdf");
			str = str.replace("$PNG_WIDTH", "" + pngWidth);
			str = str.replace("$PDF_WIDTH", "" + pdfWidth);

			out.println(str);
		}

		in.close();
		out.close();
	}

	@Override
	public DendrogramAnalysis call()
		throws Exception
	{
		RunR runner = new RunR(rPath, matrix.getParentFile(), rScript);
		runner.runR();

		return this;
	}
}