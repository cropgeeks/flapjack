package jhi.flapjack.servlet;

import org.restlet.representation.*;
import org.restlet.resource.*;

public class DendrogramByIdServerResource extends ServerResource
{
	// The ID from the URI (need to get this in overridden doInit method)
	private String id;

	@Override
	public void doInit()
	{
		super.doInit();
		this.id = (String)getRequestAttributes().get("id");
	}

	@Get("zip")
	public Representation getDendrogramZip()
	{
		FlapjackTask task = FlapjackServlet.getAndRemoveTask(id);

		return task.getRepresentation();
	}
}