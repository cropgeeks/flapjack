// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.sql.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

public class ProjectSerializerDB
{
	private static Connection c;

	// Tracks "cached" objects that are not yet in the database. This is things
	// like sim-matrix objects that have been serialized to disk as a memory-
	// saving placeholder until they can be streamed to the database
	private static HashMap<ISerializableDB,File> cache = new HashMap<>();

	static void initConnection(FlapjackFile file, boolean isSaveOperation)
		throws IOException, SQLException
	{
		if (c != null)
			c.close();

		c = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());

		// If we're attempting to save, then make sure it is a database!
		if (isSaveOperation && isDatabase(file) == false)
		{
			System.out.println("No SQLite format 3 header");
			new BufferedWriter(new FileWriter(file.getPath()));
		}
	}

	// Returns true if the given file contains the SQLite binary header
	static boolean isDatabase(FlapjackFile file)
		throws IOException
	{
		BufferedInputStream in = new BufferedInputStream(file.getInputStream());
		byte[] header = new byte[16];
		in.read(header, 0, 16);
		in.close();

		return (new String(header).startsWith("SQLite format 3"));
	}

	static void initDatabase()
		throws SQLException
	{
		// Set up the sqlite modes
		Statement st = c.createStatement();
		st.execute("PRAGMA locking_mode = EXCLUSIVE;");
		st.execute("PRAGMA journal_mode = OFF;");
		st.execute("PRAGMA synchronous = OFF;");
		st.execute("PRAGMA count_changes = false;");

		// Create the database tables
		st.executeUpdate("CREATE TABLE IF NOT EXISTS project (data BLOB);");
		st.executeUpdate("CREATE TABLE IF NOT EXISTS objects (id STRING, type STRING, data BLOB);");
		st.close();


		// Remove any previous project records
		PreparedStatement ps = c.prepareStatement("DELETE FROM project;");
		ps.executeUpdate();
		ps.close();
	}

	static OutputStream getProjectOutputStream()
		throws SQLException
	{
		PreparedStatement ps = c.prepareStatement("INSERT INTO project (data) VALUES (?);");

		return new DatabaseOutputStream(ps, 1);
	}

	static InputStream getProjectInputStream()
		throws SQLException
	{
		PreparedStatement ps = c.prepareStatement("SELECT * FROM project;");

		return new DatabaseInputStream(ps, 1);
	}

	static void close()
		throws SQLException
	{
		if (c != null)
			c.close();
	}

	static void closeAndVacuum()
		throws SQLException
	{
		if (c != null)
		{
			PreparedStatement ps = c.prepareStatement("VACUUM;");
			ps.execute();
			ps.close();

			c.close();
		}
	}

	/**
	 * Takes the given object and serializes it (using Java Object Serialization)
	 * to disk in a temp location. The object and the file are tracked by this
	 * class for later inclusion in the database .project file
	 */
	public static void cacheToDisk(ISerializableDB obj)
		throws Exception
	{
		File file = new File(FlapjackUtils.getCacheDir(), obj.getDatabaseID() + ".obj");
		file.deleteOnExit();

		cache.put(obj, file);

		ObjectOutputStream out = new ObjectOutputStream(
			new BufferedOutputStream(new FileOutputStream(file)));
		out.writeObject(obj.dbGetObject());
		out.close();

		System.out.println("Cached " + obj.getDatabaseID() + ": " + obj);
	}

	/**
	 * Saves each object in the cache list to the database, placing them into
	 * tables based on their class type. The objects are simply read as byte
	 * streams from disk direct to database, meaning they are still Java
	 * Serialized objects when in the database.
	 */
	public static void cacheToDatabase()
		throws Exception
	{
		for (ISerializableDB obj: cache.keySet())
		{
			PreparedStatement ps = c.prepareStatement("INSERT INTO objects (id, type, data) VALUES (?, ?, ?);");
			ps.setString(1, obj.getDatabaseID());
			ps.setString(2, obj.dbGetType());

			// Write to the DB
			OutputStream out = new DatabaseOutputStream(ps, 3);
			BufferedInputStream in = new BufferedInputStream(
				new FileInputStream(cache.get(obj)));

			byte[] buffer = new byte[1024];
			for (int length = 0; (length = in.read(buffer)) > 0;)
				out.write(buffer, 0, length);

			in.close();
			out.close();

			// TODO: Delete the *.obj file from disk
		}

		cache.clear();
	}

	public static void setFromCache(ISerializableDB obj)
	{
		try
		{
			ObjectInputStream in = null;

			// Read from the disk cache
			if (cache.containsKey(obj))
			{
				System.out.println("Load from disk...");

				File file = new File(FlapjackUtils.getCacheDir(), obj.getDatabaseID() + ".obj");
				in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			}

			// Or read from the database cache
			else
			{
				System.out.println("Load from DB...");

				PreparedStatement ps = c.prepareStatement("SELECT data FROM objects WHERE id=?;");
				ps.setString(1, obj.getDatabaseID());

				in = new ObjectInputStream(new BufferedInputStream(new DatabaseInputStream(ps, 1)));
			}

			// Read it...
			obj.dbSetObject(in.readObject());
			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}