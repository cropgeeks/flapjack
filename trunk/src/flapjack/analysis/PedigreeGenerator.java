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

public class PedigreeGenerator implements ITrackableJob
{
	private GTViewSet viewSet;
	private File pFile;
	private boolean isOK = true;

	private BufferedImage image;
	private String selectedButton;

	public PedigreeGenerator(GTViewSet viewSet, File pFile, String selectedButton)
	{
		this.viewSet = viewSet;
		this.pFile = pFile;
		this.selectedButton = selectedButton;
	}

	public void runJob()
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
		header.append("#graph_size=" + selectedButton + "\r\n");
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
		while (read > 0 && isOK)
		{
			out.write(read);
			read = fis.read();
		}

		fis.close();

		out.write(footer.toString());
		out.close();


		if (isOK)
		{
			// TODO: Need a way to cancel a long-download?
			BufferedInputStream in = new BufferedInputStream(c.getInputStream());
			image = ImageIO.read(in);
			in.close();

	/*		String str = null;
			while ((str = in.readLine()) != null)
				System.out.println(str);
			in.close();
	*/

		}

		c.disconnect();
	}

	public BufferedImage getImage()
		{ return image; }

	public boolean isIndeterminate()
		{ return true; }

	public int getMaximum()
		{ return 0; }

	public int getValue()
		{ return 0; }

	public void cancelJob()
		{ isOK = false; }
}