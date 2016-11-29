// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet;

import java.io.*;
import javax.servlet.*;

import jhi.flapjack.servlet.dendrogram.*;
import jhi.flapjack.servlet.pcoa.*;

import org.restlet.*;
import org.restlet.routing.*;

import org.ggf.drmaa.*;
import org.restlet.resource.ResourceException;

public class FlapjackServlet extends Application implements ServletContextListener
{
	// DRMAA session object for submitting jobs to a queue management engine
	private static Session session = null;

	// Servlet context-parameters (overriden by values in META-INF/context.xml)
	public static String rPath = "/usr/bin/R";

	public static String fjPath;

	public FlapjackServlet()
	{
		setName("Flapjack");
		setDescription("Flapjack Web Services");
		setOwner("The James Hutton Institute");
		setAuthor("Information & Computational Sciences, JHI");
	}

	@Override
	public void start()
		throws Exception
	{
		super.start();

		// Read context parameters (context is null during the constructor)
		rPath = getContext().getParameters().getFirstValue("r.path");

		// Path to flapjack.jar
		ServletContext sc = (ServletContext) getContext().getAttributes().get(
			"org.restlet.ext.servlet.ServletContext");
		fjPath = sc.getRealPath("/WEB-INF/lib/flapjack.jar");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0)
	{

	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0)
	{
		try
		{
			if (session != null)
				session.exit();
		}
		catch (DrmaaException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Restlet createInboundRoot()
	{
		Router router = new Router(getContext());

		router.attach("/pcoa/",				PCoAServerResource.class);
		router.attach("/pcoa/{id}", 		PCoAServerResource.class);
		router.attach("/dendrogram/",		DendrogramServerResource.class);
		router.attach("/dendrogram/{id}",	DendrogramServerResource.class);

		return router;
	}

	public static File getWorkingDir(String taskId)
	{
		File tmpdir = new File(System.getProperty("java.io.tmpdir"), "flapjack");
		File wrkdir = new File(tmpdir, taskId);
		wrkdir.mkdirs();

		return wrkdir;
	}

	public static boolean isJobFinished(String id)
	{
		// Strip off the DRMAA job id
		String drmaaID = id.substring(id.lastIndexOf("-")+1);

		try
		{
			int status = session.getJobProgramStatus(drmaaID);

			switch (status)
			{
				case Session.DONE:
					System.out.println("####### Job " + drmaaID + " is DONE");
					return true;

				case Session.UNDETERMINED:
				case Session.FAILED:
					System.out.println("####### Job " + drmaaID + " UNDERTERMINED OR FAILED");
					throw new ResourceException(500);

				default:
					System.out.println("####### Job " + drmaaID + " is " + status);
					return false;
			}
		}
		catch (DrmaaException e)
		{
			e.printStackTrace();
			throw new ResourceException(500);
		}
	}

	public static void cancelJob(String id)
	{
		try
		{
			// Strip off the DRMAA job id
			String drmaaID = id.substring(id.lastIndexOf("-")+1);

			session.control(drmaaID, Session.TERMINATE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ResourceException(500);
		}
	}

	public static Session getDRMAASession()
	{
		// DRMAA NOTES:
		// Added drmaa.jar to ${catalina.base}/shared/lib/
		// Added the following to tomcat's bin/setenv.sh file:
		//   export LD_LIBRARY_PATH=/opt/sge/lib/lx-amd64
		// Added the following to ${catalina.base}/conf/catalina.properties
		//   shared.loader="${catalina.base}/shared/lib/*.jar"

		try
		{
			if (session == null)
			{
				SessionFactory factory = SessionFactory.getFactory();
				session = factory.getSession();
				session.init(null);
			}
		}
		catch (DrmaaException e)
		{
			e.printStackTrace();
		}

		return session;
	}
}