// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet;

import java.io.*;
import java.util.concurrent.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import org.restlet.data.*;
import org.restlet.representation.*;
import org.restlet.resource.*;

public class PCoAClient
{
	private boolean okToRun = true;
	private UriPoller poller;

	private FutureTask<Boolean> pollTask;

	public PCoAResult generatePco(SimMatrix matrix)
		throws Exception
	{
		Reference pcoaId  = postPcoa(matrix);

		String pcoaText = getPcoa(pcoaId);

		if (okToRun)
			return createPcoaFromResponse(pcoaText, matrix);

		return null;
	}

	private Reference postPcoa(SimMatrix matrix)
	{
		ClientResource pcoaResource = new ClientResource(FlapjackServlet.PCOA_ROUTE);
		pcoaResource.setFollowingRedirects(false);
		pcoaResource.addQueryParameter("flapjackId", Prefs.flapjackID);

		SimMatrixWriterRepresentation writerRep = new SimMatrixWriterRepresentation(MediaType.TEXT_PLAIN, matrix);

		pcoaResource.post(writerRep);

		return pcoaResource.getLocationRef();
	}

	private String getPcoa(Reference pcoaTaskUri)
		throws Exception
	{
		poller = new UriPoller(pcoaTaskUri);
		pollTask = new FutureTask<Boolean>(poller);
		pollTask.run();

		if (okToRun && pollTask.get())
		{
			Reference ref = poller.result();

			ClientResource resultResource = new ClientResource(ref);
			Representation pcoaRep = resultResource.get();

			return pcoaRep.getText();
		}

		return null;
	}


	private PCoAResult createPcoaFromResponse(String responseText, SimMatrix matrix)
	{
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
					data[i - 1] = Float.parseFloat(tokens[i]);

				result.addDataRow(data);
			}

			return result;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public void cancelJob()
	{
		okToRun = false;

		if (poller != null)
		{
			pollTask.cancel(true);
			poller.cancelJob();
		}
	}
}