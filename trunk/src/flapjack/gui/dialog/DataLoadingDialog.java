package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.io.*;

import sbrn.commons.file.*;

public class DataLoadingDialog extends JDialog implements Runnable
{
	private DataSet dataSet = new DataSet();

	// To load the map file...
	private File mapFile;
	private ChromosomeMapImporter mapImporter;

	// To load the genotype file...
	private File genoFile;
	private GenotypeDataImporter genoImporter;

	private JLabel mapsLabel, mrksLabel, lineLabel;
	private JProgressBar pBar;

	public DataLoadingDialog(File mapFile, File genoFile)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.DataLoadingDialog.title"),
			true
		);

		mapImporter  = new ChromosomeMapImporter(mapFile, dataSet);
		genoImporter = new GenotypeDataImporter(genoFile, dataSet);

		add(createControls());

		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				loadData();
			}
		});

		pack();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createControls()
	{
		pBar = new JProgressBar();
		pBar.setPreferredSize(new Dimension(300, pBar.getPreferredSize().height));

		mapsLabel = new JLabel();
		mrksLabel = new JLabel();
		lineLabel = new JLabel();


		JPanel panel = new JPanel(new BorderLayout(5, 5));

		JPanel labelPanel = new JPanel(new GridLayout(3, 2));
		labelPanel.add(
			new JLabel(RB.getString("gui.dialog.DataLoadingDialog.maps")));
		labelPanel.add(mapsLabel);
		labelPanel.add(
			new JLabel(RB.getString("gui.dialog.DataLoadingDialog.line")));
		labelPanel.add(lineLabel);
		labelPanel.add(
			new JLabel(RB.getString("gui.dialog.DataLoadingDialog.mrks")));
		labelPanel.add(mrksLabel);


		panel.add(labelPanel, BorderLayout.WEST);
		panel.add(pBar, BorderLayout.SOUTH);

		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		return panel;
	}

	public DataSet getDataSet()
	{
		return dataSet;
	}

	private void loadData()
	{
		final NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());

		Runnable r = new Runnable() {
			public void run()
			{
				while (isVisible())
				{
					pBar.setValue(genoImporter.getLineCount());

					lineLabel.setText("  " + nf.format(genoImporter.getLineCount()));
					mrksLabel.setText("  " + nf.format(genoImporter.getMarkerCount()));

					try { Thread.sleep(100); }
					catch (InterruptedException e) {}
				}
			}
		};

		new Thread(r).start();
		new Thread(this).start();
	}

	public void run()
	{
		try
		{
			long s = System.currentTimeMillis();
			mapImporter.importMap();
			long e = System.currentTimeMillis();

			System.out.println("Map loaded in " + (e-s) + "ms");

			mapsLabel.setText("  " + dataSet.countChromosomeMaps());

			int lineCount = FileUtils.countLines(genoImporter.getFile(), 16384);
			pBar.setMaximum(lineCount);


			s = System.currentTimeMillis();
			genoImporter.importGenotypeData();
			e = System.currentTimeMillis();

			System.out.println("Genotype data loaded in " + (e-s) + "ms");

			setVisible(false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}