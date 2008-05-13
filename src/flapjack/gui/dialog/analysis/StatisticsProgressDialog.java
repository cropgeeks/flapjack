package flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.*;

public class StatisticsProgressDialog extends JDialog implements Runnable
{
	private GTViewSet viewSet;

	private NBStatisticsProgressPanel nbPanel;
	private boolean isOK = true;

	// Class that will gather the actual statistics for us
	private AlleleStatistics statistics;

	public StatisticsProgressDialog(GTViewSet viewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.StatisticsProgressDialog.title"),
			true);

		this.viewSet = viewSet;

		add(nbPanel = new NBStatisticsProgressPanel());

		addWindowListener(new WindowAdapter()
		{
			public void windowOpened(WindowEvent e)
			{
				computeStatistics();
			}
			public void windowClosing(WindowEvent e)
			{
				isOK = false;
				statistics.cancel();
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
		{ return statistics.getResults(); }

	private void computeStatistics()
	{
		nbPanel.pBar.setMaximum(viewSet.getAlleleCount());

		new Thread(this).start();
		new MonitorThread().start();
	}

	public void run()
	{
		statistics = new AlleleStatistics(viewSet);
		statistics.computeStatistics();

		setVisible(false);
	}

	private class MonitorThread extends Thread
	{
		public void run()
		{
			while (isVisible())
			{
				Runnable r = new Runnable() {
					public void run()
					{
						if (statistics != null)
							nbPanel.pBar.setValue(statistics.getAlleleCount());
					}
				};

				SwingUtilities.invokeLater(r);

				try { Thread.sleep(100); }
				catch (InterruptedException e) {}
			}
		}
	}
}