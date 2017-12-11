// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet.api;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public abstract class StreamCatcher extends Thread
{
	private BufferedReader reader = null;

	public StreamCatcher(InputStream in)
	{
		reader = new BufferedReader(new InputStreamReader(in));
		start();
	}

	public void run()
	{
		try
		{
			String line = reader.readLine();
			StringTokenizer st = null;

			while (line != null)
			{
				processLine(line);
				line = reader.readLine();
			}
		}
		catch (Exception e) {
			Logger.getLogger(StreamCatcher.class.getName())
				.log(Level.SEVERE, e.getMessage(), e);
		}

		try { reader.close(); }
		catch (IOException e) {}
	}

	protected abstract void processLine(String line)
		throws Exception;
}