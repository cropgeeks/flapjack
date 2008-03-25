package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.gui.*;
import flapjack.gui.visualization.*;
import flapjack.io.*;

import scri.commons.gui.*;

public class ExportingImageDialog extends JDialog
{
	private GenotypePanel gPanel;

	// The file being saved to
	private File file;
	// And any exception generated during the process
	private Exception exception;

	public ExportingImageDialog(GenotypePanel gPanel, File file)
	{
		super(Flapjack.winMain, RB.getString("gui.dialog.ExportingImageDialog.title"), true);

		this.gPanel = gPanel;
		this.file = file;

		add(new NBExportingImagePanel());

		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				exportImage();
			}
		});

		addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				displayResult();
			}
		});

		pack();
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private void exportImage()
	{
		Runnable r = new Runnable() {
			public void run()
			{
				// TODO: catch exceptions at this point? popups in non GUI thread?
				try
				{
					ImageExporter exporter = new ImageExporter(gPanel, file);
					exporter.doExport();
				}
				catch (Exception e)
				{
					System.out.println(e);

					exception = e;
				}

				setVisible(false);
			}
		};

		new Thread(r).start();
	}

	private void displayResult()
	{
		if (exception == null)
			TaskDialog.info(RB.format("gui.dialog.ExportingImageDialog.success", file),
				RB.getString("gui.text.close"));

		else
		{
			TaskDialog.error(RB.format("gui.dialog.ExportingImageDialog.exception", exception),
				RB.getString("gui.text.close"));
		}
	}
}