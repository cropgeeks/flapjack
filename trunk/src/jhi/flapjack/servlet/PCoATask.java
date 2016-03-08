package jhi.flapjack.servlet;

import java.io.*;

import org.restlet.data.*;
import org.restlet.representation.*;
import org.restlet.resource.*;

import scri.commons.io.*;

public class PCoATask implements FlapjackTask
{
	private final String taskId;

	public PCoATask(String taskId)
	{
		this.taskId = taskId;
	}

	@Override
	public PCoATask call()
		throws Exception
	{
		// Work out what everything is going to get called
		File rScript = new File(FileUtils.getTempDirectory(), taskId + ".R");
		File matrix = new File(FileUtils.getTempDirectory(), taskId + ".matrix");
		File fit  = new File(FileUtils.getTempDirectory(), taskId + ".fit");

		// Write out the R script, replacing its variables as needed
		try
		{
			writeScript(rScript, taskId);

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

	private void writeScript(File rScript, String simMatrixId)
		throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(
			getClass().getResourceAsStream("/src/arrr/PrincipalCoordinatesAnalysis.R")));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(rScript)));

		String str;
		while ((str = in.readLine()) != null)
		{
			str = str.replace("$MATRIX", simMatrixId + ".matrix");
			str = str.replace("$FIT", simMatrixId + ".fit");

			out.println(str);
		}

		in.close();
		out.close();
	}

	@Override
	public Representation getRepresentation()
	{
		File fit  = new File(FileUtils.getTempDirectory(), taskId + ".fit");

		return new FileRepresentation(fit, MediaType.TEXT_ALL);
	}

	@Override
	public String getURI()
	{
		return FlapjackServlet.PCOA_ROUTE;
	}
}
