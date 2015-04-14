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

/**
 * Common/shared class that is used in several places by different components.
 */
public class BrowseDialog extends JDialog implements ActionListener
{
	private JButton bImport, bCancel, bHelp;
	private boolean isOK = false;

	private BrowsePanelNB nbPanel;

	/**
	 * @param fileHistory the history string to populate the combo box with
	 * @param rbTitle resource bundle string for the dialog's title
	 * @param rbLabel resource bundle string for the dialog's label
	 * @param rbButton resource bundle string for the dialog's main button
	 * @param help help topic to visit if the Help button is clicked (can be
	 * null)
	 */
	public BrowseDialog(String fileHistory, String rbTitle, String rbLabel, String rbButton, String help)
	{
		super( Flapjack.winMain, RB.getString(rbTitle), true);

		nbPanel = new BrowsePanelNB(rbLabel, fileHistory);

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(rbButton, help), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bImport);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons(String rbButton, String help)
	{
		bImport = SwingUtils.getButton(RB.getString(rbButton));
		bImport.addActionListener(this);
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");

		JPanel p1 = new DialogPanel();
		p1.add(bImport);
		p1.add(bCancel);

		if (help != null)
		{
			FlapjackUtils.setHelp(bHelp, help);
			p1.add(bHelp);
		}

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