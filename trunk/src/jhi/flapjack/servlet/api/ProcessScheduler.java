// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet.api;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.logging.*;

public class ProcessScheduler implements IScheduler
{
	private Logger LOG;

	private AtomicInteger jobCount = new AtomicInteger(0);
	private ConcurrentHashMap<String,Process> jobs = new ConcurrentHashMap<>();

	@Override
	public void initialize()
	{
		LOG = Logger.getLogger(ProcessScheduler.class.getName());
	}

	@Override
	public void destroy()
		throws Exception
	{
	}

	@Override
	public String submit(String command, List<String> args, String wrkDir)
		throws Exception
	{
		LOG.info("Submitting a ProcessBuilder job...");

		final String id = "" + jobCount.addAndGet(1);

		Runnable r = new Runnable() {
			public void run()
			{
				try
				{
					args.add(0, command);

					ProcessBuilder pb = new ProcessBuilder(args);
					pb.directory(new File(wrkDir));

					LOG.info("Starting process");
					Process proc = pb.start();
					jobs.put(id, proc);

					LOG.info("Waiting for process");
					File oFile = new File(wrkDir, command + ".o" + id);
					SOutputCatcher oStream = new SOutputCatcher(proc.getInputStream(), oFile);
					File eFile = new File(wrkDir, command + ".e" + id);
					SOutputCatcher eStream = new SOutputCatcher(proc.getErrorStream(), eFile);

					proc.waitFor();

					oStream.close();
					eStream.close();

					LOG.info("Process finished");
				}
				catch (Exception e)
				{
					LOG.log(Level.SEVERE, e.getMessage(), e);
				}

				jobs.remove(id);
			}
		};

		new Thread(r).start();

		return id;
	}

	private class SOutputCatcher extends StreamCatcher
	{
		PrintWriter out;

		SOutputCatcher(InputStream in, File oFile)
			throws IOException
		{
			super(in);
			out = new PrintWriter(new BufferedWriter(new FileWriter(oFile)));
		}

		@Override
		protected void processLine(String line)
			throws Exception
		{
			out.println(line);
		}

		void close()
			throws Exception
		{
			out.close();
		}
	}

	@Override
	public boolean isJobFinished(String id)
		throws Exception
	{
		Process proc = jobs.get(id);

		if (proc != null && proc.isAlive())
			return false;

		return true;
	}

	@Override
	public void cancelJob(String id)
		throws Exception
	{
		Process proc = jobs.get(id);

		if (proc != null && proc.isAlive())
			proc.destroy();

		jobs.remove(id);

		LOG.info("Cancelled job with ID " + id);
	}
}