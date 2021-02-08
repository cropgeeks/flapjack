// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet.dendrogram;

import java.io.*;
import java.util.zip.*;

import jhi.flapjack.servlet.*;

public class DendrogramTask
{
	private int lineCount;

	private String rPath;
	private String wrkDir;

	public static void main(String args[])
		throws Exception
	{
		DendrogramTask task = new DendrogramTask();

		task.rPath = args[0];
		task.wrkDir = args[1];
		task.lineCount = Integer.parseInt(args[2]);

		task.run();
	}

	private void run()
		throws Exception
	{
		File rScript = new File(wrkDir, "script.R");
		File matrix = new File(wrkDir, "matrix.txt");

		// Write out the R script, replacing its variables as needed
		writeScript(rScript, lineCount);

		// Run R
		RunR runner = new RunR(rPath, matrix.getParentFile(), rScript);
		runner.runR();

		// And then make a zip of the results
		zipResults();
	}

	private void writeScript(File rScript, int lineCount)
		throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(
			getClass().getResourceAsStream("/src/arrr/Dendrogram.R")));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(rScript)));

		// Work out what width the image should be
		int pngWidth = 12 * lineCount;
		pngWidth = (pngWidth < 500) ? 500 : pngWidth;
		int pdfWidth = (int) Math.ceil(lineCount / 4);

		String str;
		while ((str = in.readLine()) != null)
		{
			str = str.replace("$MATRIX", "matrix.txt");
			str = str.replace("$ORDER", "order.txt");
			str = str.replace("$PNG_FILE", "dendrogram.png");
			str = str.replace("$PDF_FILE", "dendrogram.pdf");
			str = str.replace("$PNG_WIDTH", "" + pngWidth);
			str = str.replace("$PDF_WIDTH", "" + pdfWidth);

			out.println(str);
		}

		in.close();
		out.close();
	}

	private void zipResults()
	{
		File order  = new File(wrkDir, "order.txt");
		File png = new File(wrkDir, "dendrogram.png");
		File pdf = new File(wrkDir, "dendrogram.pdf");

		File zipFile = new File(wrkDir, "results.zip");
		try(ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFile));
			BufferedInputStream pngIn = new BufferedInputStream(new FileInputStream(png));
			BufferedInputStream pdfIn = new BufferedInputStream(new FileInputStream(pdf));
			BufferedInputStream orderIn = new BufferedInputStream(new FileInputStream(order)))
		{
			createZipEntry(zout, "dendrogram.png", pngIn);
			createZipEntry(zout, "dendrogram.pdf", pdfIn);
			createZipEntry(zout, "order.txt", orderIn);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void createZipEntry(ZipOutputStream zout, String name, BufferedInputStream inStream)
		throws IOException
	{
		// Send the png
		zout.putNextEntry(new ZipEntry(name));

		byte[] buffer = new byte[1024];
		for (int length; (length = inStream.read(buffer)) > 0; )
			zout.write(buffer, 0, length);
		inStream.close();
	}
}