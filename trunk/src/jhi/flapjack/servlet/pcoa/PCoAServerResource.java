// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet.pcoa;

import java.io.*;
import java.util.*;

import jhi.flapjack.servlet.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.ggf.drmaa.*;
import org.restlet.data.*;
import org.restlet.ext.fileupload.*;
import org.restlet.representation.*;
import org.restlet.resource.*;

import scri.commons.io.*;

public class PCoAServerResource extends ServerResource
{
	private String id;

	private String flapjackUID;

	private String noDimensions;

	@Override
	public void doInit()
	{
		super.doInit();

		// Params for POST
		try
		{
			flapjackUID = getQueryValue("flapjackUID");
			noDimensions = getQueryValue("noDimensions");
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
	public Representation store(Representation entity)
		throws Exception
	{
		String taskId = flapjackUID + System.currentTimeMillis();
		File wrkDir = FlapjackServlet.getWorkingDir(taskId);

		if (entity != null && MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true))
		{
			DiskFileItemFactory factory = new DiskFileItemFactory();
			RestletFileUpload upload = new RestletFileUpload(factory);
			FileItemIterator fileIterator = upload.getItemIterator(entity);

			while (fileIterator.hasNext())
			{
				FileItemStream fi = fileIterator.next();
				if (fi.getFieldName().equals("matrix"))
				{
					FileUtils.writeFile(new File(wrkDir, "matrix.txt"), fi.openStream());

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
						args.add(noDimensions);
						jt.setArgs(args);

						jt.setWorkingDirectory(wrkDir.toString());

						taskId += "-" + session.runJob(jt);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						throw new ResourceException(500);
					}
				}
			}
		}
		else
		{
			throw new ResourceException(
				org.restlet.data.Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
		}

		return new StringRepresentation(taskId);
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