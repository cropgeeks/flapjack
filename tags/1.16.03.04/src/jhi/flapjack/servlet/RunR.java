// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet;

import java.io.*;

class RunR
{
	private String rPath;
	private File wrkDir, rScript;

	RunR(String rPath, File wrkDir, File rScript)
	{
		this.rPath = rPath;
		this.wrkDir = wrkDir;
		this.rScript = rScript;
	}

	void runR()
		throws Exception
	{
		ProcessBuilder pb = new ProcessBuilder(rPath, "--vanilla");
		pb.directory(wrkDir);
		pb.redirectErrorStream(true);

		Process proc = pb.start();

		// Open up the output stream (to write to) (prog's in stream)
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
			proc.getOutputStream()));
		writer.print("source(\"" + rScript.getPath() + "\")");
		writer.close();

		// Open up the input stream (to read from) (prog's out stream)
		StreamCatcher oStream = new StreamCatcher(proc.getInputStream(), true);

		proc.waitFor();

		while (oStream.isAlive())
			Thread.sleep(10);
	}

	private static class StreamCatcher extends Thread
	{
		protected BufferedReader reader = null;
		protected boolean showOutput = false;

		public StreamCatcher(InputStream in, boolean showOutput)
		{
			reader = new BufferedReader(new InputStreamReader(in));
			this.showOutput = showOutput;

			start();
		}

		public void run()
		{
			try
			{
				String line = reader.readLine();

				while (line != null)
				{
					if (showOutput)
						System.out.println(line);

					line = reader.readLine();
				}
			}
			catch (Exception e) {}

			try { reader.close(); }
			catch (IOException e) {}
		}
	}
}