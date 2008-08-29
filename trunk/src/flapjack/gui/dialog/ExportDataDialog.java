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

	public ExportDataDialog(GTViewSet viewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.ExportDataDialog.title"),
			true
		);

		this.viewSet = viewSet;

		nbPanel = new NBExportDataPanel(this);

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

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
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

	private void exportMap()
	{
		File filename = promptForFilename("export.map");

		boolean allMarkers = nbPanel.rMapAll.isSelected();

		if (filename != null)
		{
			try {
				new ChromosomeMapExporter(filename, viewSet).export(allMarkers);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	private void exportDat()
	{
		File filename = promptForFilename("export.dat");

		boolean allMarkers = nbPanel.rMapAll.isSelected();
		boolean allLines = nbPanel.rDatAll.isSelected();

		if (filename != null)
		{
			try {
				new GenotypeDataExporter(filename, viewSet).export(allMarkers, allLines);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	private File promptForFilename(String baseName)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(RB.getString("gui.dialog.ExportDataDialog.saveDialog"));
		fc.setAcceptAllFileFilterUsed(false);
		fc.setSelectedFile(new File(Prefs.guiCurrentDir, baseName));

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

				int response = TaskDialog.show(msg, MsgBox.WAR, 0, options);

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