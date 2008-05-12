package flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

public class StatisticsProgressDialog extends JDialog implements Runnable
{
	private GTViewSet viewSet;

	private NBStatisticsProgressPanel nbPanel;

	private Vector<int[]> results;
	private int alleleCount = 0;
	private boolean isOK = true;

	public StatisticsProgressDialog(GTViewSet viewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.StatisticsProgressDialog.title"),
			true);

		this.viewSet = viewSet;

		add(nbPanel = new NBStatisticsProgressPanel());

		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				getStatistics();
			}
			public void windowClosing(WindowEvent e) {
				isOK = false;
			}
		});

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	public boolean isOK()
		{ return isOK; }

	public Vector<int[]> getResults()
	{
		return results;
	}

	private void getStatistics()
	{
		new Thread(new MonitorThread()).start();
		new Thread(this).start();
	}

	public void run()
	{
		StateTable stateTable = viewSet.getDataSet().getStateTable();

		nbPanel.pBar.setMaximum(viewSet.getAlleleCount());

		int viewCount = viewSet.getViews().size();

		results = new Vector<int[]>(viewCount);

		// TODO: This could be multi-core optimized
		for (GTView view: viewSet.getViews())
			results.add(getStatistics(view));

		setVisible(false);
	}

	// Returns an array with each element being the total number of alleles for
	// that state (where each index is equivalent to a state in the state table.
	int[] getStatistics(GTView view)
	{
		int stateCount = viewSet.getDataSet().getStateTable().size();

		// +1 because we use the last location to store the total count of
		// alleles within this view (chromosome)
		int[] statistics = new int[stateCount+1];

		view.cacheLines();

		for (int line = 0; line < view.getLineCount(); line++)
			for (int marker = 0; marker < view.getMarkerCount() && isOK; marker++)
			{
				int state = view.getState(line, marker);
				statistics[state]++;

				// Track the total
				statistics[statistics.length-1]++;
				alleleCount++;
			}

		return statistics;
	}

	private class MonitorThread implements Runnable
	{
		public void run()
		{
			while (isVisible())
			{
				Runnable r = new Runnable() {
					public void run() {
						nbPanel.pBar.setValue(alleleCount);
					}
				};

				SwingUtilities.invokeLater(r);

				try { Thread.sleep(50); }
				catch (InterruptedException e) {}
			}
		}
	}
}