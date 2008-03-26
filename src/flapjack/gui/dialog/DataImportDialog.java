package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

public class DataImportDialog extends JDialog implements ActionListener
{
	private JButton bImport, bCancel;
	private boolean isOK = false;

	private NBDataImportPanel nbPanel = new NBDataImportPanel();

	public DataImportDialog()
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.DataImportDialog.title"),
			true
		);

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bImport);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bImport = SwingUtils.getButton(RB.getString("gui.dialog.DataImportDialog.import"));
		bImport.addActionListener(this);
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bImport);
		p1.add(bCancel);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bImport && nbPanel.isOK())
		{
			isOK = true;
			setVisible(false);
		}

		else if (e.getSource() == bCancel)
			setVisible(false);
	}

	public boolean isOK() {
		return isOK;
	}

	public File getMapFile() {
		return nbPanel.getMapFile();
	}

	public File getGenotypeFile() {
		return nbPanel.getGenotypeFile();
	}
}