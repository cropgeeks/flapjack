package jhi.flapjack.servlet;

import org.restlet.data.*;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import scri.commons.io.FileUtils;

import java.io.*;

public class RestUtils
{
	public static void cancelJob(Reference uri)
	{
		ClientResource cancel = new ClientResource(uri);
		cancel.delete();
	}

	public static void writeSimMatrix(String taskId, Representation representation)
	{
		File file = new File(FileUtils.getTempDirectory(), taskId + ".matrix");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(representation.getStream()));
			 PrintWriter writer = new PrintWriter(new FileWriter(file)))
		{
			String line;
			while ((line = reader.readLine()) != null)
				writer.println(line);
		}
		catch (Exception e) { e.printStackTrace(); }
	}
}
