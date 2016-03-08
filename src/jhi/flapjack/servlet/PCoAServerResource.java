package jhi.flapjack.servlet;

import org.restlet.representation.*;
import org.restlet.resource.*;

public class PCoAServerResource extends ServerResource
{
	private static final String FLAPJACK_ID = "flapjackId";

	private String flapjackId;

	@Override
	public void doInit()
	{
		super.doInit();
		this.flapjackId = getQueryValue(FLAPJACK_ID);
	}

	@Post
	public void store(Representation representation)
	{
		String taskId = FlapjackServlet.getUserTaskId(flapjackId);

		// We have to write out our similarity matrix to disk before we create and run our FlapjackTask (PCoATask in
		// this case)
		RestUtils.writeSimMatrix(taskId, representation);

		PCoATask task = new PCoATask(taskId);

		FlapjackServlet.submitFlapjackServletTask(task, taskId);

		// Set the status to 303 and tell our caller where to go to find the created resource
		redirectSeeOther(FlapjackServlet.PCOA_TASK_ROUTE + taskId);
	}
}