package flapjack.gui.dialog;

import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

/**
 * Common class used by most of the trackable job types as they run to display
 * a dialog with a tracking progress bar.
 */
public class ProgressDialog extends JDialog
{
	private NBProgressPanel nbPanel;

	// True while the job is ok and hasn't been cancelled/failed
	private boolean isOK = true;

	// Runnable object that will be active while the dialog is visible
	private ITrackableJob job;

	// A reference to any exception thrown while the job was active
	private Exception exception = null;

	public ProgressDialog(final ITrackableJob job, String title, String label)
	{
		super(Flapjack.winMain, "", true);
		this.job = job;

		nbPanel = new NBProgressPanel(job, label);
		add(nbPanel);

		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e)	{
				runJob();
			}
			public void windowClosing(WindowEvent e)
			{
				job.cancelJob();
				isOK = false;
			}
		});

		pack();
		setTitle(title);
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(true);
		setVisible(true);
	}

	// Starts the job running in its own thread, catching any exceptions that
	// may occur as it runs.
	private void runJob()
	{
		Runnable r = new Runnable() {
			public void run()
			{
				try
				{
					job.runJob();
				}
				catch (Exception e)
				{
					exception = e;
					isOK = false;
				}

				setVisible(false);
			}
		};

		new MonitorThread().start();
		new Thread(r).start();
	}

	public boolean isOK()
		{ return isOK; }

	public Exception getException()
		{ return exception; }

	// Simple monitor thread that tracks the progress of the job, updating the
	// progress bar as it goes
	private class MonitorThread extends Thread
	{
		public void run()
		{
			Runnable r = new Runnable() {
				public void run()
				{
					nbPanel.pBar.setValue(job.getValue());

					// We set the progress bar to indeterminate once the progess
					// has reached 100% as it's sometimes possible for
					// additional (non-trackable) post-processing to happen too.
					if (nbPanel.pBar.getValue() == nbPanel.pBar.getMaximum())
						nbPanel.pBar.setIndeterminate(true);
				}
			};

			while (isVisible())
			{
				SwingUtilities.invokeLater(r);

				try { Thread.sleep(100); }
				catch (InterruptedException e) {}
			}
		}
	}
}