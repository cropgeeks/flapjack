package flapjack.servlet;

import java.io.*;
import java.util.concurrent.*;
import javax.servlet.http.*;

/**
 * The interface that needs to be implemented for jobs which need to be submitted
 * to our "grid engine-lite". It extends the Callable interface (which is like
 * Runnable, but provides a return value) so classes which implement this interface
 * also need to implement the call() method from the callable interface.
 */
public interface FlapjackTask extends Callable<FlapjackTask>
{
	// Should be called to write an http response once a job has completed running
	// on our "grid engine-lite"
	void writeResponse(HttpServletResponse response) throws IOException;
}
