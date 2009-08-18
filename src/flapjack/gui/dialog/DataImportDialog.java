package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.gui.*;

import java.util.LinkedList;
import scri.commons.gui.*;

public class DataImportDialog extends JDialog implements ActionListener
{
	private JButton bImport, bCancel, bHelp;
	private boolean isOK = false;

	private NBDataImportPanel nbPanel = new NBDataImportPanel();

	public DataImportDialog()
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.DataImportDialog.title"),
			true
		);

		add(new TitlePanel2(), BorderLayout.NORTH);
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
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "gui.dialog.DataImportDialog");

		JPanel p1 = FlapjackUtils.getButtonPanel();
		p1.add(bImport);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bImport && nbPanel.isOK())
		{
			isOK = true;
			nbPanel.mapComboBox.updateComboBox(nbPanel.mapComboBox.getSelectedItem().toString());
			nbPanel.genoComboBox.updateComboBox(nbPanel.genoComboBox.getSelectedItem().toString());
			Prefs.guiMapList = nbPanel.mapComboBox.getHistory();
			Prefs.guiGenoList = nbPanel.genoComboBox.getHistory();
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