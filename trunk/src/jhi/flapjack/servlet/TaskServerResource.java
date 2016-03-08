package jhi.flapjack.servlet;

import org.restlet.representation.*;
import org.restlet.resource.*;

public class TaskServerResource extends ServerResource
{
	private String id;

	@Override
	public void doInit()
	{
		super.doInit();
		this.id = (String)getRequestAttributes().get("id");
	}

	@Get
	public Representation respond()
	{
		Representation rep = new StringRepresentation("Task is currently processing");

		if (FlapjackServlet.checkTask(id))
		{
			rep = new StringRepresentation("Task is finished");

			FlapjackTask task = FlapjackServlet.getTask(id);

			redirectSeeOther(task.getURI() + id);
		}

		return rep;
	}

	@Delete
	public void cancelTask()
	{
		FlapjackServlet.cancelTask(id);
	}
}
