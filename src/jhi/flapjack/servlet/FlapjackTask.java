package jhi.flapjack.servlet;

import java.util.concurrent.*;

import org.restlet.representation.*;

public interface FlapjackTask extends Callable<FlapjackTask>
{
	Representation getRepresentation();

	String getURI();
}