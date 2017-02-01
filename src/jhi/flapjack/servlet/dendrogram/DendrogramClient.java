// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet.dendrogram;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.io.*;

import okhttp3.*;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.*;

public class DendrogramClient
{
	private final String baseURL = "http://143.234.127.88:8080/flapjack-services-test/";
//	private final String baseURL = "https://ics.hutton.ac.uk/flapjack-services-test/";
//	private final String URL = "https://ics.hutton.ac.uk/flapjack-services-20160817/dendrogram/";
	private String taskId;

	private Retrofit retrofit;
	private DendrogramService service;

	private final SimMatrix matrix;
	private final int lineCount;

	private ArrayList<Integer> lineOrder = new ArrayList<>();

	private boolean okToRun = true;
	private Thread runnerThread;

	public DendrogramClient(SimMatrix matrix, int lineCount)
	{
		this.matrix = matrix;
		this.lineCount = lineCount;

		retrofit = new Retrofit.Builder()
			.baseUrl(baseURL)
			.addConverterFactory(JacksonConverterFactory.create())
			.build();

		service = retrofit.create(DendrogramService.class);
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
		runnerThread = Thread.currentThread();

		// Send similarity matrix to the dendrogram API endpoint
		taskId = postSimMatrix();

		if (okToRun)
		{
			// Try to get the generated dendrogram back from the web service
			Response<ResponseBody> response = service.getDendrogram(taskId).execute();

			// Poll until we get a successful response which has content
			while (okToRun && (!response.isSuccessful() || (response.isSuccessful() && response.body() == null)))
			{
				System.out.println("Waiting for result...");

				try { Thread.sleep(15000); }
				catch (InterruptedException e) {}

				if (okToRun)
					response = service.getDendrogram(taskId).execute();
			}

			// Once we have a response with content, create our dendrogram
			// objects from the returned byte array
			if (okToRun && response.isSuccessful() && response.body() != null)
			{
				byte[] dendrogramBytes = response.body().bytes();
				return createDendrogramFromZip(dendrogramBytes);
			}
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
	private String postSimMatrix()
		throws Exception
	{
		File temp = new File(FlapjackUtils.getCacheDir(), Prefs.flapjackID + ".matrix");
		SimMatrixExporter exporter = new SimMatrixExporter(matrix, new PrintWriter(new FileWriter(temp)));
		exporter.runJob(0);

		// Create RequestBody instance from file
		RequestBody requestFile = RequestBody.create(okhttp3.MediaType.parse("multipart/format-data"), temp);

		// MultiparBody.Part is used to send also the actual file name
		MultipartBody.Part body = MultipartBody.Part.createFormData("matrix", temp.getName(), requestFile);

		Response<ResponseBody> response = service.postSimMatrix(body, "" + lineCount, Prefs.flapjackID)
			.execute();

		return response.body().string();
	}

	/**
	 * Takes a byte array representing a zip File and creates a Dendrogram from
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
		if (runnerThread != null)
			runnerThread.interrupt();

		try
		{
			okToRun = false;
			service.cancelJob(taskId).execute();
		} catch (Exception e) {}
	}
}