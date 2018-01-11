// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet.api;

import java.util.*;
import java.util.logging.*;

import org.ggf.drmaa.*;

public class DRMAAScheduler implements IScheduler
{
	private Logger LOG;

	// TODO: Make private
	public static Session session;

	public void initialize()
	{
		LOG = Logger.getLogger(DRMAAScheduler.class.getName());

		// DRMAA NOTES:
		// Added drmaa.jar to ${catalina.base}/shared/lib/
		// Added the following to tomcat's bin/setenv.sh file:
		//   export LD_LIBRARY_PATH=/opt/sge/lib/lx-amd64
		// Added the following to ${catalina.base}/conf/catalina.properties
		//   shared.loader="${catalina.base}/shared/lib/*.jar"

		try
		{
			// TODO: Is there a race condition here; two requests come in together and both think the session is null?
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
	}

	public void destroy()
		throws DrmaaException
	{
		if (session != null)
			session.exit();
	}

	public String submit(String command, List<String> args, String wrkDir)
		throws DrmaaException
	{
		JobTemplate jt = session.createJobTemplate();

		jt.setRemoteCommand(command);
		jt.setArgs(args);
		jt.setWorkingDirectory(wrkDir.toString());

		return session.runJob(jt);
	}

	public boolean isJobFinished(String id)
		throws Exception
	{
		int status = session.getJobProgramStatus(id);

		switch (status)
		{
			case Session.DONE:
				LOG.info("## DRMAA Job " + id + " is DONE");
				return true;

			case Session.UNDETERMINED:
			case Session.FAILED:
				LOG.severe("## DRMAA Job " + id + " UNDETERMINED OR FAILED");
				throw new Exception("Job " + id + " undetermined or failed");

			default:
				LOG.info("## DRMAA Job " + id + " is " + status);
				return false;
		}
	}

	public void cancelJob(String id)
		throws DrmaaException
	{
		session.control(id, Session.TERMINATE);

		LOG.info("Cancelled job with ID " + id);
	}
}