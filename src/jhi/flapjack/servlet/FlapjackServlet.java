// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.servlet.*;
import jhi.flapjack.servlet.api.*;

import jhi.flapjack.servlet.dendrogram.*;
import jhi.flapjack.servlet.pcoa.*;

import org.restlet.*;
import org.restlet.engine.application.*;
import org.restlet.routing.*;

public class FlapjackServlet extends Application implements ServletContextListener
{
	public static Logger LOG;

//	private static IScheduler scheduler = new DRMAAScheduler();
//	private static IScheduler scheduler = new SLURMScheduler();
	private static IScheduler scheduler = new ProcessScheduler();

	// Servlet context-parameters (overriden by values in META-INF/context.xml)
	public static String rPath = "/usr/bin/R";

	public static String fjPath;

	public FlapjackServlet()
	{
		setName("Flapjack");
		setDescription("Flapjack Web Services");
		setOwner("The James Hutton Institute");
		setAuthor("Information & Computational Sciences, JHI");

		LOG = Logger.getLogger(FlapjackServlet.class.getName());
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
			scheduler.destroy();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Restlet createInboundRoot()
	{
		Router router = new Router(getContext());

		// Set the Cors filter
		CorsFilter corsFilter = new CorsFilter(getContext(), router);
		corsFilter.setAllowedOrigins(new HashSet<>(Collections.singletonList("*")));
		corsFilter.setAllowedCredentials(true);
		corsFilter.setSkippingResourceForCorsOptions(false);

		router.attach("/pcoa",				PCoAServerResource.class);
		router.attach("/pcoa/",				PCoAServerResource.class);
		router.attach("/pcoa/{id}", 		PCoAServerResource.class);
		router.attach("/dendrogram",		DendrogramServerResource.class);
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

	public static IScheduler getScheduler()
		{ return scheduler; }
}