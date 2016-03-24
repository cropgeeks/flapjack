// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet.dendrogram;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.servlet.*;

import static org.restlet.data.Status.*;
import org.restlet.data.*;
import org.restlet.resource.*;
import org.restlet.representation.*;

public class DendrogramClient
{
	private final String URL = "http://wildcat:8080/flapjack-test/dendrogram/";
	private Reference taskURI;

	private final SimMatrix matrix;
	private final int lineCount;

	private ArrayList<Integer> lineOrder = new ArrayList<>();

	private boolean okToRun = true;

	public DendrogramClient(SimMatrix matrix, int lineCount)
	{
		this.matrix = matrix;
		this.lineCount = lineCount;
	}

	/**
	 * Generates a Dendrogram by interacting with Flapjack's web services. Sends a SimMatrix and lineCount to the web
	 * service for creating a Dendrogram and gets a Dendrogram in return.
	 *
	 * @return	the Dendrogram that was generated.
	 */
	public Dendrogram generateDendrogram()
		throws Exception
	{
		taskURI = postSimMatrix();

		if (okToRun)
		{
			byte[] dendrogramBytes = getDendrogramAsByteArray(taskURI);

			if (dendrogramBytes != null)
				return createDendrogramFromZip(dendrogramBytes);
		}

		return null;
	}

	/**
	 * Sends a similarity matrix using HTTP POST to the dendrogram server resource and returns a Reference which
	 * contains the URI to a task resource which can be polled to discover the current status of the job to create the
	 * Dendrogram.
	 *
	 * @return	a URI to poll a server task in the form of a Reference
	 */
	private Reference postSimMatrix()
	{
		ClientResource dendrogramResource = new ClientResource(URL);

		// If we allow Restlet to follow redirects it makes processing our asynchronous job more difficult in the client
		dendrogramResource.setFollowingRedirects(false);
		dendrogramResource.addQueryParameter("lineCount", "" + lineCount);
		dendrogramResource.addQueryParameter("flapjackUID", Prefs.flapjackID);

		// Create a representation of the SimMatrix that allows its content to be streamed to the server
		SimMatrixWriterRepresentation writerRep = new SimMatrixWriterRepresentation(MediaType.TEXT_PLAIN, matrix);
		dendrogramResource.post(writerRep);

		return dendrogramResource.getLocationRef();
	}

	/**
	 * Polls the task resource for the creation of this Dendrogram until the resource has been created and either
	 * goes on to get the Dendrogram from the URI returned by the polling process, or throws an exception (indicating
	 * something went wrong with the creation of the Dendrogram).
	 *
	 * @param 	dendrogramTaskUri the URI for which to poll the current status of dendrogram creation
	 * @return	a byte[] representing a zipFile
	 * @throws	IOException
	 */
	private byte[] getDendrogramAsByteArray(Reference uri)
		throws Exception
	{
		ClientResource cr = new ClientResource(uri);
		cr.accept(MediaType.APPLICATION_ZIP);
		Representation r = cr.get();

		while (okToRun && cr.getStatus().equals(SUCCESS_NO_CONTENT))
		{
			System.out.println("Waiting for result...");

			try { Thread.sleep(500); }
			catch (InterruptedException e) {}

			// We've been waiting a while...the user may have cancelled
			if (okToRun)
			{
				cr.setReference(uri);
				r = cr.get();
			}
		}

		if (okToRun && cr.getStatus().equals(SUCCESS_OK))
		{
			System.out.println("Grabbing result...");

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			r.write(outputStream);

			return outputStream.toByteArray();
		}

		return null;
	}

	/**
	 * Takes a byte array representing a zip File and creates a {@link jhi.flapjack.data.Dendrogram Dendrogram} from
	 * its contents. The zip File should contain entries called order.txt, dendrogram.png and dendrogram.pdf.
	 *
	 * @param 	zipBytes a byte[] representing the contents of a zip File
	 * @return 	a Dendrogram object
	 * @throws 	IOException
	 */
	private Dendrogram createDendrogramFromZip(byte[] zipBytes)
		throws IOException
	{
		Dendrogram d = new Dendrogram();

		ZipInputStream zis = new ZipInputStream(new BufferedInputStream((new ByteArrayInputStream(zipBytes))));
		ZipEntry entry;

		while ((entry = zis.getNextEntry()) != null)
		{
			switch (entry.getName())
			{
				case "order.txt" : 		byte[] orderBytes = readBinaryZipEntry(zis);
										lineOrder = readLineOrderFromByteArray(orderBytes);
										break;

				case "dendrogram.png" :	d.getPng().image = readBinaryZipEntry(zis);
										break;

				case "dendrogram.pdf" : d.getPdf().data = readBinaryZipEntry(zis);
										break;
			}
		}

		return d;
	}

	/**
	 * Reads a binary ZipEntry from a ZipInputStream and returns the contents of the entry as a byte array.
	 *
	 * @param 	zis a ZipInputStream for a binary ZipEntry
	 * @return	a byte[] representing the contents of that entry
	 * @throws 	IOException
	 */
	private byte[] readBinaryZipEntry(ZipInputStream zis)
		throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int read;
		byte[] buffer = new byte[1024];

		while ((read = zis.read(buffer, 0, buffer.length)) != -1)
			bos.write(buffer, 0, read);

		return bos.toByteArray();
	}

	/**
	 * Takes a byte[] which represents the contents of a file with one integer id on each of its lines and
	 * returns an ArrayList of these.
	 *
	 * @param 	orderBytes a byte[] representing the contents of this file
	 * @return	an ArrayList<Integer> representing the order of the lines in the Dendrogram
	 * @throws 	IOException
	 */
	private ArrayList<Integer> readLineOrderFromByteArray(byte[] orderBytes)
		throws IOException
	{
		ArrayList<Integer> order = new ArrayList<>();
		ByteArrayInputStream bis = new ByteArrayInputStream(orderBytes);
		BufferedReader in = new BufferedReader(new InputStreamReader(bis));
		String str;

		// Read the line order
		while ((str = in.readLine()) != null && str.length() > 0)
			order.add(Integer.parseInt(str) - 1);

		return order;
	}

	public ArrayList<Integer> getLineOrder()
		{ return lineOrder; }

	public void cancelJob()
	{
		okToRun = false;
		RestUtils.cancelJob(taskURI);
	}
}