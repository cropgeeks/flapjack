// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet.pcoa;

import java.io.*;
import java.text.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.servlet.*;

import static org.restlet.data.Status.*;
import org.restlet.data.*;
import org.restlet.representation.*;
import org.restlet.resource.*;

public class PCoAClient
{
	private final String URL = "http://wildcat:8080/flapjack-test/pcoa/";
	private Reference taskURI;

	private boolean okToRun = true;

	public PCoAResult generatePco(SimMatrix matrix, String noDimensions)
		throws Exception
	{
		taskURI = postPcoa(matrix, noDimensions);

		if (okToRun)
		{
			String pcoaText = getPcoa(taskURI);

			if (pcoaText != null && pcoaText.isEmpty() == false)
				return createPcoaFromResponse(pcoaText, matrix);
		}

		return null;
	}

	private Reference postPcoa(SimMatrix matrix, String noDimensions)
	{
		ClientResource pcoaResource = new ClientResource(URL);
		pcoaResource.setFollowingRedirects(false);
		pcoaResource.addQueryParameter("flapjackUID", Prefs.flapjackID);
		pcoaResource.addQueryParameter("noDimensions", noDimensions);

		System.out.println("Posting to: " + URL);

		SimMatrixWriterRepresentation writerRep = new SimMatrixWriterRepresentation(MediaType.TEXT_PLAIN, matrix);
		pcoaResource.post(writerRep);

		return pcoaResource.getLocationRef();
	}

	private String getPcoa(Reference uri)
		throws Exception
	{
		ClientResource cr = new ClientResource(uri);
		cr.accept(MediaType.TEXT_PLAIN);
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

			return r.getText();
		}

		return null;
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
		RestUtils.cancelJob(taskURI);
	}
}