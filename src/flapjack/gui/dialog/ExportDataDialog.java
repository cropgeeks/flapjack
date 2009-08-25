package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.io.*;
import flapjack.other.*;

import scri.commons.gui.*;

public class ExportDataDialog extends JDialog implements ActionListener
{
	private JButton bExport, bClose, bHelp;
	private NBExportDataPanel nbPanel;

	private GTViewSet viewSet;
	private String baseName;

	public ExportDataDialog(GTViewSet viewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.ExportDataDialog.title"),
			true
		);

		this.viewSet = viewSet;
		baseName = viewSet.getDataSet().getName();
		baseName = baseName.substring(0, baseName.lastIndexOf(" "));

		nbPanel = new NBExportDataPanel(this);

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bExport);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bExport = SwingUtils.getButton(RB.getString("gui.dialog.ExportDataDialog.bExport"));
		bExport.addActionListener(this);
		bClose = SwingUtils.getButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "gui.dialog.ExportDataDialog");

		JPanel p1 = FlapjackUtils.getButtonPanel();
		p1.add(bExport);
		p1.add(bClose);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bClose)
			setVisible(false);

		else if (e.getSource() == bExport)
		{
			if (nbPanel.combo.getSelectedIndex() == 0)
				exportMap();
			else
				exportDat();
		}
	}

	// Counts how many markers will be exported
	private int getMarkerCount()
	{
		boolean allMarkers = nbPanel.rMapAll.isSelected();

		int count = 0;
		for (GTView view: viewSet.getViews())
		{
			if (allMarkers)
				count += view.getMarkerCount();
			else
				count += view.countSelectedMarkers();
		}

		return count;
	}

	// Counts how many lines will be exported
	private int getLineCount()
	{
		boolean allLines = nbPanel.rDatAll.isSelected();
		int count = 0;

		if (allLines)
			return viewSet.getView(0).getLineCount();
		else
			return viewSet.getView(0).countSelectedLines();
	}

	// Export the map to disk
	private void exportMap()
	{
		boolean allMarkers = nbPanel.rMapAll.isSelected();

		String name = baseName + "_" + getMarkerCount() + ".map";
		File filename = promptForFilename(name, "map");

		if (filename != null)
		{
			ChromosomeMapExporter exporter
				= new ChromosomeMapExporter(filename, viewSet, allMarkers);

			displayDialog(exporter, filename);
		}
	}

	// Export the genotype data to disk
	private void exportDat()
	{
		String name = baseName + "_" + viewSet.getName() + "_"
			+ getLineCount() + "x" + getMarkerCount() + ".dat";
		File filename = promptForFilename(name, "dat");

		boolean allMarkers = nbPanel.rMapAll.isSelected();
		boolean allLines = nbPanel.rDatAll.isSelected();

		if (filename != null)
		{
			GenotypeDataExporter exporter
				= new GenotypeDataExporter(filename, viewSet, allMarkers, allLines);

			displayDialog(exporter, filename);
		}
	}

	private void displayDialog(ITrackableJob exporter, File filename)
	{
		ProgressDialog dialog = new ProgressDialog(exporter,
			 RB.format("gui.dialog.ExportDataDialog.exportTitle"),
			 RB.format("gui.dialog.ExportDataDialog.exportLabel"));

		// If the operation failed or was cancelled...
		if (dialog.isOK() == false)
		{
			if (dialog.getException() != null)
			{
				dialog.getException().printStackTrace();
				TaskDialog.error(
					RB.format("gui.dialog.ExportDataDialog.exportException",
					dialog.getException().getMessage()),
					RB.getString("gui.text.close"));
			}

			return;
		}

		TaskDialog.info(
			RB.format("gui.dialog.ExportDataDialog.exportSuccess", filename),
			RB.getString("gui.text.close"));
	}

	private File promptForFilename(String baseName, String extension)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(RB.getString("gui.dialog.ExportDataDialog.saveDialog"));
		fc.setSelectedFile(new File(Prefs.guiCurrentDir, baseName));

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters." + extension), extension);
		fc.setFileFilter(filter);

		while (fc.showSaveDialog(Flapjack.winMain) == JFileChooser.APPROVE_OPTION)
		{
			File file = FileNameExtensionFilter.getSelectedFileForSaving(fc);

			// Confirm overwrite
			if (file.exists())
			{
				String msg = RB.format("gui.dialog.ExportDataDialog.confirm", file);
				String[] options = new String[] {
					RB.getString("gui.dialog.ExportDataDialog.overwrite"),
					RB.getString("gui.dialog.ExportDataDialog.rename"),
					RB.getString("gui.text.cancel")
				};

				int response = TaskDialog.show(msg, TaskDialog.WAR, 0, options);

				if (response == 1)
					continue;
				else if (response == -1 || response == 2)
					return null;
			}

			// Otherwise it's ok to save...
			Prefs.guiCurrentDir = fc.getCurrentDirectory().getPath();

			return file;
		}

		return null;
	}
}