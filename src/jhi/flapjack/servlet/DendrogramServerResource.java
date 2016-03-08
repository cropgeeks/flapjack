package jhi.flapjack.servlet;

import org.restlet.representation.*;
import org.restlet.resource.*;

public class DendrogramServerResource extends ServerResource
{
	private static final String PARAM_LINE_COUNT = "lineCount";
	private static final String FLAPJACK_ID = "flapjackId";

	private int lineCount;
	private String flapjackId;

	@Override
	public void doInit()
	{
		super.doInit();
		this.lineCount = Integer.parseInt(getQueryValue(PARAM_LINE_COUNT));
		this.flapjackId = getQueryValue(FLAPJACK_ID);
	}

	@Post
	public void store(Representation representation)
	{
		String taskId = FlapjackServlet.getUserTaskId(flapjackId);

		// We have to write out our similarity matrix to disk before we create and run our FlapjackTask (Dendrogram
		// Task in this case)
		RestUtils.writeSimMatrix(taskId, representation);

		DendrogramTask task = new DendrogramTask(taskId, lineCount);

		FlapjackServlet.submitFlapjackServletTask(task, taskId);

		// Set the status to 303 and tell our caller where to go to find the created resource
		redirectSeeOther(FlapjackServlet.DENDROGRAM_TASK_ROUTE + taskId);
	}
}