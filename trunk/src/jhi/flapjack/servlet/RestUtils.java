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
		ClientResource cancel = new ClientResource(uri);
		cancel.delete();
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
