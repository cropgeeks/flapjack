// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui;

/**
 * Interface for tasks that are likely to be run in a threaded environment,
 * and can provide progress-bar feedback to a GUI as they run.
 */
public interface ITrackableJob
{
	/** Returns the number of sub jobs that this trackable job will run. */
	public int getJobCount();

	/** Runs the job with index jobIndex (counting from 0). */
	public void runJob(int jobIndex) throws Exception;

	/** Returns true if this job cannot currently be progress bar tracked. */
	public boolean isIndeterminate();

	/** Returns the current maximum "value" that the job is trying to reach. */
	public int getMaximum();

	/** Returns the current value for progress through the job. */
	public int getValue();

	/** Indicates to the job that it should cancel whatever it is doing. */
	public void cancelJob();

	/** An optional (null if not used) message to display while running. */
	public String getMessage();
}