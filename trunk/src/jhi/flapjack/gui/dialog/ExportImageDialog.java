// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.*;
import jhi.flapjack.io.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class ExportImageDialog extends JDialog implements ActionListener
{
	private JButton bExport, bCabcel, bHelp;

	private File file = null;

	private GenotypePanel gPanel;
	private ExportImagePanelNB nbPanel;

	public ExportImageDialog(GenotypePanel gPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.ExportImageDialog.title"),
			true
		);

		this.gPanel = gPanel;
		nbPanel = new ExportImagePanelNB(gPanel, new DblClickListener());

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		FlapjackUtils.initDialog(this, bExport, bCabcel, true, getContentPane());
	}

	private JPanel createButtons()
	{
		bExport = new JButton(RB.getString("gui.dialog.ExportImageDialog.bExport"));
		bExport.addActionListener(this);
		bCabcel = new JButton(RB.getString("gui.text.cancel"));
		bCabcel.addActionListener(this);
		bHelp = new JButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "_-_Export_Image");

		JPanel p1 = new DialogPanel();
		p1.add(bExport);
		p1.add(bCabcel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bExport && nbPanel.isOK() && promptForFilename())
			export();

		else if (e.getSource() == bCabcel)
			setVisible(false);
	}

	private void export()
	{
		ImageExporter exporter = new ImageExporter(gPanel, file);

		ProgressDialog dialog = new ProgressDialog(exporter,
			 RB.format("gui.dialog.ExportImageDialog.title"),
			 RB.format("gui.dialog.ExportImageDialog.label"),
			 Flapjack.winMain);

		// If the operation failed or was cancelled...
		if (dialog.failed("gui.error"))
			return;

		TaskDialog.info(
			RB.format("gui.dialog.ExportImageDialog.success", file),
			RB.getString("gui.text.close"));
	}

	private boolean promptForFilename()
	{
		File basename = new File(Prefs.guiCurrentDir, "Image.png");

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters.png"), "png");

		String filename = FlapjackUtils.getSaveFilename(
			RB.getString("gui.dialog.ExportImageDialog.saveDialog"), basename, filter);

		if (filename != null)
			file = new File(filename);

		return file != null;
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