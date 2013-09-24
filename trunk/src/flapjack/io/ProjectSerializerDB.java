// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
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

		// Create the database table
		st.executeUpdate("CREATE TABLE IF NOT EXISTS flapjack (id INTEGER, data BLOB);");
		st.executeUpdate("CREATE TABLE IF NOT EXISTS simmatrix (id STRING, data BLOB);");
		st.close();


		// Remove any previous flapjack data entries
		PreparedStatement ps = c.prepareStatement("DELETE FROM flapjack;");
		ps.executeUpdate();
		ps.close();
	}

	static OutputStream getProjectOutputStream()
		throws SQLException
	{
		PreparedStatement ps = c.prepareStatement("INSERT INTO flapjack (id, data) VALUES (?, ?);");
		ps.setInt(1, 0);

		return new DatabaseOutputStream(ps, 2);
	}

	static InputStream getProjectInputStream()
		throws SQLException
	{
		PreparedStatement ps = c.prepareStatement("SELECT * FROM flapjack;");

		return new DatabaseInputStream(ps, 2);
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
		cache.put(obj, file);

		ObjectOutputStream out = new ObjectOutputStream(
			new BufferedOutputStream(new FileOutputStream(file)));
		out.writeObject(obj.dbGetObject());
		out.close();
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
			PreparedStatement ps = c.prepareStatement("INSERT INTO " + getTable(obj) + " (id, data) VALUES (?, ?);");
			ps.setString(1, obj.getDatabaseID());

			// Write to the DB
			OutputStream out = new DatabaseOutputStream(ps, 2);
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

	private static String getTable(ISerializableDB obj)
	{
		if (obj instanceof SimMatrix)
			return "simmatrix";

		throw new RuntimeException("Unknown database object type");
	}

	// TEMP TESTING METHOD
	public static void loadCache(Project project)
		throws Exception
	{
		PreparedStatement ps1 = c.prepareStatement("SELECT DISTINCT id FROM simmatrix;");

		// For each simmatrix in the table...
		ResultSet rs = ps1.executeQuery();
		while (rs.next())
		{
			String id = rs.getString(1);

			// Fetch it...
			PreparedStatement ps2 = c.prepareStatement("SELECT * FROM simmatrix WHERE id=?;");
			ps2.setString(1, id);

			// Deserialize it...
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new DatabaseInputStream(ps2, 2)));
			Object obj = in.readObject();

			in.close();

			// Find its original owner
			for (DataSet dataSet: project.getDataSets())
				for (GTViewSet viewSet: dataSet.getViewSets())
					for (SimMatrix matrix: viewSet.getMatrices())
					{
						System.out.println("Checking " + matrix.getDatabaseID() + " and " + id);

						if (matrix.getDatabaseID().equals(id))
							matrix.dbSetObject(obj);
					}
		}

		rs.close();


//		PreparedStatement ps = c.prepareStatement("SELECT * FROM simmatrix;");

		//new DatabaseInputStream(ps, 2);
	}
}