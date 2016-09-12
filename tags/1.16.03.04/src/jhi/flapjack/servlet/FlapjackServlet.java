// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import jhi.flapjack.servlet.DendrogramAnalysis.*;
import scri.commons.gui.SystemUtils;

@MultipartConfig
public class FlapjackServlet extends HttpServlet
{
	private static Logger LOG;

	private ExecutorService executor;
	private HashMap<String, FutureTask<FlapjackTask>> runningTasks;

	@Override
	public void init(ServletConfig servletConfig)
		throws ServletException
	{
		super.init(servletConfig);

		// Get the number of threads to use that is specified in the web.xml
		int numthreads = Integer.parseInt(getServletContext().getInitParameter("numthreads"));

		// Creates a fixed size thread pool which automatically queues any additional
		// jobs which are added to the work queue
		executor = Executors.newFixedThreadPool(numthreads);
		runningTasks = new HashMap<String, FutureTask<FlapjackTask>>();

		// Initialise logging
		try
		{
			LOG = Logger.getLogger(FlapjackServlet.class.getName());
			FileHandler fh = new FileHandler("/home/tomcat/flapjack.log", 0, 1, true);
			fh.setFormatter(new SimpleFormatter());
			LOG.addHandler(fh);
		}
		catch (SecurityException | IOException e) {}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		String id = request.getParameter("ID");

		FutureTask<FlapjackTask> task = runningTasks.get(id);
		if (task != null && task.isDone())
		{
			if (task.isDone())
			{
				try
				{
					// The task has completed and we can send the response via
					// out returend FlapjackTask object.
					FlapjackTask fTask = task.get();
					fTask.writeResponse(response);
					runningTasks.remove(id);

					LOG.info("End\t\t" + id);
				}
				catch (Exception e) { e.printStackTrace(); }
			}
		}

		// If the task is still running send a service unavailable response
		response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		// Get the path to R defined in the web.xml file
		String rPath = getServletContext().getInitParameter("r-path");

		// The type of analysis the request wants to carry out
		String analysis = request.getParameter("analysis");

		String id = SystemUtils.createGUID(8);

		switch (analysis)
		{
			case "DENDROGRAM":
				LOG.info("Run\tDendrogramAnalysis\t" + id);
				DendrogramAnalysis dAnalysis = new DendrogramAnalysis(request, rPath);
				submitFlapjackServletTask(dAnalysis, id);
				break;

			case "PCOA":
				LOG.info("Run\tPCoAAnalysis\t" + id);
				PCoAAnalysis pAnalysis = new PCoAAnalysis(request, rPath);
				submitFlapjackServletTask(pAnalysis, id);
				break;
		}

		// Return the job id so we can poll
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println(id);
		out.close();
	}

	// Wraps the analysis in a FutureTask and submits this to the executor
	// where it is either queued, or run immediately. Also adds the task to a
	// HashMap where it is keyed by id.
	private void submitFlapjackServletTask(FlapjackTask analysis, String id)
	{
		FutureTask<FlapjackTask> task = new FutureTask<FlapjackTask>(analysis);
		executor.submit(task);
		runningTasks.put(id, task);
	}
}