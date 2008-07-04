package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.*;
import flapjack.io.*;

import scri.commons.file.*;
import scri.commons.gui.*;

// TODO: This dialog allows itself to be closed during the loading operation
// which cancels the load in terms of data appearing in the interface, but the
// actual loading thread (in the background) will still run to completion.

/**
 * Dialog that appears during the importing of data. Shows a progress bar and
 * information/stats on the loading process and the data being read.
 */
public class DataImportingDialog extends JDialog implements Runnable
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
	private boolean isIndeterminate = false;
	private boolean isOK = false;

	public DataImportingDialog(File mapFile, File genoFile)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.DataImportingDialog.title"),
			true
		);

		this.mapFile = mapFile;
		this.genoFile = genoFile;

		mapImporter  = new ChromosomeMapImporter(mapFile, dataSet);
		genoImporter = new GenotypeDataImporter(genoFile, dataSet,
			Prefs.ioMissingData, Prefs.ioHeteroSeparator);

		add(createControls());

		addWindowListener(new WindowAdapter()
		{
			public void windowOpened(WindowEvent e) {
				loadData();
			}

			public void windowClosing(WindowEvent e) {
				mapImporter.cancelImport();
				genoImporter.cancelImport();
			}
		});

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createControls()
	{
		pBar = new JProgressBar();
		pBar.setIndeterminate(true);
		pBar.setPreferredSize(new Dimension(300, pBar.getPreferredSize().height));

		mapsLabel = new JLabel();
		mrksLabel = new JLabel();
		lineLabel = new JLabel();

		int[] array = new int[5];
		array[10] = 10;

		JPanel panel = new JPanel(new BorderLayout(5, 10));

		JPanel labelPanel = new JPanel(new GridLayout(3, 2));
		labelPanel.add(
			new JLabel(RB.getString("gui.dialog.DataImportingDialog.maps")));
		labelPanel.add(mapsLabel);
		labelPanel.add(
			new JLabel(RB.getString("gui.dialog.DataImportingDialog.line")));
		labelPanel.add(lineLabel);
		labelPanel.add(
			new JLabel(RB.getString("gui.dialog.DataImportingDialog.mrks")));
		labelPanel.add(mrksLabel);


		panel.add(labelPanel, BorderLayout.WEST);
		panel.add(pBar, BorderLayout.SOUTH);

		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		return panel;
	}

	public boolean isOK() {
		return isOK;
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	private void loadData()
	{
		new Thread(new MonitorThread()).start();
		new Thread(this).start();
	}

	public void run()
	{
		try
		{
			// Read the map
			mapImporter.importMap();

			mapsLabel.setText("  " + dataSet.countChromosomeMaps());

			int lineCount = FileUtils.countLines(genoImporter.getFile(), 16384);
			pBar.setMaximum(lineCount);

			// Read the genotype data
			genoImporter.importGenotypeData();

			// Post-import stuff...
			PostImportOperations pio = new PostImportOperations(dataSet);

			isIndeterminate = true;

			pio.setName(genoFile);

			// Collapse heterozyous states
			if (Prefs.ioHeteroCollapse)
				pio.collapseHeterozygotes();


//			fakeQTLs(dataSet.getMapByIndex(0).getQTLs());

			pio.calculateMarkerFrequencies();
			pio.createDefaultView();

			if (Prefs.warnDuplicateMarkers)
				displayDuplicates();

			isOK = true;
		}
		catch (IOException e)
		{
			TaskDialog.error(
				RB.format("gui.dialog.DataImportingDialog.ioException", e.getMessage()),
				RB.getString("gui.text.close"));

			e.printStackTrace();
		}
		catch (Exception e)
		{
			TaskDialog.error(
				RB.format("gui.dialog.DataImportingDialog.exception", e.getMessage()),
				RB.getString("gui.text.close"));

			e.printStackTrace();
		}

		setVisible(false);
	}

	private void displayDuplicates()
	{
		if (mapImporter.getDuplicates().size() == 0)
			return;

		Runnable r = new Runnable() {
			public void run() {
				new DuplicateMarkersDialog(mapImporter.getDuplicates());
			}
		};

		try { SwingUtilities.invokeAndWait(r); }
		catch (Exception e) {}
	}



	private void fakeQTLs(Vector<QTL> qtls)
	{
		qtls.add(new QTL("TestA", 5f, 3f, 7f, 39.31f));
		qtls.add(new QTL("TestB", 141f, 138f, 141.5f, 39.31f));
		qtls.add(new QTL("TestC", 28f, 24f, 30f, 19.96f));
	}

	private class MonitorThread implements Runnable
	{
		public void run()
		{
			final NumberFormat nf = NumberFormat.getInstance();

			Runnable r = new Runnable()
			{
				public void run()
				{
					int lineCount = genoImporter.getLineCount();

					pBar.setIndeterminate(isIndeterminate);

					if (lineCount > 0)
						pBar.setValue(genoImporter.getLineCount());

					lineLabel.setText("  " + nf.format(genoImporter.getLineCount()));
					mrksLabel.setText("  " + nf.format(genoImporter.getMarkerCount()));
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