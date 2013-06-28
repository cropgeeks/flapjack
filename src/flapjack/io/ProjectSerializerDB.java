// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.sql.*;

class ProjectSerializerDB
{
	private static Connection c;

	static void initConnection(FlapjackFile file, boolean isSaveOperation)
		throws IOException, SQLException
	{
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
		PreparedStatement ps = c.prepareStatement("SELECT * from flapjack;");

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
}