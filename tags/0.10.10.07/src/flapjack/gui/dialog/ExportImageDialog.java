// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.gui.*;
import flapjack.gui.visualization.*;
import flapjack.io.*;
import flapjack.other.*;

import scri.commons.gui.*;

public class ExportImageDialog extends JDialog implements ActionListener
{
	private JButton bExport, bClose, bHelp;

	private File file = null;

	private GenotypePanel gPanel;
	private NBExportImagePanel nbPanel;

	public ExportImageDialog(GenotypePanel gPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.ExportImageDialog.title"),
			true
		);

		this.gPanel = gPanel;
		nbPanel = new NBExportImagePanel(gPanel, new DblClickListener());

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
		bExport = SwingUtils.getButton(RB.getString("gui.dialog.ExportImageDialog.bExport"));
		bExport.addActionListener(this);
		bClose = SwingUtils.getButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "gui.dialog.ExportImageDialog");

		JPanel p1 = FlapjackUtils.getButtonPanel();
		p1.add(bExport);
		p1.add(bClose);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bExport && nbPanel.isOK() && promptForFilename())
			export();

		else if (e.getSource() == bClose)
			setVisible(false);
	}

	private void export()
	{
		ImageExporter exporter = new ImageExporter(gPanel, file);

		ProgressDialog dialog = new ProgressDialog(exporter,
			 RB.format("gui.dialog.ExportImageDialog.title"),
			 RB.format("gui.dialog.ExportImageDialog.label"));

		// If the operation failed or was cancelled...
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				dialog.getException().printStackTrace();
				TaskDialog.error(
					RB.format("gui.dialog.ExportImageDialog.exception",
					dialog.getException().getMessage()),
					RB.getString("gui.text.close"));
			}

			return;
		}

		TaskDialog.info(
			RB.format("gui.dialog.ExportImageDialog.success", file),
			RB.getString("gui.text.close"));
	}

	private boolean promptForFilename()
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(RB.getString("gui.dialog.ExportImageDialog.saveDialog"));
		fc.setAcceptAllFileFilterUsed(false);

		// TODO: Determine a proper filename to use
		fc.setSelectedFile(new File(Prefs.guiCurrentDir, "Image.png"));

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters.png"), "png");
		fc.setFileFilter(filter);

		while (fc.showSaveDialog(Flapjack.winMain) == JFileChooser.APPROVE_OPTION)
		{
			file = FileNameExtensionFilter.getSelectedFileForSaving(fc);

			// Confirm overwrite
			if (file.exists())
			{
				String msg = RB.format("gui.dialog.ExportImageDialog.confirm", file);
				String[] options = new String[] {
					RB.getString("gui.dialog.ExportImageDialog.overwrite"),
					RB.getString("gui.dialog.ExportImageDialog.rename"),
					RB.getString("gui.text.cancel")
				};

				int response = TaskDialog.show(msg, TaskDialog.WAR, 1, options);

				if (response == 1)
					continue;
				else if (response == -1 || response == 2)
					return false;
			}

			// Otherwise it's ok to save...
			Prefs.guiCurrentDir = fc.getCurrentDirectory().getPath();

			return true;
		}

		return false;
	}

	private class DblClickListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount() != 2)
				return;

			bExport.doClick();
		}
	}
}