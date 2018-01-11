// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet.api;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class SLURMScheduler implements IScheduler
{
	private Logger LOG;

	@Override
	public void initialize()
	{
		LOG = Logger.getLogger(SLURMScheduler.class.getName());
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
		LOG.info("Submitting a SLURM job...");

		// Modify the command to include all its parameters
		for (String arg: args)
			command += " " + arg;

		// Write out an SBATCH script file to run this command
		File script = new File(wrkDir, "submit.sh");
		writeScript(command, script);

		ProcessBuilder pb = new ProcessBuilder("sbatch", "submit.sh");
		pb.directory(new File(wrkDir));
		pb.redirectErrorStream(true);

		LOG.info("Starting process");
		Process proc = pb.start();

		// Open up the input stream (to read from) (prog's out stream)
		SBatchCatcher oStream = new SBatchCatcher(proc.getInputStream());

		LOG.info("Waiting for process");
		proc.waitFor();

		while (oStream.isAlive())
			Thread.sleep(10);

		LOG.info("Process finished");
		LOG.info("JOB ID IS: " + oStream.jobId);

		if (oStream.jobId != null)
			return "" + oStream.jobId;
		else
			throw new Exception("Unable to submit job");
	}

	private void writeScript(String command, File script)
		throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(
			getClass().getResourceAsStream("/src/arrr/Dendrogram.SLURM")));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(script)));

		String str;
		while ((str = in.readLine()) != null)
		{
			str = str.replace("$COMMAND", command);
			out.println(str);
		}

		in.close();
		out.close();
	}

	private class SBatchCatcher extends StreamCatcher
	{
		SBatchCatcher(InputStream in) { super(in); }
		Integer jobId;

		@Override
		protected void processLine(String line)
			throws Exception
		{
			LOG.info(line);

			if (line.startsWith("Submitted batch job"))
			{
				String id = line.substring(20);
				jobId = Integer.parseInt(id);
				LOG.info("Parsed job ID: " + jobId);
			}
		}
	}

	@Override
	public boolean isJobFinished(String id)
		throws Exception
	{
		LOG.info("Querying SLURM job...");

		ProcessBuilder pb = new ProcessBuilder("squeue", "--jobs=" + id);
		pb.redirectErrorStream(true);

		LOG.info("Starting process");
		Process proc = pb.start();

		// Open up the input stream (to read from) (prog's out stream)
		SQueueCatcher oStream = new SQueueCatcher(proc.getInputStream(), id);

		LOG.info("Waiting for process");
		proc.waitFor();

		while (oStream.isAlive())
			Thread.sleep(10);

		LOG.info("Process finished");
		LOG.info("JOB STATUS IS: " + oStream.status);

		if (oStream.status != null)
		{
			// If the job is in one of the following states, we'll assume it's
			// still active
			switch (oStream.status)
			{
				case "CF": // CF CONFIGURING Job has been allocated resources, but are waiting for them to become ready for use (e.g. booting).
				case "CG": // CG COMPLETING  Job is in the process of completing. Some processes on some nodes may still be active.
				case "PD": // PD PENDING     Job is awaiting resource allocation.
				case " R": // R RUNNING      Job currently has an allocation.
				case "ST": // ST STOPPED     Job has an allocation, but execution has been stopped with SIGSTOP signal. CPUS have been retained by this job.
				case " S": // S SUSPENDED    Job has an allocation, but execution has been suspended and CPUs have been released for other jobs.
					return false;
			}
		}

		return true;
	}

	private class SQueueCatcher extends StreamCatcher
	{
		String id;
		String status;

		SQueueCatcher(InputStream in, String id)
		{
			super(in);
			this.id = id;
		}

		@Override
		protected void processLine(String line)
			throws Exception
		{
			LOG.info(line);

			if (line.trim().startsWith(id))
			{
				status = line.substring(47,49);
				LOG.info("Parsed job status: " + status);
			}
		}
	}

	@Override
	public void cancelJob(String id)
		throws Exception
	{
		ProcessBuilder pb = new ProcessBuilder("scancel", id);
		pb.redirectErrorStream(true);
		Process proc = pb.start();

		// Open up the input stream (to read from) (prog's out stream)
		SCancelCatcher oStream = new SCancelCatcher(proc.getInputStream());
		proc.waitFor();

		while (oStream.isAlive())
			Thread.sleep(10);

		LOG.info("Cancelled job with ID " + id);
	}

	private class SCancelCatcher extends StreamCatcher
	{
		SCancelCatcher(InputStream in) { super(in); }

		@Override
		protected void processLine(String line)
			throws Exception
		{
			LOG.info(line);
		}
	}
}