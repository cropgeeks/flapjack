package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

public class ExportDataDialog extends JDialog implements ActionListener
{
	private JButton bClose, bHelp;

	private NBExportDataPanel nbPanel;

	public ExportDataDialog()
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.ExportDataDialog.title"),
			true
		);

		nbPanel = new NBExportDataPanel();

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bClose);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bClose = SwingUtils.getButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "gui.dialog.ExportDataDialog");

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bClose);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bClose)
			setVisible(false);
	}

/*	private boolean promptForFilename()
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
	*/
}