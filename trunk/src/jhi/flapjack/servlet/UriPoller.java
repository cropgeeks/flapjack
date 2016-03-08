package jhi.flapjack.servlet;

import java.util.concurrent.*;

import org.restlet.data.*;
import org.restlet.resource.ClientResource;

/**
 * Class which takes a URI in the form of a {@link org.restlet.data.Reference Reference} and can be used to poll this
 * URI until such time as it stops returning a 200 status. This is useful as our REST API returns a 200 for task
 * resources while processing is occurring, before returing a 303 status with a location header set for the client to
 * follow upon a job's successful completion.
 */
public class UriPoller implements Callable<Boolean>
{
	private final Reference uri;
	private final ClientResource pollResource;

	private boolean okToRun = true;

	public UriPoller(Reference uri)
	{
		this.uri = uri;
		pollResource = new ClientResource(uri);
	}

	public Boolean call()
	{
		pollResource.setFollowingRedirects(false);
		// We could get a Representation back here and display to the user, may want to if the jobs are genuinely
		// very long running. Not sure if we can do any progress tracking anyway
		pollResource.get();

		Status status = pollResource.getStatus();
		while (status.isSuccess() && okToRun)
		{
			try
			{
				Thread.sleep(10000);
			}
			catch (InterruptedException e) {}

			if (okToRun)
			{
				pollResource.setReference(uri);
				pollResource.get();
				status = pollResource.getStatus();
			}
			else
			{
				RestUtils.cancelJob(uri);
				return false;
			}
		}

		return (status.equals(Status.REDIRECTION_SEE_OTHER));
	}

	public void cancelJob()
	{
		okToRun = false;
	}

	public Reference result()
	{
		return pollResource.getLocationRef();
	}
}