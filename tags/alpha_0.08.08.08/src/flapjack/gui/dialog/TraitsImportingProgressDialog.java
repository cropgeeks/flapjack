package flapjack.gui.dialog;

import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.io.*;

import scri.commons.file.*;
import scri.commons.gui.*;

public class TraitsImportingProgressDialog extends JDialog implements Runnable
{
	private File file;
	private DataSet dataSet;

	private NBTraitsImportingProgressPanel nbPanel;
	private boolean isOK = true;

	private TraitImporter importer;

	public TraitsImportingProgressDialog(File file, DataSet dataSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.TraitsImportingProgressDialog.title"),
			true);

		this.file = file;
		this.dataSet = dataSet;

		add(nbPanel = new NBTraitsImportingProgressPanel());

		addWindowListener(new WindowAdapter()
		{
			public void windowOpened(WindowEvent e)
			{
				importTraits();
			}
			public void windowClosing(WindowEvent e)
			{
				isOK = false;
				importer.cancel();
			}
		});

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	public boolean isOK()
		{ return isOK; }

	private void importTraits()
	{
		try
		{
			int lineCount = FileUtils.countLines(file, 16384);
			nbPanel.pBar.setMaximum(lineCount);
		}
		catch (Exception e) {}

		new Thread(this).start();
		new MonitorThread().start();
	}

	public void run()
	{
		try
		{
			importer = new TraitImporter(file, dataSet);
			importer.importTraitData();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			TaskDialog.error(
				RB.format("gui.dialog.TraitsImportingProgressDialog.error", file, e),
				RB.getString("gui.text.close"));
		}

		setVisible(false);
	}

	private class MonitorThread extends Thread
	{
		public void run()
		{
			Runnable r = new Runnable() {
				public void run()
				{
					if (importer != null)
						nbPanel.pBar.setValue(importer.getLineCount());

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