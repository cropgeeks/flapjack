// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;

import java.io.*;
import java.net.*;
import java.util.zip.*;

/**
 * Class that represents a file, that may be a traditional file on
 * disk, or a reference to a file located on the web (in http:// format).
 */
public class FlapjackFile
{
	public static final int UNKNOWN = 0;
	public static final int PROJECT_XML = 1;
	public static final int PROJECT_ZXML = 2;
	public static final int PROJECT_BIN = 3;
	public static final int MAP = 4;
	public static final int GENOTYPE = 5;
	public static final int PHENOTYPE = 6;
	public static final int QTL = 7;
	public static final int GRAPH = 8;
	public static final int WIGGLE = 9;
	public static final int HDF5 = 10;

	private String filename;
	private URL url;
	private File file;

	private int type = UNKNOWN;

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

	public int getType()
		{ return type; }

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

	public boolean isProjectFile()
	{
		try
		{
			if (ProjectSerializerDB.isDatabase(this))
				return true;
		}
		catch (Exception e) {}

		// Otherwise just look at the extension
		return filename.toLowerCase().endsWith(".xml") ||
			filename.toLowerCase().endsWith(".flapjack");
	}

	public boolean canDetermineType()
	{
		try
		{
			// Read the first line of the file
			String str = getFirstLine();

			if (str != null)
			{
				if (isMap(str))
					type = MAP;
				else if (isGenotype(str))
					type = GENOTYPE;
				else if (isPhenotype(str))
					type = PHENOTYPE;
				else if (isQTL(str))
					type = QTL;
				else if (isGraph(str))
					type = GRAPH;
				else if (isWiggle(str))
					type = WIGGLE;
				else if (isHDF5(str))
					type = HDF5;
			}
		}
		catch (Exception e) { System.out.println(e);}

		System.out.println("Detected " + type);

		return (type != UNKNOWN);
	}

	private boolean isMap(String str)
	{
		return str.toLowerCase().startsWith("# fjfile = map");
	}

	private boolean isGenotype(String str)
	{
		return
			str.toLowerCase().startsWith("# fjfile = genotype") ||
			str.toLowerCase().startsWith("# fjfile = allele_frequency");
	}

	private boolean isPhenotype(String str)
	{
		return str.toLowerCase().startsWith("# fjfile = phenotype");
	}

	private boolean isQTL(String str)
	{
		return str.toLowerCase().startsWith("# fjfile = qtl");
	}

	private boolean isGraph(String str)
	{
		return str.toLowerCase().startsWith("# fjfile = graph");
	}

	private boolean isWiggle(String str)
	{
		return (str.toLowerCase().startsWith("track type=wiggle_0") || str.startsWith("#"));
	}

	private boolean isHDF5(String str)
	{
		return str.contains("HDF");
	}

	// Attempts to read the first 2048 bytes of the file, converting the stream
	// into a string that is then split by line separator and the first line
	// returned (if any).
	private String getFirstLine()
	{
		try
		{
			Reader rd = new InputStreamReader(getInputStream());//, "ASCII");
	        char[] buf = new char[2048];

			int num = rd.read(buf);
			rd.close();

			for (int c = 0; c < num; c++)
				if (buf[c] == '\n' || buf[c] == '\r')
					return new String(buf, 0, c);

			return new String(buf);
		}
		catch (Exception e) { e.printStackTrace(); }

		return null;
	}
}