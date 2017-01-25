// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
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

	public String submit(List<String> args, String wrkDir)
		throws DrmaaException
	{
		JobTemplate jt = session.createJobTemplate();

		jt.setRemoteCommand("java");
		jt.setArgs(args);
		jt.setWorkingDirectory(wrkDir.toString());

		return session.runJob(jt);
	}

	public boolean isJobFinished(String id)
		throws Exception
	{
		// Strip off the DRMAA job id
		String drmaaID = id.substring(id.lastIndexOf("-")+1);

		int status = session.getJobProgramStatus(drmaaID);

		switch (status)
		{
			case Session.DONE:
				LOG.info("## DRMAA Job " + drmaaID + " is DONE");
				return true;

			case Session.UNDETERMINED:
			case Session.FAILED:
				LOG.severe("## DRMAA Job " + drmaaID + " UNDETERMINED OR FAILED");
				throw new Exception("Job " + drmaaID + " undetermined or failed");

			default:
				LOG.info("## DRMAA Job " + drmaaID + " is " + status);
				return false;
		}
	}

	public void cancelJob(String id)
		throws DrmaaException
	{
		// Strip off the DRMAA job id
		String drmaaID = id.substring(id.lastIndexOf("-")+1);

		session.control(drmaaID, Session.TERMINATE);

		LOG.info("Cancelled job with ID " + id);
	}
}