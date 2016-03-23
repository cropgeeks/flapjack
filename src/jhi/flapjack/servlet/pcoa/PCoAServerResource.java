// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet.pcoa;

import java.io.*;
import java.util.*;

import jhi.flapjack.servlet.*;

import org.ggf.drmaa.*;
import org.restlet.data.*;

import org.restlet.representation.*;
import org.restlet.resource.*;

public class PCoAServerResource extends ServerResource
{
	private String id;

	private String flapjackUID;

	@Override
	public void doInit()
	{
		super.doInit();

		// Params for POST
		try
		{
			flapjackUID = getQueryValue("flapjackUID");
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
			args.add(FlapjackServlet.fjPath);
			args.add("jhi.flapjack.servlet.pcoa.PCoATask");
			args.add(FlapjackServlet.rPath);
			args.add(wrkDir.toString());
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
		return new StringRepresentation("/pcoa - " + new Date());
	}

	@Get("txt")
	public Representation getFitAsTextFile()
	{
		// TODO: How can we really be sure the job finished correctly?
		if (FlapjackServlet.isJobFinished(id))
		{
			// Work out where the working folder was (from the ID param)
			String taskId = id.substring(0, id.indexOf("-"));
			File wrkDir = FlapjackServlet.getWorkingDir(taskId);

			File fit = new File(wrkDir, "fit.txt");
			return new FileRepresentation(fit, MediaType.TEXT_PLAIN);
		}

		else
		{
			// HTTP 204 NO CONTENT
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