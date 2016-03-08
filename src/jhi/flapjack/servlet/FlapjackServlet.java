package jhi.flapjack.servlet;

import java.util.*;
import java.util.concurrent.*;

import org.restlet.*;
import org.restlet.routing.*;

public class FlapjackServlet extends Application
{
	private static ExecutorService executor;
	private static HashMap<String, FutureTask<FlapjackTask>> runningTasks;
	private static HashMap<String, Integer> tasksByUser;

	private static Set<String> cancelledTasks;

	private static final int NUM_THREADS = 4;

	public static final String BASE_URL = "http://localhost:8080/flapjack-test/";

	public static final String DENDROGRAM = "dendrogram/";
	public static final String DENDROGRAM_TASK = DENDROGRAM + "task/";
	public static final String PCOA = "pcoa/";
	public static final String PCOA_TASK = PCOA + "task/";

	public static final String DENDROGRAM_TASK_ROUTE = BASE_URL + DENDROGRAM_TASK;
	public static final String DENDROGRAM_ROUTE = BASE_URL + DENDROGRAM;
	public static final String PCOA_TASK_ROUTE = BASE_URL + PCOA_TASK;
	public static final String PCOA_ROUTE = BASE_URL + PCOA;

	public static final String R_PATH = "C:/Program Files/R/R-3.2.3/bin/R.exe";
	//		String rPath = "/usr/bin/R";

	public FlapjackServlet()
	{
		setName("Restful Flapjack Server");
		setDescription("Test plant breeding API (BRAPI) implementation");
		setOwner("The James Hutton Institute");
		setAuthor("Information & Computational Sciences, JHI");

		// Creates a fixed size thread pool which automatically queues any additional
		// jobs which are added to the work queue
		executor = Executors.newFixedThreadPool(NUM_THREADS);
		runningTasks = new HashMap<String, FutureTask<FlapjackTask>>();
		cancelledTasks = new HashSet<String>();
		tasksByUser = new HashMap<String, Integer>();
	}

	@Override
	public Restlet createInboundRoot()
	{
		Router router = new Router(getContext());

		router.attach("/" + PCOA, 						PCoAServerResource.class);
		router.attach("/" + PCOA + "{id}", 				PCoAByIdServerResource.class);
		router.attach("/" + PCOA_TASK + "{id}", 		TaskServerResource.class);
		router.attach("/" + DENDROGRAM, 				DendrogramServerResource.class);
		router.attach("/" + DENDROGRAM + "{id}", 		DendrogramByIdServerResource.class);
		router.attach("/" + DENDROGRAM_TASK + "{id}", 	TaskServerResource.class);

		return router;
	}

	public static boolean checkTask(String id)
	{
		return runningTasks.get(id).isDone();
	}

	public static FlapjackTask getTask(String id)
	{
		FutureTask<FlapjackTask> task = runningTasks.get(id);
		if (task.isDone())
		{
			try
			{
				return task.get();
			}
			catch (Exception e) { e.printStackTrace(); }
		}
		return null;
	}

	public static FlapjackTask getAndRemoveTask(String id)
	{
		FutureTask<FlapjackTask> task = runningTasks.get(id);
		if (task.isDone())
		{
			try
			{
				runningTasks.remove(id);
				return task.get();
			}
			catch (Exception e) { e.printStackTrace(); }
		}
		return null;
	}

	public static void cancelTask(String id)
	{
		FutureTask<FlapjackTask> task = runningTasks.get(id);
		if (task != null)
		{
			task.cancel(true);
			runningTasks.remove(id);
			cancelledTasks.add(id);
		}
	}

	// Wraps the analysis in a FutureTask and submits this to the executor
	// where it is either queued, or run immediately. Also adds the task to a
	// HashMap where it is keyed by id.
	public static void submitFlapjackServletTask(FlapjackTask analysis, String taskId)
	{
		FutureTask<FlapjackTask> task = new FutureTask<>(analysis);
		executor.submit(task);
		runningTasks.put(taskId, task);
	}

	public static String getUserTaskId(String flapjackId)
	{
		String taskId;

		Integer result = tasksByUser.putIfAbsent(flapjackId, 0);
		if (result != null)
			taskId =  flapjackId + "-" + tasksByUser.put(flapjackId, result + 1);
		else
			taskId =  flapjackId + "-" + 0;

		return taskId;
	}
}