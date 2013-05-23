// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.servlet;

import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import javax.imageio.*;

import flapjack.data.*;

public class DendrogramClient
{
	private static final String url = "http://bioinf:8080/flapjack/servlets/dendrogram";

	private ArrayList<Integer> lineOrder = new ArrayList<Integer>();

	public DendrogramClient()
	{
	}

	public ArrayList<Integer> getLineOrder()
		{ return lineOrder; }

	public Dendrogram doClientStuff(SimMatrix matrix, int lineCount)
		throws Exception
	{
		String charset = "UTF-8";

//		File textFile = new File(filename);
//		File binaryFile = new File("/path/to/file.bin");
		String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
		String CRLF = "\r\n"; // Line separator required by multipart/form-data.

//		URLConnection connection = new URL(url).openConnection();
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		PrintWriter writer = null;


		OutputStream output = connection.getOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(output, charset), true); // true = autoFlush, important!
//		writer = new PrintWriter(new OutputStreamWriter(System.out));

		// Send normal param.
		writer.append("--" + boundary).append(CRLF);
		writer.append("Content-Disposition: form-data; name=\"lineCount\"").append(CRLF);
		writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
		writer.append(CRLF);
		writer.append("" + lineCount).append(CRLF).flush();


		// Send text file.
//		System.out.println("WRITING FILE " + filename);
		writer.append("--" + boundary).append(CRLF);
		writer.append("Content-Disposition: form-data; name=\"textfile\"; filename=\"" + "FILENAME" + "\"").append(CRLF);
		writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
		writer.append(CRLF).flush();
		BufferedReader reader = null;

		// Send the sim-matrix
		System.out.print("SENDING MATRIX...");
		writer.println(matrix.createFileHeaderLine());
		for (int i = 0; i < matrix.size(); i++)
			writer.println(matrix.createFileLine(i));
		System.out.println("DONE");


		writer.flush();

			// Send binary file.
	/*		writer.append("--" + boundary).append(CRLF);
			writer.append("Content-Disposition: form-data; name=\"binaryFile\"; filename=\"" + binaryFile.getName() + "\"").append(CRLF);
			writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
			writer.append("Content-Transfer-Encoding: binary").append(CRLF);
			writer.append(CRLF).flush();
			InputStream input = null;
			try {
			input = new FileInputStream(binaryFile);
			byte[] buffer = new byte[1024];
			for (int length = 0; (length = input.read(buffer)) > 0;) {
			output.write(buffer, 0, length);
			}
			output.flush(); // Important! Output cannot be closed. Close of writer will close output as well.
			} finally {
			if (input != null) try { input.close(); } catch (IOException logOrIgnore) {}
			}
			writer.append(CRLF).flush(); // CRLF is important! It indicates end of binary boundary.
	*/

		// End of multipart/form-data.
		writer.append("--" + boundary + "--").append(CRLF);
		writer.close();

		int code = connection.getResponseCode();

		if (code == HttpURLConnection.HTTP_OK)
		{
			Dendrogram d = new Dendrogram();

			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(connection.getInputStream()));
			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null)
			{
				System.out.println("ENTRY: " + entry.getName());

				if (entry.getName().equals("dendrogram.png"))
				{
					ByteArrayOutputStream bos = new ByteArrayOutputStream();

					int read;
					byte[] buffer = new byte[1024];

					while ((read = zis.read(buffer, 0, buffer.length)) != -1)
						bos.write(buffer, 0, read);

					ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
					BufferedImage image = ImageIO.read(bis);

					d.setImage(image);
				}
				else if (entry.getName().equals("order.txt"))
				{
					ByteArrayOutputStream bos = new ByteArrayOutputStream();

					int read;
					byte[] buffer = new byte[1024];

					while ((read = zis.read(buffer, 0, buffer.length)) != -1)
						bos.write(buffer, 0, read);

					ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

					BufferedReader in = new BufferedReader(new InputStreamReader(bis));
					String str = null;

					// Read the line order
					while ((str = in.readLine()) != null && str.length() > 0)
						lineOrder.add(Integer.parseInt(str) - 1);

					in.close();
				}
			}

			return d;
		}
		else if (code == HttpURLConnection.HTTP_ACCEPTED)
		{
			// TODO: Assumption here is R failed for some reason (detected by
			// servlet and perhaps handed back in error code?)
		}
		else
		{
			// TODO: Unknown failure

			System.out.println("FAILED: " + code);
		}

		return null;
	}
}