package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.gui.*;
import flapjack.gui.visualization.*;
import flapjack.other.Filters;
import static flapjack.other.Filters.*;

import scri.commons.gui.*;

public class ExportImageDialog extends JDialog implements ActionListener
{
	private JButton bOK, bCancel;

	private File file = null;
	private boolean isOK = false;

	private NBExportImagePanel nbPanel;

	public ExportImageDialog(GenotypePanel gPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.ExportImageDialog.title"),
			true
		);

		nbPanel = new NBExportImagePanel(gPanel, new DblClickListener());

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bOK);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bOK = SwingUtils.getButton(RB.getString("gui.dialog.ExportImageDialog.bExport"));
		bOK.addActionListener(this);
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bOK);
		p1.add(bCancel);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
		{
			if (nbPanel.isOK() == false)
				return;

			// Hide the dialog...
			setVisible(false);

			// ...and then ask for the filename to save the image as
			isOK = promptForFilename();
		}

		else if (e.getSource() == bCancel)
			setVisible(false);
	}

	public boolean isOK()
		{ return isOK; }

	public File getFile()
		{ return file; }

	private boolean promptForFilename()
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(RB.getString("gui.dialog.ExportImageDialog.saveDialog"));
		fc.setAcceptAllFileFilterUsed(false);

		// TODO: Determine a proper filename to use
		fc.setSelectedFile(new File(Prefs.guiCurrentDir, "Image.png"));

		Filters.setFilters(fc, PNG, PNG);

		while (fc.showSaveDialog(Flapjack.winMain) == JFileChooser.APPROVE_OPTION)
		{
			file = Filters.getSelectedFileForSaving(fc);

			// Confirm overwrite
			if (file.exists())
			{
				String msg = RB.format("gui.dialog.ExportImageDialog.confirm", file);
				String[] options = new String[] {
					RB.getString("gui.dialog.ExportImageDialog.overwrite"),
					RB.getString("gui.dialog.ExportImageDialog.rename"),
					RB.getString("gui.text.cancel")
				};

				int response = TaskDialog.show(msg, MsgBox.WAR, 0, options);

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

			bOK.doClick();
		}
	}
}