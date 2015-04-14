// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class PedigreeSettingsDialog extends JDialog implements ActionListener
{
	private JButton bGenerate, bCancel, bHelp;
	private boolean isOK = false;

	private PedigreeSettingsPanelNB nbPanel = new PedigreeSettingsPanelNB();

	public PedigreeSettingsDialog(String fileHistory)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.PedigreeSettingsDialog.title"),
			true
		);

		nbPanel.browseComboBox.setHistory(fileHistory);

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bGenerate);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bGenerate = SwingUtils.getButton(RB.getString("gui.dialog.PedigreeSettingsDialog.bGenerate"));
		bGenerate.addActionListener(this);
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
//		FlapjackUtils.setHelp(bHelp, "gui.dialog.DataImportDialog");

		JPanel p1 = new DialogPanel();
		p1.add(bGenerate);
		p1.add(bCancel);
//		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bGenerate && nbPanel.isOK())
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

	public String getSelectedButton()
	{
		return nbPanel.group.getSelection().getActionCommand();
	}
}