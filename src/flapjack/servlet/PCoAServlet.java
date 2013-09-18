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
public class PCoAServlet extends HttpServlet
{
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		out.println("<html>");
		out.println("<p>PCoAServlet - " + getClass() + "</p>");
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
		File fit  = new File(SystemUtils.getTempDirectory(), id + ".fit");

		// Save the simmatrix file to disk
		saveMatrix(textfile, matrix, id);

		// Path to R
		String rPath = getServletContext().getInitParameter("r-path");

		// Write out the R script, replacing its variables as needed
		writeScript(rScript, id);

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


		// Send the fit
		zout.putNextEntry(new ZipEntry("fit.txt"));
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(fit));
		byte[] buffer = new byte[1024];
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
}