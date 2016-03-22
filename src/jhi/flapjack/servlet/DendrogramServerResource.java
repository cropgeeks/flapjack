package jhi.flapjack.servlet;

import java.io.*;
import java.util.*;

import org.restlet.data.*;
import org.restlet.representation.*;
import org.restlet.resource.*;

import org.ggf.drmaa.*;

public class DendrogramServerResource extends ServerResource
{
	private String id;

	private String flapjackUID;
	private int lineCount;

	@Override
	public void doInit()
	{
		super.doInit();

		// Params for POST
		try
		{
			flapjackUID = getQueryValue("flapjackUID");
			lineCount = Integer.parseInt(getQueryValue("lineCount"));
		}
		catch (Exception e) {}

		// Params for GET
		try
		{
			this.id = (String)getRequestAttributes().get("id");
		}
		catch (Exception e){}
	}

	@Post
	public void store(Representation representation)
	{
		String taskId = flapjackUID + System.currentTimeMillis();
		File wrkDir = FlapjackServlet.getWorkingDir(taskId);
		File matrix = new File(wrkDir, "matrix.txt");

		// Write out our similarity matrix to disk
		RestUtils.writeSimMatrix(matrix, representation);

		try
		{
			Session session = FlapjackServlet.getDRMAASession();

			JobTemplate jt = session.createJobTemplate();

			jt.setRemoteCommand("java");
			List<String> args = new ArrayList<>();
			args.add("-cp");
			args.add("/home/tomcat/www/webapps/flapjack-test/WEB-INF/lib/flapjack.jar");
			args.add("jhi.flapjack.servlet.DendrogramTask");
			args.add(FlapjackServlet.rPath);
			args.add(wrkDir.toString());
			args.add("" + lineCount);
			jt.setArgs(args);

			jt.setWorkingDirectory(wrkDir.toString());

			taskId += "-" + session.runJob(jt);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ResourceException(500);
		}

		// Returns /dendrogram/{id} for the client to use for progress/results
		redirectSeeOther(taskId);
	}

	@Get("html")
	public Representation getHtml()
	{
		return new StringRepresentation("/dendrogram - " + new Date());
	}

	@Get("zip")
	public Representation getDendrogramAsZipFile()
	{
		// TODO: How can we really be sure the job finished correctly?
		if (FlapjackServlet.isJobFinished(id))
		{
			// Work out where the working folder was (from the ID param)
			String taskId = id.substring(0, id.indexOf("-"));
			File wrkDir = FlapjackServlet.getWorkingDir(taskId);

			File zipFile = new File(wrkDir, "results.zip");
			return new FileRepresentation(zipFile, MediaType.APPLICATION_ZIP);
		}

		else
		{
			setStatus(org.restlet.data.Status.SUCCESS_NO_CONTENT);
			return null;
		}
	}

	@Delete
	public void cancelJob()
	{
		FlapjackServlet.cancelJob(id);
	}
}