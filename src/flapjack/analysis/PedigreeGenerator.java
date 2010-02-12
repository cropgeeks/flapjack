// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;
import scri.commons.file.*;

public class PedigreeGenerator extends SimpleJob
{
	private GTViewSet viewSet;
	private File pFile;

	private BufferedImage image;
	private String selectedButton;

	private String message;
	private ProgressInputStream ps;

	public PedigreeGenerator(GTViewSet viewSet, File pFile, String selectedButton)
	{
		this.viewSet = viewSet;
		this.pFile = pFile;
		this.selectedButton = selectedButton;

		message = "Sending pedigree information to server";
	}

	public void runJob(int index)
		throws Exception
	{
		String boundary = SystemUtils.createGUID(10);
		String name = "flapjack-pedigree-" + boundary;

		URL url = new URL("http://penguin.scri.ac.uk/flapjack/pedigrees/upload.cgi");

		String formHeader = "--" + boundary + "\r\n"
			+ "Content-Disposition: form-data; name=\"file\"; "
			+ "filename=\"" + name +"\"" + "\r\n\r\n";

		String footer = "\r\n--" + boundary + "\r\n";

		StringBuilder header = new StringBuilder();
		header.append("#graph_size=" + selectedButton.toLowerCase() + "\r\n");
		for (LineInfo line: viewSet.getLines())
		{
			if (line.getSelected())
				header.append("#selected_line=" + line.getLine().getName() + "\r\n");
		}

		HttpURLConnection c = (HttpURLConnection) url.openConnection();

		c.setDoOutput(true);
		c.setDoInput(true);

		c.setRequestMethod("POST");
		c.setRequestProperty("Connection", "Keep-Alive");
		c.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		int length = formHeader.length() + header.length() + (int) pFile.length() + footer.length();
		c.setFixedLengthStreamingMode(length);

		BufferedWriter out = new BufferedWriter(
			new OutputStreamWriter(c.getOutputStream()));
		out.write(formHeader.toString());
		out.write(header.toString());

		BufferedInputStream fis = new BufferedInputStream(
			new DataInputStream(new FileInputStream(pFile)));

		int read = fis.read();
		while (read > 0 && okToRun)
		{
			out.write(read);
			read = fis.read();
		}

		fis.close();

		out.write(footer.toString());
		out.close();

		message = "Waiting on pedigree image generation";


		if (okToRun)
		{
			ps = new ProgressInputStream(c.getInputStream());
			maximum = c.getContentLength();
			System.out.println("max= " + maximum);
			message = "Downloading result from server";

			// TODO: Need a way to cancel a long-download?
			BufferedInputStream in = new BufferedInputStream(ps);
			image = ImageIO.read(ps);
			in.close();
		}

		c.disconnect();
	}

	public BufferedImage getImage()
		{ return image; }

	public boolean isIndeterminate()
	{
		if (ps == null)
			return true;
		else
			return false;
	}

	public int getValue()
	{
		if (ps == null)
			return 0;
		else
			return (int) ps.getBytesRead();
	}

	public String getMessage()
	{
		return message;
	}
}