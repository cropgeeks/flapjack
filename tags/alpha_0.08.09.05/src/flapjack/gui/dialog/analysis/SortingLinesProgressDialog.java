package flapjack.gui.dialog.analysis;

import java.awt.event.*;
import javax.swing.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.visualization.*;

public class SortingLinesProgressDialog extends JDialog
{
	private JProgressBar pBar;
	private GenotypePanel gPanel;

	// Which chromosomes should be used during this sort?
	private boolean[] chromosomes;

	// Runnable object that will be active while the dialog is visible
	private Runnable runnable;
	// Which will be running this sort
	private ILineSorter sort;

	public SortingLinesProgressDialog(GenotypePanel gPanel, ILineSorter sort)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.SortingLinesProgressDialog.title"),
			true);

		this.gPanel = gPanel;
		this.sort = sort;

		NBSortingLinesProgressPanel panel = new NBSortingLinesProgressPanel();
		pBar = panel.getProgressBar();

		add(panel);

		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e)
			{
				new Thread(new SortThread()).start();
				new Thread(new MonitorThread()).start();
			}
		});

		pack();
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
	}

	public void runSort()
	{
		pBar.setMaximum(sort.getMaximum());
		setVisible(true);
	}

	private void sortCompleted()
	{
		gPanel.refreshView();
		gPanel.jumpToPosition(0, -1);

		Actions.projectModified();

		setVisible(false);
	}

	private class SortThread implements Runnable
	{
		public void run()
		{
			MovedLinesState state = new MovedLinesState(gPanel.getViewSet(),
				RB.getString("gui.visualization.MovedLinesState.sortedLines"));

			gPanel.getViewSet().setDisplayLineScores(false);

			state.createUndoState();

			// Make sure any dummy lines have been stripped out before sorting
			gPanel.getViewSet().removeAllDummyLines();

			sort.doSort();
			state.createRedoState();

			gPanel.addUndoState(state);

			Runnable r = new Runnable() {
				public void run() {
					sortCompleted();
				}
			};

			SwingUtilities.invokeLater(r);
		}
	}

	private class MonitorThread implements Runnable
	{
		public void run()
		{
			Runnable r = new Runnable() {
				public void run() {
					pBar.setValue(sort.getValue());
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