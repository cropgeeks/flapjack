// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.net.*;
import java.util.zip.*;

/**
 * Class that represents a file, that may be a traditional file on
 * disk, or a reference to a file located on the web (in http:// format).
 */
public class FlapjackFile
{
	private String filename;
	private URL url;
	private File file;

	public FlapjackFile(String filename)
	{
		this.filename = filename;

		try
		{
			url = new URL(filename);
		}
		catch (MalformedURLException e)
		{
			file = new File(filename);
		}
	}

	public String getPath()
		{ return filename; }

	public String getName()
	{
		// Return either the name of the file
		if (file != null)
			return file.getName();

		// Or parse the URL to determine the filename part of it:
		// http://someserver/somefolder/file.ext?argument=parameter
		//                              ^^^^^^^^
		else
		{
			String name = filename;

			if (name.indexOf("?") != -1)
				name = name.substring(0, name.indexOf("?"));

			int slashIndex = name.lastIndexOf("/");
			if (slashIndex != -1)
				name = name.substring(slashIndex + 1);

			return name;
		}
	}

	long length()
	{
		if (file != null)
			return file.length();

		// This might fail, but it doesn't matter, as any subsequent load will
		// fail too, and the error can be caught then
		try
		{
			URLConnection conn = url.openConnection();
			conn.getInputStream().close();

			return conn.getContentLength();
		}
		catch (Exception e) { return 0; }
	}

	boolean exists()
	{
		if (file != null)
			return file.exists();

		try
		{
			URLConnection conn = url.openConnection();
			conn.getInputStream().close();

			return true;
		}
		catch (Exception e) { return false; }
	}

	// Returns the main input stream for this file
	public InputStream getInputStream()
		throws IOException
	{
		if (file != null)
			return new FileInputStream(file);

		return url.openStream();
	}

	URL getURL()
	{
		return url;
	}

	public boolean isURL()
	{
		return url != null;
	}

	public File getFile()
	{
		return file;
	}
}