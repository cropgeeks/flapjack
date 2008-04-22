package flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.visualization.*;

public class SortingLinesProgressDialog extends JDialog
{
	private JProgressBar pBar;
	private GenotypePanel gPanel;

	// Runnable object that will be active while the dialog is visible
	private Runnable runnable;
	// Which will be running this sort
	private ILineSorter sort;

	public SortingLinesProgressDialog()
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.SortingLinesProgressDialog.title"),
			true);

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

	public void runSort(GenotypePanel gPanel, int method)
	{
		this.gPanel = gPanel;

		GTView view = gPanel.getView();
		int line = view.mouseOverLine;
		int loci = view.mouseOverMarker;

		if (method == 0)
			sort = new SortLinesBySimilarity(view, line);
		else if (method == 1)
			sort = new SortLinesByLocus(view, line, loci);

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

			state.createUndoState();
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
			while (isVisible())
			{
				Runnable r = new Runnable() {
					public void run() {
						pBar.setValue(sort.getValue());
					}
				};

				SwingUtilities.invokeLater(r);

				try { Thread.sleep(250); }
				catch (InterruptedException e) {}
			}
		}
	}
}