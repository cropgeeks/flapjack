// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.servlet;

import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;

public class DendrogramClient
{
	String url = "http://bioinf:8080/flapjack/servlets/dendrogram";
//	String url = "http://cgi-lib.berkeley.edu/ex/simple-form.cgi";

	public DendrogramClient()
	{
	}

	public void uploadFile(StringBuilder sb, int lineCount)
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


//		reader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), charset));
		reader = new BufferedReader(new StringReader(sb.toString()));
		for (String line; (line = reader.readLine()) != null;)
		{
			writer.append(line).append(CRLF);
		}
		reader.close();

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

		System.out.println("DONE");

		int code = connection.getResponseCode();

		if (code == HttpURLConnection.HTTP_OK)
		{
			BufferedImage image;

			image = ImageIO.read(new BufferedInputStream(connection.getInputStream()));

			System.out.println(image.getWidth());
		}
		else if (code == HttpURLConnection.HTTP_ACCEPTED)
		{
		}
		else
		{
		}



//		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//		String str = null;
//		System.out.println("RESPONSE:");
//		while ((str = in.readLine()) != null)
		{
//			System.out.println(str);
		}
//		in.close();

	}
}