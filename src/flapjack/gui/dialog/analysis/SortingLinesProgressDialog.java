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
	private GenotypePanel gPanel;

	// Runnable object that will be active while the dialog is visible
	private Runnable runnable;

	public SortingLinesProgressDialog()
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.SortingLinesProgressDialog.title"),
			true);

		add(new NBSortingLinesProgressPanel());

		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				new Thread(runnable).start();
			}
		});

		pack();
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
	}

	public void runSort(GenotypePanel gPanel)
	{
		this.gPanel = gPanel;
		runnable = new SortThread();

		setVisible(true);
	}

	private void sortCompleted()
	{
		gPanel.refreshView();
		gPanel.jumpToPosition(0, -1);

		setVisible(false);
	}

	private class SortThread implements Runnable
	{
		public void run()
		{
			try { Thread.sleep(500); }
			catch (Exception e) {}

			GTView view = gPanel.getViewSet().getSelectedView();

			int line = view.selectedLine;
			new SimilaritySort(view, line).run();


			Runnable r = new Runnable() {
				public void run() {
					sortCompleted();
				}
			};

			SwingUtilities.invokeLater(r);
		}
	}
}