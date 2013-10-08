// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.servlet;

import javax.servlet.annotation.MultipartConfig;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Enumeration;
import java.util.Collection;
import java.util.zip.*;

import scri.commons.gui.*;

@MultipartConfig
public class DendrogramServlet extends HttpServlet
{
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		out.println("<html>");
		out.println("<p>DendrogramServlet - " + getClass() + "</p>");
		out.println("</html>");
		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		Part textfile = request.getPart("textfile");

		// Work out what everything is going to get called
		String id = SystemUtils.createGUID(32);
		File rScript = new File(SystemUtils.getTempDirectory(), id + ".R");
		File matrix = new File(SystemUtils.getTempDirectory(), id + ".matrix");
		File order  = new File(SystemUtils.getTempDirectory(), id + ".order");
		File png = new File(SystemUtils.getTempDirectory(), id + ".png");
		File pdf = new File(SystemUtils.getTempDirectory(), id + ".pdf");

		// Save the simmatrix file to disk
		saveMatrix(textfile, matrix, id);

		// Get the number of lines
		int lineCount = Integer.parseInt(request.getParameter("lineCount"));

		// Path to R
		String rPath = getServletContext().getInitParameter("r-path");

		// Write out the R script, replacing its variables as needed
		writeScript(rScript, id, lineCount);

		// Run R
		try
		{
			RunR runner = new RunR(rPath, matrix.getParentFile(), rScript);
			runner.runR();
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			return;
		}


/*		response.setContentType("image/png");
		OutputStream out = response.getOutputStream();

		BufferedInputStream in = new BufferedInputStream(
			new FileInputStream(new File(matrix.getParent(), id + ".png")));

		byte[] buffer = new byte[1024];
		for (int length = 0; (length = in.read(buffer)) > 0;)
			out.write(buffer, 0, length);

		in.close();
		out.close();
*/

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


//		PrintWriter out = response.getWriter();
//		out.println("<html>");
//		out.println("<p>OK</p>");
//		out.println("<h1>Username1: " + request.getParameter("username1") + "</h1>");
//		out.println("</html>");
//		out.close();

		// Tomcat picks its own temp folder for io.tmpdir, so it's probably best
		// to clean up when finished rather than assuming Tomcat will
//		rScript.delete();
//		matrix.delete();
//		order.delete();
//		png.delete();
	}

	private void saveMatrix(Part textfile, File matrix, String id)
		throws IOException
	{
		FileOutputStream out = new FileOutputStream(matrix);
        InputStream in = textfile.getInputStream();

		int read = 0;
		final byte[] bytes = new byte[1024];

		while ((read = in.read(bytes)) != -1)
			out.write(bytes, 0, read);

		out.close();
		in.close();
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
}