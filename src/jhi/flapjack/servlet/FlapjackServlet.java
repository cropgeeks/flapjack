package jhi.flapjack.servlet;

import java.io.*;
import javax.servlet.*;

import org.restlet.*;
import org.restlet.routing.*;

import org.ggf.drmaa.*;
import org.restlet.resource.ResourceException;

public class FlapjackServlet extends Application implements ServletContextListener
{
	// DRMAA session object for submitting jobs to a queue mangement engine
	private static Session session = null;

	// Servlet context-parameters (overriden by values in WEB-INF/web.xml)
	public static String rPath = "/usr/bin/R";
	public static String tmpPath = "/tmp/flapjack-services";

	public FlapjackServlet()
	{
		setName("Flapjack");
		setDescription("Flapjack Web Services");
		setOwner("The James Hutton Institute");
		setAuthor("Information & Computational Sciences, JHI");

//		String rPath = getContext().getParameters().getFirstValue("r.path");
//		String tmpPath = getContext().getParameters().getFirstValue("tmp.path");
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
//		router.attach("/pcoa/{id}", 		PCoAByIdServerResource.class);
		router.attach("/dendrogram/",		DendrogramServerResource.class);
		router.attach("/dendrogram/{id}",	DendrogramServerResource.class);

		return router;
	}

	static File getWorkingDir(String taskId)
	{
		File tmpdir = new File(FlapjackServlet.tmpPath);
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
		// Strip off the DRMAA job id
		String drmaaID = id.substring(id.lastIndexOf("-")+1);

		// TODO
	}

	static Session getDRMAASession()
	{
		// DRMAA NOTES:
		// We added the following to wildcat.tomcat's .bashrc file
		// export LD_LIBRARY_PATH=/opt/sge/lib/lx-amd64:$LD_LIBRARY_PATH

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