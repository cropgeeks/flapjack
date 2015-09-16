// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package	jhi.flapjack.io;

import java.io.*;
import java.sql.*;

public class DatabaseInputStream extends InputStream
{
	private PreparedStatement ps;
	private ResultSet rs;
	private int blobColumn;


	// Temporary buffer to hold each blob as it comes back from the DB
	protected byte buf[] = new byte[0];

	// The index of the next character to read from the input stream buffer.
	protected int pos;

	// The index one greater than the last valid character in the buffer
	protected int count;

	public DatabaseInputStream(PreparedStatement ps, int blobColumn)
	{
		this.ps = ps;
		this.blobColumn = blobColumn;
	}

	private void queryDatabase()
		throws IOException
	{
		try
		{
			if (rs == null)
				rs = ps.executeQuery();

			if (rs.next())
			{
				buf = rs.getBytes(blobColumn);

				pos = 0;
				count = buf.length;
			}
			else
			{
				rs.close();
				pos = -1;
			}
		}
		catch (SQLException e)
		{
			throw new IOException(e.getMessage(), e);
		}
	}

	public synchronized	int	read()
	{
		// Do we need to read more data first?
		if (pos >= buf.length)
		{
			try { queryDatabase(); }
			catch (Exception e)
			{
				e.printStackTrace();
				return -1;
			}
		}

		// If we have data available in the current buffer
		if (pos < buf.length)
			return (buf[pos++] & 0xff);

		return -1;
	}

	public synchronized	int	read(byte b[], int off,	int	len)
		throws IOException
	{
		if (b == null)
			throw new NullPointerException();
		else if (off < 0 || len <	0 || len > b.length	- off)
			throw new IndexOutOfBoundsException();

		int copied = 0;

		while (len > 0)
		{
			// If the pointer is at the end of the buffer, try to read more data
			if (pos >= buf.length)
				queryDatabase();

			// If there's no more data; break the loop
			if (pos == -1)
				break;

			// Copy everything left in the buffer, or just len bytes?
			int bytes = (buf.length-pos > len) ? len : buf.length-pos;

			// Copy in the data
			System.arraycopy(buf, pos, b, off, bytes);
			copied += bytes;
			pos += bytes;
			off += bytes;
			len -= bytes;
		}

		// Return either how much was copied, or -1 (for no more data)
		return (copied > 0) ? copied : -1;
	}

	public synchronized	long skip(long n)
	{
		long k = count - pos;
		if (n <	k) {
			k =	n <	0 ?	0 : n;
		}

		pos	+= k;
		return k;
	}

	public synchronized	int	available() {
		return count - pos;
	}

	public boolean markSupported()
	{
		return false;
	}

	public void	mark(int readAheadLimit)
	{
		throw new RuntimeException();
	}

	public synchronized	void reset()
	{
		throw new RuntimeException();
	}

	public void	close()
		throws IOException
	{
		try
		{
			if (rs != null)
				rs.close();
		}
		catch (SQLException e)
		{
			throw new IOException(e.getMessage(), e);
		}
	}
}