// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package	jhi.flapjack.io;

import java.io.*;
import java.util.*;
import java.sql.*;

/**
 * Streams data fed to it by connecting to a database (using the supplied
 * PreparedStatement) and storing the stream as a series of BLOB objects broken
 * up into chunks of CHUNKSIZE bytes.
 */
public class DatabaseOutputStream extends OutputStream
{
	/**
	 * The buffer where	data is	stored.
	 */
	protected byte buf[];

	/**
	 * The number of valid bytes in	the	buffer.
	 */
	protected int count;

	// The size of the buffer
	private static final int CHUNKSIZE = 15 * 1024 * 1024;

	private PreparedStatement ps;
	private int blobColumn;

	/**
	 * Creates a new DatabaseOutputStream.
	 */
	public DatabaseOutputStream(PreparedStatement ps, int blobColumn)
	{
		this.ps = ps;
		this.blobColumn = blobColumn;

		buf	= new byte[CHUNKSIZE];
	}

	public void flush()
		throws IOException
	{
		// "Empty" flushes seem to get called at the end (by close()) and we
		// don't want them attempting to write to the database
		if (count == 0)
			return;

		try
		{
			if (count < buf.length)
				ps.setBytes(blobColumn, Arrays.copyOf(buf, count));
			else
				ps.setBytes(blobColumn, buf);

			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new IOException(e.getMessage(), e);
		}

		// Reset the buffer
		count = 0;
	}

	/**
	 * Writes the specified	byte to	this byte array	output stream.
	 *
	 * @param	b	the	byte to	be written.
	 */
	public synchronized	void write(int b)
		throws IOException
	{
		if (count >= buf.length)
			flush();

		buf[count] = (byte) b;
		count += 1;
	}

	/**
	 * Writes <code>len</code> bytes from the specified	byte array
	 * starting	at offset <code>off</code> to this byte	array output stream.
	 *
	 * @param	b	  the data.
	 * @param	off	  the start	offset in the data.
	 * @param	len	  the number of	bytes to write.
	 */
	public synchronized	void write(byte	b[], int off, int len)
		throws IOException
	{
		if ((off < 0) || (off >	b.length) || (len <	0) || ((off + len) - b.length > 0))
		{
			throw new IndexOutOfBoundsException();
		}

		while (len > 0)
		{
			// If what's left to be written is less than the buffer's free space
			// then we know we can just copy it all in
			if (len <= (buf.length-count))
			{
				System.arraycopy(b,	off, buf, count, len);
				count += len;
				len = 0;
			}

			// Otherwise, there must be more to copy than there is space in the
			// buffer, so we need to only copy what will fit on this iteration
			else
			{
				// How many bytes can still be written?
				int bytes = buf.length-count;

				System.arraycopy(b, off, buf, count, bytes);
				count += bytes;
				off += bytes;
				len -= bytes;

				flush();
			}
		}
	}

	public void	close()
		throws IOException
	{
		flush();
	}
}