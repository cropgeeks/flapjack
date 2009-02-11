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
	private boolean isOK = false;

	public DataImportingDialog(File mapFile, File genoFile)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.DataImportingDialog.title"),
			true
		);

		mapImporter  = new ChromosomeMapImporter(mapFile, dataSet);
		genoImporter = new GenotypeDataImporter(genoFile, dataSet,
			Prefs.ioMissingData, Prefs.ioHeteroSeparator);

		add(createControls());

		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				loadData();
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
		pBar.setPreferredSize(new Dimension(300, pBar.getPreferredSize().height));

		mapsLabel = new JLabel();
		mrksLabel = new JLabel();
		lineLabel = new JLabel();


		JPanel panel = new JPanel(new BorderLayout(5, 5));

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

			// Create (and add) a default view of the dataset
			GTViewSet viewSet = new GTViewSet(dataSet, "Default View");
			dataSet.getViewSets().add(viewSet);

			System.out.println("Genotype data loaded in " + (e-s) + "ms");

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
				RB.format("gui.dialog.DataImportingDialog.exception", e),
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
}