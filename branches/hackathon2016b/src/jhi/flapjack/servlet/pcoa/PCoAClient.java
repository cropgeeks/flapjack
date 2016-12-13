// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet.pcoa;

import java.io.*;
import java.text.*;

import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.io.*;

import okhttp3.*;
import retrofit2.*;
import retrofit2.Response;
import retrofit2.converter.jackson.*;

public class PCoAClient
{
	private final String baseURL = "https://ics.hutton.ac.uk/flapjack-services-test/";
	//	private final String URL = "https://ics.hutton.ac.uk/flapjack-services-20160817/pcoa/";
	private String taskId;

	private Retrofit retrofit;
	private PCoAService service;

	private final SimMatrix matrix;
	private final String noDimensions;

	private boolean okToRun = true;

	public PCoAClient(SimMatrix matrix, String noDimensions)
	{
		this.matrix = matrix;
		this.noDimensions = noDimensions;

		retrofit = new Retrofit.Builder()
			.baseUrl(baseURL)
			.addConverterFactory(JacksonConverterFactory.create())
			.build();

		service = retrofit.create(PCoAService.class);
	}

	public PCoAResult generatePco()
		throws Exception
	{
		// Send similarity matrix to the pcoa API endpoint
		taskId = postSimMatrix();

		if (okToRun)
		{
			// Try to get the generated pcoa back from the web service
			Response<ResponseBody> response = service.getPcoa(taskId).execute();

			// Poll until we get a successful response which has content
			while (okToRun && (!response.isSuccessful() || (response.isSuccessful() && response.body() == null)))
			{
				System.out.println("Waiting for result...");

				try { Thread.sleep(15000); }
				catch (InterruptedException e) {}

				if (okToRun)
					response = service.getPcoa(taskId).execute();
			}

			// Once we have a response with content, create our pcoa
			// object from the returned string
			if (okToRun && response.isSuccessful() && response.body() != null)
			{
				String pcoaText = response.body().string();
				if (!pcoaText.isEmpty())
					return createPcoaFromResponse(pcoaText, matrix);
			}
		}

		return null;
	}

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

		Response<ResponseBody> response = service.postSimMatrix(body, noDimensions, Prefs.flapjackID)
			.execute();

		return response.body().string();
	}

	private PCoAResult createPcoaFromResponse(String responseText, SimMatrix matrix)
	{
		NumberFormat nf = NumberFormat.getInstance();

		try (BufferedReader in = new BufferedReader(new StringReader(responseText)))
		{
			PCoAResult result = new PCoAResult(matrix.getLineInfos());

			String str;

			// Read the line order
			while ((str = in.readLine()) != null && str.length() > 0)
			{
				String[] tokens = str.split("\t");
				float[] data = new float[tokens.length - 1];

				// TODO: Will R always return comma for its decimals???
				for (int i = 1; i < tokens.length; i++)
					data[i - 1] = nf.parse(tokens[i]).floatValue();

				result.addDataRow(data);
			}

			return result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public void cancelJob()
	{
		okToRun = false;
		service.cancelJob(taskId);
	}
}