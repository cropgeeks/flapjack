package jhi.flapjack.io.brapi;

import java.util.*;

import uk.ac.hutton.ics.brapi.resource.base.Status;

public class AsyncChecker
{
	public static final String ASYNCID = "asyncid";
	public static final String ASYNCSTATUS = "asyncstatus";
	public static final String ASYNC_PENDING = "PENDING";
	public static final String ASYNC_INPROCESS = "INPROCESS";
	public static final String ASYNC_FINISHED = "FINISHED";
	public static final String ASYNC_FAILED = "FAILED";

	public enum AsyncStatus
	{
		PENDING,
		INPROCESS,
		FINISHED,
		FAILED,
		UNKNOWN
	}

	public static Status hasAsyncId(List<Status> statuses)
	{
		Status status = null;

		Optional<Status> asyncStatus = statuses.stream()
			.filter(s -> s.getMessageType().equalsIgnoreCase(ASYNCID) || s.getMessageType().equalsIgnoreCase("asynchid"))
			.findFirst();

		if (asyncStatus.isPresent())
			status = asyncStatus.get();

		return status;
	}

	public static AsyncStatus checkStatus(List<Status> statuses)
	{
		Status status = null;

		Optional<Status> asyncStatus = statuses.stream()
			.filter(s -> s.getMessageType().equalsIgnoreCase(ASYNCSTATUS) || s.getMessageType().equalsIgnoreCase("asynchstatus"))
			.findFirst();

		if (asyncStatus.isPresent())
			status = asyncStatus.get();

		AsyncStatus found = AsyncStatus.UNKNOWN;

		if (callPending(status))
			found = AsyncStatus.PENDING;
		else if (callInProcess(status))
			found = AsyncStatus.INPROCESS;
		else if (callFinished(status))
			found = AsyncStatus.FINISHED;
		else if (callFailed(status))
			found = AsyncStatus.FAILED;

		return found;
	}

	public static Status checkAsyncStatus(List<Status> statuses)
	{
		Status status = null;

		Optional<Status> asyncStatus = statuses.stream()
			.filter(s -> s.getMessageType().equalsIgnoreCase(ASYNCSTATUS) || s.getMessageType().equalsIgnoreCase("asynchstatus"))
			.findFirst();

		if (asyncStatus.isPresent())
			status = asyncStatus.get();

		return status;
	}

	public static boolean callPending(Status status)
	{
		return status != null && status.getMessage().equalsIgnoreCase(ASYNC_PENDING);
	}

	public static boolean callInProcess(Status status)
	{
		return status != null && status.getMessage().equalsIgnoreCase(ASYNC_INPROCESS);
	}

	public static boolean callFinished(Status status)
	{
		return status != null && status.getMessage().equalsIgnoreCase(ASYNC_FINISHED);
	}

	public static boolean callFailed(Status status)
	{
		return status != null && status.getMessage().equalsIgnoreCase(ASYNC_FAILED);
	}
}