// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

public class BrowseDialog extends JDialog implements ActionListener
{
	private JButton bImport, bCancel, bHelp;
	private boolean isOK = false;

	private NBBrowsePanel nbPanel = new NBBrowsePanel();

	public BrowseDialog(String fileHistory)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.BrowseDialog.title"),
			true
		);

		nbPanel.browseComboBox.setHistory(fileHistory);

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
		bImport = SwingUtils.getButton(RB.getString("gui.dialog.BrowseDialog.bImport"));
		bImport.addActionListener(this);
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
//		FlapjackUtils.setHelp(bHelp, "gui.dialog.DataImportDialog");

		JPanel p1 = FlapjackUtils.getButtonPanel();
		p1.add(bImport);
		p1.add(bCancel);
//		p1.add(bHelp);

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

	public File getFile()
		{ return new File(nbPanel.browseComboBox.getText()); }

	/**
	 * @return browseComboBox.history
	 */
	public String getHistory()
	{
		return nbPanel.browseComboBox.getHistory();
	}

}