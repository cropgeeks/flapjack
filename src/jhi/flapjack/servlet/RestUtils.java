// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet;

import java.io.*;

import org.restlet.data.*;
import org.restlet.representation.Representation;
import org.restlet.resource.*;

public class RestUtils
{
	// Client helper methods
	public static void cancelJob(Reference uri)
	{
		// We don't care about any exceptions - we tried to cancel...so be it if
		// it fails for whatever reason - the client doesn't care.

		try
		{
			ClientResource cancel = new ClientResource(uri);
			cancel.delete();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// Servlet helper methods
	public static void writeSimMatrix(File matrix, Representation representation)
	{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(representation.getStream()));
			 PrintWriter writer = new PrintWriter(new FileWriter(matrix)))
		{
			String line;
			while ((line = reader.readLine()) != null)
				writer.println(line);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ResourceException(500);
		}
	}
}
