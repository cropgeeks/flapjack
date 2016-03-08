package jhi.flapjack.servlet;

import java.io.*;
import java.util.zip.*;

import org.restlet.data.*;
import org.restlet.representation.*;
import org.restlet.resource.*;

import scri.commons.io.*;

public class DendrogramTask implements FlapjackTask
{
	private final int lineCount;

	private final String taskId;

	public DendrogramTask(String taskId, int lineCount)
	{
		this.taskId = taskId;
		this.lineCount = lineCount;
	}

	@Override
	public DendrogramTask call()
		throws Exception
	{
		File rScript = new File(FileUtils.getTempDirectory(), taskId + ".R");
		File matrix = new File(FileUtils.getTempDirectory(), taskId + ".matrix");

		// Write out the R script, replacing its variables as needed
		try
		{
			writeScript(rScript, taskId, lineCount);

			RunR runner = new RunR(FlapjackServlet.R_PATH, matrix.getParentFile(), rScript);
			runner.runR();

			rScript.delete();
			matrix.delete();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ResourceException(404);
		}

		return this;
	}

	private void writeScript(File rScript, String simMatrixId, int lineCount)
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
			str = str.replace("$MATRIX", simMatrixId + ".matrix");
			str = str.replace("$ORDER", simMatrixId + ".order");
			str = str.replace("$PNG_FILE", simMatrixId + ".png");
			str = str.replace("$PDF_FILE", simMatrixId + ".pdf");
			str = str.replace("$PNG_WIDTH", "" + pngWidth);
			str = str.replace("$PDF_WIDTH", "" + pdfWidth);

			out.println(str);
		}

		in.close();
		out.close();
	}

	@Override
	public Representation getRepresentation()
	{
		File order  = new File(FileUtils.getTempDirectory(), taskId + ".order");
		File png = new File(FileUtils.getTempDirectory(), taskId + ".png");
		File pdf = new File(FileUtils.getTempDirectory(), taskId + ".pdf");

		File zipFile = new File(FileUtils.getTempDirectory(), taskId + ".zip");
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
			throw new ResourceException(500);
		}

		FileRepresentation fileRep = new FileRepresentation(zipFile, MediaType.APPLICATION_ZIP);

		return fileRep;
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

	@Override
	public String getURI()
	{
		return FlapjackServlet.DENDROGRAM_ROUTE;
	}
}
