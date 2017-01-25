// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet.dendrogram;

import java.io.*;
import java.util.*;

import jhi.flapjack.servlet.*;
import static jhi.flapjack.servlet.FlapjackServlet.LOG;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;

import org.restlet.data.*;
import org.restlet.ext.fileupload.*;
import org.restlet.representation.*;
import org.restlet.resource.*;

import scri.commons.io.*;

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
	public Representation store(Representation entity)
		throws Exception
	{
		LOG.info("####################");

		String taskId = flapjackUID + System.currentTimeMillis();
		File wrkDir = FlapjackServlet.getWorkingDir(taskId);
//		File matrix = new File(wrkDir, "matrix.txt");

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

					List<String> args = new ArrayList<>();
					args.add("-cp");
					args.add(FlapjackServlet.fjPath);
					args.add("jhi.flapjack.servlet.dendrogram.DendrogramTask");
					args.add(FlapjackServlet.rPath);
					args.add(wrkDir.toString());
					args.add("" + lineCount);

					try
					{
						FlapjackServlet.getScheduler().initialize();
						String jobId = FlapjackServlet.getScheduler().submit(args, wrkDir.toString());

						taskId += "-" + jobId;

						LOG.info("TASKID: " + taskId);
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
		return new StringRepresentation("/dendrogram - " + new Date());
	}

	@Get("zip")
	public Representation getDendrogramAsZipFile()
	{
		try
		{
			// TODO: How can we really be sure the job finished correctly?
			if (FlapjackServlet.getScheduler().isJobFinished(id))
			{
				// Work out where the working folder was (from the ID param)
				String taskId = id.substring(0, id.indexOf("-"));
				File wrkDir = FlapjackServlet.getWorkingDir(taskId);

				File zipFile = new File(wrkDir, "results.zip");
				return new FileRepresentation(zipFile, MediaType.APPLICATION_ZIP);
			}

			else
			{
				// HTTP 204 NO CONTENT
				setStatus(org.restlet.data.Status.SUCCESS_NO_CONTENT);
				return null;
			}
		}
		catch (Exception e)
		{
			throw new ResourceException(500, e);
		}
	}

	@Delete
	public void cancelJob()
	{
		try
		{
			FlapjackServlet.getScheduler().cancelJob(id);
		}
		catch (Exception e)
		{
			throw new ResourceException(500, e);
		}
	}
}