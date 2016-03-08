package jhi.flapjack.servlet;

import org.restlet.representation.*;
import org.restlet.resource.*;

public class PCoAByIdServerResource extends ServerResource
{
	// The ID from the URI (need to get this in overridden doInit method)
	private String id;

	@Override
	public void doInit()
	{
		super.doInit();
		this.id = (String)getRequestAttributes().get("id");
	}

	@Get("txt")
	public Representation getPcoaFile()
	{
		FlapjackTask task = FlapjackServlet.getAndRemoveTask(id);

		return task.getRepresentation();
	}
}