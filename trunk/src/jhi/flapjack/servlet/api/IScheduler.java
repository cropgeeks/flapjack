// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet.api;

import java.util.*;

public interface IScheduler
{
	public void initialize()
		throws Exception;

	public void destroy()
		throws Exception;

	public String submit(List<String> args, String wrkDir)
		throws Exception;

	public boolean isJobFinished(String id)
		throws Exception;

	public void cancelJob(String id)
		throws Exception;
}